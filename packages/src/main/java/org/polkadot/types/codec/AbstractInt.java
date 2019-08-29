package org.polkadot.types.codec;

import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public abstract class AbstractInt extends BigInteger implements Codec {
    private static final Logger logger = LoggerFactory.getLogger(AbstractInt.class);

    private int bitLength;
    private boolean isHexJson;
    private boolean isNegative;

    public AbstractInt(boolean isNegative, Object value, int bitLength, boolean isHexJson) {
        super(decodeAbstractInt(value, bitLength, isNegative));

        this.bitLength = bitLength;
        this.isHexJson = isHexJson;
        this.isNegative = isNegative;

    }

    //BN | Uint8Array | number | string;
    static String decodeAbstractInt(Object value, int bitLength, boolean isNegative) {
        if (Utils.isHex(value)) {
            return Utils.hexToBn(value, false, isNegative).toString();
        } else if (Utils.isU8a(value)) {
            // NOTE When passing u8a in (typically from decoded data), it is always LE
            try {
                byte[] subarray = ArrayUtils.subarray(Utils.u8aToU8a(value), 0, bitLength / 8);
                //TODO return u8aToBn(value.subarray(0, bitLength / 8), { isLe: true, isNegative }).toString();
                return Utils.u8aToBn(subarray, true, isNegative).toString();
            } catch (Exception e) {
                logger.error("AbstractInt value decoding failed {}", value, e);
                return "0";
            }
        } else if (value instanceof String) {
            return new BigInteger((String) value, 10).toString();
        }
        return Utils.bnToBn(value).toString();
    }

    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return this.bitLength / 8;
    }

    /**
     * Checks if the value is a zero value (align elsewhere)
     */
    @Override
    public boolean isEmpty() {
        return this.equals(ZERO);
    }

    /**
     * Returns the number of bits in the value
     */
    @Override
    public int bitLength() {
        return this.bitLength;
    }

    /**
     * Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {
        // Here we are actually overriding the built-in .eq to take care of both
        // number and BN inputs (no `.eqn` needed) - numbers will be converted
        //    return super.eq(
        //            isHex(other)
        //                    ? hexToBn(other.toString(), { isLe: false, isNegative: this._isNegative })
        //    : bnToBn(other)
        //);
        BigInteger bigInteger = Utils.isHex(other)
                ? Utils.hexToBn(other, false, this.isNegative)
                : Utils.bnToBn(other);

        return super.equals(bigInteger);
    }


    /**
     * Returns the BN representation of the number. (Compatibility)
     */
    public BigInteger toBn() {
        return this;
    }

    /**
     * Returns a hex string representation of the value
     */
    @Override
    public abstract String toHex();

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        // Maximum allowed integer for JS is 2^53 - 1, set limit at 52
        //    return this._isHexJson || (super.bitLength() > 52)
        //            ? this.toHex()
        //            : this.toNumber();
        //}
        return this.isHexJson ? this.toHex() : this.intValue();
    }


    /**
     * Returns the string representation of the value
     * @param base The base to use for the conversion
     */
    //toString (base?: number): string {
    //    // only included here since we do not inherit docs
    //    return super.toString(base);
    //}

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     *               Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public abstract byte[] toU8a(boolean isBare);


    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public abstract String toRawType();

}
