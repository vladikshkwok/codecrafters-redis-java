package ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.types.RString;
import ru.vladikshk.myRedis.types.RType;

import java.util.List;

@Slf4j
public class ReplConfDefaultSubhandlerImpl implements ReplConfSubhandler {
    @Override
    public boolean canHandle(String command) {
        return true;
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        RType resp = new RString("OK");
        log.info("Got REPLCONF command, send OK");
        print(serverConnection, resp.getBytes());
    }
}
