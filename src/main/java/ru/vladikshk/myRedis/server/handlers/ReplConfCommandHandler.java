package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig.ReplConfDefaultSubhandlerImpl;
import ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig.ReplConfSubhandler;
import ru.vladikshk.myRedis.types.RString;
import ru.vladikshk.myRedis.types.RType;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.server.handlers.CommandHandler.print;

@Slf4j
@RequiredArgsConstructor
public class ReplConfCommandHandler implements CommandHandler {
    private final List<ReplConfSubhandler> subHandlers;

    @Override
    public boolean canHandle(String command) {
        return "replconf".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        log.info("Using repl handler");

        subHandlers.stream()
            .filter(sub -> sub.canHandle(args.get(1)))
            .findAny()
            .orElse(new ReplConfDefaultSubhandlerImpl())
            .handle(args, out);
    }
}
