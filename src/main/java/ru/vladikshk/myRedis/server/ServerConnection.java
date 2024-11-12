package ru.vladikshk.myRedis.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.server.handlers.*;
import ru.vladikshk.myRedis.server.handlers.subhandlers.config.ConfigGetCommandSubHandler;
import ru.vladikshk.myRedis.server.handlers.subhandlers.info.ReplicationInfoSubhandler;
import ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig.ReplConfGetAckSubhandlerImpl;
import ru.vladikshk.myRedis.service.ReplicationService;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RArray;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static ru.vladikshk.myRedis.data.HandlerType.WRITE;

@Slf4j
public class ServerConnection implements Runnable {
    private final List<CommandHandler> commandHandlers;
    private final ReplicationService replicationService;

    private final BufferedReader in;
    private final OutputStream out;

    public ServerConnection(StorageService storageService, RedisConfig redisConfig,
                            ReplicationService replicationService, Socket socket,
                            boolean isReplica) throws IOException {
        this.replicationService = replicationService;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        if (isReplica) {
            this.out = System.out;
        } else {
            this.out = new BufferedOutputStream(socket.getOutputStream());
        }
        this.commandHandlers = List.of(
            new PingCommandHandler(), new EchoCommandHandler(), new SetCommandHandler(storageService),
            new GetCommandHandler(storageService),
            new ConfigCommandHandler(List.of(new ConfigGetCommandSubHandler(redisConfig))),
            new KeysCommandHandler(storageService),
            new InfoCommandHandler(List.of(new ReplicationInfoSubhandler(redisConfig))),
            new ReplConfCommandHandler(List.of(new ReplConfGetAckSubhandlerImpl())),
            new PsyncCommandHandler(replicationService)
        );
    }

    @Override
    public void run() {
        while (true) {
            List<String> inputArgs = parseInput();

            if (inputArgs == null || inputArgs.isEmpty()) continue;

            CommandHandler handler = commandHandlers.stream()
                .filter(commandHandler -> commandHandler.canHandle(inputArgs.getFirst()))
                .findAny()
                .orElse(new DefaultCommandHandler());

            handler.handle(inputArgs, out);

            if (WRITE.equals(handler.getHandlerType())) {
                replicationService.sendCommand(new RArray(inputArgs).getBytes());
            }
        }
    }

    public List<String> parseInput() {
        try {
            String command = in.readLine();
            if (command == null || command.trim().isEmpty()) {
                return null;
            }
            log.info("Received input: " + command);

            if (command.charAt(0) == '*') {
                return parseRedisArray(command);
            }
            if (command.charAt(0) == '$') {
                return List.of(parseRedisString(command));
            }
        } catch (IOException e) {
            log.error("Couldn't parse input", e);
        }
        return null;
    }

    private String parseRedisString(String command) throws IOException {
        int strlen = Integer.parseInt(command.substring(1));
        String str = in.readLine();
        log.info("Reading redis string={} with length={}", str, strlen);
        if (str.length() > strlen) {
            throw new IllegalArgumentException("String length differ with given length value");
        }
        return str;
    }

    private List<String> parseRedisArray(String command) throws IOException {
        List<String> list = new ArrayList<>();
        int arrayLength = Integer.parseInt(command.substring(1));
        log.info("Reading redis array with length: {}", arrayLength);
        for (int i = 0; i < arrayLength; i++) {
            list.add(parseInput().getFirst());
        }
        return list;
    }
}
