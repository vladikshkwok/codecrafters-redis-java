package ru.vladikshk.myRedis.server.handlers;

import ru.vladikshk.myRedis.data.HandlerType;
import ru.vladikshk.myRedis.server.ServerConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.data.HandlerType.*;

public interface CommandHandler {

    default void print(ServerConnection serverConnection, byte[] bytes) {
        print(serverConnection, bytes, true);
    }

    default void print(ServerConnection serverConnection, byte[] bytes, boolean autoFlush) {
        if (serverConnection.isReplica() && !REPL.equals(getHandlerType()) ) {
            return;
        }

        try {
            serverConnection.getOut().write(bytes);
            if (autoFlush) {
                serverConnection.getOut().flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean canHandle(String command);

    void handle(List<String> args, ServerConnection serverConnection);

    default HandlerType getHandlerType() {
        return READ;
    }
}
