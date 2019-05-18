package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.primitive.H256;

/**
 * @name Hash
 * @description The default hash that is used accross the system. It is basically just a thin
 * wrapper around [[H256]], representing a 32-byte blake2b (Substrate) value
 */
public class Hash extends H256 implements Types.IHash {
    public Hash(Object value) {
        super(value);
    }
}
