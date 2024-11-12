package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig.ReplConfDefaultSubhandlerImpl;
import ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig.ReplConfSubhandler;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ReplConfCommandHandler implements CommandHandler {
    private final List<ReplConfSubhandler> subHandlers;

    @Override
    public boolean canHandle(String command) {
        return "replconf".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        log.info("Using repl handler");

        subHandlers.stream()
            .filter(sub -> sub.canHandle(args.get(1)))
            .findAny()
            .orElse(new ReplConfDefaultSubhandlerImpl())
            .handle(args, serverConnection);
    }
}
