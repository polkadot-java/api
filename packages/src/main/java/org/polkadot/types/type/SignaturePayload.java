package org.polkadot.types.type;

import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.utils.CryptoUtils;

/**
 * @name SignaturePayload
 * A signing payload for an [[Extrinsic]]. For the final encoding, it is variable length based
 * on the conetnts included
 * <p>
 * 8 bytes The Transaction Index/Nonce as provided in the transaction itself.
 * 2+ bytes The Function Descriptor as provided in the transaction itself.
 * 2 bytes The Transaction Era as provided in the transaction itself.
 * 32 bytes The hash of the authoring block implied by the Transaction Era and the current block.
 */
public class SignaturePayload extends Struct {

    public static class SignaturePayloadValue {

        //nonce? AnyNumber,
        //method? Method,
        //era? AnyU8a | ExtrinsicEra
        //blockHash? AnyU8a
        Object nonce;
        Method method;
        ExtrinsicEra era;
        Hash blockHash;
    }


    protected byte[] _signature;

    //constructor (value? SignaturePayloadValue | Uint8Array) {
    public SignaturePayload(Object value) {
        super(new Types.ConstructorDef()
                        .add("nonce", Nonce.class)
                        .add("method", Method.class)
                        .add("era", ExtrinsicEra.class)
                        .add("blockHash", Hash.class),
                value);
    }

    /**
     * `true` if the payload refers to a valid signature
     */
    public boolean isSigned() {
        return this._signature != null && this._signature.length == 64;
    }


    /**
     * The block [[Hash]] the signature applies to (mortal/immortal)
     */
    public Hash getBlockHash() {
        return this.getField("blockHash");
    }

    /**
     * The [[Method]] contained in the payload
     */
    public Method getMethod() {
        return this.getField("method");
    }

    /**
     * The [[ExtrinsicEra]]
     */
    public ExtrinsicEra getEra() {
        return this.getField("era");
    }

    /**
     * The [[Nonce]]
     */
    public Nonce getNonce() {
        return this.getField("nonce");
    }

    /**
     * The raw signature as a `Uint8Array`
     */
    public byte[] getSignature() {
        if (!this.isSigned()) {
            throw new RuntimeException("Transaction is not signed");
        }

        return this._signature;
    }


    /**
     * Sign the payload with the keypair
     */
    public byte[] sign(KeyringPair signerPair, RuntimeVersion version) {

        byte[] u8a = this.toU8a();
        byte[] encoded = u8a.length > 256
                ? CryptoUtils.blake2AsU8a(u8a)
                : u8a;
        this._signature = signerPair.sign(encoded);
        return this._signature;
    }

}
