package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.ReplicationService;
import ru.vladikshk.myRedis.types.RInteger;
import ru.vladikshk.myRedis.types.RType;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class WaitCommandHandler implements CommandHandler {
    private final ReplicationService replicationService;

    @Override
    public boolean canHandle(String command) {
        return "WAIT".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        log.info("Waiting");
        int waitForReplicasCount = Integer.parseInt(args.get(1));
        long timeoutMs = Long.parseLong(args.get(2));
        RType pong = new RInteger(replicationService.getReplicaCount());

        replicationService.waitForReplicasOrTimeout(waitForReplicasCount, timeoutMs);
        print(serverConnection, pong.getBytes());
    }
}
