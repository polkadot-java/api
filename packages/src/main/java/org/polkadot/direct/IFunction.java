package org.polkadot.direct;


public interface IFunction<R> {
    default R apply(Object... args) {
        //TODO 2019-05-09 18:54
        throw new UnsupportedOperationException();
    }


    //default Object apply(Object... args) {
    //    //TODO 2019-05-09 10:58
    //    throw new UnsupportedOperationException();
    //}
    //
    //Object applyCb(Types.CodecCallback callback);
    //
    //Object applyCbP1(Object arg0, Types.CodecCallback callback);
    //
    //Object applyCbP2(Object arg0, Object arg1, Types.CodecCallback callback);
    //
    //Object applyCbP3(Object arg0, Object arg1, Object arg2, Types.CodecCallback callback);
}


//export type SubscriptionResult = Promise<() => any>;

//export type CodecResult = Promise<Codec>;

////////////////derive
//interface DeriveMethodPromise<CodecResult, SubscriptionResult> extends DeriveMethodBase<CodecResult, SubscriptionResult> {
//  (callback: CodecCallback): SubscriptionResult;
//  (arg0: CodecArg, callback: CodecCallback): SubscriptionResult;
//  (arg0: CodecArg, arg1: CodecArg, callback: CodecCallback): SubscriptionResult;
//  (arg0: CodecArg, arg1: CodecArg, arg2: CodecArg, callback: CodecCallback): SubscriptionResult;
//}

////////////////tx
//export interface SubmittableExtrinsicFunction<CodecResult, SubscriptionResult> extends MethodFunction {
//  (...params: Array<CodecArg>): SubmittableExtrinsic<CodecResult, SubscriptionResult>;
//}

////////////////rpc
//// checked against max. params in jsonrpc, 1 for subs, 3 without
//export interface DecoratedRpc$Method<CodecResult, SubscriptionResult> {
//  (callback: CodecCallback): SubscriptionResult;
//  (arg1: CodecArg, callback: CodecCallback): SubscriptionResult;
//  (arg1?: CodecArg, arg2?: CodecArg, arg3?: CodecArg): CodecResult;
//}

////////////////query
//export type QueryableStorageFunction<CodecResult, SubscriptionResult> =
//    CodecResult extends Observable<any>
//? QueryableStorageFunctionBase<CodecResult, SubscriptionResult>
//: QueryableStorageFunctionPromise<CodecResult, SubscriptionResult>;

//export interface QueryableStorageFunctionBase<CodecResult, SubscriptionResult> extends StorageFunction {
//  (arg?: CodecArg): CodecResult;
//    at: (hash: Hash | Uint8Array | string, arg?: CodecArg) => CodecResult;
//    hash: (arg?: CodecArg) => HashResult<CodecResult, SubscriptionResult>;
//    key: (arg?: CodecArg) => string;
//    size: (arg?: CodecArg) => U64Result<CodecResult, SubscriptionResult>;
//}

//interface QueryableStorageFunctionPromise<CodecResult, SubscriptionResult> extends QueryableStorageFunctionBase<CodecResult, SubscriptionResult> {
//  (callback: CodecCallback): SubscriptionResult;
//  (arg: CodecArg, callback: CodecCallback): SubscriptionResult;
//}