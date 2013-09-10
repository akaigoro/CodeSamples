/*
 * Copyright 2011 by Alexei Kaigorodov
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package threadedRingBuffer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.Assert;

import org.junit.Test;

public class ThreadedRingBufferTest {    
	RingBuffer<LongValue> ringBuffer;
	Cursor<LongValue> writeWindow;
	Cursor<LongValue> readWindow;

	public void init() {
	    int bufSize = 1024;
        ringBuffer=new RingBuffer<LongValue>(bufSize);
	    writeWindow=ringBuffer.writeWindow;
	    readWindow=ringBuffer.readWindow;	    
        for (int k=0; k<bufSize; k++) {
            writeWindow.set(k, new LongValue(0));
        }
	}
	
    static abstract class Cursor<T> {
        RingBuffer<T> ringBuffer;
        Object[] entries;
        int bufSize;
        protected long position;
        private final Lock lock = new ReentrantLock();
        private final Condition posChanged  = lock.newCondition();
        int waitCount=0;        

        public Cursor(RingBuffer<T> ringBuffer) {
            this.ringBuffer = ringBuffer;
            this.entries = ringBuffer.entries;
            this.bufSize = ringBuffer.bufSize;
        }

        @SuppressWarnings("unchecked")
        public T get(long position) {
            return (T)entries[(int)(position%bufSize)];
        }

        public void set(long position, T object) {
            entries[(int)(position%bufSize)]=object;
        }

        public abstract void waitLimit(long position2) throws InterruptedException;

        public abstract long getLimit();

        public long getPosition() {
            lock.lock();
            try {
               return position;
            } finally {
               lock.unlock();
            }
        }

        /** not for public use */
        void waitPosition(long position2) throws InterruptedException {
            lock.lock();
            try {
                while (position2>=position) {
                    waitCount++;
                    posChanged.await();
                }
            } finally {
                lock.unlock();
            }
        }

        public void setPosition(long position) {
            lock.lock();
            try {
                this.position=position;
                posChanged.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    static class RingBuffer<T> {
        int bufSize;
        Object[] entries;
        Cursor<T> writeWindow;
        Cursor<T> readWindow;

        public RingBuffer(int bufSize) {
            this.bufSize = bufSize;
            entries=new Object[bufSize];
            writeWindow=new WriteCursor();
            readWindow=new ReadCursor();
        }
      
        class WriteCursor extends Cursor<T> {
            public WriteCursor() {
                super(RingBuffer.this);
            }

            public long getLimit() {
                return readWindow.getPosition()+bufSize;
            }

            public void waitLimit(long limit) throws InterruptedException {
                readWindow.waitPosition(limit-bufSize);
            }
        }
        
        class ReadCursor extends Cursor<T> {
            
            public ReadCursor() {
                super(RingBuffer.this);
            }

            @Override
            public long getLimit() {
                return writeWindow.getPosition();
            }

            @Override
            public void waitLimit(long limit) throws InterruptedException {
                writeWindow.waitPosition(limit);
            }
        }
    }

    static abstract class Worker implements Runnable {
        Cursor<LongValue> cursor;
        int iterations;
        String name;

        public Worker(Cursor<LongValue> cursor, int iterations, String name) {
            this.cursor = cursor;
            this.iterations = iterations;
            this.name = name;
        }

        @Override
        public void run() {
            System.out.printf("%s started; position=%,d\n", name, cursor.getPosition());
            try {
                for (long position = 0; ; ) {
                    cursor.waitLimit(position);
                    act(position);
                    position++;
                    cursor.setPosition(position);
                    if (position==iterations) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
            }
            System.out.printf("%s; waitCount=%,d; position=%,d\n"
                    , name, cursor.waitCount, cursor.getPosition());
       }

        protected abstract void act(long position);
    }

    static class Writer extends Worker {

        public Writer(Cursor<LongValue> cursor, int iterations) {
            super(cursor, iterations, "Writer");
        }

        protected void act(long position) {
            LongValue item = cursor.get(position);
            item.value=position;
//          cursor.set(position, null);
        }
    }
    
    static class Reader extends Worker {

        public Reader(Cursor<LongValue> cursor, int iterations) {
            super(cursor, iterations, "Reader");
        }

        protected void act(long position) {
            LongValue item = cursor.get(position);
            Assert.assertTrue(item.value==position);
//          cursor.set(position, null);
        }
    }
    
    void test(int iterations) throws InterruptedException {
        System.out.printf("<<iterations=%,d\n", iterations);
        init();
        Writer writer=new Writer(writeWindow, iterations);
        Reader reader=new Reader(readWindow, iterations);
        
        Thread rt=new Thread(reader);
        Thread wt=new Thread(writer);
        long start=System.currentTimeMillis();
        wt.start();
        rt.start();
        rt.join();
        wt.join();
        long end=System.currentTimeMillis();
        long elapsed = end-start;
        float throughput=iterations/(elapsed/1000f);
        System.out.printf("elapsed=%,d ms, throughput=%,d>>\n", elapsed, (int)throughput);
    }

    @Test
    public void test1() throws InterruptedException {
        int iterations = 1000000;
        test(iterations/2);
        test(iterations/2);
        test(iterations);
        test(iterations);
    }

    public static void main(String args[]) throws InterruptedException, ExecutionException {
        ThreadedRingBufferTest nt = new ThreadedRingBufferTest();
        nt.test1();
    }

}
