package ru.vladikshk.myRedis.server.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.types.RString;
import ru.vladikshk.myRedis.types.RType;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.server.handlers.CommandHandler.print;

@Slf4j
public class ReplConfCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return "replconf".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        RType resp = new RString("OK");
        log.info("Got REPLCONF command, send OK");
        print(out, resp.getBytes());
    }
}
