package org.polkadot.api;

import com.google.common.collect.Maps;
import com.onehilltech.promises.Promise;
import org.polkadot.direct.IModule;
import org.polkadot.direct.ISection;
import org.polkadot.types.Types.CodecCallback;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.primitive.U64;
import org.polkadot.types.type.Hash;

import java.util.List;
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

        public abstract Promise at(Hash hash, Object arg);

        public abstract Promise<Hash> hash(Object arg);

        public abstract String key(Object arg);

        public abstract Promise<U64> size(Object arg);

        public abstract Promise subCall(Object args, CodecCallback callback);

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
        public abstract SubmittableExtrinsic call(List<Object> params);
    }

    class SubmittableModuleExtrinsics extends ISection<SubmittableExtrinsicFunction> {

    }

    class SubmittableExtrinsics implements IModule<SubmittableModuleExtrinsics> {

        @Override
        public SubmittableModuleExtrinsics section(String section) {
            return null;
        }

        @Override
        public Set<String> sectionNames() {
            return null;
        }
    }

}
