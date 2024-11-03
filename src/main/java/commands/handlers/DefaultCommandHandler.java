package commands.handlers;

import commands.typeResolvers.types.RBulkString;
import commands.typeResolvers.types.RType;

import java.io.OutputStream;
import java.util.List;

import static commands.handlers.CommandHandler.print;

public class DefaultCommandHandler implements CommandHandler {

    private static final RType COULDNT_HANDLE_COMMAND = new RBulkString("Couldn't handle command");

    @Override
    public boolean canHandle(String command) {
        return true;
    }

    @Override
    public void handle(List<String> command, OutputStream out) {
        print(out, COULDNT_HANDLE_COMMAND.getBytes());
    }
}
