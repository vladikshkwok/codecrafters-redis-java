package ru.vladikshk.myRedis.commands.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.service.StorageService;
import ru.vladikshk.myRedis.typeResolvers.types.RBulkString;

import java.io.OutputStream;
import java.util.List;

import static ru.vladikshk.myRedis.commands.handlers.CommandHandler.print;

@Slf4j
public class GetCommandHandler implements CommandHandler {
    private final StorageService storageService;

    public GetCommandHandler(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public boolean canHandle(String command) {
        return "GET".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        String key = args.get(1);

        String value = storageService.get(key);
        print(out, new RBulkString(value).getBytes());
        log.info("Got element={}:{} from storage", key, value);
    }

}