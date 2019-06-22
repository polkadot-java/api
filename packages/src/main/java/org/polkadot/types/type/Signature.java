package org.polkadot.types.type;

import org.polkadot.types.primitive.H512;

/**
 * The default signature that is used accross the system. It is currectly defined
 * as a 512-bit value, represented by a {@link org.polkadot.types.primitive.H512}.
 */
public class Signature extends H512 {
    public Signature(Object value) {
        super(value);
    }


    /**
     * The default Ed25519 that is used accross the system. It is currectly defined
     * as a 512-bit value, represented by a {@link org.polkadot.types.primitive.H512}.
     */
    public static class Ed25519Signature extends Signature {
        public Ed25519Signature(Object value) {
            super(value);
        }
    }

    /**
     * The default Sr25519 signature that is used accross the system. It is currectly defined
     * as a 512-bit value, represented by a {@link org.polkadot.types.primitive.H512}.
     */
    public static class Sr25519Signature extends Signature {
        public Sr25519Signature(Object value) {
            super(value);
        }
    }
}