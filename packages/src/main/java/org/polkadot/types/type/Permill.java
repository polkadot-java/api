package org.polkadot.types.type;

import org.polkadot.types.primitive.U32;

/**
 * @name Permill
 * @description
 * Parts per million (See also [[Perbill]])
 */
// TODO We need to think about the toNumber() and toString() here, so we
// want to multiply by 1_000_000 for those purposes?
public class Permill extends U32 {
    public Permill(Object value) {
        super(value);
    }
}
