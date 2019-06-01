package org.polkadot.api;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onehilltech.promises.Promise;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.api.Types.*;
import org.polkadot.api.derive.Index;
import org.polkadot.api.derive.Types.DeriveRealFunction;
import org.polkadot.api.rx.Types.RxResult;
import org.polkadot.api.types.ApiOptions;
import org.polkadot.api.types.DecoratedRpc;
import org.polkadot.api.types.DecoratedRpc.DecoratedRpcMethod;
import org.polkadot.api.types.Types;
import org.polkadot.api.types.Types.OnCallFunction;
import org.polkadot.common.EventEmitter;
import org.polkadot.common.ReflectionUtils;
import org.polkadot.direct.IApi;
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
import org.polkadot.types.Codec;
import org.polkadot.types.Types.CodecArg;
import org.polkadot.types.Types.CodecCallback;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.CodecUtils;
import org.polkadot.types.codec.Linkage;
import org.polkadot.types.metadata.Metadata;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.Null;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.primitive.U64;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.types.type.Event;
import org.polkadot.types.type.Hash;
import org.polkadot.utils.Utils;

import java.util.List;
import java.util.Map;

public abstract class ApiBase<CodecResult, SubscriptionResult> implements IApi<CodecResult, QueryableStorage> {


    public enum ApiType {
        RX, PROMISE
    }


    //private derive?: Derive<CodecResult, SubscriptionResult>;
    private Derive derive;
    private EventEmitter eventemitter;
    //private _eventemitter: EventEmitter;
    //private _extrinsics?: SubmittableExtrinsics<CodecResult, SubscriptionResult>;
    //private _genesisHash?: Hash;
    private boolean isReady;
    //protected readonly _options: ApiOptions;
    //private _query?: QueryableStorage<CodecResult, SubscriptionResult>;
    private DecoratedRpc<CodecResult, SubscriptionResult> rpc;

    /**
     * @description An external signer which will be used to sign extrinsic when account passed in is not KeyringPair
     */
    public Signer signer;

    protected RpcCore rpcBase;
    protected ApiOptions options = new ApiOptions();
    //protected _rpcBase: RpcBase; // FIXME These two could be merged
    //protected _rpcRx: RpcRx; // FIXME These two could be merged
    protected RpcRx rpcRx;
    //private _runtimeMetadata?: Metadata;
    //private _runtimeVersion?: RuntimeVersion;
    //private _rx: Partial<ApiInterface$Rx> = {};
    //private _type: ApiType;

    /**
     * The type of this API instance, either 'rxjs' or 'promise'
     */
    private ApiType type;


    /**
     * Contains the genesis Hash of the attached chain. Apart from being useful to determine the actual chain, it can also be used to sign immortal transactions.
     */
    public Hash genesisHash;

    /**
     * Yields the current attached runtime metadata. Generally this is only used to construct extrinsics & storage, but is useful for current runtime inspection.
     */
    private Metadata runtimeMetadata;
    /**
     * Contains the version information for the current runtime.
     */
    public RuntimeVersion runtimeVersion;

    private Storage oriStorage;
    private QueryableStorage storage;
    private Method.ModulesWithMethods oriExtrinsics;
    private SubmittableExtrinsics extrinsics;

    /**
     * Create an instance of the class
     *
     * @param provider the Provider instance
     * @param apiType  the type of the API
     *                 <p>
     *                 **Example**
     *                 ```java
     *                 <p>
     *                 import org.polkadot.api.ApiBase;
     *                 <p>
     *                 ApiBase api = new ApiBase();
     *                 <p>
     *                 api.rpc().subscribeNewHead((header) => {
     *                 //System.out.println(`new block #${header.blockNumber.toNumber()}`);
     *                 });
     *                 ```
     */
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
            ApiBase.this.oriExtrinsics = modulesWithMethods;
            ApiBase.this.extrinsics = decorateExtrinsics(modulesWithMethods);

            ApiBase.this.derive = decorateDerive();

            //ApiBase.this.runtimeVersion = (RuntimeVersion) results.get(0);

            // only inject if we are not a clone (global init)
            //if (!this._options.source) {
            Event.injectMetadata(this.runtimeMetadata.asV0());
            Method.injectMethods(modulesWithMethods);
            //}


