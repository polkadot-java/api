package org.polkadot.types.primitive;


/**
 * An 64-bit signed integer
 */
public class I64 extends Int {
    public I64(Object value) {
        super(value, 64, true);
    }
}
