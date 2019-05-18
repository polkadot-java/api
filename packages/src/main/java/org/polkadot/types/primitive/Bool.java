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
     * @description The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean eq(Object other) {
        return this.raw == (
                other instanceof Boolean
                        ? ((Boolean) other).booleanValue()
                        : decodeBool(other)
        );
    }

    /**
     * @description Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a());
    }

    @Override
    public Object toJson() {
        return this.raw;
    }

    @Override
    public byte[] toU8a(boolean isBare) {
        return new byte[]{(byte) (this.raw ? 1 : 0)};
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public boolean rawBool() {
        return this.raw;
    }
}
