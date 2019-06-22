package org.polkadot.types.primitive;

import org.polkadot.types.codec.UInt;

/**
 * An 256-bit unsigned integer
 */
public class U256 extends UInt {
    public U256(Object value) {
        super(value, 256);
    }
}
