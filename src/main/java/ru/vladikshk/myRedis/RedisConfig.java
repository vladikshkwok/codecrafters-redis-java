package ru.vladikshk.myRedis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import ru.vladikshk.myRedis.service.SimpleStorageService;
import ru.vladikshk.myRedis.service.StorageService;

import java.io.File;

@Getter
@Setter
public class RedisConfig {
    private static volatile RedisConfig INSTANCE;
    private String dir;
    private String dbFileName;
    private File dbFile;

    private RedisConfig() {}

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
