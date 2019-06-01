package org.polkadot.types.type;

import org.polkadot.types.primitive.U32;

/**
 * @name Perbill
 * @description Parts per billion (see also [[Permill]])
 */
// TODO We need to think about the toNumber() and toString() here, so we
// want to multiply by 1_000_000_000 for those purposes?
public class Perbill extends U32 {
    public Perbill(Object value) {
        super(value);
    }
}
