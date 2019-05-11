package org.polkadot.types.codec;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CodecUtils {
    /**
     * Given an u8a, and an array of Type constructors, decode the u8a against the
     * types, and return an array of decoded values.
     *
     * @param u8a   - The u8a to decode.
     * @param types - The array of Constructor to decode the U8a against.
     */
    public static List<Codec> decodeU8a(byte[] u8a, Types.ConstructorDef types) {
        List<Codec> results = Lists.newArrayList();

        decodeU8a(u8a, types.getTypes(), results);

        return results;
    }


    public static void decodeU8a(byte[] u8a, List<Types.ConstructorCodec> types, List<Codec> results) {

        if (CollectionUtils.isEmpty(types)) {
            return;
        }
        Types.ConstructorCodec constructorCodec = types.get(0);

        Codec codec = constructorCodec.newInstance(u8a);

        results.add(codec);

        byte[] subarray = ArrayUtils.subarray(u8a, codec.getEncodedLength(), u8a.length);

        decodeU8a(subarray, types.subList(1, types.size()), results);
    }

    public static List<Codec> decodeU8a(byte[] u8a, List<Types.ConstructorCodec> types) {

        Types.ConstructorCodec constructorCodec = types.get(0);

        //TODO 2019-05-06 14:58
        throw new UnsupportedOperationException();
    }
    //  export default function decodeU8a (u8a: Uint8Array, _types: Constructor[] | { [index: string]: Constructor }): Codec[] {
    //const types = Array.isArray(_types)
    //              ? _types
    //              : Object.values(_types);
    //
    //      if (!types.length) {
    //          return [];
    //      }
    //
    //const Type = types[0];
    //const value = new Type(u8a);
    //
    //      return [value].concat(decodeU8a(u8a.subarray(value.encodedLength), types.slice(1)));
    //  }

    public static List<Object> arrayLikeToList(Object value) {
        List<Object> ret = new ArrayList<>();

        if (value instanceof List) {
            for (Object obj : ((List) value)) {
                ret.add(obj);
            }
            return ret;
        } else if (value.getClass().isArray()) {

            //Type gType = ((ParameterizedType) type.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            //int length = Array.getLength(value);
            //for (int i = 0; i < length; i++) {
            //    Object obj = Array.get(value, i);
            //    genInstance(ret, type, obj);
            //}

            Class<?> componentType = value.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    Object obj = Array.get(value, i);
                    ret.add(obj);
                }
            } else {
                Object[] objects = (Object[]) value;
                for (Object obj : objects) {
                    ret.add(obj);
                }
            }
            return ret;
        }
        return ret;
    }

}
