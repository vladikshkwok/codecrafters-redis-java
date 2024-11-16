package ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig;

import lombok.RequiredArgsConstructor;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.ReplicationService;
import ru.vladikshk.myRedis.types.RArray;

import java.util.List;

@RequiredArgsConstructor
public class ReplConfAckSubhandlerImpl implements ReplConfSubhandler {
    private final ReplicationService replicationService;
    @Override
    public boolean canHandle(String command) {
        return "ACK".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        String bytesReceived = args.get(2);
        replicationService.
    }
}
