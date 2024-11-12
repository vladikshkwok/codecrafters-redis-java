package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RArray;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class KeysCommandHandler implements CommandHandler {
    private final StorageService readerService;

    @Override
    public boolean canHandle(String command) {
        return "KEYS".equals(command);
    }

    @SneakyThrows
    @Override
    public void handle(List<String> args, ServerConnection serverConnection) {
        String keyPattern = args.get(1);
        List<String> keys = readerService.keys();

        print(serverConnection, new RArray(keys).getBytes());
        log.info("Got keys from rdb file: {}", keys);
    }

}
