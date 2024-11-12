package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.ReplicationService;
import ru.vladikshk.myRedis.types.RString;
import ru.vladikshk.myRedis.types.RType;

import java.util.Base64;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PsyncCommandHandler implements CommandHandler {
    private static final String EMPTY_RDB = "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==";
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
        byte[] emptyDB = Base64.getDecoder().decode(EMPTY_RDB);
        print(serverConnection, ("$" + emptyDB.length + "\r\n").getBytes(), false);
        print(serverConnection, emptyDB);
        replicationService.addReplica(serverConnection); // todo refactor
    }
}
