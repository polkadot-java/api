package org.polkadot.types.type;

import org.polkadot.types.primitive.Bytes;


/**
 * The Substrate Key representation as a {@link org.polkadot.types.primitive.Bytes} (`vec<u8>`).
 */
public class Key extends Bytes {
    public Key(Object value) {
        super(value);
    }
}
