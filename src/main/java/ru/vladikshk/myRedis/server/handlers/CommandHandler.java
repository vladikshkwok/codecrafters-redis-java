package ru.vladikshk.myRedis.server.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface CommandHandler {
    static void print(OutputStream out, byte[] bytes, boolean autoFlush) {
        try {
            out.write(bytes);
            if (autoFlush) {
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void print(OutputStream out, byte[] bytes) {
        print(out, bytes, true);
    }

    boolean canHandle(String command);

    void handle(List<String> args, OutputStream out);
}
