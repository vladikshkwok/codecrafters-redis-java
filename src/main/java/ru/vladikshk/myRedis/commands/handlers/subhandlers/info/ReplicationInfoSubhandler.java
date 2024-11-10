package ru.vladikshk.myRedis.commands.handlers.subhandlers.info;

import ru.vladikshk.myRedis.commands.handlers.CommandHandler;
import ru.vladikshk.myRedis.types.RBulkString;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

public class ReplicationInfoSubhandler implements InfoSubhandler {
    @Override
    public boolean canHandle(String command) {
        return "replication".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        print(out, new RBulkString("role:master").getBytes());
    }
}
