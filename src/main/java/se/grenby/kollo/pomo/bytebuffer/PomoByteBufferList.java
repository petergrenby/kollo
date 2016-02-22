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
package se.grenby.kollo.pomo.bytebuffer;

import se.grenby.kollo.json.JsonDataList;

import java.nio.ByteBuffer;

import static se.grenby.kollo.pomo.PomoConstants.*;

/**
 * Created by peteri on 07/02/16.
 */
public class PomoByteBufferList extends PomoByteBufferObject {

    private final int listStartPosition;
    private final int listTotalLength;
    private int nextElementPosition;

    PomoByteBufferList(ByteBuffer buffer, int position) {
        super(buffer, position);

        byte valueType = buffer.get(blockPosition);
        blockPosition += Byte.BYTES;
        if (valueType == LIST_VALUE) {
            listTotalLength = buffer.getShort(blockPosition);
            blockPosition += Short.BYTES;
            listStartPosition = blockPosition;
            nextElementPosition = listStartPosition;
        } else {
            throw new RuntimeException("This is not a list structure " + valueType);
        }
    }

    public PomoByteBufferMap getNextMapValue() {
        return getNextValue(PomoByteBufferMap.class);
    }

    public PomoByteBufferList getNextListValue() {
        return getNextValue(PomoByteBufferList.class);
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
            int valueType = buffer.get(blockPosition);
            blockPosition += Byte.BYTES;
            if (valueType == MAP_VALUE) {
                value = klass.cast(new PomoByteBufferMap(buffer, valuePosition));
            } else if (valueType == LIST_VALUE) {
                value = klass.cast(new PomoByteBufferList(buffer, valuePosition));
            } else {
                value = getValue(klass, valueType);
            }
            nextElementPosition = blockPosition;
        } else {
            throw new RuntimeException("End of list has been reached");
        }

        return value;
    }

    public JsonDataList extractJSonDataList() {
        JsonDataList list = new JsonDataList();
        blockPosition = listStartPosition;

        while (blockPosition < listStartPosition + listTotalLength) {
            int valuePosition = blockPosition;
            int valueType = buffer.get(blockPosition);
            blockPosition += Byte.BYTES;
            if (valueType == MAP_VALUE) {
                PomoByteBufferMap cdm = new PomoByteBufferMap(buffer, valuePosition);
                list.addMap(cdm.extractJSonDataMap());
                skipMapOrListValueInByteBuffer();
            } else if (valueType == LIST_VALUE) {
                PomoByteBufferList cdl = new PomoByteBufferList(buffer, valuePosition);
                list.addList(cdl.extractJSonDataList());
                skipMapOrListValueInByteBuffer();
            } else if (valueType == BYTE_VALUE) {
                list.addByte(getValue(Byte.class, valueType));
            } else if (valueType == SHORT_VALUE) {
                list.addShort(getValue(Short.class, valueType));
            } else if (valueType == INTEGER_VALUE) {
                list.addInt(getValue(Integer.class, valueType));
            } else if (valueType == LONG_VALUE) {
                list.addLong(getValue(Long.class, valueType));
            } else if (valueType == STRING_VALUE) {
                list.addString(getValue(String.class, valueType));
            } else if (valueType == FLOAT_VALUE) {
                list.addFloat(getValue(Float.class, valueType));
            } else if (valueType == DOUBLE_VALUE) {
                list.addDouble(getValue(Double.class, valueType));
            } else {
                throw new IllegalStateException(valueType + " is not a correct value type.");
            }
        }

        return list;
    }
}
