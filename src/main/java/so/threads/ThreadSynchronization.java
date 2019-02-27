package so.threads;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.reactivex.schedulers.Schedulers.shutdown;

public class ThreadSynchronization implements Runnable {

    private int start;
    private Semaphore semaphore1;
    private Semaphore semaphore2;

    private ThreadSynchronization(int start, Semaphore semaphore1, Semaphore semaphore2) {
        this.start = start;
        this.semaphore1 = semaphore1;
        this.semaphore2 = semaphore2;
    }

    private static void start(int start, Semaphore semaphore1, Semaphore semaphore2) {
        ThreadSynchronization ts = new ThreadSynchronization(start, semaphore1, semaphore2);
        Thread thread = new Thread(ts);
        thread.start();
    }

   // @Override
    public void run1() {
        for (int i = start; i <= 100; i += 2) {
            semaphore1.acquireUninterruptibly();
            System.out.println("Thread " + start + ": The number is '" + i + "'");
            semaphore2.release();
        }
    }


    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override public void run() {
     //   checkState(running.compareAndSet(false, true));
        //         startup();
        RuntimeException exception = null;
        try {
            mainLoop();
        } catch (RuntimeException e) {
            //         logger.error("Uncaught exception, attempting a clean shutdown", e);
            exception = e; // Used in the finally-clause
        }
        try {
            shutdown();
        } catch (RuntimeException e) {
            //        logger.error("Clean shutdown failed", e);
            if (exception != null) {
                exception.addSuppressed(e);
            } else {
                exception = e; // Used in the finally-clause
            }
        }
        running.set(false);
        if (exception != null) {
            throw exception;
        }
    }

    private void mainLoop() throws RuntimeException {
    }

    public static void main(String[] args) {
        Semaphore semaphore1 = new Semaphore(1);
        Semaphore semaphore2 = new Semaphore(0);

        start(1, semaphore1, semaphore2);
        start(2, semaphore2, semaphore1);
    }
}