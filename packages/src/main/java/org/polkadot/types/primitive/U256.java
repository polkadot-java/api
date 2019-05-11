package org.polkadot.types.primitive;

import org.polkadot.types.codec.UInt;

public class U256 extends UInt {
    public U256(Object value) {
        super(value, 256);
    }
}
