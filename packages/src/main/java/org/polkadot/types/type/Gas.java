package org.polkadot.types.type;

import org.polkadot.types.primitive.U64;

/**
 * @name Gas
 * @description A gas number type for Substrate, extending [[U64]]
 */
public class Gas extends U64 {
    public Gas(Object value) {
        super(value);
    }
}
