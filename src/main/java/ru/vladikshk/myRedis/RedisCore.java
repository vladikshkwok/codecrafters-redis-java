package ru.vladikshk.myRedis;

import lombok.extern.slf4j.Slf4j;
import ru.vladikshk.myRedis.commands.GeneralCommandHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RedisCore {
    static List<Socket> clientsSockets = new ArrayList<>();

    public static void startRedis() {
        int port = 6379;
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            serverSocket.setReuseAddress(true);
            log.info("Binded on localhost:{}", port);

            while (true) {
                waitForClients(serverSocket);
            }
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage(), e);
        } finally {
            closeClientsSockets();
        }
    }

    private static void waitForClients(ServerSocket serverSocket) throws IOException {
        log.info("waiting for client connection...");
        Socket clientSocket = serverSocket.accept();
        clientsSockets.add(clientSocket);
        log.info("client connected: {}", clientSocket.getInetAddress());
        new Thread(new GeneralCommandHandler(clientSocket)).start();
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
}
