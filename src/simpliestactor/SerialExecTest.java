package simpliestactor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

public class SerialExecTest {
	Executor executor=Executors.newFixedThreadPool(2);
	CountDownLatch liveTokens;
	
    @Test
    public void serialTest() throws Throwable {
        liveTokens=new CountDownLatch(1);
		Node node=new Node();
        node.post(new Integer(1));
        // wait all the work done
		liveTokens.await();
	}

	class Node extends Actor {
		boolean isRunning=false;

		public Node() {
			super(executor);
		}
		
	    public void post(final Integer t) {
	        super.execute(new Runnable() {
	            public void run() {
	                Assert.assertFalse(isRunning);
	                isRunning=true;
	                act(t);
	                isRunning=false;
	            }
	        });
	    }

		private void act(Integer t) {
            if (t>0) {
                post(t-1);
            } else {
                liveTokens.countDown();
            }
		    try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
	}
}
