package org.polkadot.api;


import com.onehilltech.promises.Promise;
import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.api.Types.QueryableModuleStorage;
import org.polkadot.api.Types.QueryableStorage;
import org.polkadot.api.Types.QueryableStorageFunction;
import org.polkadot.api.rx.Types.RxResult;
import org.polkadot.api.types.DecoratedRpc;
import org.polkadot.api.types.DecoratedRpc.DecoratedRpcMethod;
import org.polkadot.api.types.Types;
import org.polkadot.api.types.Types.OnCallFunction;
import org.polkadot.common.EventEmitter;
import org.polkadot.common.ReflectionUtils;
import org.polkadot.direct.IApi;
import org.polkadot.direct.IModule;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.direct.IRpcModule;
import org.polkadot.rpc.core.IRpc;
import org.polkadot.rpc.core.RpcCore;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.rx.RpcRx;
import org.polkadot.rpc.rx.types.IRpcRx;
import org.polkadot.type.storage.FromMetadata;
import org.polkadot.type.storage.Types.ModuleStorage;
import org.polkadot.type.storage.Types.Storage;
import org.polkadot.types.Types.CodecArg;
import org.polkadot.types.Types.CodecCallback;
import org.polkadot.types.metadata.Metadata;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.primitive.U64;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.types.type.Hash;

import java.util.List;
import java.util.Map;

public abstract class ApiBase<CodecResult, SubscriptionResult> implements IApi<CodecResult, QueryableStorage> {


    public enum ApiType {
        RX, PROMISE
    }


    //private _derive?: Derive<CodecResult, SubscriptionResult>;
    private EventEmitter eventemitter;
    //private _eventemitter: EventEmitter;
    //private _extrinsics?: SubmittableExtrinsics<CodecResult, SubscriptionResult>;
    //private _genesisHash?: Hash;
    private boolean isReady;
    //protected readonly _options: ApiOptions;
    //private _query?: QueryableStorage<CodecResult, SubscriptionResult>;
    private DecoratedRpc<CodecResult, SubscriptionResult> rpc;

    protected RpcCore rpcBase;
    //protected _rpcBase: RpcBase; // FIXME These two could be merged
    //protected _rpcRx: RpcRx; // FIXME These two could be merged
    protected RpcRx rpcRx;
    //private _runtimeMetadata?: Metadata;
    //private _runtimeVersion?: RuntimeVersion;
    //private _rx: Partial<ApiInterface$Rx> = {};
    //private _type: ApiType;
    private ApiType type;


    private Hash genesisHash;
    private Metadata runtimeMetadata;
    private RuntimeVersion runtimeVersion;

    private Storage oriStorage;
    private QueryableStorage storage;
    private Method.ModulesWithMethods extrinsics;

    public ApiBase(IProvider provider, ApiType apiType) {


        this.type = apiType;

        final IProvider thisProvider = provider;

        this.rpcBase = new RpcCore(thisProvider);
        this.eventemitter = new EventEmitter();
        //this.rpcRx = new RpcRx(thisProvider);
        //this.rpc = this.decoratedRpc(this.rpcRx, this::onCall);

        this.init();


    }


    protected void emit(IProvider.ProviderInterfaceEmitted type, Object... args) {
        this.eventemitter.emit(type, args);
    }

