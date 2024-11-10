package ru.vladikshk.myRedis.server.handlers.subhandlers.config;

import lombok.RequiredArgsConstructor;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.server.handlers.CommandHandler;
import ru.vladikshk.myRedis.types.RArray;

import java.io.OutputStream;
import java.util.List;

@RequiredArgsConstructor
public class ConfigGetCommandSubHandler implements ConfigSubhandler {
    private final RedisConfig redisConfig;

    @Override
    public boolean canHandle(String command) {
        return "GET".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        String key = args.get(2);
        String result = redisConfig.getParam(key)
            .orElse("Unknown config parameter");

        CommandHandler.print(out, new RArray(List.of(key, result)).getBytes());
    }
}
