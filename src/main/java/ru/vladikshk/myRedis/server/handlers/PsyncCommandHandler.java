package ru.vladikshk.myRedis.server.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.types.RDBFile;
import ru.vladikshk.myRedis.types.RString;
import ru.vladikshk.myRedis.types.RType;

import java.io.OutputStream;
import java.util.Base64;
import java.util.List;

import static ru.vladikshk.myRedis.server.handlers.CommandHandler.print;

@Slf4j
public class PsyncCommandHandler implements CommandHandler {
    private static final String EMPTY_RDB = "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==";
    @Override
    public boolean canHandle(String command) {
        return "psync".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        RType resp = new RString("FULLRESYNC 8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb 0"); // todo change this hardcode (this logic should be in replicationService)
        log.info("Got PSYNC command, send response");
        print(out, resp.getBytes());
        byte[] emptyDB = Base64.getDecoder().decode(EMPTY_RDB);
        print(out, new RDBFile(emptyDB).getBytes());
    }
}
