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

import se.grenby.kollo.json.JsonDataMap;

import java.nio.ByteBuffer;

import static se.grenby.kollo.pomo.PomoConstants.*;

/**
 * Created by peteri on 07/02/16.
 */
public class PomoByteBufferMap extends PomoByteBufferObject {

    private final int mapStartPosition;
    private final int mapTotalLength;

    public PomoByteBufferMap(ByteBuffer buffer) {
        this(cloneByteBuffert(buffer), 0);
    }

    public static ByteBuffer cloneByteBuffert(ByteBuffer src) {
        // Allocate a new buffer with correct size
        ByteBuffer dst = ByteBuffer.allocate(src.limit());
        dst.put(src);
        dst.flip();
        return dst;
    }

    public PomoByteBufferMap(ByteBuffer buffer, int position) {
        super(buffer, position);

        byte valueType = buffer.get(blockPosition);
        blockPosition += Byte.BYTES;
        if (valueType == MAP_VALUE) {
            mapTotalLength = buffer.getShort(blockPosition);
            blockPosition += Short.BYTES;
            mapStartPosition = blockPosition;
        } else {
            throw new RuntimeException("This is not a map structure " + valueType);
        }
    }

    public PomoByteBufferMap getMapValue(String key) {
        return getValue(key, PomoByteBufferMap.class);
    }

    public PomoByteBufferList getListValue(String key) {
        return getValue(key, PomoByteBufferList.class);
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
                int valueType = buffer.get(blockPosition);
                blockPosition += Byte.BYTES;
                if (valueType == MAP_VALUE) {
                    value = klass.cast(new PomoByteBufferMap(buffer, valuePosition));
                } else if (valueType == LIST_VALUE) {
                    value = klass.cast(new PomoByteBufferList(buffer, valuePosition));
                } else {
                    value = getValue(klass, valueType);
                }
                break;
            } else {
                skipValueTypeAndValueInByteBuffer();
            }
        }

        return value;
    }

    public JsonDataMap extractJSonDataMap() {
        JsonDataMap map = new JsonDataMap();

        blockPosition = mapStartPosition;

        while (blockPosition < mapStartPosition + mapTotalLength) {
            String mk = getStringFromByteBuffer();
            int valuePosition = blockPosition;
            int valueType = buffer.get(blockPosition);
            blockPosition += Byte.BYTES;
            if (valueType == MAP_VALUE) {
                PomoByteBufferMap cdm = new PomoByteBufferMap(buffer, valuePosition);
                map.putMap(mk, cdm.extractJSonDataMap());
                skipMapOrListValueInByteBuffer();
            } else if (valueType == LIST_VALUE) {
                PomoByteBufferList cdl = new PomoByteBufferList(buffer, valuePosition);
                map.putList(mk, cdl.extractJSonDataList());
                skipMapOrListValueInByteBuffer();
            } else if (valueType == BYTE_VALUE) {
                map.putByte(mk, getValue(Byte.class, valueType));
            } else if (valueType == SHORT_VALUE) {
                map.putShort(mk, getValue(Short.class, valueType));
            } else if (valueType == INTEGER_VALUE) {
                map.putInt(mk, getValue(Integer.class, valueType));
            } else if (valueType == LONG_VALUE) {
                map.putLong(mk, getValue(Long.class, valueType));
            } else if (valueType == STRING_VALUE) {
                map.putString(mk, getValue(String.class, valueType));
            } else if (valueType == FLOAT_VALUE) {
                map.putFloat(mk, getValue(Float.class, valueType));
            } else if (valueType == DOUBLE_VALUE) {
                map.putDouble(mk, getValue(Double.class, valueType));
            } else {
                throw new IllegalStateException(valueType + " is not a correct value type.");
            }
        }

        return map;
    }

}
