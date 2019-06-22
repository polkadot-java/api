package org.polkadot.types.primitive;

import org.polkadot.types.codec.UInt;


/**
 * An 128-bit unsigned integer
 */
public class U128 extends UInt {
    public U128(Object value) {
        super(value, 128);
    }
}
