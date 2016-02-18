/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Peter Grenby
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package se.grenby.kollo.allocator;

import se.grenby.kollo.blockbuffer.ByteBlockBuffer;
import se.grenby.kollo.util.BitUtil;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import static se.grenby.kollo.constant.PrimitiveConstants.INT_VALUE_FOR_NULL;

/**
 * Created by peteri on 23/10/15.
 */
public class ByteBlockAllocator implements ByteBlockAllocationReader {

    private final static Logger logger = Logger.getLogger(ByteBlockAllocator.class.getName());

    // The smallest block that is allowed after a split.
    // Some if it will taken for overhead block handling, 8 bytes at time of writing.
    private final static int SMALLEST_BLOCK_SIZE = Integer.BYTES*4;

    private final static byte BYTE_STATUS_FREE = 0;
    private final static byte BYTE_STATUS_OCCUPIED = 127;

    private final static int RELATIVE_POINTER_STATUS = 0;
    private final static int RELATIVE_POINTER_PAYLOAD = RELATIVE_POINTER_STATUS + Byte.BYTES;

    private final static int RELATIVE_POINTER_PREVIOUS = RELATIVE_POINTER_STATUS + Byte.BYTES;
    private final static int RELATIVE_POINTER_NEXT = RELATIVE_POINTER_PREVIOUS + Integer.BYTES;
    private final static int RELATIVE_POINTER_VOID_SPACE = RELATIVE_POINTER_NEXT + Byte.BYTES;

    private final ByteBlockBuffer blockBuffer;
    private final int numberOfBins;
    private final int binBlockPointer;

    public ByteBlockAllocator(final int capacity) {
        blockBuffer = new ByteBlockBuffer(capacity);

        numberOfBins = BitUtil.numberOfBitsNeeded(blockBuffer.getCapacity());
        binBlockPointer = blockBuffer.getFirstBlock();
        int voidPointer = blockBuffer.splitBlock(binBlockPointer, numberOfBins*Integer.BYTES);

        for (int i=0; i<numberOfBins; i++) {
            blockBuffer.putInt(binBlockPointer, i*Integer.BYTES, INT_VALUE_FOR_NULL);
        }
        blockBuffer.putInt(binBlockPointer, (numberOfBins-1)*Integer.BYTES, voidPointer);

        // Set values in empty block (size of block, pointer to previous block, pointer to next block)
        // Remove size value from size of block
        blockBuffer.putByte(voidPointer, RELATIVE_POINTER_STATUS, BYTE_STATUS_FREE);
        blockBuffer.putInt(voidPointer, RELATIVE_POINTER_PREVIOUS, INT_VALUE_FOR_NULL);
        blockBuffer.putInt(voidPointer, RELATIVE_POINTER_NEXT, INT_VALUE_FOR_NULL);

        System.out.println("Total capacity " + blockBuffer.getCapacity());
        System.out.println("Number of bins " + numberOfBins);
        System.out.println("Bin-block pointer " + binBlockPointer);
        System.out.println("Void-block starts " + voidPointer);
    }

    public int allocate(final int sizeOfPayload) {
        // find smallest block that is big enough in the bin
        int blockPointer = findLeastSizedBlockBins(sizeOfPayload + RELATIVE_POINTER_PAYLOAD);
        if (blockPointer != INT_VALUE_FOR_NULL) {
            detachBlockFromList(blockPointer);

            // allocate memory from the block and return excessive memory
            int sizeOfBlock = blockBuffer.getBlockSize(blockPointer);
            if ((sizeOfBlock-sizeOfPayload) > (RELATIVE_POINTER_VOID_SPACE + RELATIVE_POINTER_PAYLOAD + SMALLEST_BLOCK_SIZE)) {
                int excessiveBlockPointer = blockBuffer.splitBlock(blockPointer, sizeOfPayload + RELATIVE_POINTER_PAYLOAD);
                attachBlockToList(excessiveBlockPointer);
            }

            blockBuffer.putByte(blockPointer, RELATIVE_POINTER_STATUS, BYTE_STATUS_OCCUPIED);
            return blockPointer;
        } else {
            return INT_VALUE_FOR_NULL;
        }
    }

    public int allocateAndClear(final int sizeOfPayload) {
        int pointer = allocate(sizeOfPayload);
        if (pointer != INT_VALUE_FOR_NULL) {
            resetBlock(pointer);
        }
        return pointer;
    }

    public int allocateAndClone(final ByteBuffer buffer) {
        int pointer = allocate(buffer.limit());
        if (pointer != INT_VALUE_FOR_NULL) {
            putByteBuffer(pointer, 0, buffer);
        }
        return pointer;
    }

