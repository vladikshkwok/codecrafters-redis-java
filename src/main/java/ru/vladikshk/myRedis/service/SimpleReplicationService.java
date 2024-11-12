package ru.vladikshk.myRedis.service;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.RedisConfig;
import ru.vladikshk.myRedis.data.ReplicaConnection;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.types.RArray;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SimpleReplicationService implements ReplicationService {
    private static volatile ReplicationService INSTANCE;
    private final RedisConfig redisConfig = RedisConfig.getInstance();
    private final Set<ReplicaConnection> replicas;

    private SimpleReplicationService() {
        replicas = ConcurrentHashMap.newKeySet(); // For thread-safe operations
    }

    public static ReplicationService getInstance() {
        if (INSTANCE == null) {
            synchronized (SimpleReplicationService.class) {
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
            Socket masterConnection = new Socket(host, port);
            if (!masterConnection.isConnected()) {
                log.error("Couldn't connect to master on {}:{}", host, port);
                throw new IllegalStateException("Couldn't connect to master on " + host);
            }

            InputStream in = masterConnection.getInputStream();
            OutputStream out = masterConnection.getOutputStream();

            sendHandShake(out, in);
            return masterConnection;
        } catch (IOException e) {
            throw new RuntimeException("Error connecting to master", e);
        }
    }

    @Override
    public void addReplica(ServerConnection serverConnection) {
        replicas.add(new ReplicaConnection(serverConnection, serverConnection.getOut()));
    }

    @Override
    public void sendCommand(byte[] command) {
        Set<ReplicaConnection> failedReplicas = new HashSet<>();
        replicas.forEach(repl -> {
            try {
                repl.getOut().write(command);
                repl.getOut().flush();
                repl.setBytesSended(repl.getBytesSended() + command.length);
            } catch (IOException e) {
                log.error("Couldn't send command to replica", e);
                failedReplicas.add(repl);
                try {
                    repl.getOut().close();
                } catch (IOException closeEx) {
                    log.error("Couldn't close output stream of failed replica", closeEx);
                }
            }
        });
        replicas.removeAll(failedReplicas); // Remove failed replicas after processing
    }

    public int getAck(ServerConnection serverConnection) {
        return replicas.stream()
            .filter(repl -> repl.getServerConnection().equals(serverConnection))
            .map(ReplicaConnection::getBytesSended)
            .findAny()
            .orElse(0);
    }

    private void sendHandShake(OutputStream out, InputStream in) throws IOException {
        log.info("Sending ping to master redis");
        out.write(new RArray(List.of("PING")).getBytes());
        out.flush();
        log.info("response: {}", readLineFromInputStream(in));

        log.info("Sending replconf to master redis with listening port");
        out.write(new RArray(List.of("REPLCONF", "listening-port", redisConfig.getPort().toString())).getBytes());
        out.flush();
        log.info("response: {}", readLineFromInputStream(in));

        log.info("Sending replconf to master redis with capabilities");
        out.write(new RArray(List.of("REPLCONF", "capa", "sync")).getBytes());
        out.flush();
        log.info("response: {}", readLineFromInputStream(in));

        log.info("Sending psync to master redis");
        out.write(new RArray(List.of("PSYNC", "?", "-1")).getBytes());
        out.flush();
        log.info("response: {}", readLineFromInputStream(in));
        int length = Integer.parseInt(readLineFromInputStream(in).substring(1));
        log.info("db length: {}", length);
        byte[] db = new byte[length];
        in.read(db);
        log.info("readed db from master");
    }

    private String readLineFromInputStream(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1 && b != '\n') {
            if (b != '\r') { // игнорируем '\r', если встречается
                buffer.write(b);
            }
        }
        return buffer.toString("UTF-8");
    }

}
