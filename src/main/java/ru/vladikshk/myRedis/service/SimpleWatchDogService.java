package ru.vladikshk.myRedis.service;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SimpleWatchDogService implements WatchdogService {
    private final StorageService storageService;
    private final Queue<QueueExpirationElement> storageExpireQueue;

    public SimpleWatchDogService(StorageService storageService) {
        this.storageService = storageService;
        this.storageExpireQueue = new PriorityQueue<>(Comparator.comparing((QueueExpirationElement::getExpireTime)));
        watchForExpiration();
    }
    
    private void watchForExpiration() {
        new Thread(() -> {
            while (true) {
                Optional.ofNullable(storageExpireQueue.poll())
                    .ifPresent(element -> {
                        log.debug("Checking if element expired: {}", element);
                        if (element.getExpireTime().isBefore(Instant.now())) {
                            log.info("Need to evict key: {}", element.getKey());
                            storageService.remove(element.getKey());
                        } else {
                            log.debug("No need to evict element: {}", element);
                            storageExpireQueue.add(element);
                        }
                    });

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public void addForWatch(String key, int millisToExpire) {
        QueueExpirationElement newElement = new QueueExpirationElement(Instant.now().plusMillis(millisToExpire), key);
        storageExpireQueue.add(newElement);
        log.info("Added new element to watch: {}", newElement);
    }
}
