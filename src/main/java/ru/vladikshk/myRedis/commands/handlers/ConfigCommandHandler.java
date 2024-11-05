package ru.vladikshk.myRedis.commands.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.commands.handlers.subhandlers.config.ConfigGetCommandSubHandler;
import ru.vladikshk.myRedis.commands.handlers.subhandlers.config.ConfigSubhandler;
import ru.vladikshk.myRedis.service.SimpleStorageService;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RBulkString;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

@Slf4j
public class ConfigCommandHandler implements CommandHandler {
    private final List<ConfigSubhandler> configSubHandlers = List.of(new ConfigGetCommandSubHandler());

    @Override
    public boolean canHandle(String command) {
        return "CONFIG".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        log.info("Using config handler");
        configSubHandlers.stream()
            .filter(sub -> sub.canHandle(args.get(1)))
            .findAny()
            .orElseThrow(IllegalArgumentException::new)
            .handle(args, out);
    }

}
