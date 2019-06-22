package org.polkadot.types.type;

import org.polkadot.types.primitive.U32;


/**
 * Identifier for a deployed parachain implemented as a {@link org.polkadot.types.primitive.U32}
 */
public class ParaId extends U32 {
    public ParaId(Object value) {
        super(value);
    }
}
