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
 * @name Vector
 * @description This manages codec arrays. Internally it keeps track of the length (as decoded) and allows
 * construction with the passed `Type` in the constructor. It is an extension to Array, providing
 * specific encoding/decoding on top of the base type.
 */
public class Vector<T extends Codec> extends AbstractArray<T> {

    private Types.ConstructorCodec<T> type;

    public Vector(Types.ConstructorCodec<T> type, Object value) {

        this.type = type;
        this.addAll(decodeVector(type, value));
    }

    //Vector<any> | Uint8Array | string | Array<any>
    static <T extends Codec> List<T> decodeVector(Types.ConstructorCodec<T> type, Object value) {
        List<T> ret = new ArrayList<>();
        if (value instanceof List) {
            for (Object obj : ((List) value)) {
                genInstance(ret, type, obj);
            }
            return ret;
        } else if (value.getClass().isArray() && !(value instanceof byte[])){
            List<Object> objects = CodecUtils.arrayLikeToList(value);
            for (Object obj : objects) {
                genInstance(ret, type, obj);
            }
            return ret;
        }

        //else if (value.getClass().isArray()) {
        //
        //    Type gType = ((ParameterizedType) type.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        //    int length = Array.getLength(value);
        //    for (int i = 0; i < length; i++) {
        //        Object obj = Array.get(value, i);
        //        genInstance(ret, type, obj);
        //    }
        //
        //    //Class<?> componentType = value.getClass().getComponentType();
        //    //if (componentType.isPrimitive()) {
        //    //    int length = Array.getLength(value);
        //    //    for (int i = 0; i < length; i++) {
        //    //        Object obj = Array.get(value, i);
        //    //        System.out.println(obj);
        //    //    }
        //    //} else {
        //    //    Object[] objects = (Object[]) value;
        //    //    for (Object obj : objects) {
        //    //        System.out.println(obj);
        //    //    }
        //    //}
        //    return ret;
        //}

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

    static class Builder<T extends Codec> implements Types.ConstructorCodec<Vector> {
        Types.ConstructorCodec<T> type;

        Builder(Types.ConstructorCodec<T> type) {
            this.type = type;
        }

        @Override
        public Vector newInstance(Object... values) {
            return new Vector<>(type, values[0]);
        }

        @Override
        public Class<Vector> getTClass() {
            return Vector.class;
        }
    }

    public static <O extends Codec> Types.ConstructorCodec<Vector<O>> with(Types.ConstructorCodec<O> type) {
        //TODO 2019-05-10 10:07 lost type
        return new Builder(type);
    }

    /**
     * @description The type for the items
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
}
