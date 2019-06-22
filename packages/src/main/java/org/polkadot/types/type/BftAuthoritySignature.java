package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.codec.Tuple;

/**
 * Represents a Bft Hash and Signature pairing, typically used in reporting
 * network behaviour.
 */
public class BftAuthoritySignature extends Tuple {
    public static class BftAuthoritySignatureValue {
        Object v1;
        Object v2;
    }

    //export type BftAuthoritySignatureValue = [AnyU8a, AnyU8a];
    //  constructor (value?: BftAuthoritySignatureValue | Uint8Array) {
    public BftAuthoritySignature(Object value) {
        super(new Types.ConstructorDef()
                        .add("AuthorityId", AuthorityId.class)
                        .add("Signature", Signature.class)
                , value);
    }


    /**
     * The wrapped AuthoriyId
     */
    public AuthorityId getAuthorityId() {
        return this.getFiled(0);
    }

    /**
     * The wrapped {@link org.polkadot.types.type.Signature} value
     */
    public Signature getSignature() {
        return this.getFiled(1);
    }

    public static class BftHashSignatureValue {
        Object v1;
        Object v2;
    }

    /**
     * Represents a Bft Hash and Signature pairing, typically used in reporting
     * network behaviour.
     */
    public static class BftHashSignature extends Tuple {
        // constructor (value?: BftHashSignatureValue | Uint8Array) {
        public BftHashSignature(Object value) {
            super(new Types.ConstructorDef()
                            .add("Hash", Hash.class)
                            .add("Signature", Signature.class)
                    , value);
        }


        /**
         * The wrapped {@link org.polkadot.types.type.Hash}
         */
        public Hash getHash() {
            return this.getFiled(0);
        }

        /**
         * The wrapped {@link org.polkadot.types.type.Signature}
         */
        public Signature getSignature() {
            return this.getFiled(1);
        }
    }

}
