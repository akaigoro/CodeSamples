package actor.cfactor;

import actor.IActor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/** 
 * using CompletableFuture
 *
 */
public abstract class CFActor<T> implements IActor<T> {
    private final Executor executor;

    /** current task */
    private CompletableFuture<Void> active = null;

    public CFActor(Executor executor) {
        this.executor = executor;
    }

    /** 
     * Frontend method which may be called from other Thread or Actor.
     * Saves the message and initiates Actor's execution.
     */
    public void post(T msg) {
        synchronized(this) {
            if (active == null) {
                active=CompletableFuture.runAsync(()->act(msg), executor);
            } else {
                active=active.thenRunAsync(()->act(msg), executor);
            }
        }
    }

    protected abstract void act(T message);
}
