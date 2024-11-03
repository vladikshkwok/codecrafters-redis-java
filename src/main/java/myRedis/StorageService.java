package myRedis;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class StorageService {
    private final Map<String, String> storageMap;
    private final Queue<QueueElement> storageExpireQueue;

    public StorageService() {
        this.storageMap = new ConcurrentHashMap<>();
        this.storageExpireQueue = new PriorityQueue<>(Comparator.comparing((QueueElement::getExpireTime)));
        storageExpireWatchdog();
    }

    public void put(String key, String value) {
        storageMap.put(key, value);
    }

    public void put(String key, String value, int expireMs) {
        storageMap.put(key, value);
        storageExpireQueue.add(new QueueElement(Instant.now().plusMillis(expireMs), key));
    }

    public String get(String key) {
        return storageMap.get(key);
    }

    public void remove(String key) {
        storageMap.remove(key);
    }

    private void storageExpireWatchdog() {
        new Thread(this::watchForExpiration).start();
    }

    private void watchForExpiration() {
        while (true) {
            Optional.ofNullable(storageExpireQueue.poll())
                .ifPresent(element -> {
                    System.out.println("Checking if element expired: " + element);
                    if (element.getExpireTime().isBefore(Instant.now())) {
                        System.out.println("Need to evict key: " + element.getKey());
                        remove(element.getKey());
                    } else {
                        storageExpireQueue.add(element);
                    }
                });

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class QueueElement {
        private final Instant expireTime;
        private final String key;

        public QueueElement(Instant expireTime, String key) {
            this.expireTime = expireTime;
            this.key = key;
        }

        public Instant getExpireTime() {
            return expireTime;
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "QueueElement{" +
                "expireTime=" + expireTime +
                ", key='" + key + '\'' +
                '}';
        }
    }
}
