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

import java.nio.charset.StandardCharsets;

import static se.grenby.kollo.sos.SosConstants.*;
import static se.grenby.kollo.util.BitUtil.HEXES;

/**
 * Created by peteri on 30/01/16.
 */
public abstract class SosByteBlockBufferObject {
    protected final ByteBlockBufferReader blockReader;
    protected final int blockPointer;
    protected final int startBlockPosition;

    SosByteBlockBufferObject(ByteBlockBufferReader block, int blockPointer, int position) {
        this.blockReader = block;
        this.blockPointer = blockPointer;
        this.startBlockPosition = position;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blockReader.getAllocatedSize(blockPointer); i++) {
            byte b = blockReader.getByte(blockPointer, i);
            sb.append(HEXES[(b & 0xff) >>> 4]);
            sb.append(HEXES[(b & 0xf)]);
            sb.append(" ");
        }
        return sb.toString();
    }

    protected <T> T getValue(Class<T> klass, int valueType, SosByteBlockBufferPosition position) {
        T value;
        if (valueType == BYTE_VALUE) {
            value = klass.cast(blockReader.getByte(blockPointer, position.position()));
            position.incByte();
        } else if (valueType == SHORT_VALUE) {
            value = klass.cast(blockReader.getShort(blockPointer, position.position()));
            position.incShort();
        } else if (valueType == INTEGER_VALUE) {
            value = klass.cast(blockReader.getInt(blockPointer, position.position()));
            position.incInteger();
        } else if (valueType == LONG_VALUE) {
            value = klass.cast(blockReader.getLong(blockPointer, position.position()));
            position.incLong();
        } else if (valueType == STRING_VALUE) {
            value = klass.cast(getStringFromByteBuffer(position));
        } else if (valueType == FLOAT_VALUE) {
            value = klass.cast(blockReader.getFloat(blockPointer, position.position()));
            position.incFloat();
        } else if (valueType == DOUBLE_VALUE) {
            value = klass.cast(blockReader.getDouble(blockPointer, position.position()));
            position.incDouble();
        } else {
            throw new RuntimeException("Value type " + valueType + " is unknown.");
        }
        return value;
    }

    protected void skipValueTypeAndValueInByteBuffer(SosByteBlockBufferPosition position) {
        byte valueType = blockReader.getByte(blockPointer, position.position());
        position.incByte();
        switch (valueType) {
            case BYTE_VALUE:
                position.incByte();
                break;
            case SHORT_VALUE:
                position.incShort();
                break;
            case INTEGER_VALUE:
                position.incInteger();
                break;
            case LONG_VALUE:
                position.incLong();
                break;
            case FLOAT_VALUE:
                position.incFloat();
                break;
            case DOUBLE_VALUE:
                position.incDouble();
                break;
            case MAP_VALUE:
            case LIST_VALUE:
                skipMapOrListValueInByteBuffer(position);
                break;
            case STRING_VALUE:
                skipStringValueInByteBuffer(position);
                break;
            default:
                throw new RuntimeException("Unknown value type " + valueType);
        }
    }

    private void skipStringValueInByteBuffer(SosByteBlockBufferPosition position) {
        int stringLength = blockReader.getByte(blockPointer, position.position());
        position.incByte();
        position.addLength(stringLength);
    }

    protected void skipMapOrListValueInByteBuffer(SosByteBlockBufferPosition position) {
        int mlLength = blockReader.getShort(blockPointer, position.position());
        position.incShort();
        position.addLength(mlLength);
    }

    protected String getStringFromByteBuffer(SosByteBlockBufferPosition position) {
        int length = blockReader.getByte(blockPointer, position.position());
        position.incByte();
        byte[] bs = blockReader.getBytes(blockPointer, position.position(), length);
        position.addLength(length);
        return new String(bs, StandardCharsets.UTF_8);
    }

}
