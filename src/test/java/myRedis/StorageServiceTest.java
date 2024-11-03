package myRedis;


import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class StorageServiceTest {
    private final StorageService storageService = new StorageService();

    @Test
    public void TestPutWitExpiration() throws InterruptedException {
        storageService.put("key", "value", 500);
        assertEquals("value", storageService.get("key"));
        TimeUnit.MILLISECONDS.sleep(600);
        assertNull(storageService.get("key"));
    }
}