package org.polkadot.rpc.rx.types;

import io.reactivex.Observable;
import org.polkadot.rpc.provider.IProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface IRpcRx {


    //enum A extends IProvider.ProviderInterfaceEmitted {
    //
    //}

    public interface RpcRxInterfaceMethod {
        Observable invoke(List<Object> params);
    }

    abstract class RpcRxInterfaceSection {
        Map<String, RpcRxInterfaceMethod> methods = new HashMap<>();

        public Map<String, RpcRxInterfaceMethod> getMethods() {
            return methods;
        }
    }

    RpcRxInterfaceSection author();

    RpcRxInterfaceSection chain();

    RpcRxInterfaceSection state();

    RpcRxInterfaceSection system();

    Observable<Boolean> isConnected();

    //export type RpcRxInterface$Events = ProviderInterface$Emitted;

    void on(IProvider.ProviderInterfaceEmitted type, Function function);

    //readonly author: RpcRxInterface$Section;
    //readonly chain: RpcRxInterface$Section;
    //readonly state: RpcRxInterface$Section;
    //readonly system: RpcRxInterface$Section;
    //
    //isConnected: () => Observable<boolean>
    //on: (type: RpcRxInterface$Events, handler: (...args: Array<any>) => any) => void;

}
