package ru.vladikshk.myRedis.commands;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.service.SimpleStorageService;
import ru.vladikshk.myRedis.commands.handlers.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GeneralCommandHandler implements Runnable {
    private static SimpleStorageService storageService = new SimpleStorageService();
    private static List<CommandHandler> commandHandlers = List.of(
        new PingCommandHandler(), new EchoCommandHandler(), new SetCommandHandler(storageService),
        new GetCommandHandler(storageService)
    );

    private BufferedReader in;
    private OutputStream out;

    public GeneralCommandHandler(Socket socket) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            List<String> redisArray = parseInput();

            if (redisArray.isEmpty()) continue;

            commandHandlers.stream()
                .filter(commandHandler -> commandHandler.canHandle(redisArray.getFirst()))
                .findAny()
                .orElse(new DefaultCommandHandler())
                .handle(redisArray, out);

            out.flush();
        }
    }

    public List<String> parseInput() throws IOException {
        String command = in.readLine();
        if (command == null || command.trim().isEmpty()) {
            return null;
        }
        log.debug("Received input: " + command);

        if (command.charAt(0) == '*') {
            return parseRedisArray(command);
        }
        if (command.charAt(0) == '$') {
            return List.of(parseRedisString(command));
        }

        throw new IllegalArgumentException();
    }

    private String parseRedisString(String command) throws IOException {
        int strlen = Integer.parseInt(command.substring(1));
        log.info("Reading redis string with length: {}", strlen);
        String str = in.readLine();
        if (str.length() > strlen) {
            throw new IllegalArgumentException();
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
