package org.polkadot.types.primitive;

import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;

/**
 * @name Unconstructable
 * @description A type that should not be constructed
 */
public class Unconstructable extends Null {

    public Unconstructable() {
        super();
        throw new RuntimeException("Unconstructable should not be constructed, it is only a placeholder for compatibility");
    }

    static class Builder<T extends Codec> implements Types.ConstructorCodec<Unconstructable> {

        Builder() {
        }

        @Override
        public Unconstructable newInstance(Object... values) {
            return new Unconstructable();
        }

        @Override
        public Class<Unconstructable> getTClass() {
            return Unconstructable.class;
        }
    }


    public static Types.ConstructorCodec<Struct> with(Object typeDef) {
        return new Unconstructable.Builder();
    }

}
