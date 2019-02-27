package so.completableFuture;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Parallel {
    static Instant globstart = Instant.now();

    public static void now() {
        System.out.print("" + Duration.between(globstart, Instant.now()).toMillis()+": ");

    }
    private static List<Integer> getFirstList() {
        now();
        Instant start = Instant.now();
        System.out.println("First list is being created by: "+ Thread.currentThread().getName());
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            l.add(i);
        }
        now();
        System.out.println("getFirstList time: " + Duration.between(start, Instant.now()).toMillis());
        return l;
    }

    private static List<Integer> getSecondList() {
        now();
        Instant start = Instant.now();

        System.out.println("Second list is being created by: "+ Thread.currentThread().getName());
        List<Integer> l = new ArrayList<>();
        for (int i = 10000000; i < 20000000; i++) {
            l.add(i);
        }
        now();
        System.out.println("getSecondList time: " + Duration.between(start, Instant.now()).toMillis());
        return l;
    }

    private static List<Integer> combine(List<Integer> l1, List<Integer> l2) {
        now();
        Instant start = Instant.now();

        System.out.println("Third list is being created by: "+ Thread.currentThread().getName());
        ArrayList<Integer> l3 = new ArrayList<>();
        l3.addAll(l1);
        l3.addAll(l2);
        now();
        System.out.println("combine time: " + Duration.between(start, Instant.now()).toMillis());
        return l3;
    }

    private static void sequ() {
        Instant s = Instant.now();
        List<Integer> l1 = getFirstList();
        List<Integer> l2 = getSecondList();
        List<Integer> l3 = combine(l1, l2);
        now();
        System.out.println("sequ Execution time: " + Duration.between(s, Instant.now()).toMillis());
    }

    private static void parallel() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Instant start = Instant.now();
        CompletableFuture<List<Integer>> cf1 = CompletableFuture.supplyAsync(() -> getFirstList(), executor);
        CompletableFuture<List<Integer>> cf2 = CompletableFuture.supplyAsync(() -> getSecondList(), executor);

        CompletableFuture<Void> cf3 = cf1.thenAcceptBothAsync(cf2, (l1, l2) -> combine(l1, l2), executor);
        try {
            cf3.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        now();
        System.out.println("parallel Execution time: " + Duration.between(start, Instant.now()).toMillis());

        executor.shutdown();
    }

    public static void main(String[] argd) {
        sequ();
        System.out.println();
        parallel();
        System.out.println();
        sequ();
        System.out.println();
        parallel();
        System.out.println();
    }
}
