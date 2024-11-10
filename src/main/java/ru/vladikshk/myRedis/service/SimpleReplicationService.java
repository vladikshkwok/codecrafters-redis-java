package ru.vladikshk.myRedis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.types.RArray;

import java.io.*;
import java.net.Socket;
import java.util.List;

@Slf4j
public class SimpleReplicationService implements ReplicationService {
    private static volatile ReplicationService INSTANCE;
    private final RedisConfig redisConfig = RedisConfig.getInstance();

    private BufferedReader in;
    private OutputStream out;

    public static synchronized ReplicationService getInstance() {
        if (INSTANCE == null) {
            synchronized (SimpleStorageService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SimpleReplicationService();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void connect(String host, int port) {
        try (var masterConnection = new Socket(host, port);) {
            if (!masterConnection.isConnected()) {
                log.error("Couldn't connect to master on {}:{}", host, port);
                throw new IllegalStateException("Couldn't connect to master on " + host);
            }

            in = new BufferedReader(new InputStreamReader(masterConnection.getInputStream()));
            out = new BufferedOutputStream(masterConnection.getOutputStream());

            sendHandShake();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendHandShake() throws IOException {
        log.info("Sending ping to master redis");
        out.write(new RArray(List.of("PING")).getBytes());
        out.flush();
        in.readLine();
        log.info("Sending replconf to master redis with listening port");
        out.write(new RArray(List.of("REPLCONF", "listening-port", redisConfig.getPort().toString())).getBytes());
        out.flush();
        in.readLine();
        log.info("Sending replconf to master redis with capabilities");
        out.write(new RArray(List.of("REPLCONF", "capa", "sync")).getBytes());
        out.flush();
        in.readLine();
    }
}
