package ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig;

import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.types.RArray;

import java.util.List;

public class ReplConfGetAckSubhandlerImpl implements ReplConfSubhandler {
    @Override
    public boolean canHandle(String command) {
        return "GETACK".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        print(serverConnection, new RArray(List.of("REPLCONF", "ACK", "0")).getBytes());
    }
}
