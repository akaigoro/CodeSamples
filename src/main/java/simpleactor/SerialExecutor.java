package simpleactor;

import java.util.concurrent.Executor;

/** 
 * Messages are already Runnables.
 */
public class SerialExecutor extends SimpleActor<Runnable> implements Executor {

    public SerialExecutor(Executor executor) {
        super(executor);
    }

    public void execute(Runnable task) {
        post(task);
    }

	@Override
	protected void act(Runnable task) {
		task.run();
	}
}
