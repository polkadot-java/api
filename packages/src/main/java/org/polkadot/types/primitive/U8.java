package org.polkadot.types.primitive;

import org.polkadot.types.codec.UInt;

/**
 * @name U8
 * @description An 8-bit unsigned integer
 */
public class U8 extends UInt {
    public U8(Object value) {
        super(value, 8);
    }
}
