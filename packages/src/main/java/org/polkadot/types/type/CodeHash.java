package org.polkadot.types.type;

/**
 * @name CodeHash
 * @description
 * The default contract code hash that is used accross the system. It is a
 * wrapper around [[Hash]], representing a 32-byte blake2b (Substrate) value
 */
public class CodeHash extends Hash {
    public CodeHash(Object value) {
        super(value);
    }
}
