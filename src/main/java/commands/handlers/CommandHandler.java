package commands.handlers;

import java.io.PrintWriter;
import java.util.List;

public interface CommandHandler {
    boolean canHandle(String command);
    void handle(List<String> command, PrintWriter out);
}
