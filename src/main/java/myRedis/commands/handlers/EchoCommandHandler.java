package myRedis.commands.handlers;

import myRedis.typeResolvers.types.RBulkString;
import myRedis.typeResolvers.types.RType;

import java.io.OutputStream;
import java.util.List;

import static myRedis.commands.handlers.CommandHandler.print;

public class EchoCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return "ECHO".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        RType echoArgs = new RBulkString(args.get(1));
        print(out, echoArgs.getBytes());
    }

}
