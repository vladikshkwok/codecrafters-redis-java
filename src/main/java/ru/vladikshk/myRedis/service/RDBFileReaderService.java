package ru.vladikshk.myRedis.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.RedisConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RDBFileReaderService implements FileReaderService {
    private static final int REDIS_HASH_TABLE_SECTION = 0xFB;
    private static final int EXPIRE_TIME_MILLIS = 0xFC;
    private static final int EXPIRE_TIME_SEC = 0xFD;
    private static final int REDIS_EOF = 0xFF;
    private static volatile FileReaderService INSTANCE;
    private final RedisConfig redisConfig = RedisConfig.getInstance();

    public static synchronized FileReaderService getInstance() {
        if (INSTANCE == null) {
            synchronized (SimpleStorageService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RDBFileReaderService();
                }
            }
        }
        return INSTANCE;
    }

    @SneakyThrows
    @Override
    public List<String> readAllKeys() {
        List<String> keys = new ArrayList<>();
        if (redisConfig.getDbFile() == null || !redisConfig.getDbFile().exists()) {
            return keys;
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
                if (b == EXPIRE_TIME_MILLIS) {
                    is.readNBytes(8); // skip expiration timestamp (not needed now)
                } else if (b == EXPIRE_TIME_SEC) {
                    is.readNBytes(4); // skip expiration timestamp (not needed now)
                }

                int keyLength = getLength(is);
                byte[] keyBuff = new byte[keyLength];
                is.read(keyBuff);
                String key = new String(keyBuff);
                log.info("Read key from dbFile: {}", key);
                keys.add(key);
                int valueLength = getLength(is);
                is.readNBytes(valueLength); // skip value (for now)
            }
        }
        return keys;
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
