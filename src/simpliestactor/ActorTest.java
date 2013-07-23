package simpliestactor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ActorTest {
	Executor executor=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	CountDownLatch liveTokens;
	AtomicInteger totalPassed=new AtomicInteger(0);
	
    @Test
    public void test1M() throws Throwable {
        ringTest(100, 1000000, 0);
    }

    @Test
    public void test1K() throws Throwable {
        ringTest(1000, 1000, 999);
    }

    @Test
    public void ringTest1() throws Throwable {
        ringTest(1, 1, 1);
    }

    /**
     * @param N number of Nodes in the ring
     * @param mCount mean time to live
     */
    public void ringTest(int N, int T, int mCount) throws Throwable {
        long startTime0=System.currentTimeMillis();
        // make ring of Nodes
		Node[] nodes=new Node[N];
		nodes[0]=new Node();
		for (int k=1; k<N; k++) {
			Node n=new Node();
			n.next=nodes[k-1];
			nodes[k]=n;
		}
		nodes[0].next=nodes[N-1];
        long startTime=System.currentTimeMillis();
		long elapsed = startTime-startTime0;
        System.out.print("creating "+N+" actors::"+elapsed+" ms;");
        if (elapsed==0) {
            System.out.println();
        } else {
            System.out.println("throughput:"+N/elapsed+" K actors/sec");
        }

        // start execution
		// pass N tokens to random nodes
		liveTokens=new CountDownLatch(N);
		for (int k=0; k<T; k++) {
			nodes[k%N].post(new Token(mCount));
		}
		
		// wait all the work done
		liveTokens.await();
		elapsed=System.currentTimeMillis()-startTime;
		final int total = totalPassed.get();
        System.out.print(executor.getClass().getSimpleName()+": messages:"+total+
                "; time:"+elapsed+" ms;");
        if (elapsed==0) {
            System.out.println();
        } else {
            System.out.println(" throughput:"+(total)/elapsed+" K messages/sec");
        }
	}

	static class Token {
		int value;

		public Token(int value) {
			this.value = value;
		}
	}

	class Node extends Actor {
		Node next;

		public Node() {
			super(executor);
		}
		
	    public void post(final Token token) {
	        super.execute(new Runnable() {
	            public void run() {
	                act(token);
	            }
	        });
	    }

		private void act(final Token token) {
			totalPassed.incrementAndGet();
			if (token.value==0) {
			    liveTokens.countDown();
			} else {
			    token.value--;
			    next.post(token);
			}
		}
	}
}
