package concurrency_interest;

import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class CurrentExec {

    public static Executor currentExecutor() {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof  ForkJoinWorkerThread) {
            return ((ForkJoinWorkerThread) currentThread).getPool();
        }
        ThreadGroup group = currentThread.getThreadGroup();
        if (group instanceof Executor) {
            return (Executor)group;
        }
        return null;
    }

    static String asyncJob0() {
        return "asyncJob0";
    }

    @Test
    public void test0() throws ExecutionException, InterruptedException {
        Executor exec = ForkJoinPool.commonPool();
        CompletableFuture<String> cf0 = CompletableFuture.supplyAsync(CurrentExec::asyncJob0, exec);
        System.out.println(cf0.get());
    }

    static String asyncJob1() {
        Executor exec = currentExecutor();
        CompletableFuture<String> cf0 = CompletableFuture.supplyAsync(CurrentExec::asyncJob0, exec);
        String part1 = "+asyncJob1";
        try {
            return cf0.get()+part1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        Executor exec = Executors.newFixedThreadPool(2);// ForkJoinPool.commonPool();
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(CurrentExec::asyncJob1, exec);
        System.out.println(cf1.get());
    }

}

