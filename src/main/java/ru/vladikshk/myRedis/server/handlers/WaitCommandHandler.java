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
        boolean acked = replicationService.waitForReplicasOrTimeout(waitForReplicasCount, timeoutMs);
        log.info("Awaited for {} replicas, success = {}", waitForReplicasCount, acked);

        RType count = acked ? new RInteger(waitForReplicasCount) : new RInteger(replicationService.getReplicaCount());

        log.info("Respond for wait {} {}, with answer: {}", waitForReplicasCount, timeoutMs, count);
        print(serverConnection, count.getBytes());
    }
}
