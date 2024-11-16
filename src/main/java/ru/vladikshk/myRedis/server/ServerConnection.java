package ru.vladikshk.myRedis.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.server.handlers.*;
import ru.vladikshk.myRedis.server.handlers.subhandlers.config.ConfigGetCommandSubHandler;
import ru.vladikshk.myRedis.server.handlers.subhandlers.info.ReplicationInfoSubhandler;
import ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig.ReplConfAckSubhandlerImpl;
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
@Getter
public class ServerConnection implements Runnable {
    private final List<CommandHandler> commandHandlers;
    private final ReplicationService replicationService;

    private final OutputStream out;
    private final BufferedReader reader;
    private final boolean isReplica;
    private final Socket connection;
    private long receivedBytes = 0;

    public ServerConnection(StorageService storageService, RedisConfig redisConfig,
                            ReplicationService replicationService, Socket socket,
                            boolean isReplica) throws IOException {
        this.replicationService = replicationService;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedOutputStream(socket.getOutputStream());
        this.isReplica = isReplica;
        this.connection = socket;

        this.commandHandlers = List.of(
            new PingCommandHandler(), new EchoCommandHandler(), new SetCommandHandler(storageService),
            new GetCommandHandler(storageService),
            new ConfigCommandHandler(List.of(new ConfigGetCommandSubHandler(redisConfig))),
            new KeysCommandHandler(storageService),
            new InfoCommandHandler(List.of(new ReplicationInfoSubhandler(redisConfig))),
            new ReplConfCommandHandler(List.of(new ReplConfGetAckSubhandlerImpl(), new ReplConfAckSubhandlerImpl(replicationService))),
            new PsyncCommandHandler(replicationService), new WaitCommandHandler(replicationService),
            new TypeCommandHandler(storageService)
        );
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                log.info("[{}] Waiting for incoming command...", isReplica ? "slave" : "master");
                List<String> inputArgs = parseInput();

                if (inputArgs.isEmpty()) continue;

                CommandHandler handler = commandHandlers.stream()
                    .filter(commandHandler -> commandHandler.canHandle(inputArgs.getFirst()))
                    .findFirst()
                    .orElse(new DefaultCommandHandler());

                try {
                    handler.handle(inputArgs, this);

                    if (WRITE.equals(handler.getHandlerType())) {
                        sendToReplicas(inputArgs);
                    }
                } catch (Exception e) {
                    log.error("Error handling command: {}", e.getMessage(), e);
                }

                receivedBytes += new RArray(inputArgs).getBytes().length;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    public List<String> parseInput() throws IOException {

        String command = reader.readLine();
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty command");
        }
        log.info("Received input: {}", command);

        if (command.charAt(0) == '*') {
            return parseRedisArray(command);
        } else if (command.charAt(0) == '$') {
            return List.of(parseRedisString(command));
        } else {
            log.warn("Unexpected command format: {}", command);
            throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private void sendToReplicas(List<String> inputArgs) {
        replicationService.sendCommand(new RArray(inputArgs).getBytes());
    }

    private String parseRedisString(String command) throws IOException {
        int strlen = Integer.parseInt(command.substring(1));
        String str = reader.readLine();
        log.info("Reading redis string={} with length={}", str, strlen);
        if (str.length() > strlen) {
            throw new IllegalArgumentException("String length differs from the given length value");
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

    private void closeConnection() {
        try {
            out.close();
            reader.close();
            log.info("Connection closed.");
        } catch (IOException e) {
            log.error("Error while closing connection", e);
        }
    }
}
