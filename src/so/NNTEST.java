package so;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// http://stackoverflow.com/questions/31870274/executorservice-performance-issues
public class NNTEST implements Callable<Object> {

public long id;
public int m = 100000000;
public double average;
public Random semilla = new Random();

public NNTEST(long sem) {
    this.id = sem;
    semilla.setSeed(sem);
}

@Override
public Object call() throws Exception {
    return doComputation();
}

public double doComputation() {
    System.out.println("id: " + id+" started");
    long t = System.currentTimeMillis();
    for (int j = 0; j < m; j++) {
        average = average + semilla.nextInt();
    }
    System.out.println("id: " + id+" time:"+ (System.currentTimeMillis() - t));
    average = average / m;
    return average;
}

public static void main(String[] args) throws Exception {
    ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<Callable<Object>> list = new ArrayList<>(4);

    for (int i = 0; i < 4; i++) {
        NNTEST tes = new NNTEST(i);
        list.add(tes);
    }

    try {
        long t = System.currentTimeMillis();
        List<Future<Object>> lista = es.invokeAll(list);

        System.out.println("Multi-Threaded lasted: " + (System.currentTimeMillis() - t));
        es.shutdown();
    } catch (Exception e) {
        System.out.println(e);
    }

    long t1 = System.currentTimeMillis();
    for (int j = 0; j < 4; j++) {
        NNTEST ts = new NNTEST(j);
        ts.call();
    }
    System.out.println("Sequential lasted: " + (System.currentTimeMillis() - t1));
    

}
}