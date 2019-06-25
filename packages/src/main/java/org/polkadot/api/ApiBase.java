package org.polkadot.api;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onehilltech.promises.Promise;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.api.Types.*;
import org.polkadot.api.derive.Index;
import org.polkadot.api.derive.Types.DeriveRealFunction;
import org.polkadot.api.rx.ApiRx;
import org.polkadot.common.EventEmitter;
import org.polkadot.common.ExecutorsManager;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.direct.IRpcModule;
import org.polkadot.rpc.core.IRpc;
import org.polkadot.rpc.core.RpcCore;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.type.storage.FromMetadata;
import org.polkadot.type.storage.Types.ModuleStorage;
import org.polkadot.type.storage.Types.Storage;
import org.polkadot.types.Codec;
import org.polkadot.types.Types.ConstructorCodec;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.CodecUtils;
import org.polkadot.types.codec.Linkage;
import org.polkadot.types.codec.TypeRegistry;
import org.polkadot.types.metadata.Metadata;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.Null;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.types.type.Event;
import org.polkadot.types.type.Hash;
import org.polkadot.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.polkadot.type.extrinsics.FromMetadata.fromMetadata;

public abstract class ApiBase<ApplyResult> implements Types.ApiBaseInterface<ApplyResult> {


    public enum ApiType {
        RX, PROMISE
    }

    public static final int KEEPALIVE_INTERVAL = 15000;


    private Derive derive;
    private EventEmitter eventemitter;
    private boolean isReady;

    /**
     * An external signer which will be used to sign extrinsic when account passed in is not KeyringPair
     */
    public Signer signer;

    public RpcCore rpcBase;

    protected DecoratedRpc<ApplyResult> decoratedRpc;

    protected ApiOptions options = new ApiOptions();
    private ApiInterfacePromiseDefault promisApi = new ApiInterfacePromiseDefault();

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
    private QueryableStorage<ApplyResult> storage;
    private Method.ModulesWithMethods oriExtrinsics;
    private SubmittableExtrinsics extrinsics;

    /**
     * Create an instance of the class
     *
     * @param provider the Provider instance
     * @param apiType  the type of the API
     * 
     * **Example**  
     * ```java
     * import org.polkadot.api.ApiBase;
     * ApiBase api = new ApiBase();
     * api.rpc().subscribeNewHead((header) => {
     *     System.out.println("new block ");
     *     System.out.println(header.blockNumber);
     * });
     * ```
     */
    public ApiBase(IProvider provider, ApiType apiType) {
        this(new ApiOptions(), provider, apiType);
    }


    public ApiBase(ApiOptions options, ApiType apiType) {
        this(options, null, apiType);
    }

    private ApiBase(ApiOptions options, IProvider provider, ApiType apiType) {

        IProvider thisProvider = provider;
        if (options.getSource() != null) {
            thisProvider = options.getSource().rpcBase.getProvider().clone();
        } else if (options.getProvider() != null) {
            thisProvider = options.getProvider();
        }
        this.options = options;

        this.type = apiType;

        this.rpcBase = new RpcCore(thisProvider);
        this.eventemitter = new EventEmitter();
        //this.rpcRx = new RpcRx(thisProvider);
        //this.rpc = this.decoratedRpc(this.rpcRx, this::onCall);
        this.decoratedRpc = decorateRpc(rpcBase, this::onCall);

        this.promisApi.rpc = decorateRpc(rpcBase, this.promiseOnCall);
        this.promisApi.signer = options.getSigner();

        if (options.getSource() != null) {
            this.registerTypes(options.types);
        }

        this.init();
    }


    static class ApiInterfacePromiseDefault implements ApiInterfacePromise {
        Derive<Promise> derive;
        QueryableStorage<Promise> query;
        DecoratedRpc<Promise> rpc;
        SubmittableExtrinsics<Promise> tx;
        Hash genesisHash;
        Metadata runtimeMetadata;
        RuntimeVersion runtimeVersion;
        Signer signer;

        @Override
        public Hash getGenesisHash() {
            return genesisHash;
        }

