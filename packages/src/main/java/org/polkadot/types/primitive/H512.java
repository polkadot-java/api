package org.polkadot.types.primitive;

import org.polkadot.types.codec.U8aFixed;


/**
 * @name H512
 * @description Hash containing 512 bits (64 bytes), typically used for signatures
 */
public class H512 extends U8aFixed {
    public H512(Object value) {
        super(value, 512);
    }
}
