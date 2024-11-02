package commands;

import commands.handlers.CommandHandler;
import commands.handlers.DefaultCommandHandler;
import commands.handlers.PingCommandHandler;

import java.io.*;
import java.net.Socket;
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
                String command = in.readLine();
                System.out.println("Received command: " + command);

                if (Objects.equals(command, "exit")) break;

                commandHandlers.stream()
                    .filter(commandHandler -> commandHandler.canHandle(command))
                    .findAny()
                    .orElse(new DefaultCommandHandler())
                    .handle(out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