        @Override
        public RuntimeVersion getRuntimeVersion() {
            return runtimeVersion;
        }

        @Override
        public Derive<Promise> derive() {
            return derive;
        }

        @Override
        public QueryableStorage<Promise> query() {
            return query;
        }

        @Override
        public DecoratedRpc<Promise> rpc() {
            return rpc;
        }

        @Override
        public SubmittableExtrinsics<Promise> tx() {
            return tx;
        }

        @Override
        public Signer getSigner() {
            return signer;
        }

    }

    private OnCallDefinition<Promise> promiseOnCall = new OnCallDefinition<Promise>() {
        @Override
        public Promise apply(OnCallFunction method, List<Object> params, boolean needCallback, IRpcFunction.SubscribeCallback callback) {
            List<Object> args = Lists.newArrayList();
            if (params != null) {
                args.addAll(params);
            }

            if (callback != null) {
                args.add(callback);
            }
            return method.apply(args.toArray(new Object[0]));
        }
    };


    /**
     * Register additional user-defined of chain-specific types in the type registry
     */
    void registerTypes(Map<String, ConstructorCodec> types) {
        if (types != null) {
            TypeRegistry.registerTypes(types);
        }
    }

    protected <ApplyResult> DecoratedRpc<ApplyResult> decorateRpc(RpcCore rpcCore, OnCallDefinition<ApplyResult> onCall) {

        DecoratedRpc ret = new DecoratedRpc<ApplyResult>();
        for (String sectionName : rpcCore.sectionNames()) {

            DecoratedRpcSection decoratedRpcSection = new DecoratedRpcSection<ApplyResult>();

            IRpc.RpcInterfaceSection section = rpcCore.section(sectionName);
            for (String functionName : section.functionNames()) {
                IRpcFunction function = section.function(functionName);


                DecoratedRpcMethod decoratedRpcMethod = new DecoratedRpcMethod<ApplyResult>() {
                    @Override
                    public ApplyResult invoke(Object... params) {
                        IRpcFunction.SubscribeCallback cb = null;
                        List<Object> values = Lists.newArrayList(params);
                        if (function.isSubscribe()) {
                            if (CollectionUtils.isNotEmpty(values)) {
                                Object o = values.get(values.size() - 1);
                                if (o instanceof IRpcFunction.SubscribeCallback) {
                                    Object remove = values.remove(values.size() - 1);
                                    cb = (IRpcFunction.SubscribeCallback) remove;
                                }
                            }
                        }

                        return onCall.apply(function::invoke, values, function.isSubscribe(), cb);
                    }
                };

                decoratedRpcSection.addFunction(functionName, decoratedRpcMethod);
            }

            ret.addSection(sectionName, decoratedRpcSection);
        }

        return ret;
    }


    protected void emit(IProvider.ProviderInterfaceEmitted type, Object... args) {
        this.eventemitter.emit(type, args);
    }

    ScheduledFuture<?> healthTimer = null;

    private void init() {
        //    let healthTimer: NodeJS.Timeout | null = null;
        this.rpcBase.getProvider().on(IProvider.ProviderInterfaceEmitted.disconnected, (v) -> {
            ApiBase.this.emit(IProvider.ProviderInterfaceEmitted.disconnected);
            if (healthTimer != null && !healthTimer.isCancelled() && !healthTimer.isDone()) {
                healthTimer.cancel(false);
                healthTimer = null;
            }
        });


        this.rpcBase.getProvider().on(IProvider.ProviderInterfaceEmitted.error, (error) -> {
            this.emit(IProvider.ProviderInterfaceEmitted.error, error);
        });

        this.rpcBase.getProvider().on(IProvider.ProviderInterfaceEmitted.connected, args -> {
            ApiBase.this.emit(IProvider.ProviderInterfaceEmitted.connected);

        });

        this.loadMeta().then(
                (result) -> {
                    if (result && !this.isReady) {
                        this.isReady = true;
                        this.emit(IProvider.ProviderInterfaceEmitted.ready, this);
                    }

                    healthTimer = ExecutorsManager.schedule(() -> this.rpcBase.system().function("health").invoke(), KEEPALIVE_INTERVAL, TimeUnit.MILLISECONDS);
                    return null;
                }
        )._catch(err -> {
            err.printStackTrace();
            return null;
        });
    }

