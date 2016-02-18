package se.grenby.kollo.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by peteri on 24/10/15.
 */
public class BitUtilTest {
    @Test
    public void testLeastNumberOfBits() {
        assertEquals(1, BitUtil.numberOfBitsNeeded(1));

        assertEquals(1, BitUtil.numberOfBitsNeeded(1));
        System.out.println(Integer.numberOfLeadingZeros(1));
        System.out.println(Integer.highestOneBit(15));

        assertEquals(2, BitUtil.numberOfBitsNeeded(2));
        assertEquals(2, BitUtil.numberOfBitsNeeded(3));

        assertEquals(4, BitUtil.numberOfBitsNeeded(15));

        assertEquals(7, BitUtil.numberOfBitsNeeded(120));

        assertEquals(9, BitUtil.numberOfBitsNeeded(500));

        assertEquals(16, BitUtil.numberOfBitsNeeded(((int) Math.pow(2, 16)) - 1));
    }
}