    public boolean free(int blockPointer) {
        if (blockPointer == INT_VALUE_FOR_NULL) {
            return false;
        }

        byte status = blockBuffer.getByte(blockPointer, RELATIVE_POINTER_STATUS);
        if (status == BYTE_STATUS_OCCUPIED) {
            int blockPointerPrevious = blockBuffer.previousBlock(blockPointer);
            if (blockPointerPrevious != binBlockPointer) {
                byte statusPrevious = blockBuffer.getByte(blockPointerPrevious, RELATIVE_POINTER_STATUS);
                if (statusPrevious == BYTE_STATUS_FREE) {
                    detachBlockFromList(blockPointerPrevious);
                    blockBuffer.mergeBlocks(blockPointerPrevious, blockPointer);
                    blockPointer = blockPointerPrevious;
                }
            }

            int blockPointerNext = blockBuffer.nextBlock(blockPointer);
            if (blockPointerNext != INT_VALUE_FOR_NULL) {
                byte statusNext = blockBuffer.getByte(blockPointerNext, RELATIVE_POINTER_STATUS);
                if (statusNext == BYTE_STATUS_FREE) {
                    detachBlockFromList(blockPointerNext);
                    blockBuffer.mergeBlocks(blockPointer, blockPointerNext);
                }
            }

            attachBlockToList(blockPointer);
            blockBuffer.putByte(blockPointer, RELATIVE_POINTER_STATUS, BYTE_STATUS_FREE);

            return true;
        } else {
            return false;
        }
    }

    private void attachBlockToList(int attachPointer) {
        int attachSize = blockBuffer.getBlockSize(attachPointer);
        int binIndex = BitUtil.numberOfBitsNeeded(attachSize) - 1;
        int blockPointer = blockBuffer.getInt(binBlockPointer, binIndex*Integer.BYTES);

        int pointerPreviousBlock = INT_VALUE_FOR_NULL;
        int pointerNextBlock = INT_VALUE_FOR_NULL;

        if (blockPointer == INT_VALUE_FOR_NULL) {
            // Attach block to array of buckets
            blockBuffer.putInt(binBlockPointer, binIndex*Integer.BYTES, attachPointer);
        } else {
            // Find place in block list
            while (blockBuffer.getBlockSize(blockPointer) < attachSize &&
                    blockPointer != INT_VALUE_FOR_NULL) {
                blockPointer = blockBuffer.getInt(blockPointer, RELATIVE_POINTER_NEXT);
            }

            if (blockPointer == INT_VALUE_FOR_NULL) {
                // Add block last in block list
                pointerPreviousBlock = blockBuffer.getInt(blockPointer, RELATIVE_POINTER_PREVIOUS);
                // Attach last block to empty block
                blockBuffer.putInt(pointerPreviousBlock, RELATIVE_POINTER_NEXT, attachPointer);
            } else {
                // Attach block between blocks in list
                pointerPreviousBlock = blockBuffer.getInt(blockPointer, RELATIVE_POINTER_PREVIOUS);
                pointerNextBlock = blockBuffer.getInt(blockPointer, RELATIVE_POINTER_NEXT);

                // Attach prvious and next block to empty block
                blockBuffer.putInt(pointerPreviousBlock, RELATIVE_POINTER_NEXT, attachPointer);
                blockBuffer.putInt(pointerNextBlock, RELATIVE_POINTER_PREVIOUS, attachPointer);
            }
        }

        // Set correct size and pointers into empty block
        blockBuffer.putInt(attachPointer, RELATIVE_POINTER_PREVIOUS, pointerPreviousBlock);
        blockBuffer.putInt(attachPointer, RELATIVE_POINTER_NEXT, pointerNextBlock);
    }

    private void detachBlockFromList(int detachPointer) {
        // find index of smallest possible bin
        int detachSize = blockBuffer.getBlockSize(detachPointer);
        int binIndex = BitUtil.numberOfBitsNeeded(detachSize + Integer.BYTES) - 1;
        int pointerPreviousBlock = blockBuffer.getInt(detachPointer, RELATIVE_POINTER_PREVIOUS);
        int pointerNextBlock = blockBuffer.getInt(detachPointer, RELATIVE_POINTER_NEXT);

        // detach block from linked list
        if (pointerPreviousBlock == INT_VALUE_FOR_NULL) {
            blockBuffer.putInt(binBlockPointer, binIndex*Integer.BYTES, pointerNextBlock);
        } else {
            blockBuffer.putInt(pointerPreviousBlock, RELATIVE_POINTER_NEXT, pointerNextBlock);
        }
        if (pointerNextBlock != INT_VALUE_FOR_NULL) {
            blockBuffer.putInt(pointerNextBlock, RELATIVE_POINTER_PREVIOUS, pointerPreviousBlock);
        }
    }

