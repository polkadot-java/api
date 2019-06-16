package org.polkadot.direct;

import com.onehilltech.promises.Promise;

public interface IRpcFunction<T> extends IFunction {

    interface Unsubscribe<T> {
        T unsubscribe();
    }

    interface SubscribeCallback<T> {
        void callback(T t);
    }

    Promise<T> invoke(Object... params);

    default boolean isSubscribe() {
        return false;
    }

    default Promise unsubscribe(int subscriptionId) {
        //TODO 2019-06-11 11:00
        throw new UnsupportedOperationException();
    }

}
