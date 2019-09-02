package org.polkadot.types.primitive;

import org.polkadot.types.codec.U8aFixed;


/**
 * Hash containing 512 bits (64 bytes), typically used for signatures
 */
public class H512 extends U8aFixed {
    public H512(Object value) {
        super(value, 512);
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "H512";
    }
}
