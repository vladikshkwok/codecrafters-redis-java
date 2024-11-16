package ru.vladikshk.myRedis.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.vladikshk.myRedis.server.ServerConnection;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

@Getter
@Setter
@RequiredArgsConstructor
public class ReplicaConnection {
    private final ServerConnection serverConnection;
    private final OutputStream out;
    private final BufferedReader in;
    private final BlockingQueue<String> pendingCommands;
    private int bytesSended;
}
