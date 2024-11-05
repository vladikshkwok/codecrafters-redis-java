package ru.vladikshk.myRedis.commands.handlers.subhandlers.config;

import lombok.RequiredArgsConstructor;
import ru.vladikshk.myRedis.commands.handlers.CommandHandler;
import ru.vladikshk.myRedis.commands.handlers.ConfigCommandHandler;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RArray;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

@RequiredArgsConstructor
public class ConfigGetCommandSubHandler implements ConfigSubhandler {
    private final StorageService storageService;

    @Override
    public boolean canHandle(String command) {
        return "GET".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        String key = args.get(2);
        String result = storageService.get("config--" + key);

        print(out, new RArray(List.of(key, result)).getBytes());
    }
}
