package ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig;

import ru.vladikshk.myRedis.server.handlers.CommandHandler;
import ru.vladikshk.myRedis.types.RArray;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.server.handlers.CommandHandler.print;

public class ReplConfGetAckSubhandlerImpl implements ReplConfSubhandler {
    @Override
    public boolean canHandle(String command) {
        return "GETACK".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        print(out, new RArray(List.of("REPLCONF", "ACK", "0")).getBytes());
    }
}
