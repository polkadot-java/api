package org.polkadot.types.primitive;


import org.polkadot.types.codec.UInt;

/**
 * An 16-bit unsigned integer
 */
public class U16 extends UInt {
    public U16(Object value) {
        super(value, 16);
    }

}