    private int findLeastSizedBlockBins(int requestedSize) {
        // find index of smallest possible bin
        int binIndex = BitUtil.numberOfBitsNeeded(requestedSize) - 1;

        // find smallest block that is big enough in the bin
        int allocationPointer = INT_VALUE_FOR_NULL;
        while (binIndex < numberOfBins && allocationPointer == INT_VALUE_FOR_NULL) {
            allocationPointer = findLeastSizedBlockList(blockBuffer.getInt(binBlockPointer, binIndex*Integer.BYTES), requestedSize);
            binIndex++;
        }
        return allocationPointer;
    }

    private int findLeastSizedBlockList(int pointer, int requestedSize) {
        boolean foundBlock = false;
        while (!foundBlock && pointer != INT_VALUE_FOR_NULL) {

            int sizeOfBlock = blockBuffer.getBlockSize(pointer);
            if (sizeOfBlock >= requestedSize ) {
                foundBlock = true;
            } else {
                pointer = blockBuffer.getInt(pointer, RELATIVE_POINTER_NEXT);
            }

        }

        return pointer;
    }

    public String memStructureToString() {
        StringBuilder sb = new StringBuilder();

        int bi = 0;
        while (bi < numberOfBins) {
            sb.append("bin[" + bi + "] : ");
            int p = blockBuffer.getInt(binBlockPointer, bi*Integer.BYTES);
            while (p != INT_VALUE_FOR_NULL) {
                sb.append("{p=").append(p).append(" as=").append(blockBuffer.getBlockSize(p) - RELATIVE_POINTER_PAYLOAD).append(" bs=").append(blockBuffer.getBlockSize(p)).append("}");
                p = blockBuffer.getInt(p, RELATIVE_POINTER_NEXT);

            }
            sb.append("\n");
            bi++;
        }

        sb.append(blockBuffer.blockStructureToString());

        return sb.toString();
    }

    public boolean verfiyIntegrity() {
        return blockBuffer.verfiyIntegrity();
    }

    private void resetBlock(int blockPointer) {
        int size = blockBuffer.getBlockSize(blockPointer) - RELATIVE_POINTER_PAYLOAD;
        for (int p = 0; p < size; p++) {
            blockBuffer.putByte(blockPointer, RELATIVE_POINTER_PAYLOAD + p, (byte) 0);
        }
    }

    public void putByteBuffer(int blockPointer, int position, ByteBuffer src) {
        src.position(0);
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, src.remaining());
        blockBuffer.putBuffer(blockPointer, blockPosition, src);
    }

    @Override
    public byte[] getBytes(int blockPointer, int position, int length) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, length);
        return blockBuffer.getBytes(blockPointer, blockPosition, length);
    }

    public void putByte(int blockPointer, int position, byte value) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Byte.BYTES);
        blockBuffer.putByte(blockPointer, blockPosition, value);
    }

    @Override
    public byte getByte(int blockPointer, int position) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Byte.BYTES);
        return blockBuffer.getByte(blockPointer, blockPosition);
    }

    public void putShort(int blockPointer, int position, short value) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Short.BYTES);
        blockBuffer.putShort(blockPointer, blockPosition, value);
    }

    @Override
    public short getShort(int blockPointer, int position) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Short.BYTES);
        return blockBuffer.getShort(blockPointer, blockPosition);
    }

    public void putInt(int blockPointer, int position, int value) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Integer.BYTES);
        blockBuffer.putInt(blockPointer, blockPosition, value);
    }

    @Override
    public int getInt(int blockPointer, int position) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Integer.BYTES);
        return blockBuffer.getInt(blockPointer, blockPosition);
    }

    public void putLong(int blockPointer, int position, long value) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Long.BYTES);
        blockBuffer.putLong(blockPointer, blockPosition, value);
    }


    @Override
    public long getLong(int blockPointer, int position) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Long.BYTES);
        return blockBuffer.getLong(blockPointer, blockPosition);
    }


    @Override
    public float getFloat(int blockPointer, int position) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Float.BYTES);
        return blockBuffer.getFloat(blockPointer, blockPosition);
    }

    public void putFloat(int blockPointer, int position, float value) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Float.BYTES);
        blockBuffer.putFloat(blockPointer, blockPosition, value);
    }


    @Override
    public double getDouble(int blockPointer, int position) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Double.BYTES);
        return blockBuffer.getDouble(blockPointer, blockPosition);
    }

    public void putDouble(int blockPointer, int position, double value) {
        int blockPosition = position + RELATIVE_POINTER_PAYLOAD;
        checkBoundsOfBlock(blockPointer, blockPosition, Double.BYTES);
        blockBuffer.putDouble(blockPointer, blockPosition, value);
    }

    private void checkBoundsOfBlock(int blockPointer, int position, int numberOfBytes) {
        if (position < RELATIVE_POINTER_PAYLOAD ||
                position + numberOfBytes > blockBuffer.getBlockSize(blockPointer)) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int getAllocatedSize(int blockPointer) {
        return blockBuffer.getBlockSize(blockPointer) - RELATIVE_POINTER_PAYLOAD;
    }

}
