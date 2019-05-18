package org.polkadot.types.primitive;

import org.polkadot.types.Codec;

/**
 * @name Null
 * @description Implements a type that does not contain anything (apart from `null`)
 */
public class Null implements Codec {
    @Override
    public int getEncodedLength() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean eq(Object other) {
        return other instanceof Null || other == null;
    }

    @Override
    public String toHex() {
        return "0x";
    }

    @Override
    public Object toJson() {
        return null;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public byte[] toU8a(boolean isBare) {
        return new byte[0];
    }
}
