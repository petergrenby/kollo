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
public class CtofDataList extends CtofDataObject {

    private final int listStartPosition;
    private final int listTotalLength;
    private int nextElementPosition;

    CtofDataList(ByteBlockAllocationReader reader, int blockPointer) {
        this(reader, blockPointer, 0);
    }

    CtofDataList(ByteBlockAllocationReader reader, int blockPointer, int position) {
        super(reader, blockPointer, position);

        byte valueType = reader.getByte(blockPointer, blockPosition);
        blockPosition += Byte.BYTES;
        if (valueType == LIST_VALUE) {
            listTotalLength = reader.getShort(blockPointer, blockPosition);
            blockPosition += Short.BYTES;
            listStartPosition = blockPosition;
            nextElementPosition = listStartPosition;
        } else {
            throw new RuntimeException("This is not a list structure " + valueType);
        }
    }

    public CtofDataMap getNextMapValue() {
        return getNextValue(CtofDataMap.class);
    }

    public CtofDataList getNextListValue() {
        return getNextValue(CtofDataList.class);
    }

    public byte getNextByteValue() {
        return getNextValue(Byte.class);
    }

    public short getNextShortValue() {
        return getNextValue(Short.class);
    }

    public int getNextIntValue() {
        return getNextValue(Integer.class);
    }

    public long getNextLongValue() {
        return getNextValue(Long.class);
    }

    public float getNextFloatValue() {
        return getNextValue(Float.class);
    }

    public double getNextDoubleValue() {
        return getNextValue(Double.class);
    }

    public String getNextStringValue() {
        return getNextValue(String.class);
    }

    public void resetIterator() {
        nextElementPosition = listStartPosition;
    }

    public boolean hasNext() {
        if (nextElementPosition < listStartPosition + listTotalLength) {
            return true;
        }
        return false;
    }

    public <T> T getNextValue(Class<T> klass) {
        T value = null;
        blockPosition = nextElementPosition;

        if (blockPosition < listStartPosition + listTotalLength) {
            int valuePosition = blockPosition;
            int valueType = blockReader.getByte(blockPointer, blockPosition);
            blockPosition += Byte.BYTES;
            if (valueType == MAP_VALUE) {
                value = klass.cast(new CtofDataMap(blockReader, valuePosition));
            } else if (valueType == LIST_VALUE) {
                value = klass.cast(new CtofDataList(blockReader, valuePosition));
            } else {
                value = getValue(klass, valueType);
            }
            nextElementPosition = blockPosition;
        } else {
            throw new RuntimeException("End of list has been reached");
        }

        return value;
    }

}
