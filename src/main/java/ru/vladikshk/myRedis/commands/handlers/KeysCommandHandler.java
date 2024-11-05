package ru.vladikshk.myRedis.commands.handlers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.service.FileReaderService;
import ru.vladikshk.myRedis.service.RDBFileReaderService;
import ru.vladikshk.myRedis.service.SimpleStorageService;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.types.RArray;
import ru.vladikshk.myRedis.types.RBulkString;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

@Slf4j
public class KeysCommandHandler implements CommandHandler {
    private final FileReaderService readerService = RDBFileReaderService.getInstance();

    @Override
    public boolean canHandle(String command) {
        return "KEYS".equals(command);
    }

    @SneakyThrows
    @Override
    public void handle(List<String> args, OutputStream out) {
        String keyPattern = args.get(1);
        List<String> keys = readerService.readAllKeys();

        print(out, new RArray(keys).getBytes());
    }

}
