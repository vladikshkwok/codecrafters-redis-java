package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.ReplicationService;
import ru.vladikshk.myRedis.types.RString;
import ru.vladikshk.myRedis.types.RType;

import java.io.IOException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PsyncCommandHandler implements CommandHandler {
    private static final byte[] EMPTY_RDB = HexFormat.of().parseHex(
        "524544495330303131fa0972656469732d76657205372e322e30fa0a72656469732d62697473c040fa056374696d65c26d08bc65fa08757365642d6d656dc2b0c41000fa08616f662d62617365c000fff06e3bfec0ff5aa2");
    private final ReplicationService replicationService;
    @Override
    public boolean canHandle(String command) {
        return "psync".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        RType resp = new RString("FULLRESYNC 8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb 0"); // todo change this hardcode (this logic should be in replicationService)
        log.info("Got PSYNC command, send response");
        print(serverConnection, resp.getBytes());
        print(serverConnection, ("$" + EMPTY_RDB.length + "\r\n").getBytes());
        print(serverConnection, EMPTY_RDB);
        log.info("sended {} bytes", new String(EMPTY_RDB));
        replicationService.addReplica(serverConnection); // todo refactor
    }
}
