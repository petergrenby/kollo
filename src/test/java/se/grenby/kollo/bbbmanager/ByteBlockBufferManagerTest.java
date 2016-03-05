package se.grenby.kollo.bbbmanager;

import org.junit.Test;
import se.grenby.kollo.constant.PrimitiveConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by peteri on 24/11/15.
 */
public class ByteBlockBufferManagerTest {

    @Test
    public void testAlloction() {
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1024);

        int p1 = bbbm.allocate(100);
        assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p1);
        assertEquals("Incorrect size of allocated block", 100, bbbm.getAllocatedSize(p1));
        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());
    }

    @Test
    public void testAlloctionAndDeallocation() {
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1024);

        int p1 = bbbm.allocate(100);
        assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p1);
        assertEquals("Incorrect size of allocated block", 100, bbbm.getAllocatedSize(p1));
        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());

        boolean res = bbbm.free(p1);
        assertTrue("Deallocation was unsuccessful", res);
        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());
    }

    @Test
    public void testBigAlloction() {
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1100);

        int p1 = bbbm.allocate(1000);
        assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p1);
        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());
    }

    @Test
    public void testTooBigAlloction() {
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1024);

        int p1 = bbbm.allocate(1024);
        assertEquals("Allocation pointer was returned even though allocation should not have been possible", PrimitiveConstants.INT_VALUE_FOR_NULL, p1);
        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());
    }

    @Test
    public void testMassiveAllocationAndDeallocation() {
        List<Integer> pointers = new ArrayList<>(2000);
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1024*1024*1024);

        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());

        for (int i=0; i<2000; i++) {
            int p = bbbm.allocate(1024);
            assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p);
            pointers.add(p);
        }

        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());

        int c = 0;
        for (Iterator<Integer> it = pointers.iterator(); ; it.hasNext()) {
            boolean r = bbbm.free(it.next());
            assertTrue("Deallocation was unsuccessful", r);
            it.remove();
            if (c++ > 1000)
                break;
        }

        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());

        for (int i=0; i<1000; i++) {
            int p = bbbm.allocate(1024);
            assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p);
            pointers.add(p);
        }

        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());

        int i = 1;
        for (int p : pointers) {
            boolean r = bbbm.free(p);
            assertTrue("Deallocation was unsuccessful", r);
            i++;
        }

        assertTrue("Integrity of memory management and/or byte block buffer has been compromised", bbbm.verfiyIntegrity());
    }

}
