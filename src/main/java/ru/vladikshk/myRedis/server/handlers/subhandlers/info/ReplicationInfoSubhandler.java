package ru.vladikshk.myRedis.server.handlers.subhandlers.info;

import lombok.RequiredArgsConstructor;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.types.RBulkString;

import java.util.List;

@RequiredArgsConstructor
public class ReplicationInfoSubhandler implements InfoSubhandler {
    private final RedisConfig redisConfig;

    @Override
    public boolean canHandle(String command) {
        return "replication".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        print(serverConnection, new RBulkString(redisConfig.getReplicationInfo()).getBytes());
    }
}
