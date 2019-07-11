package test.org.polkadot;

import com.google.common.primitives.UnsignedBytes;
import com.onehilltech.promises.Promise;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class TestRx {
    public static void main(String[] args) {


        System.out.println(UnsignedBytes.toInt((byte) -104));


        Observable observable = fromPromise(createPromise());
        observable.subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println(" subscribe receive " + o);
            }
        });
    }

    private static <T> Observable<T> fromPromise(Promise<T> promise) {
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

    private static Promise createPromise() {
        return new Promise((handler) -> {
            try {
                Thread.sleep(500);
                handler.resolve("output");
            } catch (Exception e1) {
                handler.reject(e1);
            }

        });
    }
}
