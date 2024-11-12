package ru.vladikshk.myRedis.server.handlers.subhandlers.replconfig;

import ru.vladikshk.myRedis.data.HandlerType;
import ru.vladikshk.myRedis.server.handlers.CommandHandler;

import static ru.vladikshk.myRedis.data.HandlerType.REPL;

public interface ReplConfSubhandler extends CommandHandler {
    @Override
    default HandlerType getHandlerType() {
        return REPL;
    }
}
