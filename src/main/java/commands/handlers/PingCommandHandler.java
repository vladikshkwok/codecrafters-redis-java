package commands.handlers;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PingCommandHandler implements CommandHandler {
    private static final String PING = "PING";

    @Override
    public boolean canHandle(String command) {
        return true;
    }

    @Override
    public void handle(PrintWriter out) {
        out.println("+PONG\r");
    }
}
