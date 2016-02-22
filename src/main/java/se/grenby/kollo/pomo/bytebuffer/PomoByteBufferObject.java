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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static se.grenby.kollo.pomo.PomoConstants.*;
import static se.grenby.kollo.util.BitUtil.HEXES;

/**
 * Created by peteri on 30/01/16.
 */
public abstract class PomoByteBufferObject {
    protected final ByteBuffer buffer;
    protected int blockPosition;

    PomoByteBufferObject(ByteBuffer buffer, int position) {
        this.buffer = buffer;
        this.blockPosition = position;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buffer.limit(); i++) {
            byte b = buffer.get(i);
            sb.append(HEXES[(b & 0xff) >>> 4]);
            sb.append(HEXES[(b & 0xf)]);
            sb.append(" ");
        }
        return sb.toString();
    }

    protected <T> T getValue(Class<T> klass, int valueType) {
        T value;
        if (valueType == BYTE_VALUE) {
            value = klass.cast(buffer.get(blockPosition));
            blockPosition += Byte.BYTES;
        } else if (valueType == SHORT_VALUE) {
            value = klass.cast(buffer.getShort(blockPosition));
            blockPosition += Short.BYTES;
        } else if (valueType == INTEGER_VALUE) {
            value = klass.cast(buffer.getInt(blockPosition));
            blockPosition += Integer.BYTES;
        } else if (valueType == LONG_VALUE) {
            value = klass.cast(buffer.getLong(blockPosition));
            blockPosition += Long.BYTES;
        } else if (valueType == STRING_VALUE) {
            value = klass.cast(getStringFromByteBuffer());
        } else if (valueType == FLOAT_VALUE) {
            value = klass.cast(buffer.getFloat(blockPosition));
            blockPosition += Float.BYTES;
        } else if (valueType == DOUBLE_VALUE) {
            value = klass.cast(buffer.getDouble(blockPosition));
            blockPosition += Double.BYTES;
        } else {
            throw new RuntimeException("Value type " + valueType + " is unknown.");
        }
        return value;
    }

    protected void skipValueTypeAndValueInByteBuffer() {
        byte valueType = buffer.get(blockPosition);
        blockPosition += Byte.BYTES;
        switch (valueType) {
            case BYTE_VALUE:
                blockPosition += Byte.BYTES;
                break;
            case SHORT_VALUE:
                blockPosition += Short.BYTES;
                break;
            case INTEGER_VALUE:
                blockPosition += Integer.BYTES;
                break;
            case LONG_VALUE:
                blockPosition += Long.BYTES;
                break;
            case FLOAT_VALUE:
                blockPosition += Float.BYTES;
                break;
            case DOUBLE_VALUE:
                blockPosition += Double.BYTES;
                break;
            case MAP_VALUE:
            case LIST_VALUE:
                skipMapOrListValueInByteBuffer();
                break;
            case STRING_VALUE:
                skipStringValueInByteBuffer();
                break;
            default:
                throw new RuntimeException("Unknown value type " + valueType);
        }
    }

    private void skipStringValueInByteBuffer() {
        int stringLength = buffer.get(blockPosition);
        blockPosition += stringLength + Byte.BYTES;
    }

    protected void skipMapOrListValueInByteBuffer() {
        int mlLength = buffer.getShort(blockPosition);
        blockPosition += mlLength + Short.BYTES;
    }

    protected String getStringFromByteBuffer() {
        int length = buffer.get(blockPosition);
        blockPosition += Byte.BYTES;
        buffer.position(blockPosition);
        byte[] bs = new byte[length];
        buffer.get(bs, 0, length);
        blockPosition += length;
        return new String(bs, StandardCharsets.UTF_8);
    }

}
