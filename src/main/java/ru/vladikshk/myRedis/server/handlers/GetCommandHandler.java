package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RBulkString;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GetCommandHandler implements CommandHandler {
    private final StorageService storageService;

    @Override
    public boolean canHandle(String command) {
        return "GET".equals(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        String key = args.get(1);

        String value = storageService.get(key);
        print(serverConnection, new RBulkString(value).getBytes());
        log.info("Got element={}:{} from storage", key, value);
    }
}
