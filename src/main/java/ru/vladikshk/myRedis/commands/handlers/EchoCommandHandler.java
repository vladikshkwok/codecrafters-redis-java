package ru.vladikshk.myRedis.commands.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.typeResolvers.types.RBulkString;
import ru.vladikshk.myRedis.typeResolvers.types.RType;

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
        RType echoArgs = new RBulkString(args.getFirst());
        print(out, echoArgs.getBytes());
        log.info("ECHO: {}", args.getFirst());
    }

}