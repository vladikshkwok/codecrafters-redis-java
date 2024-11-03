package commands.handlers;

import java.io.PrintWriter;
import java.util.List;

public class PingCommandHandler implements CommandHandler {
    private static final String PING = "PING";

    @Override
    public boolean canHandle(String command) {
        return PING.equals(command);
    }

    @Override
    public void handle(List<String> command, PrintWriter out) {
        out.println("+PONG\r");
    }
}
