package ru.vladikshk.myRedis;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.commands.ClientHandler;
import ru.vladikshk.myRedis.service.RDBFileStorageService;
import ru.vladikshk.myRedis.service.SimpleStorageService;
import ru.vladikshk.myRedis.service.StorageService;

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

@Slf4j
public class RedisCore {
    private static final RedisConfig redisConfig = RedisConfig.getInstance();
    private static StorageService storageService;
    private static int DEFAULT_PORT = 6379;
    private static final List<Socket> clientsSockets = new ArrayList<>();

    public static void startRedis(String[] args) {
        handleArgs(args);
        setDbFile();
        setStorageService();

        ExecutorService executor = Executors.newCachedThreadPool();
        System.out.println(redisConfig.getPort().orElse(DEFAULT_PORT));
        try (ServerSocket serverSocket = new ServerSocket(redisConfig.getPort().orElse(DEFAULT_PORT));) {
            serverSocket.setReuseAddress(true);
            log.info("Binded on localhost:{}", serverSocket.getLocalPort());

            while (true) {
                log.info("waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                clientsSockets.add(clientSocket);
                log.info("client connected: {}", clientSocket.getInetAddress());
                executor.submit(new ClientHandler(storageService, redisConfig, clientSocket));
            }
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage(), e);
        } finally {
            closeClientsSockets();
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
