package ru.vladikshk.myRedis.service;

public interface StorageService {
    void put(String key, String value);
    void put(String key, String value, int expireMs);
    void remove(String key);
    String get(String key);
}
