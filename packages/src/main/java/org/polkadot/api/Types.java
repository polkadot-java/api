package org.polkadot.api;

import com.google.common.collect.Maps;
import com.onehilltech.promises.Promise;
import org.polkadot.direct.IFunction;
import org.polkadot.direct.IModule;
import org.polkadot.direct.ISection;
import org.polkadot.types.Types.IExtrinsic;
import org.polkadot.types.Types.SignatureOptions;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.primitive.U64;
import org.polkadot.types.type.Hash;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    abstract class QueryableStorageFunction extends StorageKey.StorageFunction {
        public abstract Promise call(Object... args);

        //  at: (hash: Hash | Uint8Array | string, arg?: CodecArg) => CodecResult;
        public abstract Promise at(Object hash, Object arg);

        public abstract Promise<Hash> hash(Object arg);

        public abstract String key(Object arg);

        public abstract Promise<U64> size(Object arg);

        //public abstract Promise subCall(Object args, CodecCallback callback);

        //(callback: CodecCallback): SubscriptionResult;
        //(arg: CodecArg, callback: CodecCallback): SubscriptionResult;

        //* (arg?: CodecArg): CodecResult;
        //* at: (hash: Hash | Uint8Array | string, arg?: CodecArg) => CodecResult;
        //* hash: (arg?: CodecArg) => HashResult<CodecResult, SubscriptionResult>;
        //* key: (arg?: CodecArg) => string;
        //* size: (arg?: CodecArg) => U64Result<CodecResult, SubscriptionResult>;
    }

    class QueryableModuleStorage extends ISection<QueryableStorageFunction> {
    }

    class QueryableStorage implements IModule<QueryableModuleStorage> {
        Map<String, QueryableModuleStorage> sections = Maps.newLinkedHashMap();

        @Override
        public QueryableModuleStorage section(String section) {
            return sections.get(section);
        }

        @Override
        public Set<String> sectionNames() {
            return sections.keySet();
        }

        public void addSection(String name, QueryableModuleStorage section) {
            this.sections.put(name, section);
        }
    }


    //////////

    //  export interface SubmittableExtrinsicFunction<CodecResult, SubscriptionResult> extends MethodFunction {
    //(...params: Array<CodecArg>): SubmittableExtrinsic<CodecResult, SubscriptionResult>;
    //  }
    //
    //  export interface SubmittableModuleExtrinsics<CodecResult, SubscriptionResult> {
    //[index: string]: SubmittableExtrinsicFunction<CodecResult, SubscriptionResult>;
    //  }
    //
    //  export interface SubmittableExtrinsics<CodecResult, SubscriptionResult> {
    //[index: string]: SubmittableModuleExtrinsics<CodecResult, SubscriptionResult>;
    //  }

    //interface SubmittableExtrinsic<CodecResult, SubscriptionResult> extends IExtrinsic {
    //    send (): SumbitableResultResult<CodecResult, SubscriptionResult>;
    //
    //    send (statusCb: (result: SubmittableResult) => any): SumbitableResultSubscription<CodecResult, SubscriptionResult>;
    //
    //    sign (account: KeyringPair, _options: Partial<SignatureOptions>): this;
    //
    //    signAndSend (account: KeyringPair | string | AccountId | Address, options?: Partial<Partial<SignatureOptions>>): SumbitableResultResult<CodecResult, SubscriptionResult>;
    //
    //    signAndSend (account: KeyringPair | string | AccountId | Address, statusCb: StatusCb): SumbitableResultSubscription<CodecResult, SubscriptionResult>;
    //}
    //


    abstract class SubmittableExtrinsicFunction extends Method.MethodFunction {
        //(...params: Array<CodecArg>): SubmittableExtrinsic<CodecResult, SubscriptionResult>;
        public abstract SubmittableExtrinsic call(Object... params);
    }

    class SubmittableModuleExtrinsics extends ISection<SubmittableExtrinsicFunction> {

    }

    class SubmittableExtrinsics implements IModule<SubmittableModuleExtrinsics> {

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
         * @description Signs an extrinsic, returning an id (>0) that can be used to retrieve updates
         */
        Promise<Integer> sign(IExtrinsic extrinsic, String address, SignatureOptions options);

        /**
         * @description Receives an update for the extrinsic signed by a `signer.sign`
         */
        //update?: (int id, status: Hash | SubmittableResult) => void;

        void update(int id, Object status);

    }

    /*
    export interface DeriveMethodBase<CodecResult, SubscriptionResult> {
  (...params: Array<CodecArg>): CodecResult;
}

interface DeriveMethodPromise<CodecResult, SubscriptionResult> extends DeriveMethodBase<CodecResult, SubscriptionResult> {
  (callback: CodecCallback): SubscriptionResult;
  (arg0: CodecArg, callback: CodecCallback): SubscriptionResult;
  (arg0: CodecArg, arg1: CodecArg, callback: CodecCallback): SubscriptionResult;
  (arg0: CodecArg, arg1: CodecArg, arg2: CodecArg, callback: CodecCallback): SubscriptionResult;
}
     */

    abstract class DeriveMethod implements IFunction {
        public abstract Promise call(Object... params);
        //abstract Promise sendCall(Object... params);

        //abstract Promise subCall(CodecCallback callback);
        //
        //abstract Promise subCall(Object arg0, CodecCallback callback);
        //
        //abstract Promise subCall(Object arg0, Object arg1, CodecCallback callback);
        //
        //abstract Promise subCall(Object arg0, Object arg1, Object arg2, CodecCallback callback);
    }

    class DeriveSection extends ISection<DeriveMethod> {

    }

    class Derive implements IModule<DeriveSection> {
        Map<String, DeriveSection> sectionMap = new HashMap<>();

        @Override
        public DeriveSection section(String section) {
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
}
