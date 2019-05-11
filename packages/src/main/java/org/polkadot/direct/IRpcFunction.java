package org.polkadot.direct;

import com.onehilltech.promises.Promise;
import com.onehilltech.promises.PromiseExecutor;
import org.polkadot.types.Codec;

public interface IRpcFunction<T> extends IFunction {
    //

    interface Unsubscribe<T> {
        T unsubscribe();
    }

    interface SubscribeCallback<T> {
        void callback(T t);
    }

    class RpcResult<T> extends Promise<T> {
        public RpcResult(PromiseExecutor<T> impl) {
            super(impl);
        }
    }


    abstract class SubscriptionResult extends RpcResult<Unsubscribe> {
        public SubscriptionResult(PromiseExecutor<Unsubscribe> impl) {
            super(impl);
        }
    }

    abstract class CodecResult extends RpcResult<Codec> {
        public CodecResult(PromiseExecutor<Codec> impl) {
            super(impl);
        }
    }

    Promise<T> invoke(Object... params);


    //
    //abstract class RpcInterfaceMethodNew<T extends IFunction.RpcResult> implements IFunction {
    //    //String subscription;
    //
    //    abstract Promise<T> invoke(Object... params);
    //
    //    //abstract Promise<> unsubscribe(int id);
    //}
}
