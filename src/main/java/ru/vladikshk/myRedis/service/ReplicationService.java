package ru.vladikshk.myRedis.service;

import ru.vladikshk.myRedis.server.ServerConnection;

import java.io.OutputStream;
import java.net.Socket;

public interface ReplicationService {
    // todo refactor
    Socket connect(String host, int port);
    void addReplica(ServerConnection serverConnection);
    void sendCommand(byte[] command);
    int getAck(ServerConnection connection);
}
