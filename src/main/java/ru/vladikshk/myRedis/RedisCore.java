package ru.vladikshk.myRedis;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.server.ServerConnection;
import ru.vladikshk.myRedis.service.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.vladikshk.myRedis.data.RedisRole.MASTER;

@Slf4j
public class RedisCore {
    private static final RedisConfig redisConfig = RedisConfig.getInstance();
    private static final ReplicationService replicationService = SimpleReplicationService.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static StorageService storageService;
    private static final List<Socket> clientsSockets = new ArrayList<>();

    public static void startRedis(String[] args) {
        handleArgs(args);
        setDbFile();
        setStorageService();

        connectIfReplica(); // todo refactor

        try (ServerSocket serverSocket = new ServerSocket(redisConfig.getPort());) {
            serverSocket.setReuseAddress(true);
            log.info("Binded on localhost:{}", serverSocket.getLocalPort());

            while (true) {
                log.info("waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                clientsSockets.add(clientSocket);
                log.info("client connected: {}", clientSocket.getInetAddress());

                EXECUTOR.submit(
                    new ServerConnection(storageService, redisConfig, replicationService, clientSocket, false)
                );
            }
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage(), e);
        } finally {
            closeClientsSockets();
        }
    }

    private static void connectIfReplica() {
        if (MASTER.equals(redisConfig.getRole())) {
            return;
        }

        String[] connectionInfo = redisConfig.getReplicaOf()
            .map(s -> s.split(" "))
            .orElseThrow(IllegalStateException::new);

        try (Socket connection = replicationService.connect(connectionInfo[0], Integer.parseInt(connectionInfo[1]))) {
            EXECUTOR.submit(
                new ServerConnection(storageService, redisConfig, replicationService, connection, true)
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            System.exit(0);
        }
    }

    private static void setStorageService() {
        storageService = dbFileExists() ? RDBFileStorageService.getInstance() : SimpleStorageService.getInstance();
        log.info("Using {} as storage service", storageService.getClass().getSimpleName());
    }

    private static Boolean dbFileExists() {
        return Optional.ofNullable(redisConfig.getDbFile()).map(File::exists).orElse(false);
    }

    private static void closeClientsSockets() {
        for (var clientSocket : clientsSockets) {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                log.error("IOException: {}", e.getMessage(), e);
            }
        }
    }

    private static void handleArgs(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].startsWith("--")) {
                redisConfig.addParam(args[i].substring(2), args[i + 1]);

                log.info("Saved {}={} into config", args[i], args[i + 1]);
            }
        }
    }

    private static void setDbFile() {
        var dir = redisConfig.getParam("dir");
        var dbFileName = redisConfig.getParam("dbfilename");
        if (dir.isPresent() && dbFileName.isPresent()) {
            redisConfig.setDbFile(Path.of(dir.get()).resolve(dbFileName.get()).toFile());
        }
    }
}
