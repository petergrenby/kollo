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
package se.grenby.kollo.sos.bbb;

import se.grenby.kollo.bbbmanager.ByteBlockBufferReader;
import se.grenby.kollo.json.JsonDataMap;

import static se.grenby.kollo.sos.SosConstants.*;

/**
 * Created by peteri on 07/02/16.
 */
public class SosByteBlockBufferMap extends SosByteBlockBufferObject {

    private final int mapStartPosition;
    private final int mapTotalLength;

    public SosByteBlockBufferMap(ByteBlockBufferReader reader, int blockPointer) {
        this(reader, blockPointer, 0);
    }

    public SosByteBlockBufferMap(ByteBlockBufferReader reader, int blockPointer, int position) {
        super(reader, blockPointer, position);

        int blockPosition = startBlockPosition;

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

    public SosByteBlockBufferMap getMapValue(String key) {
        return getValue(key, SosByteBlockBufferMap.class);
    }

    public SosByteBlockBufferList getListValue(String key) {
        return getValue(key, SosByteBlockBufferList.class);
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
        SosByteBlockBufferPosition position = new SosByteBlockBufferPosition(mapStartPosition);

        while (position.position() < mapStartPosition + mapTotalLength) {
            String mk = getStringFromByteBuffer(position);
            if (key.equals(mk)) {
                int valuePosition = position.position();
                int valueType = blockReader.getByte(blockPointer, position.position());
                position.incByte();
                if (valueType == MAP_VALUE) {
                    value = klass.cast(new SosByteBlockBufferMap(blockReader, blockPointer, valuePosition));
                } else if (valueType == LIST_VALUE) {
                    value = klass.cast(new SosByteBlockBufferList(blockReader, blockPointer, valuePosition));
                } else {
                    value = getValue(klass, valueType, position);
                }
                break;
            } else {
                skipValueTypeAndValueInByteBuffer(position);
            }
        }

        return value;
    }

    public JsonDataMap extractJSonDataMap() {
        JsonDataMap map = new JsonDataMap();

        SosByteBlockBufferPosition position = new SosByteBlockBufferPosition(mapStartPosition);

        while (position.position() < mapStartPosition + mapTotalLength) {
            String mk = getStringFromByteBuffer(position);
            int valuePosition = position.position();
            int valueType = blockReader.getByte(blockPointer, position.position());
            position.incByte();
            if (valueType == MAP_VALUE) {
                SosByteBlockBufferMap cdm = new SosByteBlockBufferMap(blockReader, blockPointer, valuePosition);
                map.putMap(mk, cdm.extractJSonDataMap());
                skipMapOrListValueInByteBuffer(position);
            } else if (valueType == LIST_VALUE) {
                SosByteBlockBufferList cdl = new SosByteBlockBufferList(blockReader, blockPointer, valuePosition);
                map.putList(mk, cdl.extractJSonDataList());
                skipMapOrListValueInByteBuffer(position);
            } else if (valueType == BYTE_VALUE) {
                map.putByte(mk, getValue(Byte.class, valueType, position));
            } else if (valueType == SHORT_VALUE) {
                map.putShort(mk, getValue(Short.class, valueType, position));
            } else if (valueType == INTEGER_VALUE) {
                map.putInt(mk, getValue(Integer.class, valueType, position));
            } else if (valueType == LONG_VALUE) {
                map.putLong(mk, getValue(Long.class, valueType, position));
            } else if (valueType == STRING_VALUE) {
                map.putString(mk, getValue(String.class, valueType, position));
            } else if (valueType == FLOAT_VALUE) {
                map.putFloat(mk, getValue(Float.class, valueType, position));
            } else if (valueType == DOUBLE_VALUE) {
                map.putDouble(mk, getValue(Double.class, valueType, position));
            } else {
                throw new IllegalStateException(valueType + " is not a correct value type.");
            }
        }

        return map;
    }

}
