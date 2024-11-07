package ru.vladikshk.myRedis.data;

import lombok.Getter;

import java.time.Instant;

@Getter
public class QueueExpirationElement {
    private final Instant expireTime;
    private final String key;

    public QueueExpirationElement(Instant expireTime, String key) {
        this.expireTime = expireTime;
        this.key = key;
    }

    @Override
    public String toString() {
        return "QueueElement{" +
            "expireTime=" + expireTime +
            ", key='" + key + '\'' +
            '}';
    }
}
