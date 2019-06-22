package org.polkadot.types.primitive;

import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;

import java.math.BigInteger;
import java.util.Arrays;


/**
 * This is a string wrapper, along with the length. It is used both for strings as well
 * as items such as documentation. It simply extends the standard JS `String` built-in
 * object, inheriting all methods exposed from `String`.
 * @noInheritDoc
 */
// TODO
//   - Strings should probably be trimmed (docs do come through with extra padding)
//   - Potentially we want a "TypeString" extension to this. Basically something that
//     wraps the `Balance`, `T::AccountId`, etc. The reasoning - with a "TypeString"
//     we can nicely strip types down like "T::AcountId" -> "AccountId"
public class Text implements Codec, CharSequence {
    String text = null;

    public Text(Object value) {
        this.text = decodeText(value);
    }

    private static String decodeText(Object value) {
        if (value instanceof String) {
            return value.toString();
        } else if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            Pair<Integer, BigInteger> pair = Utils.compactFromU8a(bytes);

            return Utils.u8aToString(Arrays.copyOfRange(bytes, pair.getKey(), pair.getKey() + pair.getValue().intValue()));
        }
        return value.toString();
    }

  /**
   * The length of the value when encoded as a Uint8Array
   */
    @Override
    public int getEncodedLength() {
        return this.toU8a(false).length;
    }

  /**
   * Checks if the value is an empty value
   */
    @Override
    public boolean isEmpty() {
        return this.text.length() == 0;
    }

  /**
   * Compares the value of the input to see if there is a match
   */
    @Override
    public boolean eq(Object other) {
        return this.text.equals(other);
    }

  /**
   * Returns a hex string representation of the value
   */
    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a(false));
    }

  /**
   * Converts the Object to JSON, typically used for RPC transfers
   */
    @Override
    public Object toJson() {
        return this.toString();
    }

  /**
   * Returns the string representation of the value
   */
    @Override
    public String toString() {
        return this.text;
    }

  /**
   * Encodes the value as a Uint8Array as per the parity-codec specifications
   * @param isBare true when the value has none of the type-specific prefixes (internal)
   */
    @Override
    public byte[] toU8a(boolean isBare) {
        byte[] encoded = Utils.stringToU8a(this.toString());
        return isBare ? encoded : Utils.compactAddLength(encoded);
    }

  /**
   * The length of the value
   */
    @Override
    public int length() {
        return this.text.length();
    }

    @Override
    public char charAt(int index) {
        return this.text.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.text.subSequence(start, end);
    }
}
