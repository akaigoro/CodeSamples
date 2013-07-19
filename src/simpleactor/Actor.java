package simpleactor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

/** 
 * Actors work asynchronously and in parallel, using a thread pool with limited
 * number of threads, so being a light-weight alternative to Threads.
 * 
 * @author Alexei Kaigorodov
 */
public abstract class Actor implements Runnable {
    private final Executor executor;

    /** current token */
    private Message message=null;
    /** rest of tokens */
    private Queue<Message> queue = new LinkedList<Message>();

    public Actor(Executor executor) {
        this.executor = executor;
    }

    /** 
     * Frontend method which may be called from other Thread or Actor.
     * Saves the message and initiates Actor's execution.
     */
    protected void post(Message message) {
    	if (message==null) {
    		throw new IllegalArgumentException("message may not be null"); 
    	}
        synchronized(this) {
            if (this.message != null) {
                queue.add(message);
                return;
            }
            this.message=message;
        }
        executor.execute(this);
    }

    @Override
    public void run() {
        for (;;) {
            this.message.run();
            synchronized(this) {
                if ((this.message = queue.poll())==null) {
                    return;
                }
            }
        }
    }

    protected abstract class Message {
        protected abstract void run();
    }
}
