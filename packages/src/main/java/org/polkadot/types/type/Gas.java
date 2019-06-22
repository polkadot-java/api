package org.polkadot.types.type;

import org.polkadot.types.primitive.U64;

/**
 * A gas number type for Substrate, extending {@link org.polkadot.types.primitive.U64}
 */
public class Gas extends U64 {
    public Gas(Object value) {
        super(value);
    }
}
