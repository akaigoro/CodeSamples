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

import junit.framework.Assert;

import org.junit.Test;

public class ThreadedBatchRingBufferTest {    
	RingBuffer<LongValue> ringBuffer;
	Window<LongValue> writeWindow;
	Window<LongValue> readWindow;

	public void init() {
	    int bufSize = 1024;
        ringBuffer=new RingBuffer<LongValue>(bufSize);
	    writeWindow=ringBuffer.writeWindow;
	    readWindow=ringBuffer.readWindow;	    
        for (int k=0; k<bufSize; k++) {
            writeWindow.set(k, new LongValue(0));
        }
	}
	
    static abstract class Window<T> {
        RingBuffer<T> ringBuffer;
        Object[] entries;
        int bufSize;
        protected long position;
        int waitCount=0;
        
        public Window(RingBuffer<T> ringBuffer) {
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

        public synchronized long getPosition() {
            return position;
        }

        public synchronized void waitPosition(long position2) throws InterruptedException {
            while (position2>=position) {
                waitCount++;
                this.wait();
            }
        }

        public synchronized void setPosition(long position) {
            this.position=position;
            this.notifyAll();
        }
    }

    static class RingBuffer<T> {
        int bufSize;
        Object[] entries;
        Window<T> writeWindow;
        Window<T> readWindow;

        public RingBuffer(int bufSize) {
            this.bufSize = bufSize;
            entries=new Object[bufSize];
            writeWindow=new WriteWindow();
            readWindow=new ReadWindow();
        }
      
        class WriteWindow extends Window<T> {
            public WriteWindow() {
                super(RingBuffer.this);
            }

            public long getLimit() {
                return readWindow.getPosition()+bufSize;
            }

            public void waitLimit(long limit) throws InterruptedException {
                readWindow.waitPosition(limit-bufSize);
            }
        }
        
        class ReadWindow extends Window<T> {
            
            public ReadWindow() {
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
        Window<LongValue> window;
        int iterations;
        String name;

        public Worker(Window<LongValue> window, int iterations, String name) {
            this.window = window;
            this.iterations = iterations;
            this.name = name;
        }

        @Override
        public void run() {
            System.out.printf("%s started; position=%,d\n", name, window.getPosition());
            int waitCount=0;
            int setPosCount=0;
            try {
                long broadcastedPos=window.getPosition();
                long limit=window.getLimit();
                for (long position = broadcastedPos; position<iterations; position++) {
                    if (position==limit) {
                        limit=window.getLimit();
                        if (position==limit) {
                            window.setPosition(position);
                            broadcastedPos=position;
                            setPosCount++;
                            window.waitLimit(position);
                            waitCount++;
                            limit=window.getLimit();
                        }
                    } else if (position-broadcastedPos >= 25) { // optimal batch size
                        window.setPosition(position);
                        broadcastedPos=position;
                        setPosCount++;
                    }
                    act(position);
                }
            } catch (InterruptedException e) {
            }
            window.setPosition(iterations);
            setPosCount++;
            System.out.printf("%s waitLimCount=%,d; waitWinCount=%,d, setPosCount2=%,d; position=%,d\n"
                    , name, waitCount, window.waitCount, setPosCount, window.getPosition());
       }

        protected abstract void act(long position);
    }

    static class Writer extends Worker {

        public Writer(Window<LongValue> window, int iterations) {
            super(window, iterations, "Writer");
            // TODO Auto-generated constructor stub
        }

        protected void act(long position) {
            LongValue item = window.get(position);
            item.value=position;
//          window.set(position, null);
        }
    }
    
    static class Reader extends Worker {

        public Reader(Window<LongValue> window, int iterations) {
            super(window, iterations, "Reader");
        }

        protected void act(long position) {
            LongValue item = window.get(position);
            Assert.assertTrue(item.value==position);
//          window.set(position, null);
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
        int iterations = 10000000;
        test(iterations/2);
        test(iterations/2);
        test(iterations);
        test(iterations);
    }

    public static void main(String args[]) throws InterruptedException, ExecutionException {
        ThreadedBatchRingBufferTest nt = new ThreadedBatchRingBufferTest();
        nt.test1();
    }

}
