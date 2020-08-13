package so.fixedthreadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static void callServer(Runnable dataFetcher, int times) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < times; i++) {
            executorService.submit(dataFetcher);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        callServer(()-> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 50);
        Thread.sleep(5000);
    }
}
