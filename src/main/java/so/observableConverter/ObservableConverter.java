package so.observableConverter;

import io.reactivex.Observable;
import kotlin.Unit;

public class ObservableConverter {
    public static void main(String[] args) {
        Observable<Unit> u = null;

        io.reactivex.ObservableConverter<Unit, ? extends Observable<Void>> z = new io.reactivex.ObservableConverter<Unit, Observable<Void>>() {
            @Override
            public Observable<Void> apply(Observable<Unit> upstream) {
                return null;
            }
        };
        Observable<Void> v = u.as(z);
        Observable<Void> ov = u.as(unit->null);

    }
}
