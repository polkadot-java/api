package org.polkadot.types.primitive.extrinsic.v2;


import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.U8;
import org.polkadot.types.primitive.extrinsic.ExtrinsicEra;
import org.polkadot.types.primitive.extrinsic.Types.ExtrinsicSignatureOptions;
import org.polkadot.types.primitive.generic.Address;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.types.type.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.polkadot.types.primitive.extrinsic.Constants.EMPTY_U8A;

/**
 * @name ExtrinsicSignature
 * @description A container for the [[Signature]] associated with a specific [[Extrinsic]]
 */
public class ExtrinsicSignatureV2 extends Struct implements Types.IExtrinsicSignature {

    public static final byte[] IMMORTAL_ERA = new byte[]{0};
    public static final int BIT_SIGNED = 0b10000000;
    public static final int BIT_UNSIGNED = 0;
    public static final int BIT_VERSION = 0b0000001;

    // Signature Information.
    //   1/3/5/9/33 bytes: The signing account identity, in Address format
    //   64 bytes: The sr25519/ed25519 signature of the Signing Payload
    //   1-8 bytes: The Compact<Nonce> of the signing account
    //   1/2 bytes: The Transaction Era
    //  public constructor (value?: ExtrinsicSignatureV1 | Uint8Array, { isSigned }: ExtrinsicSignatureOptions = {}) {
    public ExtrinsicSignatureV2(Object value, ExtrinsicSignatureOptions options) {
        super(new Types.ConstructorDef()
                        .add("signer", Address.class)
                        .add("signature", Signature.class)
                        .add("nonce", Compact.with(TypesUtils.getConstructorCodec(Index.class)))
                        .add("tip", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
                        .add("era", ExtrinsicEra.class)
                , decodeExtrinsicSignature(value, options.isSigned));


    }

    //  static decodeExtrinsicSignature (value? Uint8Array) object | Uint8Array {
    public static Object decodeExtrinsicSignature(Object value, boolean isSigned) {
        if (value == null) {
            return EMPTY_U8A;
        } else if (value instanceof ExtrinsicSignatureV2) {
            return value;
        }

        return isSigned
                ? value
                : EMPTY_U8A;
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
     * The ExtrinsicEra (mortal or immortal) this signature applies to
     */
    public ExtrinsicEra getEra() {
        return this.getField("era");
    }


    /**
     * The {@link org.polkadot.types.type.Nonce} for the signature
     */
    public NonceCompact getNonce() {
        return this.getField("nonce");
    }

    /**
     * The actuall {@link Signature} hash
     */
    public Signature getSignature() {
        return this.getField("signature");
    }

    /**
     * The {@link Address} that signed
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

    private ExtrinsicSignatureV2 injectSignature(Signature signature, Address signer, NonceCompact nonce, ExtrinsicEra era) {
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
    ExtrinsicSignatureV2 addSignature(Object _signer, byte[] _signature, Object _nonce, byte[] _era) {
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
    ExtrinsicSignatureV2 sign(Method method, KeyringPair account, Types.SignatureOptions signatureOptions) {


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
     *               Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        if (this.isSigned()) {
            return super.toU8a(isBare);
        } else {
            return new byte[]{(byte) this.version()};
        }
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "Method";
    }
}
