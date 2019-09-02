package org.polkadot.types.primitive;

import org.polkadot.types.codec.U8aFixed;

/**
 * Hash containing 256 bits (32 bytes), typically used in blocks, extrinsics and
 * as a sane default for fixed-length hash representations.
 */
public class H256 extends U8aFixed {
    //  constructor (value?: AnyU8a) {
    public H256(Object value) {
        super(value, 256);
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType () {
        return "H256";
    }
}
