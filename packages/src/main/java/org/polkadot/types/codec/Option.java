package org.polkadot.types.codec;

import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.primitive.Null;
import org.polkadot.types.primitive.Type;
import org.polkadot.utils.Utils;

/**
 * An Option is an optional field. Basically the first byte indicates that there is
 * is value to follow. If the byte is `1` there is an actual value. So the Option
 * implements that - decodes, checks for optionality and wraps the required structure
 * with a value if/as required/found.
 */
public class Option<T extends Codec> extends Base<T> implements Codec {

    public Option(Types.ConstructorCodec type, Object value) {
        super((T) decodeOption(type, value));
    }

    static <O> Codec decodeOption(Types.ConstructorCodec type, Object value) {
        if (value == null || value instanceof Null) {
            return new Null();
        } else if (value instanceof Option) {
            return decodeOption(type, ((Option) value).getValue());
        } else if (value instanceof Type) {
            // don't re-create, use as it (which also caters for derived types)
            return (Codec) value;
        } else if (Utils.isU8a(value)) {
            // the isU8a check happens last in the if-tree - since the wrapped value
            // may be an instance of it, so Type and Option checks go in first
            byte[] bytes = Utils.u8aToU8a(value);
            return bytes[0] == 0 ? new Null() : type.newInstance(ArrayUtils.subarray(bytes, 1, bytes.length));
        }

        return type.newInstance(value);
    }

    static class Builder implements Types.ConstructorCodec<Option> {
        Types.ConstructorCodec type;

        public Builder(Types.ConstructorCodec type) {
            this.type = type;
        }

        @Override
        public Option newInstance(Object... values) {
            return new Option(type, values[0]);
        }

        @Override
        public Class<Option> getTClass() {
            return Option.class;
        }
    }

    public static <O> Types.ConstructorCodec<Option> with(Types.ConstructorCodec type) {
        return new Builder(type);
    }

    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return 1 + this.raw.getEncodedLength();
    }

    /**
     * Checks if the Option has no value
     */
    public boolean isNone() {
        return this.raw instanceof Null;
    }

    /**
     * Checks if the Option has a value
     */
    public boolean isSome() {
        return !this.isNone();
    }

    /**
     * Checks if the Option has no value
     */
    @Override
    public boolean isEmpty() {
        return this.isNone();
    }

    /**
     * Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {
        if (other instanceof Option) {
            return this.isSome() && ((Option) other).isSome() && this.getValue().eq(((Option) other).getValue());
        }
        return this.getValue().eq(other);
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
        return this.raw.toJson();
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
        if (isBare) {
            return this.raw.toU8a(true);
        }

        byte[] u8a = new byte[this.getEncodedLength()];

        if (this.isSome()) {
            byte[] rawBytes = this.raw.toU8a(false);
            u8a[0] = 1;
            System.arraycopy(rawBytes, 0, u8a, 1, rawBytes.length);
        }

        return u8a;
    }

    /**
     * Returns the value that the Option represents (if available), throws if null
     */
    public T unwrap() {
        if (this.isNone()) {
            throw new RuntimeException("Option: unwrapping a None value");
        }
        return this.raw;
    }

    /**
     * @param defaultValue The value to return if the option isNone
     *                     Returns the value that the Option represents (if available) or defaultValue if none
     */
    public <O> Object unwrapOr(O defaultValue) {
        return this.isSome()
                ? this.unwrap()
                : defaultValue;
    }

    /**
     * The actual value for the Option
     */
    public Codec getValue() {
        return this.raw;
    }

}
