package commands.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

public interface CommandHandler {
    static void print(OutputStream out, byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean canHandle(String command);
    void handle(List<String> command, OutputStream out);
}
