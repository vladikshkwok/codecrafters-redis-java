package myRedis.commands.handlers;

import myRedis.typeResolvers.types.RBulkString;

import java.io.OutputStream;
import java.util.List;

import static myRedis.commands.handlers.CommandHandler.print;

public class DefaultCommandHandler implements CommandHandler {

    @Override
    public boolean canHandle(String command) {
        return true;
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        print(out, new RBulkString("Couldn't handle command").getBytes());
    }
}
