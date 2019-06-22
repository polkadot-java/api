package org.polkadot.types.primitive;

/**
 * An 128-bit signed integer
 */
public class I128 extends Int {
    public I128(Object value) {
        super(value, 128, true);
    }
}
