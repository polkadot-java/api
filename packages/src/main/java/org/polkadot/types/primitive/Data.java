package org.polkadot.types.primitive;

import org.polkadot.types.codec.U8a;

/**
 * @name Data
 * @description A raw data structure. It is just an encoding of a U8a, without any length encoding
 */
public class Data extends U8a {
    public Data(Object value) {
        super(value);
    }
}
