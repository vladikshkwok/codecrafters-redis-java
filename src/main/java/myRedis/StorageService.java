package myRedis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageService {
    private final Map<String, String> storageMap;

    public StorageService() {
        this.storageMap = new ConcurrentHashMap<>();
    }

    public StorageService(Map<String, String> storageMap) {
        this.storageMap = storageMap;
    }

    public void put(String key, String value) {
        storageMap.put(key, value);
    }
    public String get(String key) {
        return storageMap.get(key);
    }
}
