package actor;

public interface IActor<T> {
    void post(T msg);
}