    private Promise<Boolean> loadMeta() {

        // only load from on-chain if we are not a clone (default path), alternatively
        // just use the values from the source instance provided
        return Promise.all(
                this.options.source == null || !this.options.source.isReady
                        ? new Promise[]{ApiBase.this.rpcBase.state().function("getMetadata").invoke(),
                        ApiBase.this.rpcBase.chain().function("getRuntimeVersion").invoke(),
                        ApiBase.this.rpcBase.chain().function("getBlockHash").invoke(0)}
                        : new Promise[]{
                        Promise.value(this.options.source.runtimeMetadata),
                        Promise.value(this.options.source.runtimeVersion),
                        Promise.value(this.options.source.genesisHash)
                }
        ).then((results) -> {
            ApiBase.this.runtimeMetadata = (Metadata) results.get(0);
            ApiBase.this.runtimeVersion = (RuntimeVersion) results.get(1);
            ApiBase.this.genesisHash = (Hash) results.get(2);

            //    const extrinsics = extrinsicsFromMeta(this.runtimeMetadata.asV0);
            //    const storage = storageFromMeta(this.runtimeMetadata.asV0);
            Method.ModulesWithMethods modulesWithMethods = fromMetadata(ApiBase.this.runtimeMetadata.asV0());
            Storage storage = FromMetadata.fromMetadata(ApiBase.this.runtimeMetadata.asV0());

            ApiBase.this.oriStorage = storage;
            ApiBase.this.storage = decorateStorage(storage, this::onCall);
            ApiBase.this.oriExtrinsics = modulesWithMethods;
            ApiBase.this.extrinsics = decorateExtrinsics(modulesWithMethods, this::onCall);

            ApiBase.this.derive = decorateDerive(this.promisApi, this::onCall);

            this.promisApi.genesisHash = this.genesisHash;
            this.promisApi.runtimeVersion = this.runtimeVersion;
            this.promisApi.query = decorateStorage(storage, this.promiseOnCall);
            this.promisApi.tx = decorateExtrinsics(modulesWithMethods, this.promiseOnCall);
            this.promisApi.derive = decorateDerive(this.promisApi, this.promiseOnCall);

            // only inject if we are not a clone (global init)
            //if (this.options.source != null) {
            Event.injectMetadata(this.runtimeMetadata.asV0());
            Method.injectMethods(modulesWithMethods);
            //}
            //this.emit(IProvider.ProviderInterfaceEmitted.ready, this);

            return Promise.value(true);
        })._catch((err) -> {
            err.printStackTrace();
            return Promise.value(false);
        });
    }

    private Pair<IRpcFunction.SubscribeCallback, Object[]> parseArgs(Object... _args) {
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
        return Pair.of(callback, args);
    }

    private <ApplyResult> Derive<ApplyResult> decorateDerive(ApiInterfacePromise apiInterfacePromise, OnCallDefinition<ApplyResult> onCallDefinition) {

        Index.Derive derive = Index.decorateDerive(apiInterfacePromise, this.options.derives);

        Derive<ApplyResult> apiDerive = new Derive<>();
        for (String sectionName : derive.sectionNames()) {
            Index.DeriveRealSection section = derive.section(sectionName);

            DeriveSection<ApplyResult> deriveSection = new DeriveSection<>();
            for (String functionName : section.functionNames()) {
                DeriveRealFunction function = section.function(functionName);

                DeriveMethod<ApplyResult> deriveMethod = new DeriveMethod<ApplyResult>() {

                    @Override
                    public ApplyResult call(Object... _args) {

                        Pair<IRpcFunction.SubscribeCallback, Object[]> subscribeCallbackPair = parseArgs(_args);

                        IRpcFunction.SubscribeCallback callback = subscribeCallbackPair.getLeft();
                        Object[] args = subscribeCallbackPair.getRight();

                        IRpcFunction.SubscribeCallback finalCallback = callback;

                        return onCallDefinition.apply(
                                new OnCallFunction() {
                                    @Override
                                    public Promise apply(Object... params) {
                                        //TODO 2019-06-16 17:32 just once
                                        Promise call = function.call(params);
                                        return call.then(result -> {
                                            finalCallback.callback(result);
                                            return Promise.value(result);
                                        });
                                    }
                                },
                                //function::call,
                                Lists.newArrayList(args),
                                callback != null,
                                callback);
                    }
                };

                deriveSection.addFunction(functionName, deriveMethod);
            }

            apiDerive.addSection(sectionName, deriveSection);
        }
        return apiDerive;
    }


