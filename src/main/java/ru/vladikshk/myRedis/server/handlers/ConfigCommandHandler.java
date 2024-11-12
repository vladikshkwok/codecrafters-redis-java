package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.server.handlers.subhandlers.config.ConfigSubhandler;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ConfigCommandHandler implements CommandHandler {
    private final List<ConfigSubhandler> configSubHandlers;

    @Override
    public boolean canHandle(String command) {
        return "config".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        log.info("Using config handler");
        configSubHandlers.stream()
            .filter(sub -> sub.canHandle(args.get(1)))
            .findAny()
            .orElseThrow(IllegalArgumentException::new)
            .handle(args, serverConnection);
    }

}
