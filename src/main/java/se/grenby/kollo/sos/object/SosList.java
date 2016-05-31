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
package se.grenby.kollo.sos.object;

import se.grenby.kollo.sos.reader.BufferReader;
import se.grenby.kollo.json.JsonDataList;

import java.util.Iterator;

import static se.grenby.kollo.sos.constant.SosConstants.*;
import static se.grenby.kollo.sos.constant.SosConstants.DOUBLE_VALUE;
import static se.grenby.kollo.sos.constant.SosConstants.FLOAT_VALUE;

/**
 * Created by peteri on 07/02/16.
 */
public class SosList extends SosObject implements Iterable<Object> {

    private final int listStartPosition;
    private final int listTotalLength;

    SosList(BufferReader bufferReader, int position) {
        super(bufferReader, position);

        int blockPosition = startBlockPosition;

        byte valueType = bufferReader.getByte(blockPosition);
        blockPosition += Byte.BYTES;
        if (valueType == LIST_VALUE) {
            listTotalLength = bufferReader.getShort(blockPosition);
            blockPosition += Short.BYTES;
            listStartPosition = blockPosition;
        } else {
            throw new RuntimeException("This is not a list structure " + valueType);
        }
    }


    public <T> T getNextValue(Class<T> klass, SosPosition position) {
        T value = null;

        if (position.position() < listStartPosition + listTotalLength) {
            int valuePosition = position.position();
            int valueType = bufferReader.getByte(position.position());
            position.incByte();
            if (valueType == MAP_VALUE) {
                value = klass.cast(new SosMap(bufferReader, valuePosition));
            } else if (valueType == LIST_VALUE) {
                value = klass.cast(new SosList(bufferReader, valuePosition));
            } else {
                value = getValue(klass, valueType, position);
            }
        } else {
            throw new RuntimeException("End of list has been reached");
        }

        return value;
    }

    public JsonDataList extractJSonDataList() {
        JsonDataList list = new JsonDataList();
        SosPosition position = new SosPosition(listStartPosition);

        while (position.position() < listStartPosition + listTotalLength) {
            int valuePosition = position.position();
            int valueType = bufferReader.getByte(position.position());
            position.incByte();
            if (valueType == MAP_VALUE) {
                SosMap cdm = new SosMap(bufferReader, valuePosition);
                list.addMap(cdm.extractJSonDataMap());
                skipMapOrListValueInByteBuffer(position);
            } else if (valueType == LIST_VALUE) {
                SosList cdl = new SosList(bufferReader, valuePosition);
                list.addList(cdl.extractJSonDataList());
                skipMapOrListValueInByteBuffer(position);
            } else if (valueType == BYTE_VALUE) {
                list.addByte(getValue(Byte.class, valueType, position));
            } else if (valueType == SHORT_VALUE) {
                list.addShort(getValue(Short.class, valueType, position));
            } else if (valueType == INTEGER_VALUE) {
                list.addInt(getValue(Integer.class, valueType, position));
            } else if (valueType == LONG_VALUE) {
                list.addLong(getValue(Long.class, valueType, position));
            } else if (valueType == STRING_VALUE) {
                list.addString(getValue(String.class, valueType, position));
            } else if (valueType == FLOAT_VALUE) {
                list.addFloat(getValue(Float.class, valueType, position));
            } else if (valueType == DOUBLE_VALUE) {
                list.addDouble(getValue(Double.class, valueType, position));
            } else {
                throw new IllegalStateException(valueType + " is not a correct value type.");
            }
        }

        return list;
    }

    @Override
    public Iterator<Object> iterator() {
        return new SosBBBListIterator();
    }

    private class SosBBBListIterator implements Iterator<Object> {

        private final SosPosition position;

        public SosBBBListIterator() {
            position = new SosPosition(listStartPosition);
        }

        @Override
        public boolean hasNext() {
            if (position.position() < listStartPosition + listTotalLength) {
                return true;
            }
            return false;
        }

        @Override
        public Object next() {
            Object obj;

            int valuePosition = position.position();
            int valueType = bufferReader.getByte(position.position());
            position.incByte();
            if (valueType == MAP_VALUE) {
                SosMap cdm = new SosMap(bufferReader, valuePosition);
                obj = cdm.extractJSonDataMap();
                skipMapOrListValueInByteBuffer(position);
            } else if (valueType == LIST_VALUE) {
                SosList cdl = new SosList(bufferReader, valuePosition);
                obj = cdl.extractJSonDataList();
                skipMapOrListValueInByteBuffer(position);
            } else if (valueType == BYTE_VALUE) {
                obj = getValue(Byte.class, valueType, position);
            } else if (valueType == SHORT_VALUE) {
                obj = getValue(Short.class, valueType, position);
            } else if (valueType == INTEGER_VALUE) {
                obj = getValue(Integer.class, valueType, position);
            } else if (valueType == LONG_VALUE) {
                obj = getValue(Long.class, valueType, position);
            } else if (valueType == STRING_VALUE) {
                obj = getValue(String.class, valueType, position);
            } else if (valueType == FLOAT_VALUE) {
                obj = getValue(Float.class, valueType, position);
            } else if (valueType == DOUBLE_VALUE) {
                obj = getValue(Double.class, valueType, position);
            } else {
                throw new IllegalStateException(valueType + " is not a correct value type.");
            }

            return obj;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
