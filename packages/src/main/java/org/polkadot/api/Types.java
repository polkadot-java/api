package org.polkadot.api;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.onehilltech.promises.Promise;
import org.polkadot.api.derive.Index;
import org.polkadot.common.EventEmitter;
import org.polkadot.direct.*;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.types.Types.ConstructorCodec;
import org.polkadot.types.Types.IExtrinsic;
import org.polkadot.types.Types.SignatureOptions;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.StorageKey;

import java.util.*;

public interface Types {


    /**
     * export interface QueryableStorageFunctionBase<CodecResult, SubscriptionResult> extends StorageFunction {
     * (arg?: CodecArg): CodecResult;
     * at: (hash: Hash | Uint8Array | string, arg?: CodecArg) => CodecResult;
     * hash: (arg?: CodecArg) => HashResult<CodecResult, SubscriptionResult>;
     * key: (arg?: CodecArg) => string;
     * size: (arg?: CodecArg) => U64Result<CodecResult, SubscriptionResult>;
     * }
     * <p>
     * interface QueryableStorageFunctionPromise<CodecResult, SubscriptionResult> extends QueryableStorageFunctionBase<CodecResult, SubscriptionResult> {
     * (callback: CodecCallback): SubscriptionResult;
     * (arg: CodecArg, callback: CodecCallback): SubscriptionResult;
     * }
     * <p>
     * export type QueryableStorageFunction<CodecResult, SubscriptionResult> =
     * CodecResult extends Observable<any>
     * ? QueryableStorageFunctionBase<CodecResult, SubscriptionResult>
     * : QueryableStorageFunctionPromise<CodecResult, SubscriptionResult>;
     * <p>
     * export interface QueryableModuleStorage<CodecResult, SubscriptionResult> {
     * [index: string]: QueryableStorageFunction<CodecResult, SubscriptionResult>;
     * }
     * <p>
     * export interface QueryableStorage<CodecResult, SubscriptionResult> {
     * [index: string]: QueryableModuleStorage<CodecResult, SubscriptionResult>;
     * }
     */

    abstract class QueryableStorageFunction<ApplyResult> extends StorageKey.StorageFunction {
        public abstract ApplyResult call(Object... args);

        //  at: (hash: Hash | Uint8Array | string, arg?: CodecArg) => CodecResult;
        public abstract ApplyResult at(Object hash, Object arg);

        public abstract ApplyResult hash(Object arg);
        //public abstract Promise<Hash> hash(Object arg);

        public abstract String key(Object arg);

        public abstract ApplyResult size(Object arg);
    }

    class QueryableModuleStorage<ApplyResult> extends ISection<QueryableStorageFunction<ApplyResult>> {
    }

    class QueryableStorage<ApplyResult> implements IModule<QueryableModuleStorage<ApplyResult>> {
        Map<String, QueryableModuleStorage<ApplyResult>> sections = Maps.newLinkedHashMap();

        @Override
        public QueryableModuleStorage<ApplyResult> section(String section) {
            return sections.get(section);
        }

        @Override
        public Set<String> sectionNames() {
            return sections.keySet();
        }

        @Override
        public void addSection(String name, QueryableModuleStorage<ApplyResult> section) {
            this.sections.put(name, section);
        }
    }

    abstract class SubmittableExtrinsicFunction<ApplyResult> extends Method.MethodFunction {
        //(...params: Array<CodecArg>): SubmittableExtrinsic<CodecResult, SubscriptionResult>;
        public abstract SubmittableExtrinsic<ApplyResult> call(Object... params);
    }

    class SubmittableModuleExtrinsics extends ISection<SubmittableExtrinsicFunction> {

    }

    class SubmittableExtrinsics<ApplyResult> implements IModule<SubmittableModuleExtrinsics> {

        Map<String, SubmittableModuleExtrinsics> sections = new LinkedHashMap<>();

        @Override
        public SubmittableModuleExtrinsics section(String section) {
            return sections.get(section);
        }

        @Override
        public Set<String> sectionNames() {
            return sections.keySet();
        }

        @Override
        public void addSection(String sectionName, SubmittableModuleExtrinsics section) {
            sections.put(sectionName, section);
        }
    }


    interface Signer {
        /**
         * Signs an extrinsic, returning an id (>0) that can be used to retrieve updates
         */
        Promise<Integer> sign(IExtrinsic extrinsic, String address, SignatureOptions options);

