package simpleactor;

import java.util.Random;
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
	public void ringTest() throws Throwable {
		int N=1000000; // number of Nodes in the ring
		int mCount=10; // mean time to live

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
		System.out.println("creating "+N+" actors::"+elapsed+" ms; throughput:"+N/elapsed+" K actors/sec");

        // start execution
		// pass N tokens to random nodes
		liveTokens=new CountDownLatch(N);
		Random rand=new Random();
		for (int k=0; k<N; k++) {
			int count=rand.nextInt(2*mCount);
			int nodeIndex=rand.nextInt(N);
			nodes[nodeIndex].post(new Token(count));
		}
		
		// wait all the work done
		liveTokens.await();
		elapsed=System.currentTimeMillis()-startTime;
		final int total = totalPassed.get();
		System.out.println(executor.getClass().getSimpleName()+": messages:"+total+
				"; time:"+elapsed+" ms; throughput:"+(total)/elapsed+" K messages/sec");
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
	        super.post(new Message() {
	            protected void run() {
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
