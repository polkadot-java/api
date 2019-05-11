package org.polkadot.types.codec;


import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.utils.Utils;

import java.math.BigInteger;

/**
 * @name Compact
 * @description A compact length-encoding codec wrapper. It performs the same function as Length, however
 * differs in that it uses a variable number of bytes to do the actual encoding. This is mostly
 * used by other types to add length-prefixed encoding, or in the case of wrapped types, taking
 * a number and making the compact representation thereof
 */
//TODO export default class Compact extends Base<UInt | Moment> implements Codec {
public class Compact extends Base<UInt> implements Codec {

    public Compact(Types.ConstructorCodec<UInt> type, Object value) {
        super(Compact.decodeCompact(type, value));
    }

    static class Builder implements Types.ConstructorCodec<Compact> {
        Types.ConstructorCodec<UInt> type;

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

    public static Types.ConstructorCodec<Compact> with(Types.ConstructorCodec<? extends UInt> type) {
        return new Builder(type);
    }

    static UInt decodeCompact(Types.ConstructorCodec<UInt> type, Object value) {
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

        Pair<Integer, BigInteger> pair = Utils.compactFromU8a(value, type.newInstance(0).bitLength());
        return type.newInstance(pair.getRight());
    }

    /**
     * @description Returns the BN representation of the number
     */
    public BigInteger toBn() {
        return this.raw.toBn();
    }

    @Override
    public int getEncodedLength() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean eq(Object other) {
        return false;
    }

    @Override
    public String toHex() {
        return null;
    }

    @Override
    public Object toJson() {
        return null;
    }

    @Override
    public byte[] toU8a(boolean isBare) {
        return new byte[0];
    }
}
