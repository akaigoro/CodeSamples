package simpliestactor;

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

    /** rest of tokens */
    private final Queue<Message> queue = new LinkedList<Message>();

    public Actor(Executor executor) {
        this.executor = executor;
    }

    /** 
     * Frontend method which may be called from other Thread or Actor.
     * Saves the message and initiates Actor's execution.
     */
    protected final void post(Message message) {
        if (message==null) {
            throw new IllegalArgumentException("message may not be null"); 
        }
        synchronized(queue) {
            boolean wasEmpty = queue.isEmpty();
            queue.add(message);
            if (!wasEmpty) {
                return;
            }
        }
        executor.execute(this);
    }

    @Override
    public final void run() {
        for (;;) {
            /** current token */
            Message message;
            synchronized(queue) {
                if ((message = queue.poll())==null) {
                    return;
                }
            }
            message.run();
        }
    }

    protected abstract class Message {
        protected abstract void run();
    }
}
