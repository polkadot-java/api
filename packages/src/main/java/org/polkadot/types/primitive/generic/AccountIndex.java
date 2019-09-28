package org.polkadot.types.primitive.generic;

import com.google.common.primitives.UnsignedBytes;
import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.common.keyring.address.AddressCodec;
import org.polkadot.types.primitive.U32;
import org.polkadot.utils.Utils;

import java.math.BigInteger;

/**
 * A wrapper around an AccountIndex, which is a shortened, variable-length encoding
 * for an Account. We extends from {@link org.polkadot.types.primitive.U32} to provide the number-like properties.
 */
public class AccountIndex extends U32 {
    public static final BigInteger ENUMSET_SIZE = BigInteger.valueOf(64);

    //public static final int PREFIX_1BYTE = 0xef;
    //public static final int PREFIX_2BYTE = 0xfc;
    //public static final int PREFIX_4BYTE = 0xfd;
    //public static final int PREFIX_8BYTE = 0xfe;


    public static final byte PREFIX_1BYTE = UnsignedBytes.checkedCast(0xef);
    public static final byte PREFIX_2BYTE = UnsignedBytes.checkedCast(0xfc);
    public static final byte PREFIX_4BYTE = UnsignedBytes.checkedCast(0xfd);
    public static final byte PREFIX_8BYTE = UnsignedBytes.checkedCast(0xfe);


    public static final BigInteger MAX_1BYTE = BigInteger.valueOf(PREFIX_1BYTE);
    public static final BigInteger MAX_2BYTE = BigInteger.ONE.shiftLeft(16);
    public static final BigInteger MAX_4BYTE = BigInteger.ONE.shiftLeft(32);

    //  constructor (value: AnyNumber = new BN(0)) {
    public AccountIndex(Object value) {
        super(decodeAccountIndex(value));
    }

    static Object decodeAccountIndex(Object value) {
        if (value instanceof AccountIndex) {
            // `value.toBn()` on AccountIndex returns a pure BN (i.e. not an
            // AccountIndex), which has the initial `toString()` implementation.
            return ((AccountIndex) value).toBn();

        } else if (value instanceof BigInteger || value instanceof Number || Utils.isHex(value) || Utils.isU8a(value)) {
            return value;
        }

        return decodeAccountIndex(AddressCodec.decodeAddress((byte[]) value));
    }

    //static calcLength (_value: BN | number): number {
    static int calcLength(Number _value) {
        BigInteger value = Utils.bnToBn(_value);
        if (value.compareTo(MAX_1BYTE) <= 0) {
            return 1;
        } else if (value.compareTo(MAX_2BYTE) < 0) {
            return 2;
        } else if (value.compareTo(MAX_4BYTE) < 0) {
            return 4;
        }

        return 8;
    }

    static int[] readLength(byte[] input) {
        if (input.length == 0) {
            return new int[]{0, 1};
        }
        int first = UnsignedBytes.toInt(input[0]);
        if (first == UnsignedBytes.toInt(PREFIX_2BYTE)) {
            return new int[]{1, 2};
        } else if (first == UnsignedBytes.toInt(PREFIX_4BYTE)) {
            return new int[]{1, 4};
        } else if (first == UnsignedBytes.toInt(PREFIX_8BYTE)) {
            return new int[]{1, 8};
        }

        return new int[]{0, 1};
    }

    static byte[] writeLength(byte[] input) {
        switch (input.length) {
            case 2:
                return new byte[]{PREFIX_2BYTE};
            case 4:
                return new byte[]{PREFIX_4BYTE};
            case 8:
                return new byte[]{PREFIX_8BYTE};
            default:
                return new byte[]{};
        }
    }

    /**
     * Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {
        // shortcut for BN or Number, don't create an object
        if (other instanceof BigInteger || other instanceof Number) {
            return super.eq(other);
        }

        // convert and compare
        return super.eq(new AccountIndex(other));
    }

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        return super.toJson();
    }

    /**
     * Returns the string representation of the value
     */
    @Override
    public String toString() {
        int length = calcLength(this);
        return AddressCodec.encodeAddress(ArrayUtils.subarray(this.toU8a(), 0, length));
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "AccountIndex";
    }
}
