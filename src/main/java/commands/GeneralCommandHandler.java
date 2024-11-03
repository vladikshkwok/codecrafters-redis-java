package commands;

import commands.handlers.CommandHandler;
import commands.handlers.DefaultCommandHandler;
import commands.handlers.EchoCommandHandler;
import commands.handlers.PingCommandHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneralCommandHandler implements Runnable {
    private static List<CommandHandler> commandHandlers = List.of(new PingCommandHandler(), new EchoCommandHandler());
    private final Socket socket;
    private BufferedReader in;
    private OutputStream out;

    public GeneralCommandHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object o = parseCommand();
                List<String> array;
                if (o instanceof List<?>) {
                    array = (List<String>) o;
                } else {
                    array = Collections.emptyList();
                }

                if (array.size() < 1) continue;

                commandHandlers.stream()
                    .filter(commandHandler -> commandHandler.canHandle(array.getFirst()))
                    .findAny()
                    .orElse(new DefaultCommandHandler())
                    .handle(array, out);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object parseCommand() throws IOException {
        String command = in.readLine();
        System.out.println("Received command: " + command);
        if (command == null || command.trim().isEmpty()) {
            return null;
        }

        if (command.charAt(0) == '*') {
            return parseRedisArray(command);
        }
        if (command.charAt(0) == '$') {
            return parseRedisString(command);
        }
        throw new IllegalArgumentException();
    }

    private String parseRedisString(String command) throws IOException {
        int strlen = Integer.parseInt(command.substring(1));
        String str = in.readLine();
        if (str.length() > strlen) {
            throw new IllegalArgumentException();
        }
        return str;
    }

    private List<String> parseRedisArray(String command) throws IOException {
        List<String> list = new ArrayList<>();
        int arraylen = Integer.parseInt(command.substring(1));
        for (int i = 0; i < arraylen; i++) {
            list.add((String) parseCommand());
        }
        return list;
    }
}
