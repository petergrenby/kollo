package se.grenby.kollo.bbbmanager;

import org.junit.Test;

/**
 * Created by peteri on 24/11/15.
 */
public class JAllocatorTest {

    @Test
    public void testAlloction() {
        BBBMemoryAllocator ja = new BBBMemoryManager(1024);

        int p1 = ja.allocate(100);
    }

    @Test
    public void testAlloctionAndFree() {
        BBBMemoryManager ja = new BBBMemoryManager(1024);

        int p1 = ja.allocate(100);
        ja.free(p1);
    }

}
