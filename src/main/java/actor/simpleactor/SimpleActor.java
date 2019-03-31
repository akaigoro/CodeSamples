package actor.simpleactor;

import actor.IActor;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;


public abstract class SimpleActor<T> implements IActor<T> {
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
        synchronized(this) {
            if (active != null) {
                msgs.add(msg);
                return;
            }
            active=msg;
        }
        executor.execute(this::run);
    }

    private void run() {
        act(active);
        synchronized(this) {
            if ((active = msgs.poll()) == null) {
                return;
            }
        }
        executor.execute(this::run);
    }
    
    protected abstract void act(T message);
}
