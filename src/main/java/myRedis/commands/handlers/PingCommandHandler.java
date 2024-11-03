package myRedis.commands.handlers;

import myRedis.typeResolvers.types.RString;
import myRedis.typeResolvers.types.RType;

import java.io.OutputStream;
import java.util.List;

import static myRedis.commands.handlers.CommandHandler.print;

public class PingCommandHandler implements CommandHandler {
    @Override
    public boolean canHandle(String command) {
        return "PING".equals(command);
    }

    @Override
    public void handle(List<String> args, OutputStream out) {
        RType pong = new RString("PONG");
        print(out, pong.getBytes());
    }
}
