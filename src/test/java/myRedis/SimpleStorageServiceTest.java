package myRedis;


import ru.vladikshk.myRedis.service.SimpleStorageService;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SimpleStorageServiceTest {
    private final SimpleStorageService storageService = new SimpleStorageService();

    @Test
    public void TestPutWitExpiration() throws InterruptedException {
        storageService.put("key", "value", 500);
        assertEquals("value", storageService.get("key"));
        TimeUnit.MILLISECONDS.sleep(600);
        assertNull(storageService.get("key"));
    }
}