package org.polkadot.types.primitive;


/**
 * An 256-bit signed integer
 */
public class I256 extends Int {
    public I256(Object value) {
        super(value, 256, true);
    }
}
