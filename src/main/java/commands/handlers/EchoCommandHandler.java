package commands.handlers;

import commands.typeResolvers.types.RBulkString;
import commands.typeResolvers.types.RType;

import java.io.OutputStream;
import java.util.List;

import static commands.handlers.CommandHandler.print;

public class EchoCommandHandler implements CommandHandler {
    private static final String ECHO = "ECHO";

    @Override
    public boolean canHandle(String command) {
        return ECHO.equals(command);
    }

    @Override
    public void handle(List<String> command, OutputStream out) {
        RType echoArgs = new RBulkString(command.get(1));
        print(out, echoArgs.getBytes());
    }

}
