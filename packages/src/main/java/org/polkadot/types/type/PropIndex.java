package org.polkadot.types.type;

import org.polkadot.types.primitive.U32;

/**
 * @name PropIndex
 * @description An increasing number that represents a specific public proposal index in the
 * system, implemented as a [[U32]]
 */
public class PropIndex extends U32 {
    public PropIndex(Object value) {
        super(value);
    }
}
