package ru.vladikshk.myRedis.commands.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.commands.handlers.subhandlers.config.ConfigSubhandler;
import ru.vladikshk.myRedis.commands.handlers.subhandlers.info.InfoSubhandler;

import java.io.OutputStream;
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
    public void handle(List<String> args, OutputStream out) {
        log.info("Using config handler");
        infoSubHandlers.stream()
            .filter(sub -> sub.canHandle(args.get(1)))
            .findAny()
            .orElseThrow(IllegalArgumentException::new)
            .handle(args, out);
    }

}
