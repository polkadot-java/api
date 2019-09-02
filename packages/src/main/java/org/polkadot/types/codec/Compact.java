package org.polkadot.types.codec;


import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.utils.Utils;

import java.math.BigInteger;

/**
 * Compact
 * A compact length-encoding codec wrapper. It performs the same function as Length, however
 * differs in that it uses a variable number of bytes to do the actual encoding. This is mostly
 * used by other types to add length-prefixed encoding, or in the case of wrapped types, taking
 * a number and making the compact representation thereof
 */
//TODO export default class Compact extends Base<UInt | Moment> implements Codec {
public class Compact extends Base<Compactable> implements Codec {

    public Compact(Types.ConstructorCodec<? extends Compactable> type, Object value) {
        super(Compact.decodeCompact(type, value));
    }

    static class Builder implements Types.ConstructorCodec<Compact> {
        Types.ConstructorCodec<Compactable> type;

        Builder(Types.ConstructorCodec type) {
            this.type = type;
        }

        @Override
        public Compact newInstance(Object... values) {
            return new Compact(this.type, values[0]);
        }

        @Override
        public Class<Compact> getTClass() {
            return Compact.class;
        }
    }

    public static Types.ConstructorCodec<Compact> with(Types.ConstructorCodec<? extends Compactable> type) {
        return new Builder(type);
    }

    static Compactable decodeCompact(Types.ConstructorCodec<? extends Compactable> type, Object value) {
        if (value instanceof Compact) {
            return type.newInstance(((Compact) value).raw);
        } else if (value instanceof String) {
            return type.newInstance(
                    Utils.isHex(value, -1, true) ?
                            Utils.hexToBn(value, false, false) : new BigInteger((String) value, 10)
            );
        } else if (value instanceof Number) {
            return type.newInstance(Utils.bnToBn(value));
        }

        //const [, _value] = Compact.decodeU8a(value, new Type(0).bitLength());
        //    return new Type(_value);
        //Utils.compactFromU8a(value, type.newInstance(0).bitLength());
        Pair<Integer, BigInteger> pair = Utils.compactFromU8a(value, type.newInstance(0).bitLength());
        return type.newInstance(pair.getRight());
    }

    /**
     * Returns the BN representation of the number
     */
    public BigInteger toBn() {
        return this.raw.toBn();
    }

    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return this.toU8a().length;
    }

    /**
     * Checks if the value is an empty value
     */
    @Override
    public boolean isEmpty() {
        return this.raw.isEmpty();
    }


    /**
     * Returns the number of bits in the value
     */
    public int bitLength() {
        return this.raw.bitLength();
    }

    /**
     * Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {

        return this.raw.eq(
                other instanceof Compact
                        ? ((Compact) other).raw
                        : other
        );
    }

    /**
     * Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return this.raw.toHex();
    }

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        return this.raw.toJson();
    }

    /**
     * Returns the number representation for the value
     */
    public long toNumber() {
        return this.raw.toNumber();
    }

    /**
     * Returns the string representation of the value
     */
    @Override
    public String toString() {
        return this.raw.toString();
    }

    /**
     * Encodes the value as a Uint8Array as per the parity-codec specifications
     *
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return Utils.compactToU8a(this.raw.toBn());
    }


    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "Compact<" + this.raw.toRawType() + ">";
    }

}
