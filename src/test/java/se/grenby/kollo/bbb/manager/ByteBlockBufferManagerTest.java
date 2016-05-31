package se.grenby.kollo.bbb.manager;

import org.junit.Test;
import se.grenby.kollo.bbb.ByteBlockBufferManager;
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

        int orginalSpace = bbbm.getTotalAvailableSpace();

        final int allocationSize = 100;
        int p1 = bbbm.allocate(allocationSize);
        assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p1);
        assertTrue("Incorrect size of allocated block", allocationSize <= bbbm.allocatedSize(p1));
        assertEquals("Available space to manager is not correct after allocation", orginalSpace - allocationSize - ByteBlockBufferManager.MANAGED_BLOCK_OVERHEAD_IN_BYTES, bbbm.getTotalAvailableSpace());
        assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());
    }

    @Test
    public void testAlloctionAndDeallocation() {
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1024);

        int orginalSpace = bbbm.getTotalAvailableSpace();

        final int allocationSize = 100;
        int p1 = bbbm.allocate(allocationSize);
        assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p1);
        assertTrue("Incorrect size of allocated block", allocationSize <= bbbm.allocatedSize(p1));
        assertEquals("Available space to manager is not correct after allocation", orginalSpace - allocationSize - ByteBlockBufferManager.MANAGED_BLOCK_OVERHEAD_IN_BYTES, bbbm.getTotalAvailableSpace());
        assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());


        boolean res = bbbm.deallocate(p1);
        assertTrue("Deallocation was unsuccessful", res);
        assertEquals("Available space to manager is not correct after allocation", orginalSpace, bbbm.getTotalAvailableSpace());
        assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());
    }

    @Test
    public void testBigAlloction() {
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1100);

        int p1 = bbbm.allocate(1000);
        assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p1);
        assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());
    }

    @Test
    public void testTooBigAlloction() {
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1024);

        int p1 = bbbm.allocate(1024);
        assertEquals("Allocation pointer was returned even though allocation should not have been possible", PrimitiveConstants.INT_VALUE_FOR_NULL, p1);
        assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());
    }

    @Test
    public void testMassiveAllocationAndDeallocation() {
        List<Integer> pointers = new ArrayList<>(2000);
        ByteBlockBufferManager bbbm = new ByteBlockBufferManager(1024*1024*1024);

        assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());

        final int allocationSize = 1024;
        for (int i=0; i<2000; i++) {
            int p = bbbm.allocate(allocationSize);
            assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p);
            int actualSize = bbbm.allocatedSize(p);
            assertTrue("Incorrect size of allocated block", allocationSize <= actualSize);
            assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());
            pointers.add(p);
        }

        int c = 0;
        for (Iterator<Integer> it = pointers.iterator(); it.hasNext() & (c < 1000); c++) {
            int p = it.next();
            boolean r = bbbm.deallocate(p);
            assertTrue("Deallocation was unsuccessful", r);
            assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());
            it.remove();
        }


        for (int i=0; i<1000; i++) {
            int p = bbbm.allocate(1024);
            assertNotEquals("No block allocation pointer was returned", PrimitiveConstants.INT_VALUE_FOR_NULL, p);
            int actualSize = bbbm.allocatedSize(p);
            assertTrue("Incorrect size of allocated block", allocationSize <= actualSize);
            assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());
            pointers.add(p);
        }

        for (int p : pointers) {
            boolean r = bbbm.deallocate(p);
            assertTrue("Deallocation was unsuccessful", r);
            assertTrue("Integrity of memory management and/or byte block blockbuffer has been compromised", bbbm.verfiyIntegrity());
        }
    }

}
