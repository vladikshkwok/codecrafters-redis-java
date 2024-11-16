package ru.vladikshk.myRedis.ex;

public class AwaitingTimeoutException extends RuntimeException {
    public AwaitingTimeoutException(String msg) {
        super(msg);
    }
}
