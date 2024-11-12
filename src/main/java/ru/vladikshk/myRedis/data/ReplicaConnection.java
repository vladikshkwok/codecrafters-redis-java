package ru.vladikshk.myRedis.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.vladikshk.myRedis.server.ServerConnection;

import java.io.OutputStream;

@Getter
@Setter
@RequiredArgsConstructor
public class ReplicaConnection {
    private final ServerConnection serverConnection;
    private final OutputStream out;
    private int bytesSended;
}
