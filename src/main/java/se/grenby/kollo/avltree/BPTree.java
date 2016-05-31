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
package se.grenby.kollo.avltree;

/**
 * Created by peteri on 17/12/15.
 */
public class BPTree {


//    private static int SIZE_OF_BUCKET_TYPE = Byte.BYTES;
//    private static int SIZE_OF_SLOT_COUNT = Byte.BYTES;
//    private static int SIZE_OF_SLOT_KEY = Integer.BYTES;
//    private static int SIZE_OF_SLOT_VALUE = Integer.BYTES;
//    private static int SIZE_OF_SLOT = SIZE_OF_SLOT_KEY + SIZE_OF_SLOT_VALUE;
//    private static int MAX_NUMBER_OF_SLOTS_IN_BUCKET = 10;
//    private static int SIZE_OF_BUCKET = SIZE_OF_BUCKET_TYPE + SIZE_OF_SLOT_COUNT +
//            MAX_NUMBER_OF_SLOTS_IN_BUCKET *SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE;
//
//    private static int RELATIVE_POINTER_BUCKET_TYPE = 0;
//    private static int RELATIVE_POINTER_SLOT_COUNT = SIZE_OF_BUCKET_TYPE;
//    private static int RELATIVE_POINTER_SLOTS = RELATIVE_POINTER_SLOT_COUNT + SIZE_OF_SLOT_COUNT;
//    private static int RELATIVE_POINTER_NEXT_BUCKET = RELATIVE_POINTER_SLOTS + SIZE_OF_SLOT*MAX_NUMBER_OF_SLOTS_IN_BUCKET;
//
//    private static byte NODE_TYPE_INTERNAL = 5;
//    private static byte NODE_TYPE_LEAF = 10;
//
//    private final ByteBlockBufferManager manager;
//    private int rootBlockPointer;
//
//    public BPTree(ByteBlockBufferManager manager) {
//        this.manager = manager;
//
//        rootBlockPointer = createBucket(manager, NODE_TYPE_LEAF);
//    }
//
//    private int createBucket(ByteBlockBufferManager manager, byte nodeType) {
//        int p = manager.allocateAndClear(SIZE_OF_BUCKET);
//        manager.putByte(p, RELATIVE_POINTER_BUCKET_TYPE, nodeType);
//        return p;
//    }
//
//    public int search(int key) {
//        return searchBucketTree(rootBlockPointer, key);
//    }
//
//    private int searchBucketTree(int bucketPointer, int searchKey) {
//        if (bucketPointer == INT_VALUE_FOR_NULL) {
//            return INT_VALUE_FOR_NULL;
//        }
//
//        boolean isLeafNode = manager.getByte(bucketPointer, RELATIVE_POINTER_BUCKET_TYPE) == NODE_TYPE_LEAF;
//        boolean valueFound = false;
//        int valueBlockPointer = INT_VALUE_FOR_NULL;
//        int slotNum = 0;
//        while (slotNum < MAX_NUMBER_OF_SLOTS_IN_BUCKET && !valueFound) {
//            int slotKey = manager.getInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE);
//            if (searchKey <= slotKey) {
//                if (isLeafNode) {
//                    if (searchKey == slotKey) {
//                        valueBlockPointer = manager.getInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT);
//                        valueFound = true;
//                    } else {
//                        valueBlockPointer = INT_VALUE_FOR_NULL;
//                        valueFound = true;
//                    }
//                } else {
//                    // If key is less or equal the correct
//                    // block is always the pointer before the key
//                    int childBlockPointer = manager.getInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT);
//                    valueBlockPointer = searchBucketTree(childBlockPointer, searchKey);
//                    valueFound = true;
//                }
//            } else {
//                // If key is greater the correct block is after or in the next block
//                if (slotNum == (MAX_NUMBER_OF_SLOTS_IN_BUCKET -1)) {
//                    int nextBlockPointer = manager.getInt(bucketPointer, RELATIVE_POINTER_NEXT_BUCKET);
//                    valueBlockPointer = searchBucketTree(nextBlockPointer, searchKey);
//                    valueFound = true;
//                } else {
//                    slotNum++;
//                }
//            }
//        }
//
//        return valueBlockPointer;
//    }
//
//    public void insert(int key, int valueBlockPointer) {
//
//        insertBlockHandler(rootBlockPointer, key, valueBlockPointer);
//    }
//
//    private SplitObj insertBlockHandler(int bucketPointer, int insertKey, int insertBlockPointer) {
//        if (bucketPointer == INT_VALUE_FOR_NULL) {
//            return null;
//        }
//
//        boolean isLeafNode = manager.getByte(bucketPointer, RELATIVE_POINTER_BUCKET_TYPE) == NODE_TYPE_LEAF;
//        boolean valueFound = false;
//        SplitObj splitter = null;
//        int slotNum = 0;
//        while (slotNum < MAX_NUMBER_OF_SLOTS_IN_BUCKET && !valueFound) {
//            int slotKey = manager.getInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE);
//            if (insertKey <= slotKey) {
//                if (isLeafNode) {
//                    if (insertKey == slotKey) {
//                        splitter = new SplitObj();
//                        splitter.oldValueBlock = manager.getInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT);
//                        manager.putInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT, insertBlockPointer);
//                        valueFound = true;
//                    } else {
//                        byte slotCount = manager.getByte(bucketPointer, RELATIVE_POINTER_SLOT_COUNT);
//                        if (slotCount < MAX_NUMBER_OF_SLOTS_IN_BUCKET) {
//                            insertInToBucket(bucketPointer, insertKey, insertBlockPointer, slotNum, slotCount);
//                        } else {
//                            int newBucketPointer = splitBucket(bucketPointer);
//
//                            slotCount = manager.getByte(bucketPointer, RELATIVE_POINTER_SLOT_COUNT);
//                            if (slotNum < slotCount) {
//                                insertInToBucket(bucketPointer, insertKey, insertBlockPointer, slotNum, slotCount);
//                            } else {
//                                // Find the correct position
//                               // insertInToBucket(newBucketPointer, insertKey, insertBlockPointer, , slotCount);
//                            }
//                            splitter = new SplitObj();
//                            splitter.createdBucket = newBucketPointer;
//                        }
//                        valueFound = true;
//                    }
//                } else {
////                    int childBlockPointer = manager.getInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT);
////                    valueBlockPointer = searchBucketTree(childBlockPointer, insertKey);
////                    valueFound = true;
//                }
//            } else {
//                if (slotNum == (MAX_NUMBER_OF_SLOTS_IN_BUCKET -1)) {
////                    int nextBlockPointer = manager.getInt(bucketPointer, RELATIVE_POINTER_NEXT_BUCKET);
////                    valueBlockPointer = searchBucketTree(nextBlockPointer, insertKey);
////                    valueFound = true;
//                } else {
//                    slotNum++;
//                }
//            }
//        }
//
//        return splitter;
//    }
//
//    private int splitBucket(int oldBucketPointer) {
//        int newBucketPointer = createBucket(manager, NODE_TYPE_LEAF);
//        int slotsToMove = MAX_NUMBER_OF_SLOTS_IN_BUCKET - MAX_NUMBER_OF_SLOTS_IN_BUCKET/2;
//        for (int i = 0; i < slotsToMove; i++) {
//            int v = manager.getInt(oldBucketPointer, RELATIVE_POINTER_SLOTS + (MAX_NUMBER_OF_SLOTS_IN_BUCKET/2+i)*SIZE_OF_SLOT);
//            int k = manager.getInt(oldBucketPointer, RELATIVE_POINTER_SLOTS + (MAX_NUMBER_OF_SLOTS_IN_BUCKET/2+i)*SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE);
//            manager.putInt(newBucketPointer, RELATIVE_POINTER_SLOTS + i*SIZE_OF_SLOT, v);
//            manager.putInt(newBucketPointer, RELATIVE_POINTER_SLOTS + i*SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE, k);
//            manager.putInt(oldBucketPointer, RELATIVE_POINTER_SLOTS + (MAX_NUMBER_OF_SLOTS_IN_BUCKET/2+i)*SIZE_OF_SLOT, 0);
//            manager.putInt(oldBucketPointer, RELATIVE_POINTER_SLOTS + (MAX_NUMBER_OF_SLOTS_IN_BUCKET/2+i)*SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE, 0);
//        }
//        manager.putInt(oldBucketPointer, RELATIVE_POINTER_SLOT_COUNT, MAX_NUMBER_OF_SLOTS_IN_BUCKET/2);
//        manager.putInt(newBucketPointer, RELATIVE_POINTER_SLOT_COUNT, slotsToMove);
//        return newBucketPointer;
//    }
//
//    private void insertInToBucket(int bucketPointer, int insertKey, int insertBlockPointer, int slotNum, byte slotCount) {
//        // Move all bigger keys and their values one slot up
//        for (int i = slotCount; i > slotNum; i--) {
//            int v = manager.getInt(bucketPointer, RELATIVE_POINTER_SLOTS + (i-1)*SIZE_OF_SLOT);
//            int k = manager.getInt(bucketPointer, RELATIVE_POINTER_SLOTS + (i-1)*SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE);
//            manager.putInt(bucketPointer, RELATIVE_POINTER_SLOTS + i*SIZE_OF_SLOT, v);
//            manager.putInt(bucketPointer, RELATIVE_POINTER_SLOTS + i*SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE, k);
//        }
//        manager.putInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT, insertBlockPointer);
//        manager.putInt(bucketPointer, RELATIVE_POINTER_SLOTS + slotNum*SIZE_OF_SLOT + SIZE_OF_SLOT_VALUE, insertKey);
//
//        manager.putInt(bucketPointer, RELATIVE_POINTER_SLOT_COUNT, slotCount+1);
//    }
//
//    private static class SplitObj {
//        int createdBucket = INT_VALUE_FOR_NULL;
//        int oldValueBlock = INT_VALUE_FOR_NULL;
//    }
}
