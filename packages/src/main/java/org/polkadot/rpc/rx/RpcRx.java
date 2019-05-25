package org.polkadot.rpc.rx;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.polkadot.common.EventEmitter;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.rpc.core.IRpc;
import org.polkadot.rpc.core.RpcCore;
import org.polkadot.rpc.provider.IProvider;

/**
 * RpcRx
 * The RxJS API is a wrapper around the API.
 * It allows wrapping API components with observables using RxJS.
 * <p>
 * **Example**
 * ```java
 * import org.polkadot.rpc.rx.RpcRx;
 * import org.polkadot.rpc.provider.wsWsProvider;
 * <p>
 * WsProvider provider = new WsProvider('http://127.0.0.1:9944');
 * RpcRx api = new RpcRx(provider);
 * ```
 */
public class RpcRx extends Types.RpcRxInterface {

    private RpcCore api;
    private EventEmitter eventEmitter;
    private BehaviorSubject<Boolean> isConnected;

    //protected RpcRxInterfaceSection author;
    //protected RpcRxInterfaceSection chain;
    //protected RpcRxInterfaceSection state;
    //protected RpcRxInterfaceSection system;

    /**
     * @param provider An API provider using HTTP or WebSocket
     */
    public RpcRx(IProvider provider) {
        this(new RpcCore(provider));
    }

    /**
     * @param rpc An API provider using HTTP or WebSocket
     */
    public RpcRx(RpcCore rpc) {
        this.api = rpc;
        this.eventEmitter = new EventEmitter();
        this.isConnected = BehaviorSubject.createDefault(this.api.getProvider().isConnected());

        this.initEmitters(this.api.getProvider());

        this.author = this.createInterface(this.api.author());
        this.chain = this.createInterface(this.api.chain());
        this.state = this.createInterface(this.api.state());
        this.system = this.createInterface(this.api.system());
    }

    private void initEmitters(IProvider provider) {
        provider.on(IProvider.ProviderInterfaceEmitted.connected, value -> {
            RpcRx.this.isConnected.onNext(true);
            RpcRx.this.emit(IProvider.ProviderInterfaceEmitted.connected);
        });

        provider.on(IProvider.ProviderInterfaceEmitted.disconnected, value -> {
            RpcRx.this.isConnected.onNext(false);
            RpcRx.this.emit(IProvider.ProviderInterfaceEmitted.disconnected);
        });

    }


    @Override
    public Observable<Boolean> isConnected() {
        return this.isConnected;
    }

    @Override
    void on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler) {
        this.eventEmitter.on(type, handler);
    }

    protected void emit(IProvider.ProviderInterfaceEmitted type, Object... args) {
        this.eventEmitter.emit(type, args);
    }


    private Types.RpcRxInterfaceSection createInterface(IRpc.RpcInterfaceSection section) {

        Types.RpcRxInterfaceSection ret = new Types.RpcRxInterfaceSection();

        for (String functionName : section.functionNames()) {
            if (functionName.equals("subscribe")
                    || functionName.equals("unsubscribe")) {
                continue;
            }

            ret.put(functionName, this.createObservable(functionName, section));
        }

        return ret;
    }

    private Types.RpcRxInterfaceMethod createObservable(String name, IRpc.RpcInterfaceSection section) {

        IRpcFunction function = section.function(name);

//TODO 2019-05-24 01:11
throw new UnsupportedOperationException();

    }


}
