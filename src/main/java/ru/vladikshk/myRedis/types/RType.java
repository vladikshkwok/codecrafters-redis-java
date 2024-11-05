package ru.vladikshk.myRedis.types;

public abstract class RType {
    public byte[] getBytes() {
        return toString().getBytes();
    }
}
