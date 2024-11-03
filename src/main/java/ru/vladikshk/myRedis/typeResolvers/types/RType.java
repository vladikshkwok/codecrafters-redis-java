package ru.vladikshk.myRedis.typeResolvers.types;

public abstract class RType {
    public byte[] getBytes() {
        return toString().getBytes();
    }
}
