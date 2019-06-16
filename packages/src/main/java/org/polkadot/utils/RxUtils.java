package org.polkadot.utils;

import com.onehilltech.promises.Promise;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class RxUtils {

    public static <T> Observable<T> fromPromise(Promise<T> promise) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                promise.then((result) -> {
                    emitter.onNext(result);
                    emitter.onComplete();
                    return null;
                })._catch(err -> {
                    emitter.onError(err);
                    return null;
                });
            }
        });
    }

    public static <T> Observable<T> fromPromise(Promise<T> promise, ObservableOnSubscribe observableOnSubscribe) {
        return Observable.create(observableOnSubscribe);
    }
}
