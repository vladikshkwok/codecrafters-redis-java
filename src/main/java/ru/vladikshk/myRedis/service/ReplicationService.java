package ru.vladikshk.myRedis.service;

import java.io.OutputStream;
import java.net.Socket;

public interface ReplicationService {
    // todo refactor
    Socket connect(String host, int port);
    void addReplica(OutputStream out);
    void sendCommand(byte[] command);
}
