package org.polkadot.types.primitive.extrinsic.v1;

import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.extrinsic.ExtrinsicEra;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.types.type.Hash;
import org.polkadot.types.type.Index;
import org.polkadot.types.type.NonceCompact;
import org.polkadot.utils.UtilsCrypto;

/**
 * @name ExtrinsicPayloadV1
 * @description A signing payload for an [[Extrinsic]]. For the final encoding, it is variable length based
 * on the contents included
 * <p>
 * 1-8 bytes: The Transaction Compact<Index/Nonce> as provided in the transaction itself.
 * 2+ bytes: The Function Descriptor as provided in the transaction itself.
 * 1/2 bytes: The Transaction Era as provided in the transaction itself.
 * 32 bytes: The hash of the authoring block implied by the Transaction Era and the current block.
 */
public class ExtrinsicPayloadV1 extends Struct {

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
    public ExtrinsicPayloadV1(Object value) {
        super(new Types.ConstructorDef()
                        .add("nonce", Compact.with(TypesUtils.getConstructorCodec(Index.class)))
                        .add("method", Bytes.class)
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
     * The block {@link Hash} the signature applies to (mortal/immortal)
     */
    public Hash getBlockHash() {
        return this.getField("blockHash");
    }

    /**
     * The {@link Bytes} contained in the payload
     */
    public Bytes getMethod() {
        return this.getField("method");
    }

    /**
     * The ExtrinsicEra
     */
    public ExtrinsicEra getEra() {
        return this.getField("era");
    }

    /**
     * The {@link org.polkadot.types.type.Nonce}
     */
    public Compact getNonce() {
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
                ? UtilsCrypto.blake2AsU8a(u8a)
                : u8a;
        this._signature = signerPair.sign(encoded);
        return this._signature;
    }

}
