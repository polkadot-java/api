package org.polkadot.types.primitive;

/**
 * An 8-bit signed integer
 */
public class I8 extends Int {
    public I8(Object value) {
        super(value, 8, true);
    }
}
