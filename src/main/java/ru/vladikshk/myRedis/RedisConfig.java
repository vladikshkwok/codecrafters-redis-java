package ru.vladikshk.myRedis;

import lombok.Getter;
import lombok.Setter;
import ru.vladikshk.myRedis.service.SimpleStorageService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class RedisConfig {
    private static volatile RedisConfig INSTANCE;
    private final Map<String, String> configMap;
    private File dbFile;

    private RedisConfig() {
        this.configMap = new HashMap<>();
    }

    public void addParam(String key, String value) {
        configMap.put(key, value);
    }

    public Optional<String> getParam(String key) {
        return Optional.ofNullable(configMap.get(key));
    }

    public Optional<Integer> getPort() {
        return Optional.ofNullable(configMap.get("port"))
            .map(Integer::parseInt);
    }

    public static synchronized RedisConfig getInstance() {
        if (INSTANCE == null) {
            synchronized (SimpleStorageService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RedisConfig();
                }
            }
        }
        return INSTANCE;
    }
}
