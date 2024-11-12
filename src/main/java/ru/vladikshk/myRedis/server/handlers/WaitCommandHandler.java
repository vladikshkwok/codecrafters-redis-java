package ru.vladikshk.myRedis.server.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.types.RInteger;
import ru.vladikshk.myRedis.types.RString;
import ru.vladikshk.myRedis.types.RType;

import java.util.List;

@Slf4j
public class WaitCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return "WAIT".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        log.info("Waiting");
        RType pong = new RInteger(0);

        print(serverConnection, pong.getBytes());
    }
}
