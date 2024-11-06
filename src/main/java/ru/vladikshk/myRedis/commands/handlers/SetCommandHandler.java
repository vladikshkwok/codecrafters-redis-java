package ru.vladikshk.myRedis.commands.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.service.SimpleStorageService;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RBulkString;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

@Slf4j
@RequiredArgsConstructor
public class SetCommandHandler implements CommandHandler {
    private final StorageService storageService;

    @Override
    public boolean canHandle(String command) {
        return "SET".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        String key = args.get(1);
        String value = args.get(2);

        if (args.size() > 3) {
            int expireMs = Integer.parseInt(args.get(4));
            storageService.put(key, value, expireMs);
            log.info("Set element {}:{} with expiration in {} ms", key, value, expireMs);
        } else {
            storageService.put(key, value);
            log.info("Set element {}:{}", key, value);
        }

        print(out, new RBulkString("OK").getBytes());
    }

}
