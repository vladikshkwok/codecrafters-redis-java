package ru.vladikshk.myRedis.commands.handlers.subhandlers.info;

import lombok.RequiredArgsConstructor;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.commands.handlers.CommandHandler;
import ru.vladikshk.myRedis.types.RBulkString;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

@RequiredArgsConstructor
public class ReplicationInfoSubhandler implements InfoSubhandler {
    private final RedisConfig redisConfig;

    @Override
    public boolean canHandle(String command) {
        return "replication".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        print(out, new RBulkString("role:" + redisConfig.getRole()).getBytes());
    }
}