            this.emit(IProvider.ProviderInterfaceEmitted.ready, this);

            return null;
        })._catch((err) -> {
            err.printStackTrace();
            return null;
        });

    }

    //  private decorateDerive<C, S> (apiRx: ApiInterface$Rx, onCall: OnCallDefinition<C, S>): Derive<C, S> {
    private Derive decorateDerive() {

        Index.Derive derive = Index.decorateDerive(this, this.options.derives);

        Derive apiDerive = new Derive();
        for (String sectionName : derive.sectionNames()) {
            Index.DeriveRealSection section = derive.section(sectionName);

            DeriveSection deriveSection = new DeriveSection();
            for (String functionName : section.functionNames()) {
                DeriveRealFunction function = section.function(functionName);

                DeriveMethod deriveMethod = new DeriveMethod() {

                    @Override
                    public Promise call(Object... _args) {

                        IRpcFunction.SubscribeCallback callback = null;
                        Object[] args = null;
                        if (ArrayUtils.isNotEmpty(_args)
                                && _args[_args.length - 1] instanceof IRpcFunction.SubscribeCallback) {
                            callback = (IRpcFunction.SubscribeCallback) _args[_args.length - 1];
                            if (_args.length == 1) {
                                args = new Object[0];
                            } else {
                                args = ArrayUtils.subarray(_args, 0, _args.length - 1);
                            }
                        } else {
                            args = _args;
                        }
                        //TODO 2019-05-25 02:24
                        Promise call = function.call(args);
                        IRpcFunction.SubscribeCallback finalCallback = callback;
                        return call.then(result -> {
                            finalCallback.callback(result);
                            return Promise.value(result);
                        });
                    }
                };

                deriveSection.addFunction(functionName, deriveMethod);
            }

            apiDerive.addSection(sectionName, deriveSection);
        }
        return apiDerive;
    }


    private SubmittableExtrinsics decorateExtrinsics(Method.ModulesWithMethods extrinsics) {
        SubmittableExtrinsics ret = new SubmittableExtrinsics();

        for (String sectionName : extrinsics.keySet()) {
            Method.Methods section = extrinsics.get(sectionName);

            SubmittableModuleExtrinsics submittableModuleExtrinsics = new SubmittableModuleExtrinsics();

            for (String methodName : section.keySet()) {
                Method.MethodFunction methodFunction = section.get(methodName);

                SubmittableExtrinsicFunction submittableExtrinsicFunction = decorateExtrinsicEntry(methodFunction);
                submittableModuleExtrinsics.addFunction(methodName, submittableExtrinsicFunction);
            }

            ret.addSection(sectionName, submittableModuleExtrinsics);

        }

        return ret;
    }

    private SubmittableExtrinsicFunction decorateExtrinsicEntry(Method.MethodFunction method) {

        SubmittableExtrinsicFunction ret = new SubmittableExtrinsicFunction() {
            @Override
            public Method apply(Object... args) {
                //TODO 2019-05-25 02:38
                throw new UnsupportedOperationException();
            }

            @Override
            public Object toJson() {
                //TODO 2019-05-25 02:38
                throw new UnsupportedOperationException();
            }

            @Override
            public SubmittableExtrinsic call(Object... params) {
                return SubmittableExtrinsic.createSubmittableExtrinsic(ApiBase.this, method.apply(params), null);
            }
        };
        return ret;
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

    /**
     * Derived results that are injected into the API, allowing for combinations of various query results.
     * <p>
     * **Example**
     * ```java
     * api.derive.chain.bestNumber((number) => {
     * //System.out.println('best number', number);
     * });
     * ```
     */
    @Override
    public Derive derive() {
        return this.derive;
    }


    public Storage queryOri() {
        return oriStorage;
    }

    /**
     * Contains all the chain state modules and their subsequent methods in the API. These are attached dynamically from the runtime metadata.
     * <p>
     * All calls inside the namespace, is denoted by `section`.`method` and may take an optional query parameter. As an example, `api.query.timestamp.now()` (current block timestamp) does not take parameters, while `api.query.system.accountNonce(<accountId>)` (retrieving the associated nonce for an account), takes the `AccountId` as a parameter.
     * <p>
     * **Example**
     * <p>
     * ```java
     * api.query.balances.freeBalance(<accountId>, (balance) => {
     * //System.out.println('new balance', balance);
     * });
     * ```
     */
    @Override
    public QueryableStorage query() {
        return storage;
    }

    /**
     * Contains all the raw rpc sections and their subsequent methods in the API as defined by the jsonrpc interface definitions. Unlike the dynamic `api.query` and `api.tx` sections, these methods are fixed (although extensible with node upgrades) and not determined by the runtime.
     * <p>
     * RPC endpoints available here allow for the query of chain, node and system information, in addition to providing interfaces for the raw queries of state (usine known keys) and the submission of transactions.
     * <p>
     * **Example**
     * ```java
     * api.rpc.chain.subscribeNewHead((header) => {
     * //System.out.println('new header', header);
     * });
     * ```
     */
    @Override
    public IRpcModule rpc() {
        return rpcBase;
    }

    /**
     * Contains all the extrinsic modules and their subsequent methods in the API. It allows for the construction of transactions and the submission thereof. These are attached dynamically from the runtime metadata.
     * <p>
     * **Example**
     * ```java
     * api.tx.balances
     * .transfer(<recipientId>, <balance>)
     * .signAndSend(<keyPair>, ({status}) => {
     * //System.out.println('tx status', status.asFinalized.toHex());
     * });
     * ```
     */
    @Override
    public SubmittableExtrinsics tx() {
        return this.extrinsics;
    }

    @Override
    public ApiType getType() {
        return null;
    }

    /**
     * Attach an eventemitter handler to listen to a specific event
     *
     * @param type    The type of event to listen to. Available events are `connected`, `disconnected`, `ready` and `error`
     * @param handler The callback to be called when the event fires. Depending on the event type, it could fire with additional arguments.
     *                <p>
     *                **Example**
     *                ```java
     *                api.on('connected', () => {
     *                //System.out.println('API has been connected to the endpoint');
     *                });
     *                <p>
     *                api.on('disconnected', () => {
     *                //System.out.println('API has been disconnected from the endpoint');
     *                });
     *                ```
     */
    @Override
    public EventEmitter on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler) {
        return this.eventemitter.on(type, handler);
    }

    /**
     * Attach an one-time eventemitter handler to listen to a specific event
     *
     * @param type    The type of event to listen to. Available events are `connected`, `disconnected`, `ready` and `error`
     * @param handler The callback to be called when the event fires. Depending on the event type, it could fire with additional arguments.
     *                <p>
     *                **Example**
     *                ```java
     *                api.once('connected', () => {
     *                //System.out.println('API has been connected to the endpoint');
     *                });
     *                <p>
     *                api.once('disconnected', () => {
     *                //System.out.println('API has been disconnected from the endpoint');
     *                });
     *                ```
     */
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

    private QueryableStorageFunction decorateStorageEntry(StorageKey.StorageFunction storageMethod) {

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
                } else {
                    args = _args;
                }


                if (storageMethod.getHeadKey() != null && args.length == 0) {
                    return ApiBase.this.decorateStorageEntryLinked(storageMethod, callback);
                }

                IRpcModule rpc = ApiBase.this.rpc();
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction subscribeStorage = state.function("subscribeStorage");

                if (callback == null) {
                    return subscribeStorage.invoke(
                            new Object[]{
                                    new Object[]{
                                            new Object[]{storageMethod, args}
                                    }
                            }
                    ).then((result) ->
                            {
                                //System.out.println(result);
                                return Promise.value(((List) result).get(0));
                            }
                    );
                } else {
                    return subscribeStorage.invoke(
                            new Object[]{
                                    new Object[]{
                                            new Object[]{storageMethod, args}
                                    }, callback
                            }
                    );
                }
            }

            @Override
            public Promise at(Object hash, Object arg) {
                IRpcModule rpc = ApiBase.this.rpc();
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction getStorage = state.function("getStorage");
                return getStorage.invoke(new Object[]{storageMethod, arg}, hash);
            }

            @Override
            public Promise<Hash> hash(Object arg) {
                IRpcModule rpc = ApiBase.this.rpc();
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction getStorageHash = state.function("getStorageHash");
                return getStorageHash.invoke(new Object[]{storageMethod, arg});
            }

            @Override
            public String key(Object arg) {
                return Utils.u8aToHex(Utils.compactStripLength(storageMethod.apply(arg)).getRight());
            }

            @Override
            public Promise<U64> size(Object arg) {
                IRpcModule rpc = ApiBase.this.rpc();
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction getStorageSize = state.function("getStorageSize");
                return getStorageSize.invoke(new Object[]{storageMethod, arg});
            }

            //@Override
            //public Promise subCall(Object args, CodecCallback callback) {
            //    return null;
            //}
        };

        return queryableStorageFunction;
    }


    // retrieve a value based on the key, iterating if it has a next entry. Since
    // entries can be re-linked in the middle of a list, we subscribe here to make
    // sure we catch any updates, no matter the list position
    private Promise getNext(Codec head, Codec key, StorageKey.StorageFunction storageMethod) {

        Map<Codec, Pair<Codec, Linkage<Codec>>> result = Maps.newLinkedHashMap();
        //BehaviorSubject<LinkageResult>;
        final Promise[] subject = {null};

        IRpcModule rpc = ApiBase.this.rpc();
        IRpc.RpcInterfaceSection state = rpc.state();

        IRpcFunction subscribeStorage = state.function("subscribeStorage");

        return subscribeStorage.invoke(
                new Object[]{
                        new Object[]{
                                new Object[]{storageMethod, key}
                        }
                }
        ).then((data) -> {
            List<Object> objects = CodecUtils.arrayLikeToList(data);
            objects = CodecUtils.arrayLikeToList(objects.get(0));

            Linkage<Codec> linkage = (Linkage<Codec>) objects.get(1);

            result.put(key, Pair.of((Codec) objects.get(0), (Linkage<Codec>) objects.get(1)));

            // iterate from this key to the children, constructing
            // entries for all those found and available
            if (linkage.getNext().isSome()) {
                return getNext(head, linkage.getNext().unwrap(), storageMethod);
            }

            List<Codec> keys = Lists.newArrayList();
            List<Codec> values = Lists.newArrayList();
            Codec nextKey = head;

            // loop through the results collected, starting at the head an re-creating
            // the list. Our map may have old entries, based on the linking these will
            // not be returned in the final result
            while (nextKey != null) {
                Pair<Codec, Linkage<Codec>> entry = result.get(nextKey);

                if (entry == null) {
                    break;
                }

                Codec item = entry.getLeft();
                Linkage<Codec> linka = entry.getRight();
                keys.add(nextKey);
                values.add(item);

                if (linka.getNext() != null) {
                    nextKey = (Codec) linka.getNext().unwrapOr(null);
                }
            }


            Linkage.LinkageResult nextResult = values.isEmpty()
                    ? new Linkage.LinkageResult(
                    TypesUtils.getConstructorCodec(Null.class), Lists.newArrayList(),
                    TypesUtils.getConstructorCodec(Null.class), Lists.newArrayList())
                    : new Linkage.LinkageResult(
                    TypesUtils.getConstructorCodec(keys.get(0).getClass()), Lists.newArrayList(keys),
                    TypesUtils.getConstructorCodec(values.get(0).getClass()), Lists.newArrayList(values));

            if (subject[0] != null) {
                subject[0].then((r) -> Promise.value(nextResult));
            } else {
                subject[0] = Promise.value(nextResult);
            }
            return subject[0];
        });
    }

    private Promise decorateStorageEntryLinked(StorageKey.StorageFunction storageMethod, Object callback) {

        //Map<Codec, Pair<Codec, Linkage<Codec>>> result = Maps.newLinkedHashMap();


        // this handles the case where the head changes effectively, i.e. a new entry
        // appears at the top of the list, the new getNext gets kicked off

        IRpcModule rpc = ApiBase.this.rpc();
        IRpc.RpcInterfaceSection state = rpc.state();

        IRpcFunction subscribeStorage = state.function("subscribeStorage");


        if (callback == null) {
            return subscribeStorage.invoke(
                    new Object[]{
                            new Object[]{
                                    new Object[]{storageMethod.getHeadKey()}
                            }
                    }
            );
        } else {
            return subscribeStorage.invoke(
                    new Object[]{
                            new Object[]{
                                    new Object[]{storageMethod.getHeadKey()}
                            }, callback
                    }
            );
        }

    }

}