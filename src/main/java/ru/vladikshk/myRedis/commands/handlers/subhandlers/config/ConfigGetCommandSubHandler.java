package ru.vladikshk.myRedis.commands.handlers.subhandlers.config;

import lombok.RequiredArgsConstructor;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.commands.handlers.CommandHandler;
import ru.vladikshk.myRedis.commands.handlers.ConfigCommandHandler;
import ru.vladikshk.myRedis.service.SimpleStorageService;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RArray;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

public class ConfigGetCommandSubHandler implements ConfigSubhandler {
    private final RedisConfig redisConfig = RedisConfig.getInstance();

    @Override
    public boolean canHandle(String command) {
        return "GET".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        String key = args.get(2);
        String result = switch (key) {
            case "dir" -> redisConfig.getDir();
            case "dbfilename" -> redisConfig.getDbFileName();
            case null, default -> "Unknown config key: " + key;
        };

        print(out, new RArray(List.of(key, result)).getBytes());
    }
}
