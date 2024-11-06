package ru.vladikshk.myRedis.service;

import java.util.List;

public interface StorageService {
    void put(String key, String value);
    void put(String key, String value, Long expireMs);
    void remove(String key);
    String get(String key);
    List<String> keys();
}
