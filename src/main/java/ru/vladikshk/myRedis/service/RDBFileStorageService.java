package ru.vladikshk.myRedis.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.RedisConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RDBFileStorageService implements StorageService {
    private static final int REDIS_HASH_TABLE_SECTION = 0xFB;
    private static final int EXPIRE_TIME_MILLIS = 0xFC;
    private static final int EXPIRE_TIME_SEC = 0xFD;
    private static final int REDIS_EOF = 0xFF;

    private static volatile StorageService INSTANCE;
    private final RedisConfig redisConfig = RedisConfig.getInstance();

    private final Map<String, String> storageMap;
    private final WatchdogService watchdogService;

    private RDBFileStorageService() {
        this.storageMap = new ConcurrentHashMap<>();
        this.watchdogService = new SimpleWatchDogService(this);
        readRdbFile();
    }

    public static synchronized StorageService getInstance() {
        if (INSTANCE == null) {
            synchronized (SimpleStorageService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RDBFileStorageService();
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
    public void put(String key, String value, Long expireMs) {
        if (expireMs == null) {
            put(key, value);
            return;
        }
        if (expireMs <= 0) {
            return;
        }
        this.put(key, value);
        watchdogService.addForWatch(key, expireMs);
    }

    @Override
    public void remove(String key) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String get(String key) {
        return storageMap.get(key);
    }

    @SneakyThrows
    @Override
    public List<String> keys() {
        return new ArrayList<>(storageMap.keySet());
    }

    @SneakyThrows
    private void readRdbFile() {
        if (redisConfig.getDbFile() == null || !redisConfig.getDbFile().exists()) {
            return;
        }

        try (InputStream is = new FileInputStream(redisConfig.getDbFile())) {
            int read;
            while ((read = is.read()) != -1) {
                if (read == REDIS_HASH_TABLE_SECTION) {
                    getLength(is); // skip size of hash table
                    getLength(is); // skip size of expiry hash table
                    break;
                }
            }

            int b;
            while ((b = is.read()) != -1 && b != REDIS_EOF) {
                Long expireMs = null;
                if (b == EXPIRE_TIME_MILLIS) {
                    long timeStamp = ByteBuffer.wrap(is.readNBytes(8)).getLong();
                    expireMs = ChronoUnit.MILLIS.between(Instant.now(), Instant.ofEpochMilli(timeStamp));
                    log.info("Read expiration timestamp in millis {}. Diff {}", timeStamp, expireMs);
                    is.read(); // skip 1 byte (type)
                } else if (b == EXPIRE_TIME_SEC) {
                    long timeStamp = ByteBuffer.wrap(is.readNBytes(4)).getInt();
                    expireMs = ChronoUnit.MILLIS.between(Instant.now(), Instant.ofEpochMilli(timeStamp));
                    log.info("Read expiration timestamp in seconds {}. Diff {}", timeStamp, expireMs);
                    is.read(); // skip 1 byte (type)
                }

                int keyLength = getLength(is);
                byte[] keyBuff = is.readNBytes(keyLength);
                String key = new String(keyBuff);
                log.info("Read key from dbFile: {}", key);
                int valueLength = getLength(is);
                byte[] valueBuff = is.readNBytes(valueLength);
                String value = new String(valueBuff);
                this.put(key, value, expireMs);
            }
        }
    }

    @SneakyThrows
    private int getLength(InputStream is) {
        int len = 0;
        int read = is.read();
        int firstTwoBits = read >> 6;

        if (firstTwoBits == 0b00) {
            len = read & 0b00111111;
        } else if (firstTwoBits == 0b01) {
            len = (read & 0b00111111) + is.read();
        } else if (firstTwoBits == 0b10) {
            byte[] buff = new byte[4];
            is.read(buff);
            len = ByteBuffer.wrap(buff).getInt();
        }

        return len;
    }
}
