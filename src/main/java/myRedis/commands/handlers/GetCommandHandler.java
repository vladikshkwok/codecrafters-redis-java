package myRedis.commands.handlers;

import myRedis.StorageService;
import myRedis.typeResolvers.types.RBulkString;

import java.io.OutputStream;
import java.util.List;

import static myRedis.commands.handlers.CommandHandler.print;

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
    }

}
