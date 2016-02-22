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

import se.grenby.kollo.bbbmanager.ByteBlockBufferManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KolloApp
{
    public static void main( String args[] ) throws Exception {
        ByteBlockBufferManager joh = new ByteBlockBufferManager(1024);

        int p1 = joh.allocate(300);
        System.out.println(p1);
        int p2 = joh.allocate(300);
        System.out.println(p2);
        int p3 = joh.allocate(300);
        System.out.println(p3);
        int p4 = joh.allocate(300);
        System.out.println(p4);

        System.out.println(joh.memStructureToString());

        System.out.println(joh.free(p2));
        System.out.println("integrity " + joh.verfiyIntegrity());
        System.out.println(joh.free(p1));
        System.out.println("integrity " + joh.verfiyIntegrity());
        System.out.println(joh.free(p4));
        System.out.println("integrity " + joh.verfiyIntegrity());
        System.out.println(joh.free(p3));
        System.out.println("integrity " + joh.verfiyIntegrity());

        System.out.println(joh.memStructureToString());

        p1 = joh.allocate(300);
        System.out.println(p1);
        p2 = joh.allocate(300);
        System.out.println(p2);
        p3 = joh.allocate(300);
        System.out.println(p3);
        p4 = joh.allocate(300);
        System.out.println(p4);

        System.out.println(joh.memStructureToString());

        List<Integer> pointers = new ArrayList<>(2000);
        joh = new ByteBlockBufferManager(1024*1024*1024);
        for (int i=0; i<2000; i++) {
            pointers.add(joh.allocate(1024));
        }

        int c = 0;
        for (Iterator<Integer> it = pointers.iterator(); ; it.hasNext()) {
            joh.free(it.next());
            it.remove();
            if (c++ > 1000)
                break;
        }

        for (int i=0; i<1000; i++) {
            pointers.add(joh.allocate(1024));
        }

        for (int p : pointers) {
            joh.free(p);
        }
    }
}
