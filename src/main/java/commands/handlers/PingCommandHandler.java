package commands.handlers;

import commands.typeResolvers.types.RBulkString;
import commands.typeResolvers.types.RString;
import commands.typeResolvers.types.RType;

import java.io.OutputStream;
import java.util.List;

import static commands.handlers.CommandHandler.print;

public class PingCommandHandler implements CommandHandler {
    private static final String PING = "PING";

    @Override
    public boolean canHandle(String command) {
        return PING.equals(command);
    }

    @Override
    public void handle(List<String> command, OutputStream out) {
        RType pong = new RString("PONG");
        print(out, pong.getBytes());
    }
}