    private void init() {
        //    let healthTimer: NodeJS.Timeout | null = null;
        this.rpcBase.getProvider().on(IProvider.ProviderInterfaceEmitted.disconnected, (v) -> {
            ApiBase.this.emit(IProvider.ProviderInterfaceEmitted.disconnected);
            //if (healthTimer) {
            //    clearInterval(healthTimer);
            //    healthTimer = null;
            //}
        });


        this.rpcBase.getProvider().on(IProvider.ProviderInterfaceEmitted.error, (error) -> {
            this.emit(IProvider.ProviderInterfaceEmitted.error, error);
        });


        this.rpcBase.getProvider().on(IProvider.ProviderInterfaceEmitted.connected, args -> {
            ApiBase.this.emit(IProvider.ProviderInterfaceEmitted.connected);
            //TODO 2019-05-10 00:01   loadMeta


            //    try {
            //const [hasMeta, cryptoReady] = await Promise.all([
            //                this.loadMeta(),
            //                cryptoWaitReady()
            //]);
            //
            //        if (hasMeta && !this._isReady && cryptoReady) {
            //            this._isReady = true;
            //
            //            this.emit('ready', this);
            //        }
            //
            //        healthTimer = setInterval(() => {
            //                this._rpcRx.system.health().toPromise().catch(() => {
            //                // ignore
            //        });
            //}, KEEPALIVE_INTERVAL);
            //    } catch (error) {
            //        l.error('FATAL: Unable to initialize the API: ', error.message);
            //    }

            //ApiBase.this.emit(IProvider.ProviderInterfaceEmitted.ready, ApiBase.this);
        });

        loadMeta();
    }

    private Promise<Boolean> loadMeta() {

        // only load from on-chain if we are not a clone (default path), alternatively
        // just use the values from the source instance provided
        return Promise.all(
                ApiBase.this.rpc().state().function("getMetadata").invoke(),
                ApiBase.this.rpc().chain().function("getRuntimeVersion").invoke(),
                ApiBase.this.rpc().chain().function("getBlockHash").invoke(0)
                //
                //ApiBase.this.rpc().chain().function("getRuntimeVersion").invoke()
        ).then((results) -> {
            ApiBase.this.runtimeMetadata = (Metadata) results.get(0);
            ApiBase.this.runtimeVersion = (RuntimeVersion) results.get(1);
            ApiBase.this.genesisHash = (Hash) results.get(2);


//    const extrinsics = extrinsicsFromMeta(this.runtimeMetadata.asV0);
            //    const storage = storageFromMeta(this.runtimeMetadata.asV0);
            Method.ModulesWithMethods modulesWithMethods = org.polkadot.type.extrinsics.FromMetadata.fromMetadata(ApiBase.this.runtimeMetadata.asV0());
            Storage storage = FromMetadata.fromMetadata(ApiBase.this.runtimeMetadata.asV0());
            ApiBase.this.oriStorage = storage;
            ApiBase.this.storage = decorateStorage(storage);
            ApiBase.this.extrinsics = modulesWithMethods;


            //ApiBase.this.runtimeVersion = (RuntimeVersion) results.get(0);

            this.emit(IProvider.ProviderInterfaceEmitted.ready, this);

            return null;
        })._catch((err) -> {
            err.printStackTrace();
            return null;
        });

    }

    //protected abstract ApiType getType();

    protected abstract Types.BaseResult onCall(OnCallFunction<RxResult, RxResult> method, List<CodecArg> params, CodecCallback callback, boolean needsCallback);

    //protected abstract onCall (method: OnCallFunction<RxResult, RxResult>, params?: Array<CodecArg>, callback?: CodecCallback, needsCallback?: boolean): CodecResult | SubscriptionResult;


    private <C, S> DecoratedRpc<C, S> decoratedRpc(RpcRx rpc, Types.OnCallDefinition<C, S> onCall) {
        String[] sectionNames = new String[]{"author", "chain", "state", "system"};

        DecoratedRpc decoratedRpc = new DecoratedRpc();
        for (String sectionName : sectionNames) {
            IRpcRx.RpcRxInterfaceSection rxInterfaceSection = ReflectionUtils.getField(rpc, sectionName);

            Map<String, IRpcRx.RpcRxInterfaceMethod> methods = rxInterfaceSection.getMethods();

            for (String methodName : methods.keySet()) {

                // FIXME Find a better way to know if a particular method is a subscription or not
                final boolean needsCallback = methodName.contains("subscribe");


                DecoratedRpcMethod decoratedRpcMethod = new DecoratedRpcMethod() {
                    @Override
                    public Object invoke1(CodecCallback callback) {
                        return null;
                    }

                    @Override
                    public Object invoke2(CodecArg arg1, CodecCallback callback) {
                        return null;
                    }

                    @Override
                    public Object invoke3(CodecArg arg1, CodecArg arg2, CodecArg arg3) {
                        return null;
                    }

                };


            }


        }
        //TODO 2019-05-05 10:09
        return decoratedRpc;
    }

