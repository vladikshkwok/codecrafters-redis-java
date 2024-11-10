package ru.vladikshk.myRedis.types;

import java.util.Arrays;

public class RDBFile extends RType {
    private final byte[] fileBinaryData;

    public RDBFile(byte[] fileBinaryData) {
        this.fileBinaryData = fileBinaryData;
    }

    @Override
    public String toString() {
        return "$"+fileBinaryData.length+"\r\n"+fileBinaryData;
    }
}
