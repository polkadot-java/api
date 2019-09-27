package org.polkadot.types.type;

import org.polkadot.types.interfaces.metadata.Types;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.Method;

/**
 * A proposal in the system. It just extends {@link org.polkadot.types.primitive.Method} (Proposal = Call in Rust)
 */
public class Proposal extends Method {
    public Proposal(Object value, Types.FunctionMetadataV7 meta) {
        super(value, meta);
    }

    @Override
    public String toRawType(boolean isBare) {
        return null;
    }

    @Override
    public byte[] toU8a() {
        return new byte[0];
    }
}
