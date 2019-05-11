package org.polkadot.types.codec;


import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.utils.Utils;

/**
 * @name U8aFixed
 * @description A U8a that manages a a sequence of bytes up to the specified bitLength. Not meant
 * to be used directly, rather is should be subclassed with the specific lengths.
 */
public class U8aFixed extends U8a {
    //type BitLength = 8 | 16 | 32 | 64 | 128 | 160 | 256 | 512;

    public U8aFixed(Object value, int bitLength) {
        super(decodeU8aFixed(value, bitLength <= 0 ? 256 : bitLength));
    }

    private static Object decodeU8aFixed(Object value, int bitLength) {
        if (bitLength <= 0) {
            bitLength = 256;
        }
        if (Utils.isU8a(value)) {
            byte[] ba = (byte[]) value;
            // ensure that we have an actual u8a with the full length as specified by
            // the bitLength input (padded with zeros as required)
            int byteLength = bitLength / 8;
            byte[] sub = ArrayUtils.subarray(ba, 0, byteLength);

            if (sub.length == byteLength) {
                return sub;
            }

            byte[] u8a = new byte[byteLength];
            for (int i = 0; i < u8a.length && i < sub.length; i++) {
                u8a[i] = sub[i];
            }

            return u8a;
        } else if (value.getClass().isArray() || value instanceof String) {
            return decodeU8aFixed(Utils.u8aToU8a(value), bitLength);
        }
        return value;
    }

    /**
     * @description Returns the number of bits in the value
     */
    int bitLength() {
        return this.length() * 8;
    }
}
