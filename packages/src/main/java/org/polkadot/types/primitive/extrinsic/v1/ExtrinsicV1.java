package org.polkadot.types.primitive.extrinsic.v1;

import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Types;
import org.polkadot.types.Types.IExtrinsicImpl;
import org.polkadot.types.codec.CreateType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.extrinsic.ExtrinsicSignature;
import org.polkadot.types.primitive.extrinsic.Types.ExtrinsicOptions;
import org.polkadot.types.primitive.extrinsic.Types.ExtrinsicSignatureOptions;
import org.polkadot.types.primitive.generic.Call;
import org.polkadot.utils.Utils;


/**
 * @name ExtrinsicV1
 * @description
 * The first generation of compact extrinsics
 */
public class ExtrinsicV1 extends Struct implements IExtrinsicImpl {

    public static final int TRANSACTION_VERSION = 1;

    public static class ExtrinsicValueV1 {
        Call method;
        ExtrinsicSignatureV1 signature;

        public ExtrinsicValueV1(Call method, ExtrinsicSignatureV1 signature) {
            this.method = method;
            this.signature = signature;
        }
    }

    //    public constructor (value?: Uint8Array | ExtrinsicValueV1, { isSigned }: ExtrinsicOptions = {}) {
    public ExtrinsicV1(Object value, ExtrinsicOptions options) {
        super(new Types.ConstructorDef()
                        .add("signature", ExtrinsicSignatureV1.class)
                        .add("method", Call.class)
                , decodeExtrinsic(value, options));
    }

    //  static decodeExtrinsic (value: ExtrinsicValue | AnyU8a | Method): ExtrinsicValue | Array<number> | Uint8Array {
    static Object decodeExtrinsic(Object value, ExtrinsicOptions options) {
        if (value instanceof ExtrinsicV1) {
            return value;
        } else if (Utils.isU8a(value)) {
            // here we decode manually since we need to pull through the version information
            ExtrinsicSignatureV1 signature = new ExtrinsicSignatureV1(value, new ExtrinsicSignatureOptions(options.isSigned));


            Call method = (Call) CreateType.createType("Call",
                    ArrayUtils.subarray((byte[]) value, signature.getEncodedLength(), ((byte[]) value).length)
            );

            return new ExtrinsicValueV1(method, signature);
        }

        return value;
    }

    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        int length = this.length();
        return length + Utils.compactToU8a(length).length;
    }

    /**
     * The length of the encoded value
     */
    public int length() {
        return this.toU8a(true).length;
    }

    /**
     * The {@link Method} this extrinsic wraps
     */
    @Override
    public Call getMethod() {
        return this.getField("method");
    }

    /**
     * The ExtrinsicSignature
     */
    @Override
    public ExtrinsicSignature getSignature() {
        return this.getField("signature");
    }

    @Override
    public int getVersion() {
        return TRANSACTION_VERSION;
    }

    /**
     * Add an ExtrinsicSignature to the extrinsic (already generated)
     */
    //addSignature(signer:Address|Uint8Array, signature:Uint8Array, nonce:AnyNumber, era?:Uint8Array):Extrinsic
    @Override
    public ExtrinsicV1 addSignature(Object signer, byte[] signature, Object nonce, byte[] era) {
        this.getSignature().addSignature(signer, signature, nonce, era);
        return this;
    }

    /**
     * Sign the extrinsic with a specific keypair
     */
    //sign(account:KeyringPair, options:SignatureOptions):Extrinsic
    @Override
    public ExtrinsicV1 sign(KeyringPair account, Types.SignatureOptions options) {
        this.getSignature().sign(this.getMethod(), account, options);
        return this;
    }

    /**
     * Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a());
    }

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        return this.toHex();
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     *               Encodes the value as a Uint8Array as per the parity-codec specifications
     */

    @Override
    public byte[] toU8a(boolean isBare) {
        byte[] encoded = super.toU8a(false);
        return isBare
                ? encoded
                : Utils.compactAddLength(encoded);
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "Extrinsic";
    }
}
