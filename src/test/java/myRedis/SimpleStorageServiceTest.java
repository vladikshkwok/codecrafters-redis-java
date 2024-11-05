package myRedis;


import ru.vladikshk.myRedis.service.SimpleStorageService;
import org.junit.Test;
import ru.vladikshk.myRedis.service.StorageService;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SimpleStorageServiceTest {
    private final StorageService storageService = SimpleStorageService.getInstance();

    @Test
    public void TestPutWitExpiration() throws InterruptedException {
        storageService.put("key", "value", 500);
        assertEquals("value", storageService.get("key"));
        TimeUnit.MILLISECONDS.sleep(600);
        assertNull(storageService.get("key"));
    }
}