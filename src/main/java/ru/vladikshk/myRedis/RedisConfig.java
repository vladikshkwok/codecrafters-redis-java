package ru.vladikshk.myRedis;

import lombok.Getter;
import lombok.Setter;
import ru.vladikshk.myRedis.data.RedisRole;
import ru.vladikshk.myRedis.service.SimpleStorageService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.vladikshk.myRedis.data.RedisRole.MASTER;
import static ru.vladikshk.myRedis.data.RedisRole.SLAVE;

@Getter
@Setter
public class RedisConfig {
    private static int DEFAULT_PORT = 6379;

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

    public Integer getPort() {
        return Optional.ofNullable(configMap.get("port"))
            .map(Integer::parseInt)
            .orElse(DEFAULT_PORT);
    }

    public RedisRole getRole() {
        return getReplicaOf()
            .map(_ -> SLAVE)
            .orElse(MASTER);
    }

    public Optional<String> getReplicaOf() {
        return getParam("replicaof");
    }

    public String getReplicationInfo() {
        StringBuilder sb = new StringBuilder();
        RedisRole role = getRole();
        sb.append("role:").append(role).append("\r\n");
        switch (role) {
            case MASTER -> {
                sb.append("master_replid:8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb").append("\r\n"); // todo change after adding replication service
                sb.append("master_repl_offset:0"); // todo change after adding replication service
            }
            case SLAVE -> {}
        }
        return sb.toString();
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
