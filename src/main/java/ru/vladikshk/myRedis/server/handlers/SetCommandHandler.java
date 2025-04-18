package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.data.HandlerType;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RBulkString;

import java.util.List;

import static ru.vladikshk.myRedis.data.HandlerType.WRITE;

@Slf4j
@RequiredArgsConstructor
public class SetCommandHandler implements CommandHandler {
    private final StorageService storageService;

    @Override
    public boolean canHandle(String command) {
        return "SET".equals(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        String key = args.get(1);
        String value = args.get(2);

        if (args.size() > 3) {
            Long expireMs = Long.parseLong(args.get(4));
            storageService.put(key, value, expireMs);
            log.info("Set element {}:{} with expiration in {} ms", key, value, expireMs);
        } else {
            storageService.put(key, value);
            log.info("Set element {}:{}", key, value);
        }

        print(serverConnection, new RBulkString("OK").getBytes());
    }

    @Override
    public HandlerType getHandlerType() {
        return WRITE;
    }
}
