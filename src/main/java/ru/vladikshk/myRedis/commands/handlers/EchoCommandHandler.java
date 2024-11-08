package ru.vladikshk.myRedis.commands.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.types.RBulkString;
import ru.vladikshk.myRedis.types.RType;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

@Slf4j
public class EchoCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return "ECHO".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        RType echoArgs = new RBulkString(args.getLast());
        print(out, echoArgs.getBytes());
        log.info("ECHO: {}", echoArgs);
    }

}
