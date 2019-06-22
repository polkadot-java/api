package org.polkadot.types.type;

import org.polkadot.types.primitive.U64;

/**
 * A representation of a Substrate BlockNumber, implemented as a {@link org.polkadot.types.primitive.U64}
 */
public class BlockNumber extends U64 {
    public BlockNumber(Object value) {
        super(value);
    }
}
