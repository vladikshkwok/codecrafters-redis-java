package ru.vladikshk.myRedis.service;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.data.ReplicaConnection;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.types.RArray;

import java.io.*;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class SimpleReplicationService implements ReplicationService {
    private static volatile ReplicationService INSTANCE;
    private final RedisConfig redisConfig = RedisConfig.getInstance();
    private Set<ReplicaConnection> replicas;


    private SimpleReplicationService() {
        replicas = new HashSet<>();
    }

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
    public Socket connect(String host, int port) {
        try {
            var masterConnection = new Socket(host, port);
            if (!masterConnection.isConnected()) {
                log.error("Couldn't connect to master on {}:{}", host, port);
                throw new IllegalStateException("Couldn't connect to master on " + host);
            }

            var in = masterConnection.getInputStream();
            var out = new BufferedOutputStream(masterConnection.getOutputStream());

            sendHandShake(out, in);
            return masterConnection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addReplica(ServerConnection serverConnection) {
        replicas.add(new ReplicaConnection(serverConnection.getOut()));
    }

    @Override
    public void sendCommand(byte[] command) {
        replicas.forEach(repl -> {
            try {
                repl.out().flush();
                repl.out().write(command);
                repl.out().flush();
            } catch (IOException e) {
                log.error("Couldn't send command", e);
            } finally {
                try {
                    repl.out().close();
                } catch (IOException e) {
                    log.error("Couldn't close out", e);
                }
                replicas.remove(repl);
            }
        });
    }

    private void sendHandShake(OutputStream out, InputStream in) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(in));
        log.info("Sending ping to master redis");
        out.write(new RArray(List.of("PING")).getBytes());
        out.flush();
        log.info("response: {}", reader.readLine());
        log.info("Sending replconf to master redis with listening port");
        out.write(new RArray(List.of("REPLCONF", "listening-port", redisConfig.getPort().toString())).getBytes());
        out.flush();
        log.info("response: {}", reader.readLine());
        log.info("Sending replconf to master redis with capabilities");
        out.write(new RArray(List.of("REPLCONF", "capa", "sync")).getBytes());
        out.flush();
        log.info("response: {}", reader.readLine());
        log.info("Sending psync to master redis");
        out.write(new RArray(List.of("PSYNC", "?", "-1")).getBytes());
        out.flush();
        log.info("response: {}", reader.readLine());
        int length = Integer.parseInt(reader.readLine().substring(1));
        log.info("db length: {}", length);
//        byte[] db = new byte[length - 1];
        in.skip(length);
        log.info("skipped db from master: {}", new String(db));
    }

}
