package org.polkadot.types.type;

import org.polkadot.types.primitive.U32;


/**
 * @name ParaId
 * @description Identifier for a deployed parachain implemented as a [[U32]]
 */
public class ParaId extends U32 {
    public ParaId(Object value) {
        super(value);
    }
}
