package so.completableFuture;

import javax.print.attribute.standard.Finishings;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class Tasks {

    public static void main(String ... args) {
        try {
            CompletableFuture<String> task1Future = CompletableFuture.completedFuture("S").thenApply(new Task1());
            CompletableFuture<Integer> result = task1Future.thenApply(new Task2());
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static class Task1 implements Function<String, String>  {

        public Task1() {
            System.out.println("Task 1 started");
        }

        @Override
        public String apply(String s) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result Task 1";
        }
    }

    public static class Task2 implements Function<String, Integer> {

        public Task2() {
            System.out.println("Task 2 started");
        }

        @Override
        public Integer apply(String s) {
            return s.length();
        }
    }
}
