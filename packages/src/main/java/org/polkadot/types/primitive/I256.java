package org.polkadot.types.primitive;


/**
 * @name I256
 * @description An 256-bit signed integer
 */
public class I256 extends Int {
    public I256(Object value) {
        super(value, 256, true);
    }
}
