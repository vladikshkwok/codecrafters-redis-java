package ru.vladikshk.myRedis.service;

import java.util.Queue;

public interface WatchdogService {
    void addForWatch(String key, int millisToExpire);
}
