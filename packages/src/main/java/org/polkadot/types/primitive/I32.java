package org.polkadot.types.primitive;

/**
 * @name I32
 * @description An 32-bit signed integer
 */
public class I32 extends Int {
    public I32(Object value) {
        super(value, 32, true);
    }
}