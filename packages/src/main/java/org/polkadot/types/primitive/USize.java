package org.polkadot.types.primitive;

/**
 * A System default unsigned number, typically used in RPC to report non-consensus
 * data. It is a wrapper for {@link org.polkadot.types.primitive.U64} as a 64-binary default
 */
public class USize extends U64 {
    public USize(Object value) {
        super(value);
    }
}
