package org.polkadot.rpc.rx;

import io.reactivex.Observable;
import org.polkadot.common.EventEmitter;
import org.polkadot.direct.IRpcModule;
import org.polkadot.rpc.provider.IProvider;

import java.util.LinkedHashMap;

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

    /*
    export type RpcRxInterface$Section = {
  [index: string]: RpcRxInterface$Method
};
     */
    interface RpcRxOnCb {
        Object callback(Object... args);
    }

    interface RpcRxInterfaceMethod {
        Observable<Object> call(Object... params);
    }

    class RpcRxInterfaceSection extends LinkedHashMap<String, RpcRxInterfaceMethod> {

    }

    abstract class RpcRxInterface {

        //readonly author: RpcRxInterface$Section;
        //readonly chain: RpcRxInterface$Section;
        //readonly state: RpcRxInterface$Section;
        //readonly system: RpcRxInterface$Section;

        public RpcRxInterfaceSection author;
        public RpcRxInterfaceSection chain;
        public RpcRxInterfaceSection state;
        public RpcRxInterfaceSection system;

        abstract Observable<Boolean> isConnected();

        abstract void on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler);

    }
}
