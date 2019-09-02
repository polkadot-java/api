package org.polkadot.types.codec;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.math.NumberUtils;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @name VecFixed
 * @description This manages codec arrays of a fixed length
 */
public class VecFixed<T extends Codec> extends AbstractArray<T> {

    public static final int MAX_LENGTH = 32768;

    private Types.ConstructorCodec<T> type;

    public VecFixed(Types.ConstructorCodec<T> type, int length, Object value) {

        this.type = type;
        this.addAll(decodeVecFixed(type, length, value));
    }

    //Vector<any> | Uint8Array | string | Array<any>
    static <T extends Codec> List<T> decodeVecFixed(Types.ConstructorCodec<T> type, int allocLength, Object value) {

        List<T> values = Vec.decodeVec(type,
                Utils.isU8a(value)
                        ? Utils.u8aConcat(Lists.newArrayList(Utils.compactToU8a(allocLength), Utils.u8aToU8a(value)))
                        : value
        );

        while (values.size() < allocLength) {
            values.add(type.newInstance());
        }

        assert values.size() == allocLength : "Expected a length of exactly " + allocLength + " entries";

        return values;
    }

    private static <T extends Codec> void genInstance(List<T> all, Types.ConstructorCodec<T> type, Object value) {
        Class<T> tClass = type.getTClass();
        if (tClass.isInstance(value) && !Utils.isContainer(value)) {
            all.add((T) value);
        } else {
            T t1 = type.newInstance(value);
            all.add(t1);
        }
    }

    static class Builder<T extends Codec> implements Types.ConstructorCodec<VecFixed> {
        Types.ConstructorCodec<T> type;

        Builder(Types.ConstructorCodec<T> type) {
            this.type = type;
        }

        @Override
        public VecFixed newInstance(Object... values) {
            return new VecFixed<>(type, NumberUtils.toInt(values[0].toString()), values[1]);
        }

        @Override
        public Class<VecFixed> getTClass() {
            return VecFixed.class;
        }
    }

    public static <O extends Codec> Types.ConstructorCodec<VecFixed<O>> with(Types.ConstructorCodec<O> type) {
        //TODO 2019-05-10 10:07 lost type
        return new VecFixed.Builder(type);
    }

    /**
     * The type for the items
     */
    public String getType() {
        //TODO 2019-08-29 18:27
        return this.type.newInstance(null).toRawType();
    }

    /*
  public toU8a (): Uint8Array {
    // we override, we don't add the length prefix for outselves, and at the same time we
    // ignore isBare on entries, since they should be properly encoded at all times
    const encoded = this.map((entry): Uint8Array => entry.toU8a());

    return u8aConcat(...encoded);
  }

    * */

    @Override
    public byte[] toU8a() {
        List<byte[]> encoded = this.stream().map(entry -> entry.toU8a()).collect(Collectors.toList());
        return Utils.u8aConcat(encoded);
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "[" + this.getType() + ";" + this.length() + "]";
    }
}
