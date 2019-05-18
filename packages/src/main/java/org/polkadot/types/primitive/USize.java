package org.polkadot.types.primitive;

/**
 * @name USize
 * @description A System default unsigned number, typically used in RPC to report non-consensus
 * data. It is a wrapper for [[U64]] as a 64-binary default
 */
public class USize extends U64 {
    public USize(Object value) {
        super(value);
    }
}
