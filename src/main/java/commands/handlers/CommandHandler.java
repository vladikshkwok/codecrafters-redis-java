package commands.handlers;

import java.io.PrintWriter;

public interface CommandHandler {
    boolean canHandle(String command);
    void handle(PrintWriter out);
}
