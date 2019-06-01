package org.polkadot.types.type;


import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.U8;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.utils.Utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @name ExtrinsicSignature
 * A container for the [[Signature]] associated with a specific [[Extrinsic]]
 */
public class ExtrinsicSignature extends Struct implements Types.IExtrinsicSignature {

    public static final byte[] IMMORTAL_ERA = new byte[]{0};
    public static final int BIT_SIGNED = 0b10000000;
    public static final int BIT_UNSIGNED = 0;
    public static final int BIT_VERSION = 0b0000001;

    // Signature Information.
    //   1 byte version BIT_VERSION | (isSigned ? BIT_SIGNED  BIT_UNSIGNED)
    //   1/3/5/9/33 bytes The signing account identity, in Address format
    //   64 bytes The Ed25519 signature of the Signing Payload
    //   8 bytes The Transaction Index of the signing account
    //   1/2 bytes The Transaction Era
    public ExtrinsicSignature(Object value) {
        super(new Types.ConstructorDef()
                        .add("version", U8.class)
                        .add("signer", Address.class)
                        .add("signature", Signature.class)
                        .add("nonce", NonceCompact.class)
                        .add("era", ExtrinsicEra.class)
                , decodeExtrinsicSignature(value));


    }

    //  static decodeExtrinsicSignature (value? Uint8Array) object | Uint8Array {
    static Object decodeExtrinsicSignature(Object _value) {
        Map<String, Byte> ret = new HashMap<>();

        if (_value == null) {
            ret.put("version", (byte) (BIT_VERSION | BIT_UNSIGNED));
            // we always explicitly set the unsigned version
            return ret;
        }

        byte[] value = Utils.u8aToU8a(_value);
        if (value == null) {
            ret.put("version", (byte) (BIT_VERSION | BIT_UNSIGNED));
            // we always explicitly set the unsigned version
            return ret;
        }

        byte version = value[0];
        ret.put("version", version);

        // only decode the full Uint8Array if we have the signed indicator,
        // alternatively only return the version (default for others)
        return (version & BIT_SIGNED) == BIT_SIGNED
                ? value
                : ret;
    }

    @Override
    public int getEncodedLength() {
        return this.isSigned()
                ? super.getEncodedLength()
                : 1;
    }

    @Override
    public boolean isSigned() {
        return (this.version() & BIT_SIGNED) == BIT_SIGNED;
    }

    /**
     * The [[ExtrinsicEra]] (mortal or immortal) this signature applies to
     */
    public ExtrinsicEra getEra() {
        return this.getField("era");
    }


    /**
     * The [[Nonce]] for the signature
     */
    public NonceCompact getNonce() {
        return this.getField("nonce");
    }

    /**
     * The actuall [[Signature]] hash
     */
    public Signature getSignature() {
        return this.getField("signature");
    }

    /**
     * The [[Address]] that signed
     */
    public Address getSigner() {
        return this.getField("signer");
    }

    /**
     * The encoded version for the signature
     */
    public int version() {
        // Version Information.
        // 1 byte: version information:
        // - 7 low bits: version identifier (should be 0b0000001).
        // - 1 high bit: signed flag: 1 if this is a transaction (e.g. contains a signature).
        return ((U8) this.getField("version")).intValue();
    }

    private ExtrinsicSignature injectSignature(Signature signature, Address signer, NonceCompact nonce, ExtrinsicEra era) {
        this.put("era", era);
        this.put("nonce", nonce);
        this.put("signer", signer);
        this.put("signature", signature);
        this.put("version", new U8(BIT_VERSION | BIT_SIGNED));
        return this;
    }


    /**
     * Adds a raw signature
     */
    //addSignature (_signer: Address | Uint8Array, _signature: Uint8Array, _nonce: AnyNumber, _era: Uint8Array = IMMORTAL_ERA): ExtrinsicSignature {
    ExtrinsicSignature addSignature(Object _signer, byte[] _signature, Object _nonce, byte[] _era) {
        Address signer = new Address(_signer);
        NonceCompact nonce = new NonceCompact(_nonce);
        ExtrinsicEra era = new ExtrinsicEra(_era);
        Signature signature = new Signature(_signature);
        return this.injectSignature(signature, signer, nonce, era);
    }


    /**
     * Generate a payload and pplies the signature from a keypair
     */
    //sign (method: Method, account: KeyringPair, { blockHash, era, nonce, version }: SignatureOptions): ExtrinsicSignature {
    ExtrinsicSignature sign(Method method, KeyringPair account, Types.SignatureOptions signatureOptions) {


        Address signer = new Address(account.publicKey());

        Map<String, Object> values = new LinkedHashMap<>();
        values.put("nonce", signatureOptions.getNonce());
        values.put("method", method);
        values.put("era", signatureOptions.getEra() == null ? IMMORTAL_ERA : signatureOptions.getEra());
        values.put("blockHash", signatureOptions.getBlockHash());

        SignaturePayload signingPayload = new SignaturePayload(values);

        Signature signature = new Signature(signingPayload.sign(account, (RuntimeVersion) signatureOptions.getVersion()));

        return this.injectSignature(signature, signer, signingPayload.getNonce(), signingPayload.getEra());
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     * @description Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        if (this.isSigned()) {
            return super.toU8a(isBare);
        } else {
            return new byte[]{(byte) this.version()};
        }
    }
}
