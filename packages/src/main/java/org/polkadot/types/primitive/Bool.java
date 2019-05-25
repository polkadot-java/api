package org.polkadot.types.primitive;

import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;

//extends Boolean
public class Bool implements Codec {
    boolean raw = false;

    //constructor (value: Bool | Boolean | Uint8Array | boolean | number = false) {
    public Bool(Object value) {
        this.raw = decodeBool(value);
    }


    private static boolean decodeBool(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else if (Utils.isU8a(value)) {
            byte[] value1 = (byte[]) value;
            return value1[0] == 1;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() > 0;
        } else if (value instanceof String) {
            return Boolean.valueOf((String) value);
        }
        //TODO 2019-05-10 17:09 !!value;
        return value == null;
    }

  /**
   * The length of the value when encoded as a Uint8Array
   */
    @Override
    public int getEncodedLength() {
        return 1;
    }

  /**
   * Checks if the value is an empty value (always false)
   */
    @Override
    public boolean isEmpty() {
        return false;
    }

  /**
   * Compares the value of the input to see if there is a match
   */
    @Override
    public boolean eq(Object other) {
        return this.raw == (
                other instanceof Boolean
                        ? ((Boolean) other).booleanValue()
                        : decodeBool(other)
        );
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
        return this.raw;
    }

  /**
   * Encodes the value as a Uint8Array as per the parity-codec specifications
   * @param isBare true when the value has none of the type-specific prefixes (internal)
   */
    @Override
    public byte[] toU8a(boolean isBare) {
        return new byte[]{(byte) (this.raw ? 1 : 0)};
    }

  /**
   * Returns the string representation of the value
   */
    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public boolean rawBool() {
        return this.raw;
    }
}
