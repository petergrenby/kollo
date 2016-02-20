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

import java.util.*;

/**
 * Created by peteri on 10/12/15.
 */
public class JsonDataList extends ArrayList<Object> {

    public JsonDataList addByte(byte value) {
        add(value);
        return this;
    }

    public JsonDataList addShort(short value) {
        add(value);
        return this;
    }

    public JsonDataList addInt(int value) {
        add(value);
        return this;
    }

    public JsonDataList addLong(long value) {
        add(value);
        return this;
    }

    public JsonDataList addString(String value) {
        add(value);
        return this;
    }

    public JsonDataList addFloat(float value) {
        add(value);
        return this;
    }

    public JsonDataList addDouble(double value) {
        add(value);
        return this;
    }

    public JsonDataList addList(JsonDataList value) {
        add(value);
        return this;
    }

    public JsonDataList addMap(JsonDataMap value) {
        add(value);
        return this;
    }

}
