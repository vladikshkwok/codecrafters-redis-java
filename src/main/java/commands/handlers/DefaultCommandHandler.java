package commands.handlers;

import java.io.PrintWriter;

public class DefaultCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return true;
    }

    @Override
    public void handle(PrintWriter out) {
        out.println("Couldn't handle command");
    }
}
