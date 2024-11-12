package ru.vladikshk.myRedis.server.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.types.RBulkString;

import java.util.List;

@Slf4j
public class DefaultCommandHandler implements CommandHandler {

    @Override
    public boolean canHandle(String command) {
        return true;
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        print(serverConnection, new RBulkString("Couldn't handle command").getBytes());
        log.warn("Couldn't handle command with args: {}", args);
    }
}
