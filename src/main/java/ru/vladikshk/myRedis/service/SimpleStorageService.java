package ru.vladikshk.myRedis.service;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SimpleStorageService implements StorageService {
    private static volatile StorageService INSTANCE;
    private final Map<String, String> storageMap;
    private final WatchdogService watchdogService;

    private SimpleStorageService() {
        this.storageMap = new ConcurrentHashMap<>();
        this.watchdogService = new SimpleWatchDogService(this);
    }

    public static synchronized StorageService getInstance() {
        if (INSTANCE == null) {
            synchronized (SimpleStorageService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SimpleStorageService();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void put(String key, String value) {
        storageMap.put(key, value);
    }

    @Override
    public void put(String key, String value, int expireMs) {
        storageMap.put(key, value);
        watchdogService.addForWatch(key, expireMs);
    }

    @Override
    public String get(String key) {
        return storageMap.get(key);
    }

    @Override
    public void remove(String key) {
        storageMap.remove(key);
    }

}