    private <ApplyResult> SubmittableExtrinsics<ApplyResult> decorateExtrinsics(Method.ModulesWithMethods extrinsics, OnCallDefinition<ApplyResult> onCallDefinition) {
        SubmittableExtrinsics ret = new SubmittableExtrinsics();

        for (String sectionName : extrinsics.keySet()) {
            Method.Methods section = extrinsics.get(sectionName);

            SubmittableModuleExtrinsics submittableModuleExtrinsics = new SubmittableModuleExtrinsics();

            for (String methodName : section.keySet()) {
                Method.MethodFunction methodFunction = section.get(methodName);

                SubmittableExtrinsicFunction submittableExtrinsicFunction = decorateExtrinsicEntry(methodFunction, onCallDefinition);
                submittableModuleExtrinsics.addFunction(methodName, submittableExtrinsicFunction);
            }

            ret.addSection(sectionName, submittableModuleExtrinsics);

        }

        return ret;
    }

    private <ApplyResult> SubmittableExtrinsicFunction<ApplyResult> decorateExtrinsicEntry(Method.MethodFunction method, OnCallDefinition<ApplyResult> onCallDefinition) {

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
                return SubmittableExtrinsic.createSubmittableExtrinsic(promisApi, method.apply(params), null, onCallDefinition);
            }
        };
        return ret;
    }

    protected abstract ApplyResult onCall(OnCallFunction method, List<Object> params, boolean needCallback, IRpcFunction.SubscribeCallback callback);

    /**
     * Derived results that are injected into the API, allowing for combinations of various query results.
     * <p>
     * **Example**
     * ```java
     * api.derive.chain.bestNumber((number) => {
     *     System.out.print("best number ");
     *     System.out.println(number);
     * });
     * ```
     */
    @Override
    public Derive<ApplyResult> derive() {
        return this.derive;
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
     *     System.out.print("new balance ");
     *     System.out.println(balance);
     * });
     * ```
     */
    @Override
    public QueryableStorage<ApplyResult> query() {
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
     *     System.out.print("new header ");
     *     System.out.println(header);
     * });
     * ```
     */
    @Override
    public DecoratedRpc<ApplyResult> rpc() {
        return decoratedRpc;
    }

    /**
     * Contains all the extrinsic modules and their subsequent methods in the API. It allows for the construction of transactions and the submission thereof. These are attached dynamically from the runtime metadata.
     * <p>
     * **Example**
     * ```java
     * api.tx.balances
     * .transfer(<recipientId>, <balance>)
     * .signAndSend(<keyPair>, ({status}) => {
     *     System.out.print("tx status ");
     *     System.out.println(status.asFinalized.toHex());
     * });
     * ```
     */
    @Override
    public SubmittableExtrinsics tx() {
        return this.extrinsics;
    }

    /**
     * The type of this API instance, either 'rxjs' or 'promise'
     */
    @Override
    public ApiType getType() {
        return this.type;
    }

    /**
     * Attach an eventemitter handler to listen to a specific event
     *
     * @param type    The type of event to listen to. Available events are `connected`, `disconnected`, `ready` and `error`
     * @param handler The callback to be called when the event fires. Depending on the event type, it could fire with additional arguments.
     * **Example**  
	 *
     * ```java
     * api.on('connected', () => {
     *     System.out.println("API has been connected to the endpoint");
     * });
     * api.on('disconnected', () => {
     *     System.out.println("API has been disconnected from the endpoint");
     * });
     * ```
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
     * **Example**  
     * ```java
     * api.once('connected', () => {
     *     System.out.println("API has been connected to the endpoint");
     * });
     * api.once('disconnected', () => {
     *     System.out.println("API has been disconnected from the endpoint");
     * });
     * ```
     */
    @Override
    public EventEmitter once(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler) {
        return this.eventemitter.once(type, handler);
    }

    private <ApplyResult> QueryableStorage<ApplyResult> decorateStorage(Storage storage, OnCallDefinition<ApplyResult> onCallDefinition) {
        QueryableStorage<ApplyResult> queryableStorage = new QueryableStorage<>();
        for (String sectionName : storage.sectionNames()) {

            QueryableModuleStorage<ApplyResult> moduleStorage = new QueryableModuleStorage<>();

            ModuleStorage section = storage.section(sectionName);
            for (String functionName : section.functionNames()) {
                StorageKey.StorageFunction function = section.function(functionName);
                QueryableStorageFunction<ApplyResult> storageFunction = decorateStorageEntry(function, onCallDefinition);

                moduleStorage.addFunction(functionName, storageFunction);
            }
            queryableStorage.addSection(sectionName, moduleStorage);
        }
        return queryableStorage;
    }

    private <ApplyResult> QueryableStorageFunction<ApplyResult> decorateStorageEntry(StorageKey.StorageFunction storageMethod, OnCallDefinition<ApplyResult> onCallDefinition) {

        QueryableStorageFunction<ApplyResult> queryableStorageFunction = new QueryableStorageFunction<ApplyResult>() {
            @Override
            public byte[] apply(Object... args) {
                return new byte[0];
            }

            @Override
            public Object toJson() {
                return null;
            }

            @Override
            public ApplyResult call(Object... _args) {
                Pair<IRpcFunction.SubscribeCallback, Object[]> subscribeCallbackPair = parseArgs(_args);
                IRpcFunction.SubscribeCallback callback = subscribeCallbackPair.getLeft();
                Object[] args = subscribeCallbackPair.getRight();

                if (storageMethod.getHeadKey() != null && args.length == 0) {
                    return ApiBase.this.decorateStorageEntryLinked(storageMethod, callback, onCallDefinition);
                }

                IRpcModule rpc = ApiBase.this.rpcBase;
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction subscribeStorage = state.function("subscribeStorage");

                //////////////////////

                IRpcFunction.SubscribeCallback finalCallback = callback;
                IRpcFunction.SubscribeCallback packCallback = callback;
                if (callback != null) {
                    packCallback = new IRpcFunction.SubscribeCallback() {
                        IRpcFunction.SubscribeCallback realCallback = finalCallback;

                        @Override
                        public void callback(Object o) {
                            Object result = ((List) o).get(0);
                            realCallback.callback(result);
                        }
                    };
                }
                return onCallDefinition.apply(
                        new OnCallFunction() {
                            @Override
                            public Promise apply(Object... params) {
                                return subscribeStorage.invoke(
                                        params
                                ).then((result) ->
                                        {
                                            //TODO 2019-06-16 15:29 promise api return first, rx api return list
                                            if (finalCallback == null) {
                                                return Promise.value(((List) result).get(0));
                                            }
                                            IRpcFunction.Unsubscribe<Promise> result1 = (IRpcFunction.Unsubscribe<Promise>) result;
                                            return Promise.value(result1);
                                        }
                                );
                            }
                        },
                        Lists.newArrayList(new Object[]{new Object[]{new Object[]{storageMethod, args}}}),
                        //packCallback != null,
                        ApiBase.this instanceof ApiRx,
                        packCallback
                );
            }

            @Override
            public ApplyResult at(Object hash, Object arg) {
                IRpcModule rpc = ApiBase.this.rpcBase;
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction getStorage = state.function("getStorage");

                return onCallDefinition.apply(
                        new OnCallFunction() {
                            @Override
                            public Promise apply(Object... params) {
                                return getStorage.invoke(new Object[]{storageMethod, params}, hash);
                            }
                        },
                        Lists.newArrayList(arg),
                        false,
                        null
                );
                //return getStorage.invoke(new Object[]{storageMethod, arg}, hash);
            }

            @Override
            public ApplyResult hash(Object arg) {
                IRpcModule rpc = ApiBase.this.rpcBase;
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction getStorageHash = state.function("getStorageHash");

                return onCallDefinition.apply(
                        new OnCallFunction() {
                            @Override
                            public Promise apply(Object... params) {
                                return getStorageHash.invoke(new Object[]{storageMethod, params});
                            }
                        },
                        Lists.newArrayList(arg),
                        false,
                        null
                );
            }

            @Override
            public String key(Object arg) {
                return Utils.u8aToHex(Utils.compactStripLength(storageMethod.apply(arg)).getRight());
            }

            @Override
            public ApplyResult size(Object arg) {
                IRpcModule rpc = ApiBase.this.rpcBase;
                IRpc.RpcInterfaceSection state = rpc.state();
                IRpcFunction getStorageSize = state.function("getStorageSize");

                return onCallDefinition.apply(
                        new OnCallFunction() {
                            @Override
                            public Promise apply(Object... params) {
                                return getStorageSize.invoke(new Object[]{storageMethod, params});
                            }
                        },
                        Lists.newArrayList(arg),
                        false,
                        null
                );
            }

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

        IRpcModule rpc = ApiBase.this.rpcBase;
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

    private <ApplyResult> ApplyResult decorateStorageEntryLinked(StorageKey.StorageFunction storageMethod, IRpcFunction.SubscribeCallback callback, OnCallDefinition<ApplyResult> onCallDefinition) {
        // this handles the case where the head changes effectively, i.e. a new entry
        // appears at the top of the list, the new getNext gets kicked off

        IRpcModule rpc = ApiBase.this.rpcBase;
        IRpc.RpcInterfaceSection state = rpc.state();

        IRpcFunction subscribeStorage = state.function("subscribeStorage");

        AtomicReference<Codec> head = new AtomicReference<>();


        return onCallDefinition.apply(
                new OnCallFunction() {
                    @Override
                    public Promise apply(Object... params) {

                        return subscribeStorage.invoke(
                                new Object[]{
                                        new Object[]{
                                                params
                                        }
                                }
                        ).then(result -> {
                            List<Object> list = CodecUtils.arrayLikeToList(result);
                            if (!list.isEmpty()) {
                                head.set((Codec) list.get(0));
                            }
                            return getNext(head.get(), head.get(), storageMethod);

                        });

                    }
                },
                Lists.newArrayList(storageMethod.getHeadKey()),
                true,
                callback
        );
    }

    /**
     * Contains the genesis Hash of the attached chain. Apart from being useful to determine the actual chain, it can also be used to sign immortal transactions.
     */
    @Override
    public Hash getGenesisHash() {
        return this.genesisHash;
    }

    /**
     * Contains the version information for the current runtime.
     */
    @Override
    public RuntimeVersion getRuntimeVersion() {
        return this.runtimeVersion;
    }

    @Override
    public org.polkadot.api.Types.Signer getSigner() {
        return this.signer;
    }

    /**
     * `true` when subscriptions are supported
     */
    public boolean hasSubscriptions() {
        return this.rpcBase.getProvider().isHasSubscriptions();
    }

    /**
     * Yields the current attached runtime metadata. Generally this is only used to construct extrinsics & storage, but is useful for current runtime inspection.
     */
    public Metadata runtimeMetadata() {
        return this.runtimeMetadata;
    }

    /**
     * Set an external signer which will be used to sign extrinsic when account passed in is not KeyringPair
     */
    public void setSigner(Signer signer) {
        this.promisApi.signer = signer;
    }

    /**
     * Disconnect from the underlying provider, halting all comms
     */
    public void disconnect() {
        this.rpcBase.disconnect();
    }

}