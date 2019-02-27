package so.staticexec;

import kotlin.Unit;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Singleton {
    private final static Lock METHOD_1_LOCK = new ReentrantLock();
    private final static Lock METHOD_2_LOCK = new ReentrantLock();
    static {
        try {
            init();
            Unit u;
        }catch(InterruptedException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void init() throws InterruptedException {
        System.out.println("init 1");
        method1();
        method2();
        /*
        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            System.out.println("about method1");
            method1();
        });
        executorService.submit(() -> {
            System.out.println("about method2");
            method2();
        });

        executorService.submit(Singleton::method1);
        executorService.submit(Singleton::method2);

        executorService.shutdown();
        System.out.println("init 2");
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        System.out.println("init 3");
        */
    }

    public static List<String> method1() {
        System.out.println("method1");
        METHOD_1_LOCK.lock();
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1");
            return Stream.of("b").collect(Collectors.toList());
        }finally {
            METHOD_1_LOCK.unlock();
        }
    }

    public static List<String> method2() {
        System.out.println("method1");
        METHOD_2_LOCK.lock();
        try {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2");
            return Stream.of("a").collect(Collectors.toList());
        }finally {
            METHOD_2_LOCK.unlock();
        }
    }

    private Singleton() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            /*
            try {
                Class.forName(Singleton.class.getName());
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            */
            // working alternative:
             try {
                 Singleton.init();
             }catch(InterruptedException ex) {
                 ex.printStackTrace();
             }
        });
        thread.start();
        thread.join();
    }

}
