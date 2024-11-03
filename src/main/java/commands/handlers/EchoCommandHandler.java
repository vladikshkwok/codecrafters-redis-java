package commands.handlers;

import commands.typeResolvers.types.RString;

import java.io.PrintWriter;
import java.util.List;

public class EchoCommandHandler implements CommandHandler {
    private static final String ECHO = "ECHO";

    @Override
    public boolean canHandle(String command) {
        return ECHO.equals(command);
    }

    @Override
    public void handle(List<String> command, PrintWriter out) {
        out.print(new RString(command.get(1)));
    }
}
