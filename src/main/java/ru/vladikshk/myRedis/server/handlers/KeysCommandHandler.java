package ru.vladikshk.myRedis.server.handlers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RArray;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.server.handlers.CommandHandler.print;

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
    public void handle(List<String> args, OutputStream out) {
        String keyPattern = args.get(1);
        List<String> keys = readerService.keys();

        print(out, new RArray(keys).getBytes());
        log.info("Got keys from rdb file: {}", keys);
    }

}
