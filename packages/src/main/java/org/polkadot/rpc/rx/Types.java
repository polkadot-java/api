package org.polkadot.rpc.rx;

import io.reactivex.Observable;
import org.polkadot.common.EventEmitter;
import org.polkadot.direct.IRpcModule;
import org.polkadot.rpc.provider.IProvider;

import java.util.LinkedHashMap;

public interface Types {

    interface IRpcRx extends IRpcModule {
        void on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener callback);
        Observable<Boolean> isConnected();
    }

    interface RpcRxOnCb {
        Object callback(Object... args);
    }

    interface RpcRxInterfaceMethod {
        Observable<Object> call(Object... params);
    }

    class RpcRxInterfaceSection extends LinkedHashMap<String, RpcRxInterfaceMethod> {

    }

    abstract class RpcRxInterface {
        public RpcRxInterfaceSection author;
        public RpcRxInterfaceSection chain;
        public RpcRxInterfaceSection state;
        public RpcRxInterfaceSection system;

        abstract Observable<Boolean> isConnected();

        abstract void on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler);

    }
}
