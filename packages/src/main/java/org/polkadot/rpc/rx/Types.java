package org.polkadot.rpc.rx;

import io.reactivex.Observable;
import org.polkadot.common.EventEmitter;
import org.polkadot.direct.IRpcModule;
import org.polkadot.rpc.provider.IProvider;

public interface Types {

    //interface CallbackCommon {
    //    Object apply(Object... args);
    //}

    interface IRpcRx extends IRpcModule {

        void on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener callback);

        Observable<Boolean> isConnected();
        //isConnected: () => Observable<boolean>
        //on: (type: RpcRxInterface$Events, handler: (...args: Array<any>) => any) => void;
    }
}