    public DecoratedRpc<CodecResult, SubscriptionResult> getRpc() {
        return rpc;
    }


    public void once(EventEmitter.EventType eventType, EventEmitter.EventListener eventListener) {
        this.eventemitter.once(eventType, eventListener);
    }

    @Override
    public IModule derive() {
        return null;
    }


    public Storage queryOri() {
        return oriStorage;
    }

    @Override
    public QueryableStorage query() {
        return storage;
    }

    @Override
    public IRpcModule rpc() {
        return rpcBase;
    }

    @Override
    public IModule tx() {
        return null;
    }

    @Override
    public ApiType getType() {
        return null;
    }

    @Override
    public EventEmitter on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler) {
        return this.eventemitter.on(type, handler);
    }

    @Override
    public EventEmitter once(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler) {
        return this.eventemitter.once(type, handler);
    }

    private QueryableStorage decorateStorage(Storage storage) {
        QueryableStorage queryableStorage = new QueryableStorage();
        for (String sectionName : storage.sectionNames()) {

            QueryableModuleStorage moduleStorage = new QueryableModuleStorage();

            ModuleStorage section = storage.section(sectionName);
            for (String functionName : section.functionNames()) {
                StorageKey.StorageFunction function = section.function(functionName);
                QueryableStorageFunction storageFunction = decorateStorageEntry(function);

                moduleStorage.addFunction(functionName, storageFunction);
            }
            queryableStorage.addSection(sectionName, moduleStorage);
        }
        return queryableStorage;
    }

    private QueryableStorageFunction decorateStorageEntry(StorageKey.StorageFunction function) {

        // These signatures are allowed and exposed here -
        //   (arg?: CodecArg): CodecResult;
        //   (arg: CodecArg, callback: CodecCallback): SubscriptionResult;
        //   (callback: CodecCallback): SubscriptionResult;

        QueryableStorageFunction queryableStorageFunction = new QueryableStorageFunction() {
            @Override
            public byte[] apply(Object... args) {
                return new byte[0];
            }

            @Override
            public Object toJson() {
                return null;
            }

            @Override
            public Promise call(Object... _args) {


                Object callback = null;
                Object[] args = null;
                if (ArrayUtils.isNotEmpty(_args)
                        && _args[_args.length - 1] instanceof IRpcFunction.SubscribeCallback) {
                    callback = _args[_args.length - 1];
                    if (_args.length == 1) {
                        args = new Object[0];
                    } else {
                        args = ArrayUtils.subarray(_args, 0, _args.length - 1);
                    }
                }


                if (function.getHeadKey() != null && args.length == 0) {
                    //TODO
                    //return this.decorateStorageEntryLinked(method, onCall, callback);
                }



                IRpcModule rpc = ApiBase.this.rpc();
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction subscribeStorage = state.function("subscribeStorage");
                return subscribeStorage.invoke(
                        new Object[]{new Object[]{new Object[]{function, args}}, callback}
                );
            }

            @Override
            public Promise at(Hash hash, Object arg) {
                return null;
            }

            @Override
            public Promise<Hash> hash(Object arg) {
                return null;
            }

            @Override
            public String key(Object arg) {
                return null;
            }

            @Override
            public Promise<U64> size(Object arg) {
                return null;
            }

            @Override
            public Promise subCall(Object args, CodecCallback callback) {
                return null;
            }
        };

        return queryableStorageFunction;
    }

}