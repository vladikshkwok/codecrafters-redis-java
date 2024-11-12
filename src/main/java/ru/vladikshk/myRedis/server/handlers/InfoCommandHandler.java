package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.server.handlers.subhandlers.info.InfoSubhandler;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InfoCommandHandler implements CommandHandler {
    private final List<InfoSubhandler> infoSubHandlers;

    @Override
    public boolean canHandle(String command) {
        return "info".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        log.info("Using info handler");
        infoSubHandlers.stream()
            .filter(sub -> sub.canHandle(args.get(1)))
            .findAny()
            .orElseThrow(IllegalArgumentException::new)
            .handle(args, serverConnection);
    }

}
