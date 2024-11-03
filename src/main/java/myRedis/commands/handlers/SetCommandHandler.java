package myRedis.commands.handlers;

import myRedis.StorageService;
import myRedis.typeResolvers.types.RBulkString;

import java.io.OutputStream;
import java.util.List;

import static myRedis.commands.handlers.CommandHandler.print;

public class SetCommandHandler implements CommandHandler {
    private final StorageService storageService;

    public SetCommandHandler(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public boolean canHandle(String command) {
        return "SET".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        String key = args.get(1);
        String value = args.get(2);

        if (args.size() > 3) {
            int expireMs = Integer.parseInt(args.get(3));
            storageService.put(key, value, expireMs);
        } else {
            storageService.put(key, value);
        }

        print(out, new RBulkString("OK").getBytes());

    }

}
