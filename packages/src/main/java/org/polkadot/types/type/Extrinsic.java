package org.polkadot.types.type;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.Method;
import org.polkadot.utils.UtilsCrypto;
import org.polkadot.utils.Utils;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * @name Extrinsic
 * Representation of an Extrinsic in the system. It contains the actual call,
 * (optional) signature and encodes with an actual length prefix
 * <p>
 * {@link https://github.com/paritytech/wiki/blob/master/Extrinsic.md#the-extrinsic-format-for-node}.
 * <p>
 * Can be:
 * - signed, to create a transaction
 * - left as is, to create an inherent
 */
public class Extrinsic extends Struct implements Types.IExtrinsic {

    public static class ExtrinsicValue {
        Method method;
        ExtrinsicSignature signature;
    }

    //  constructor (value?: ExtrinsicValue | AnyU8a | Method) {
    public Extrinsic(Object value) {
        super(new Types.ConstructorDef()
                        .add("signature", ExtrinsicSignature.class)
                        .add("method", Method.class)
                , decodeExtrinsic(value));
    }

    //  static decodeExtrinsic (value: ExtrinsicValue | AnyU8a | Method): ExtrinsicValue | Array<number> | Uint8Array {
    static Object decodeExtrinsic(Object value) {
        if (Utils.isU8a(value)) {
            Pair<Integer, BigInteger> pair = Utils.compactFromU8a(value);
            int offset = pair.getKey();
            int length = pair.getValue().intValue();

            return ArrayUtils.subarray((byte[]) value, offset, offset + length);
        } else if (value.getClass().isArray() || Utils.isHex(value)) {
            // Instead of the block below, it should simply be:
            // return Extrinsic.decodeExtrinsic(hexToU8a(value as string));
            byte[] u8a = Utils.u8aToU8a(value);

            // HACK 11 Jan 2019 - before https://github.com/paritytech/substrate/pull/1388
            // extrinsics didn't have the length, cater for both approaches
            Pair<Integer, BigInteger> pair = Utils.compactFromU8a(u8a);
            int offset = pair.getKey();
            int length = pair.getValue().intValue();

            boolean withPrefix = u8a.length == (offset + length);
            return decodeExtrinsic(withPrefix
                    ? u8a
                    : Utils.compactAddLength(u8a));
        } else if (value instanceof Method) {
            LinkedHashMap<Object, Object> values = Maps.newLinkedHashMap();
            values.put("method", value);
            return values;
        }

        return value;
    }


    /**
     * The arguments passed to for the call, exposes args so it is compatible with [[Method]]
     */
    @Override
    public List<Codec> getArgs() {
        return this.getMethod().getArgs();
    }

    /**
     * Thge argument defintions, compatible with [[Method]]
     */
    @Override
    public Types.ConstructorDef getArgsDef() {
        return this.getMethod().getArgsDef();
    }

    /**
     * The actual `[sectionIndex, methodIndex]` as used in the Method
     */
    @Override
    public byte[] getCallIndex() {
        return this.getMethod().getCallIndex();
    }

    /**
     * The actual data for the Method
     */
    @Override
    public byte[] getData() {
        return this.getMethod().getData();
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
     * Convernience function, encodes the extrinsic and returns the actual hash
     */
    @Override
    public U8a getHash() {
        return new Hash(
                UtilsCrypto.blake2AsU8a(this.toU8a(), 256)
        );
    }

    /**
     * `true` is method has `Origin` argument (compatibility with [[Method]])
     */
    @Override
    public boolean hasOrigin() {
        return this.getMethod().hasOrigin();
    }

    /**
     * `true` id the extrinsic is signed
     */
    @Override
    public boolean isSigned() {
        return this.getSignature().isSigned();
    }

    /**
     * The length of the encoded value
     */
    public int length() {
        return this.toU8a(true).length;
    }

    /**
     * The [[FunctionMetadata]] that describes the extrinsic
     */
    @Override
    public Modules.FunctionMetadata getMeta() {
        return this.getMethod().getMeta();
    }

    /**
     * The [[Method]] this extrinsic wraps
     */
    @Override
    public Method getMethod() {
        return this.getField("method");
    }

    /**
     * The [[ExtrinsicSignature]]
     */
    @Override
    public ExtrinsicSignature getSignature() {
        return this.getField("signature");
    }

    /**
     * Add an [[ExtrinsicSignature]] to the extrinsic (already generated)
     */
    //addSignature(signer:Address|Uint8Array, signature:Uint8Array, nonce:AnyNumber, era?:Uint8Array):Extrinsic
    @Override
    public Extrinsic addSignature(Object signer, byte[] signature, Object nonce, byte[] era) {
        this.getSignature().addSignature(signer, signature, nonce, era);
        return this;
    }

    /**
     * Sign the extrinsic with a specific keypair
     */
    //sign(account:KeyringPair, options:SignatureOptions):Extrinsic
    @Override
    public Extrinsic sign(KeyringPair account, Types.SignatureOptions options) {
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

}
