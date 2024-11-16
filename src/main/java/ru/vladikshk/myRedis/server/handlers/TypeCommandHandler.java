package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RBulkString;
import ru.vladikshk.myRedis.types.RString;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class TypeCommandHandler implements CommandHandler {
    private final StorageService storageService;

    @Override
    public boolean canHandle(String command) {
        return "type".equalsIgnoreCase(command);
    }

    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        String key = args.get(1);

        String value = storageService.get(key);
        print(serverConnection, new RString(nonNull(value) ? "string" : "none").getBytes());
        log.info("Got element={}:{} from storage", key, value);
    }
}
