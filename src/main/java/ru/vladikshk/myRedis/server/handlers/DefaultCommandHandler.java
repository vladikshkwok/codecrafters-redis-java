package ru.vladikshk.myRedis.server.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.types.RBulkString;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.server.handlers.CommandHandler.print;

@Slf4j
public class DefaultCommandHandler implements CommandHandler {

    @Override
    public boolean canHandle(String command) {
        return true;
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        print(out, new RBulkString("Couldn't handle command").getBytes());
        log.warn("Couldn't handle command with args: {}", args);
    }
}
