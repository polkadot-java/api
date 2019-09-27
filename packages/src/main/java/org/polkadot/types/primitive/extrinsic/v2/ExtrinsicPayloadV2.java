package org.polkadot.types.primitive.extrinsic.v2;

import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.extrinsic.ExtrinsicEra;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.types.type.Balance;
import org.polkadot.types.type.Hash;
import org.polkadot.types.type.Index;
import org.polkadot.utils.UtilsCrypto;

/**
 * @name ExtrinsicPayloadV2
 * @description A signing payload for an [[Extrinsic]]. For the final encoding, it is variable length based
 * on the contents included
 */
public class ExtrinsicPayloadV2 extends Struct {


    protected byte[] _signature;

    //constructor (value? SignaturePayloadValue | Uint8Array) {
    public ExtrinsicPayloadV2(Object value) {
        super(new Types.ConstructorDef()
                        .add("nonce", Compact.with(TypesUtils.getConstructorCodec(Index.class)))
                        .add("method", Bytes.class)
                        .add("era", ExtrinsicEra.class)
                        .add("tip", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
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
     * The {@link Method} contained in the payload
     */
    public Bytes getMethod() {
        return this.getField("method");
    }

    /**
     * The {@link Method} contained in the payload
     */
    public Compact getTip() {
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
