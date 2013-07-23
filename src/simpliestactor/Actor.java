package simpliestactor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

/** 
 * Messages are already Runnables.
 */
public abstract class Actor implements Runnable {
    private final Executor executor;

    /** current token */
    private Runnable message=null;
    /** rest of tokens */
    private final Queue<Runnable> queue = new LinkedList<Runnable>();

    public Actor(Executor executor) {
        this.executor = executor;
    }

    /** 
     * Frontend method which may be called from other Thread or Actor.
     * Saves the message and initiates Actor's execution.
     */
    protected final void execute(Runnable message) {
        if (message==null) {
            throw new IllegalArgumentException("message may not be null"); 
        }
        synchronized(queue) {
            if (this.message != null) {
                queue.add(message);
                return;
            }
            this.message=message;
        }
        executor.execute(this);
    }

    @Override
    public final void run() {
        for (;;) {
            this.message.run();
            synchronized(queue) {
                if ((this.message = queue.poll())==null) {
                    return;
                }
            }
        }
    }
}
