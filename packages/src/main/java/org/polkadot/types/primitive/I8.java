package org.polkadot.types.primitive;

/**
 * @name I8
 * @description An 8-bit signed integer
 */
public class I8 extends Int {
    public I8(Object value) {
        super(value, 8, true);
    }
}
