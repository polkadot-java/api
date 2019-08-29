package org.polkadot.types.primitive;

import org.polkadot.types.codec.U8aFixed;

/**
 * Hash containing 160 bits (20 bytes), typically used in blocks, extrinsics and
 * as a sane default for fixed-length hash representations.
 */
public class H160 extends U8aFixed {
    public H160(Object value) {
        super(value, 160);
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "H160";
    }
}
