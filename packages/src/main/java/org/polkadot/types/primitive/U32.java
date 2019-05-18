package org.polkadot.types.primitive;


import org.polkadot.types.codec.UInt;

/**
 * @name U32
 * @description An 32-bit unsigned integer
 */
public class U32 extends UInt {
    public U32(Object value) {
        super(value, 32);
    }
}
