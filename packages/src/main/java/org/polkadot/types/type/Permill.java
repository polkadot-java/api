package org.polkadot.types.type;

import org.polkadot.types.primitive.U32;

/**
 * @description
 * Parts per million (See also {@link org.polkadot.types.type.Perbill})
 */
// TODO We need to think about the toNumber() and toString() here, so we
// want to multiply by 1_000_000 for those purposes?
public class Permill extends U32 {
    public Permill(Object value) {
        super(value);
    }
}
