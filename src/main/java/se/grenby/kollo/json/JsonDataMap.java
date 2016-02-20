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
package se.grenby.kollo.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by peteri on 10/12/15.
 */
public class JsonDataMap extends HashMap<String, Object> {

    public JsonDataMap putByte(String key, byte value) {
        put(key, value);
        return this;
    }

    public JsonDataMap putShort(String key, short value) {
        put(key, value);
        return this;
    }

    public JsonDataMap putInt(String key, int value) {
        put(key, value);
        return this;
    }

    public JsonDataMap putLong(String key, long value) {
        put(key, value);
        return this;
    }

    public JsonDataMap putString(String key, String value) {
        put(key, value);
        return this;
    }

    public JsonDataMap putFloat(String key, float value) {
        put(key, value);
        return this;
    }

    public JsonDataMap putDouble(String key, double value) {
        put(key, value);
        return this;
    }

    public JsonDataMap putMap(String key, JsonDataMap value) {
        put(key, value);
        return this;
    }

    public JsonDataMap putList(String key, JsonDataList value) {
        put(key, value);
        return this;
    }

    public byte getByte(String key) {
        return (byte) get(key);
    }


    public short getShort(String key) {
        return (short) get(key);
    }


    public int getInteger(String key) {
        return (Integer) get(key);
    }


    public long getLong(String key) {
        return (byte) get(key);
    }


    public float getFloat(String key) {
        return (float) get(key);
    }

    public double getDouble(String key) {
        return (double) get(key);
    }

    public JsonDataMap getMap(String key) {
        return (JsonDataMap) get(key);
    }

    public JsonDataList getList(String key) {
        return (JsonDataList) get(key);
    }

}
