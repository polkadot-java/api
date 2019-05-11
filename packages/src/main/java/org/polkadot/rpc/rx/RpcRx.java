package org.polkadot.rpc.rx;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.polkadot.common.EventEmitter;
import org.polkadot.rpc.core.IRpc;
import org.polkadot.rpc.core.RpcCore;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.rx.types.IRpcRx;

import java.util.List;
import java.util.function.Function;

public class RpcRx implements IRpcRx {

    private RpcCore api;
    private EventEmitter eventEmitter;
    private BehaviorSubject<Boolean> isConnected;

    protected RpcRxInterfaceSection author;
    protected RpcRxInterfaceSection chain;
    protected RpcRxInterfaceSection state;
    protected RpcRxInterfaceSection system;

    public RpcRx(IProvider provider) {
        this(new RpcCore(provider));
    }

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

    private RpcRxInterfaceSection createInterface(IRpc.RpcInterfaceSection section) {
        //TODO 2019-05-04 17:56
        throw new UnsupportedOperationException();
    }

    private void initEmitters(IProvider provider) {
        provider.on(IProvider.ProviderInterfaceEmitted.connected, value -> {
            RpcRx.this.isConnected.onNext(true);
            RpcRx.this.emit(IProvider.ProviderInterfaceEmitted.connected, null);
        });

        provider.on(IProvider.ProviderInterfaceEmitted.disconnected, value -> {
            RpcRx.this.isConnected.onNext(false);
            RpcRx.this.emit(IProvider.ProviderInterfaceEmitted.disconnected, null);
        });

    }

    @Override
    public RpcRxInterfaceSection author() {
        return null;
    }

    @Override
    public RpcRxInterfaceSection chain() {
        return null;
    }

    @Override
    public RpcRxInterfaceSection state() {
        return null;
    }

    @Override
    public RpcRxInterfaceSection system() {
        return null;
    }

    @Override
    public Observable<Boolean> isConnected() {
        return null;
    }

    @Override
    public void on(IProvider.ProviderInterfaceEmitted type, Function function) {

    }

    protected void emit(IProvider.ProviderInterfaceEmitted type, List<Object> args) {
        this.eventEmitter.emit(type, args);
    }
}
