package org.polkadot.direct;

import org.polkadot.api.Types;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.types.type.Hash;

public interface IApi<ApplyResult> {

    Hash getGenesisHash();

    RuntimeVersion getRuntimeVersion();

    Types.Derive<ApplyResult> derive();

    Types.QueryableStorage<ApplyResult> query();

    Types.DecoratedRpc<ApplyResult> rpc();

    Types.SubmittableExtrinsics<ApplyResult> tx();

    Types.Signer getSigner();

}
