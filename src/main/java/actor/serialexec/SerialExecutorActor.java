package actor.serialexec;

import actor.IActor;

import java.util.concurrent.Executor;

abstract class SerialExecutorActor<T> implements IActor<T> {
    SerialExecutor exec;

    public SerialExecutorActor(Executor executor) {
        exec = new SerialExecutor(executor);
    }

    @Override
    public void post(T msg) {
        exec.execute(()->act(msg));
    }

    protected abstract void act(T msg);
}
