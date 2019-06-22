package org.polkadot.types.primitive;

/**
 * An 32-bit signed integer
 */
public class I32 extends Int {
    public I32(Object value) {
        super(value, 32, true);
    }
}