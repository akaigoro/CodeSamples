package so.reactor;

import org.junit.Test;
import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;

public class PublishOnThreadTest {

    @Test
    public void publishOnThreadTest() throws InterruptedException {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
        CountDownLatch latch = new CountDownLatch(1);

        final Mono<String> mono = Mono.just("Publish on test: \n")
                .map(msg -> msg + "before: " + Thread.currentThread() )
                .publishOn(s)
                .map(msg -> msg + "\nafter: " + Thread.currentThread());

        new Thread(() -> mono.subscribe((String str) ->{
            System.out.println(str);
            latch.countDown();

        })).start();

        latch.await();
    }
}
