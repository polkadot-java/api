package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.primitive.U64;

public interface AttestedCandidate {


    class CandidateSignature extends Signature {
        public CandidateSignature(Object value) {
            super(value);
        }
    }

    class BalanceUpload extends Tuple {
        public BalanceUpload(Object value) {
            super(
                    new Types.ConstructorDef()
                            .add("AccountId", AccountId.class)
                            .add("U64", U64.class),
                    value
            );
        }

    }

    class EgressQueueRoot extends Tuple.with(

    {
        ParaId, Hash
    })

    {
    }

    class HeadData extends Bytes {
    }

}
