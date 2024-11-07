package ru.vladikshk.myRedis.service;

public interface WatchdogService {
    void addForWatch(String key, long millisToExpire);
}
