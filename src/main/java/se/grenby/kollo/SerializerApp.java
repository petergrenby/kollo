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
package se.grenby.kollo;

import se.grenby.kollo.bbb.ByteBlockBufferManager;
import se.grenby.sos.SosManager;
import se.grenby.sos.object.SosList;
import se.grenby.sos.object.SosMap;
import se.grenby.sos.json.JsonDataList;
import se.grenby.sos.json.JsonDataMap;

import java.util.Map;

/**
 * Created by peteri on 27/11/15.
 */
public class SerializerApp {


    public static void main(String[] args) {
        JsonDataMap jdm = new JsonDataMap();
        jdm.putByte("by", (byte) 64).putShort("sh", (short) 312).putInt("in", 45).putLong("lo", 76);
        jdm.putString("st", "ing").putFloat("fl", 36.4f).putDouble("do", 789.45436);

        JsonDataList jdl = new JsonDataList();
        jdl.addByte((byte) 1).addShort((short) 2).addInt(3).addString("elem");
        jdl.addLong(4).addFloat(6.6f).addDouble(7.7);
        jdm.putList("list", jdl);

        JsonDataMap jdm2 = new JsonDataMap();
        jdm2.putString("st2", "we dooo2").putString("st3", "we dooo3");
        jdm.putMap("map", jdm2);

        ByteBlockBufferManager memory = new ByteBlockBufferManager(1024*10);
        SosManager sosManager = new SosManager(memory);

        SosMap sos = sosManager.createSosMap(jdm);

        System.out.println(sos.toString());
        System.out.println();
        System.out.println("Byte -->" + sos.getByteValue("by") + "<--");
        System.out.println("Short -->" + sos.getShortValue("sh") + "<--");
        System.out.println("Integer -->" + sos.getIntValue("in") + "<--");
        System.out.println("Long -->" + sos.getLongValue("lo") + "<--");
        System.out.println("Float -->" + sos.getFloatValue("fl") + "<--");
        System.out.println("Double -->" + sos.getDoubleValue("do") + "<--");
        System.out.println("String -->" + sos.getStringValue("st") + "<--");

        SosMap bpo2 = sos.getMapValue("map");
        for (Map.Entry<String, Object> e : bpo2) {
            System.out.println("In map --> " + e.getValue() + "<--");
        }

        SosList bpl = sos.getListValue("list");
        for (Object o : bpl) {
            System.out.println("In list -->" + o.toString() + "<--");
        }

        System.out.println(memory.memStructureToString());

        JsonDataMap jdme = sos.extractJSonDataMap();

        sosManager.removeSosObject(sos);

        System.out.println("by -- > " + jdme.getByte("by"));
    }

}
