package simpleactor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ActorTest {
	
	Executor executor=Executors.newFixedThreadPool(2);
	CountDownLatch liveTokens;
	AtomicInteger totalPassed=new AtomicInteger(0);
	
    @Test
	public void ringTest() throws Throwable {
		int N=100; // number of Nodes in the ring
		Node[] nodes=new Node[N];

		// make ring of Nodes
		nodes[0]=new Node();
		for (int k=1; k<N; k++) {
			Node n=new Node();
			n.next=nodes[k-1];
			nodes[k]=n;
		}
		nodes[0].next=nodes[N-1];
		
        // start execution
		// pass N tokens to random nodes
        long startTime=System.currentTimeMillis();
		liveTokens=new CountDownLatch(N);
		Random rand=new Random();
		int mCount=10000; // mean time to live
		for (int k=0; k<N; k++) {
			int count=rand.nextInt(2*mCount);
			int nodeIndex=rand.nextInt(N);
			nodes[nodeIndex].post(new Token(count));
		}
		
		// wait all the work done
		liveTokens.await();
		long elapsed=System.currentTimeMillis()-startTime;
		System.out.println(executor.getClass().getSimpleName()+": messages:"+totalPassed.get()+"; time:"+elapsed+" ms");
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
	                totalPassed.incrementAndGet();
	                if (token.value==0) {
	                    liveTokens.countDown();
	                } else {
	                    token.value--;
	                    next.post(token);
	                }
	            }
	        });
	    }
	}
}
