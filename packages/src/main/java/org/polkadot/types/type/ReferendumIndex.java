package org.polkadot.types.type;

import org.polkadot.types.primitive.U32;


/**
 * An increasing number that represents a specific referendum in the system. It
 * is unique per chain. Implemented as {@link org.polkadot.types.primitive.U32}
 */
public class ReferendumIndex extends U32 {
    public ReferendumIndex(Object value) {
        super(value);
    }
}
