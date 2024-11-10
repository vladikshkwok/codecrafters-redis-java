package ru.vladikshk.myRedis.server.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.types.RString;
import ru.vladikshk.myRedis.types.RType;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.server.handlers.CommandHandler.print;

@Slf4j
public class PingCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return "PING".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        RType pong = new RString("PONG");
        log.info("get PING, sending PONG");
        print(out, pong.getBytes());
    }
}
