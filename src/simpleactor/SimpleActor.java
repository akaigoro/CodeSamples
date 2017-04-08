package simpleactor;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

/** 
 * Messages are already Runnables.
 */
public abstract class SimpleActor<T> implements Runnable {
    private final Executor executor;
    
    /** current task */
    private T active=null;
    /** rest of tasks */
    private final Queue<T> msgs  = new ArrayDeque<T>();

    public SimpleActor(Executor executor) {
        this.executor = executor;
    }

    /** 
     * Frontend method which may be called from other Thread or Actor.
     * Saves the message and initiates Actor's execution.
     */
    public void post(T msg) {
        if (msg==null) {
            throw new IllegalArgumentException("task may not be null"); 
        }
        synchronized(msgs ) {
            if (active != null) {
                msgs.add(msg);
                return;
            }
            active=msg;
        }
        executor.execute(this);
    }

    @Override
    public final void run() {
        for (;;) {
            act(active);
            synchronized(msgs) {
                if ((active = msgs.poll())==null) {
                    return;
                }
            }
        }
    }
    
    protected abstract void act(T message);
}
