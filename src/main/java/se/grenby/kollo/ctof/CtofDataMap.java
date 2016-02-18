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
package se.grenby.kollo.ctof;

import se.grenby.kollo.allocator.ByteBlockAllocationReader;

import static se.grenby.kollo.ctof.CtofConstants.LIST_VALUE;
import static se.grenby.kollo.ctof.CtofConstants.MAP_VALUE;

/**
 * Created by peteri on 07/02/16.
 */
public class CtofDataMap extends CtofDataObject {

    private final int mapStartPosition;
    private final int mapTotalLength;

    public CtofDataMap(ByteBlockAllocationReader reader, int blockPointer) {
        this(reader, blockPointer, 0);
    }

    public CtofDataMap(ByteBlockAllocationReader reader, int blockPointer, int position) {
        super(reader, blockPointer, position);

        byte valueType = blockReader.getByte(blockPointer, blockPosition);
        blockPosition += Byte.BYTES;
        if (valueType == MAP_VALUE) {
            mapTotalLength = blockReader.getShort(blockPointer, blockPosition);
            blockPosition += Short.BYTES;
            mapStartPosition = blockPosition;
        } else {
            throw new RuntimeException("This is not a map structure " + valueType);
        }
    }

    public CtofDataMap getMapValue(String key) {
        return getValue(key, CtofDataMap.class);
    }

    public CtofDataList getListValue(String key) {
        return getValue(key, CtofDataList.class);
    }

    public byte getByteValue(String key) {
        return getValue(key, Byte.class);
    }

    public short getShortValue(String key) {
        return getValue(key, Short.class);
    }

    public int getIntValue(String key) {
        return getValue(key, Integer.class);
    }

    public long getLongValue(String key) {
        return getValue(key, Long.class);
    }

    public float getFloatValue(String key) {
        return getValue(key, Float.class);
    }

    public double getDoubleValue(String key) {
        return getValue(key, Double.class);
    }

    public String getStringValue(String key) {
        return getValue(key, String.class);
    }

    public <T> T getValue(String key, Class<T> klass) {
        T value = null;
        blockPosition = mapStartPosition;

        while (blockPosition < mapStartPosition + mapTotalLength) {
            String mk = getStringFromByteBuffer();
            if (key.equals(mk)) {
                int valuePosition = blockPosition;
                int valueType = blockReader.getByte(blockPointer, blockPosition);
                blockPosition += Byte.BYTES;
                if (valueType == MAP_VALUE) {
                    value = klass.cast(new CtofDataMap(blockReader, blockPointer, valuePosition));
                } else if (valueType == LIST_VALUE) {
                    value = klass.cast(new CtofDataList(blockReader, blockPointer, valuePosition));
                } else {
                    value = getValue(klass, valueType);
                }
                break;
            } else {
                skipValueInByteBuffer();
            }
        }

        return value;
    }

}
