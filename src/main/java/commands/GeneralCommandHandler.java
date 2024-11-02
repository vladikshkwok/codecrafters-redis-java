package commands;

import commands.handlers.CommandHandler;
import commands.handlers.DefaultCommandHandler;
import commands.handlers.PingCommandHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeneralCommandHandler implements Runnable {
    private static List<CommandHandler> commandHandlers = List.of(new PingCommandHandler());
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public GeneralCommandHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object o = parseCommand();
                String first;
                if (o instanceof List<?>) {
                    first = (String) ((List<?>) o).getFirst();
                } else {
                    first = "";
                }

                if (Objects.equals(first, "exit")) break;

                commandHandlers.stream()
                    .filter(commandHandler -> commandHandler.canHandle(first))
                    .findAny()
                    .orElse(new DefaultCommandHandler())
                    .handle(out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object parseCommand() throws IOException {
        String command = in.readLine();
        System.out.println("Received command: " + command);
        if (command.charAt(0) == '*') {
            return parseRedisArray(command);
        }
        if (command.charAt(0) == '$') {
            return parseRedisString(command);
        }
        throw new IllegalArgumentException();
    }

    private String parseRedisString(String command) throws IOException {
        int strlen = Integer.parseInt(command.substring(1, command.length() - 1));
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
