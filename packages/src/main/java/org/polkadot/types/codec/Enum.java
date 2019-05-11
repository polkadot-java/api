package org.polkadot.types.codec;


import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;

import java.util.List;

/**
 * @name Enum
 * @description A codec wrapper for an enum. Enums are encoded as a single byte, where the byte
 * is a zero-indexed value. This class allows you to retrieve the value either
 * by `toNumber()` exposing the actual raw index, or `toString()` returning a
 * string representation (as provided as part of the constructor)
 */
// TODO:
//   - It would be great if this could actually wrap actual TS enums
public class Enum extends Base<Number> implements Codec {
    private List<String> enumList;


    //    type EnumDef = {
//            [index: string]: number
//} | Array<string>;
    //  static decodeEnum (def: EnumDef, value: Enum | Uint8Array | string | number): number | undefined {
    public Enum(List<String> def, Object value) {
        super(decodeEnum(def, value));

        this.enumList = def;
    }

    private static Number decodeEnum(List<String> def, Object value) {
        if (value instanceof Enum) {
            return ((Enum) value).raw;
        } else if (Utils.isU8a(value)) {
            return ((byte[]) value)[0];
        } else if (value instanceof String) {
            // return Array.isArray(def)
            //        ? def.indexOf(value)
            //        : def[value] || -1;
            return def.indexOf(value);
        }
        return (Number) value;
    }

    @Override
    public int getEncodedLength() {
        return 1;
    }

    public int getIndex() {
        return this.raw.intValue();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean eq(Object other) {
        if (other instanceof Enum) {
            return ((Enum) other).raw.equals(this.raw);
        }
        if (other instanceof String) {
            return this.toString().equals(other);
        }
        return this.raw.equals(other);
    }

    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a(false));
    }

    @Override
    public Object toJson() {
        return this.raw;
    }

    public int toNumber() {
        return this.raw.intValue();
    }

    @Override
    public String toString() {
        if (this.enumList.size() > this.raw.intValue()) {
            return this.enumList.get(this.raw.intValue());
        }
        return this.raw.toString();
    }

    @Override
    public byte[] toU8a(boolean isBare) {
        //return new Uint8Array([this.raw]);
        return new byte[]{this.raw.byteValue()};
    }
}
