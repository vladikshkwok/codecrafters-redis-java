package ru.vladikshk.myRedis.data;

public enum RedisRole {
    MASTER,
    SLAVE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