        /**
         * Receives an update for the extrinsic signed by a `signer.sign`
         */
        //update?: (int id, status: Hash | SubmittableResult) => void;

        void update(int id, Object status);

    }

    abstract class DeriveMethod<ApplyResult> implements IFunction {
        public abstract ApplyResult call(Object... params);
    }

    class DeriveSection<ApplyResult> extends ISection<DeriveMethod<ApplyResult>> {

    }

    class Derive<ApplyResult> implements IModule<DeriveSection> {
        Map<String, DeriveSection<ApplyResult>> sectionMap = new HashMap<>();

        @Override
        public DeriveSection<ApplyResult> section(String section) {
            return sectionMap.get(section);
        }

        @Override
        public Set<String> sectionNames() {
            return sectionMap.keySet();
        }

        @Override
        public void addSection(String sectionName, DeriveSection section) {
            this.sectionMap.put(sectionName, section);
        }
    }


    interface ApiBaseInterface<ApplyResult> extends IApi<ApplyResult> {

        ApiBase.ApiType getType();

        EventEmitter on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler);

        EventEmitter once(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler);
    }

    interface ApiInterfacePromise extends IApi<Promise> {

    }

    class ApiOptions {
        /**
         * Add custom derives to be injected
         */
        Index.DeriveCustom derives;

        /**
         * Transport Provider from rpc-provider. If not specified, it will default to
         * connecting to a WsProvider connecting localhost with the default port, i.e. `ws://127.0.0.1:9944`
         */
        IProvider provider;

        /**
         * An external signer which will be used to sign extrinsic when account passed in is not KeyringPair
         */
        Signer signer;
        /**
         * The source object to use for runtime information (only used when cloning)
         */
        ApiBase<?> source;
        /**
         * Additional types used by runtime modules. This is nessusary if the runtime modules
         * uses types not available in the base Substrate runtime.
         */
        Map<String, ConstructorCodec> types;

        public Index.DeriveCustom getDerives() {
            return derives;
        }

        public void setDerives(Index.DeriveCustom derives) {
            this.derives = derives;
        }

        public IProvider getProvider() {
            return provider;
        }

        public void setProvider(IProvider provider) {
            this.provider = provider;
        }

        public Signer getSigner() {
            return signer;
        }

        public void setSigner(Signer signer) {
            this.signer = signer;
        }

        public ApiBase<?> getSource() {
            return source;
        }

        public void setSource(ApiBase<?> source) {
            this.source = source;
        }

        public Map<String, ConstructorCodec> getTypes() {
            return types;
        }

        public void setTypes(Map<String, ConstructorCodec> types) {
            this.types = types;
        }
    }


    interface OnCallDefinition<ApplyResult> {
        ApplyResult apply(OnCallFunction<ApplyResult> method, List<Object> params, boolean needCallback, IRpcFunction.SubscribeCallback callback);
    }

    interface OnCallFunction<ApplyResult> {
        ApplyResult apply(Object... params);
    }

    class DecoratedRpc<ApplyResult> implements IModule<DecoratedRpcSection<ApplyResult>> {

        Map<String, DecoratedRpcSection> sectionMap = Maps.newLinkedHashMap();

        @Override
        public DecoratedRpcSection section(String section) {
            return sectionMap.get(section);
        }

        @Override
        public Set<String> sectionNames() {
            return Sets.newHashSet("author", "chain", "state", "system");
        }


        @Override
        public void addSection(String sectionName, DecoratedRpcSection section) {
            this.sectionMap.put(sectionName, section);
        }

        public DecoratedRpcSection<ApplyResult> author() {
            return this.section("author");
        }

        public DecoratedRpcSection<ApplyResult> chain() {
            return this.section("chain");
        }

        public DecoratedRpcSection<ApplyResult> state() {
            return this.section("state");
        }

        public DecoratedRpcSection<ApplyResult> system() {
            return this.section("system");
        }
    }

    class DecoratedRpcSection<ApplyResult> extends ISection<DecoratedRpcMethod<ApplyResult>> {
        Map<String, DecoratedRpcMethod> methodMap = new HashMap<>();

        public DecoratedRpcMethod getMethod(String methodName) {
            return methodMap.get(methodName);
        }
    }


    // checked against max. params in jsonrpc, 1 for subs, 3 without
    abstract class DecoratedRpcMethod<ApplyResult> implements IFunction {
        public abstract ApplyResult invoke(Object... params);
    }


}
