package org.polkadot.types.primitive;

/**
 * @name I128
 * @description An 128-bit signed integer
 */
public class I128 extends Int {
    public I128(Object value) {
        super(value, 128, true);
    }
}
