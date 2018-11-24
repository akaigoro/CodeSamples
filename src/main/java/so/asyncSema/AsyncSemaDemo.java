package so.asyncSema;
import org.df4j.core.boundconnector.permitstream.Semafor;
import org.df4j.core.tasknode.Action;
import org.df4j.core.tasknode.messagestream.Actor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;

public class AsyncSemaDemo extends Actor {
    List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5);
    Semafor sema = new Semafor(this, 2);
    Iterator<Integer> iter = ids.iterator();
    int tick = 100; // millis
    CountDownLatch done = new CountDownLatch(ids.size());
    long start = System.currentTimeMillis();

    private void printClock(String s) {
        long ticks = (System.currentTimeMillis() - start)/tick;
        System.out.println(Long.toString(ticks) + " " + s);
    }

    CompletableFuture<Integer> Retrieve(Integer e) {
        return CompletableFuture.supplyAsync(() -> {
            printClock("Req " + e + " started");
            try {
                Thread.sleep(tick); // Network
            } catch (InterruptedException ex) {
            }
            printClock(" Req " + e + " done");
            return e;
        }, executor);
    }

    void ProcessRecord(Integer s) {
        printClock(" Proc " + s + " started");
        try {
            Thread.sleep(tick*2); // Compute
        } catch (InterruptedException ex) {
        }
        printClock("  Proc " + s + " done");
    }

    @Action
    public void act() {
        if (iter.hasNext()) {
            CompletableFuture<Integer> fut = Retrieve(iter.next());
            fut.thenRun(sema::release);
            fut.thenAcceptAsync(this::ProcessRecord,  executor)
            .thenRun(done::countDown);
        } else {
            super.stop();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AsyncSemaDemo asyncSemaDemo = new AsyncSemaDemo();
        asyncSemaDemo.start(ForkJoinPool.commonPool());
        asyncSemaDemo.done.await();
    }
}
