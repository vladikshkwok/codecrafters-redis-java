package commands.handlers;

import java.io.PrintWriter;
import java.util.List;

public class DefaultCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return true;
    }

    @Override
    public void handle(List<String> command, PrintWriter out) {
        out.println("Couldn't handle command");
    }
}
