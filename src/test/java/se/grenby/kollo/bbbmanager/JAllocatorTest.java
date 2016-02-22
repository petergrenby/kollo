package se.grenby.kollo.bbbmanager;

import org.junit.Test;

/**
 * Created by peteri on 24/11/15.
 */
public class JAllocatorTest {

    @Test
    public void testAlloction() {
        ByteBlockBufferAllocator ja = new ByteBlockBufferManager(1024);

        int p1 = ja.allocate(100);
    }

    @Test
    public void testAlloctionAndFree() {
        ByteBlockBufferManager ja = new ByteBlockBufferManager(1024);

        int p1 = ja.allocate(100);
        ja.free(p1);
    }

}
