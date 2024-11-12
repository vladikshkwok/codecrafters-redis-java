package ru.vladikshk.myRedis.server.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.types.RBulkString;
import ru.vladikshk.myRedis.types.RType;

import java.util.List;

@Slf4j
public class EchoCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return "ECHO".equals(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        RType echoArgs = new RBulkString(args.getLast());
        print(serverConnection, echoArgs.getBytes());
        log.info("ECHO: {}", echoArgs);
    }

}
