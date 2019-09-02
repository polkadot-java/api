package org.polkadot.types.codec;


import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.utils.Utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Vec
 * This manages codec arrays. Internally it keeps track of the length (as decoded) and allows
 * construction with the passed `Type` in the constructor. It is an extension to Array, providing
 * specific encoding/decoding on top of the base type.
 */
public class Vec<T extends Codec> extends AbstractArray<T> {

    public static final int MAX_LENGTH = 32768;

    private Types.ConstructorCodec<T> type;

    public Vec(Types.ConstructorCodec<T> type, Object value) {

        this.type = type;
        this.addAll(decodeVec(type, value));
    }

    //Vector<any> | Uint8Array | string | Array<any>
    static <T extends Codec> List<T> decodeVec(Types.ConstructorCodec<T> type, Object value) {
        List<T> ret = new ArrayList<>();
        if (value instanceof List) {
            for (Object obj : ((List) value)) {
                genInstance(ret, type, obj);
            }
            return ret;
        } else if (value != null && value.getClass().isArray() && !(value instanceof byte[])) {
            List<Object> objects = CodecUtils.arrayLikeToList(value);
            for (Object obj : objects) {
                genInstance(ret, type, obj);
            }
            return ret;
        }

        byte[] u8a = Utils.u8aToU8a(value);
        Pair<Integer, BigInteger> pair = Utils.compactFromU8a(u8a);
        int offset = pair.getKey();
        int length = pair.getValue().intValue();

        List<Types.ConstructorCodec> typeList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            typeList.add(type);
        }

        List results = Lists.newArrayList();
        CodecUtils.decodeU8a(ArrayUtils.subarray(u8a, offset, u8a.length), typeList, results);
        return results;
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

    static class Builder<T extends Codec> implements Types.ConstructorCodec<Vec> {
        Types.ConstructorCodec<T> type;

        Builder(Types.ConstructorCodec<T> type) {
            this.type = type;
        }

        @Override
        public Vec newInstance(Object... values) {
            return new Vec<>(type, values[0]);
        }

        @Override
        public Class<Vec> getTClass() {
            return Vec.class;
        }
    }

    public static <O extends Codec> Types.ConstructorCodec<Vec<O>> with(Types.ConstructorCodec<O> type) {
        //TODO 2019-05-10 10:07 lost type
        return new Builder(type);
    }

    /**
     * The type for the items
     */
    public String getType() {
        //TODO 2019-05-07 20:17
        return this.type.toString();
    }

    @Override
    public int indexOf(Object o) {
        Type gType = ((ParameterizedType) type.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Object o1 = null;
        if (gType instanceof Class && o.getClass().isAssignableFrom((Class<?>) gType)) {
            o1 = o;
        } else {
            o1 = this.type.newInstance(o);
        }
        return super.indexOf(o1);
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "Vec<" + this.type.getTClass().getSimpleName() + ">";
    }
}
