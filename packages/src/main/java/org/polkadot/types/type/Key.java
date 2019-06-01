package org.polkadot.types.type;

import org.polkadot.types.primitive.Bytes;


/**
 * @name Key
 * @description The Substrate Key representation as a [[Bytes]] (`vec<u8>`).
 */
public class Key extends Bytes {
    public Key(Object value) {
        super(value);
    }
}
