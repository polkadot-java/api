package org.polkadot.types.primitive;


import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;

import java.math.BigInteger;
import java.util.Date;

/**
 * @name Moment
 * A wrapper around seconds/timestamps. Internally the representation only has
 * second precicion (aligning with Rust), so any numbers passed an/out are always
 * per-second. For any encoding/decoding the 1000 multiplier would be applied to
 * get it in line with JavaScript formats. It extends the base JS `Date` object
 * and has all the methods available that are applicable to any `Date`
 * @noInheritDoc
 */
//export default class Moment extends Date implements Codec {
public class Moment extends Date implements Codec {

    protected Date raw;// FIXME Remove this once we convert all types out of Base

    public static int BITLENGTH = 64;

    //  constructor (value: Moment | Date | AnyNumber = 0) {
    public Moment(Object value) {
        this.raw = decodeMoment(value);
        this.setTime(this.raw.getTime());
    }


    static Date decodeMoment(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        } else if (Utils.isU8a(value)) {
            byte[] bytes = Utils.u8aToU8a(value);
            value = Utils.u8aToBn(ArrayUtils.subarray(bytes, 0, BITLENGTH / 8), true, false);
        } else if (value instanceof String) {
            //      value = new BN(value, 10, 'le');
            value = new BigInteger((String) value, 10);
        }

        return new Date(Utils.bnToBn(value).longValue() * 1000);
    }


    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return BITLENGTH / 8;
    }

    /**
     * Checks if the value is an empty value
     */
    @Override
    public boolean isEmpty() {
        return this.getTime() == 0;
    }

    /**
     * Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {
        return Moment.decodeMoment(other).getTime() == this.getTime();
    }


    /**
     * Returns the number of bits in the value
     */
    public int bitLength() {
        return BITLENGTH;
    }


    /**
     * Returns the BN representation of the timestamp
     */
    public BigInteger toBn() {
        return BigInteger.valueOf(this.toNumber());
    }

    /**
     * Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return Utils.bnToHex(this.toBn(), BITLENGTH);
    }

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        return this.toNumber();
    }


    /**
     * Returns the number representation for the timestamp
     */
    public long toNumber() {
        return (long) Math.ceil(this.getTime() / 1000);
    }


    /**
     * Returns the string representation of the value
     */
    @Override
    public String toString() {
        // only included here since we do not inherit docs
        return super.toString();
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     * Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return Utils.bnToU8a(this.toBn(), true, false, BITLENGTH);
    }

    /**
     * @name MomentOf
     * The Substrate MomentOf representation as a [[Moment]].
     */
    public static class MomentOf extends Moment {
        public MomentOf(Object value) {
            super(value);
        }
    }
}
