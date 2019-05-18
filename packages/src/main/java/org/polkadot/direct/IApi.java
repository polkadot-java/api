package org.polkadot.direct;

import org.polkadot.api.ApiBase;
import org.polkadot.common.EventEmitter;
import org.polkadot.rpc.provider.IProvider;

public interface IApi<C, S extends IModule> {

    //genesisHash: Hash;
    //hasSubscriptions: boolean;
    //runtimeMetadata: Metadata;
    //runtimeVersion: RuntimeVersion;
    //derive: Derive<CodecResult, SubscriptionResult>;
    //query: QueryableStorage<CodecResult, SubscriptionResult>;
    //rpc: DecoratedRpc<CodecResult, SubscriptionResult>;
    //tx: SubmittableExtrinsics<CodecResult, SubscriptionResult>;
    //signer?: Signer;
    //readonly type: ApiType;
    //
    //on: (type: ApiInterface$Events, handler: (...args: Array<any>) => any) => this;
    //once: (type: ApiInterface$Events, handler: (...args: Array<any>) => any) => this;

    IModule derive();

    S query();

    IModule rpc();

    IModule tx();

    ApiBase.ApiType getType();

    EventEmitter on(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler);

    EventEmitter once(IProvider.ProviderInterfaceEmitted type, EventEmitter.EventListener handler);
}
