package se.grenby.kollo.bbb;

import org.junit.Test;

import static org.junit.Assert.*;
import static se.grenby.kollo.bbb.ByteBlockBuffer.BLOCK_OVERHEAD_IN_BYTES;

/**
 * Created by peteri on 24/11/15.
 */
public class ByteBlockBufferTest {

    @Test
    public void testCreatingBuffer() {
        ByteBlockBuffer b = new ByteBlockBuffer(1000);
        assertEquals("At start there should be one block and nothing but one block", 1, b.countBlocks());
    }

    @Test
    public void testSplitBlock() {
        final int totalSize = 100*2+BLOCK_OVERHEAD_IN_BYTES*2;

        ByteBlockBuffer b = new ByteBlockBuffer(totalSize);
        assertEquals("At start there should be one block and nothing but one block", 1, b.countBlocks());

        int p1 = b.getFirstBlock();
        int p2 = b.splitBlock(p1, totalSize/2-BLOCK_OVERHEAD_IN_BYTES);
        assertEquals("After a block has been split there should be two blocks", 2, b.countBlocks());
        assertEquals("The first split block has incorrect size", totalSize/2-BLOCK_OVERHEAD_IN_BYTES, b.getBlockSize(p1));
        assertEquals("The second split block has incorrect size", totalSize/2-BLOCK_OVERHEAD_IN_BYTES, b.getBlockSize(p2));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());
    }

    @Test
    public void testSplitAndMergeBlock() {
        final int expectedSize = 100;
        final int totalSize = expectedSize*2+BLOCK_OVERHEAD_IN_BYTES*2;

        int numOfBlocks = 1;
        ByteBlockBuffer b = new ByteBlockBuffer(totalSize);
        assertEquals("At start there should be one block and nothing but one block", 1, b.countBlocks());

        int p1 = b.getFirstBlock();
        int p2 = b.splitBlock(p1, expectedSize);
        assertEquals("After a block has been split there should be two blocks", 2, b.countBlocks());
        assertEquals("The first split block has incorrect size", expectedSize, b.getBlockSize(p1));
        assertEquals("The second split block has incorrect size", expectedSize, b.getBlockSize(p2));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());

        assertEquals("Pointer to the merged block should be the same as the first block", p1, b.mergeBlocks(p1, p2));
        assertEquals("After two blocks have been merged there should be one block", 1, b.countBlocks());
        assertEquals("The merged block has incorrect size", expectedSize*2+BLOCK_OVERHEAD_IN_BYTES, b.getBlockSize(p1));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());
    }

    @Test
    public void testMultipleSplitAndMergeBlock() {
        final int expectedSize = 100;
        final int totalSize = expectedSize*4+BLOCK_OVERHEAD_IN_BYTES*4;

        int numOfBlocks = 1;
        ByteBlockBuffer b = new ByteBlockBuffer(totalSize);
        assertEquals("At start there should be one block and nothing but one block", numOfBlocks, b.countBlocks());

        int pA1 = b.getFirstBlock();
        int pB1 = b.splitBlock(pA1, expectedSize*2+BLOCK_OVERHEAD_IN_BYTES);
        numOfBlocks++;
        assertEquals("After a block has been split there should be two blocks", numOfBlocks, b.countBlocks());
        assertEquals("The A split block has incorrect size", expectedSize*2+BLOCK_OVERHEAD_IN_BYTES, b.getBlockSize(pA1));
        assertEquals("The B split block has incorrect size", expectedSize*2+BLOCK_OVERHEAD_IN_BYTES, b.getBlockSize(pB1));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());

        int pA2 = b.splitBlock(pA1, expectedSize);
        numOfBlocks++;
        assertEquals("After a block has been split there should be one block more", numOfBlocks, b.countBlocks());
        assertEquals("First A split block has incorrect size", expectedSize, b.getBlockSize(pA1));
        assertEquals("Second A split block has incorrect size", expectedSize, b.getBlockSize(pA2));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());

        int pB2 = b.splitBlock(pB1, expectedSize);
        numOfBlocks++;
        assertEquals("After a block has been split there should be one block more", numOfBlocks, b.countBlocks());
        assertEquals("First B split block has incorrect size", expectedSize, b.getBlockSize(pB1));
        assertEquals("Second B split block has incorrect size", expectedSize, b.getBlockSize(pB2));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());

        assertEquals("Pointer to the merged block should be the same as the first block", pA2, b.mergeBlocks(pA2, pB1));
        numOfBlocks--;
        assertEquals("After merge there should be one block less", numOfBlocks, b.countBlocks());
        assertEquals("The merged block has incorrect size", expectedSize*2+BLOCK_OVERHEAD_IN_BYTES, b.getBlockSize(pA2));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());

        assertEquals("Pointer to the merged block should be the same as the first block", pA1, b.mergeBlocks(pA1, pA2));
        numOfBlocks--;
        assertEquals("After merge there should be one block less", numOfBlocks, b.countBlocks());
        assertEquals("The merged block has incorrect size", expectedSize*3+BLOCK_OVERHEAD_IN_BYTES*2, b.getBlockSize(pA1));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());

        assertEquals("Pointer to the merged block should be the same as the first block", pA1, b.mergeBlocks(pA1, pB2));
        numOfBlocks--;
        assertEquals("After merge there should be one block less", numOfBlocks, b.countBlocks());
        assertEquals("The merged block has incorrect size", expectedSize*4+BLOCK_OVERHEAD_IN_BYTES*3, b.getBlockSize(pA1));
        assertTrue("Block bbb verification failed", b.verfiyIntegrity());
    }


}
