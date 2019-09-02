package org.polkadot.types.codec;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        if (value == null) {
            return ret;
        }

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

    //TODO 2019-05-13 09:54 check
    public static boolean compareMap(Map map1, Object obj) {

        if (obj.getClass().isArray()) {
            List<Object> objectList = arrayLikeToList(obj);
            if (objectList.size() != map1.size()) {
                return false;
            }
            for (Object o : objectList) {
                List<Object> entry = arrayLikeToList(o);
                if (entry.size() != 2) {
                    return false;
                }

                Object v1 = map1.get(entry.get(0));

                if (v1 == null) {
                    return false;
                }

                if (v1 instanceof Codec && !((Codec) v1).eq(entry.get(1))) {
                    return false;
                }
                if (!v1.equals(entry.get(1))) {
                    return false;
                }
            }

            return true;
        }

        Map<Object, Object> map2 = null;
        if (obj instanceof Map) {
            map2 = (Map<Object, Object>) obj;
        } else {
            return false;
        }
        if (map1.size() != map2.size()) {
            return false;
        }

        for (Object _entry : map1.entrySet()) {
            Map.Entry entry = (Map.Entry) _entry;
            Object v2 = map2.get(entry.getKey());
            if (v2 == null) {
                return false;
            }

            if (v2 instanceof Codec && entry.getValue() instanceof Codec) {
                if (!((Codec) v2).eq(entry.getValue())) {
                    return false;
                }
            }

            if (!v2.equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }


    public static boolean compareArray(List list, Object other) {
        List<Object> objects = arrayLikeToList(other);
        if (objects.size() != list.size()) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof Codec && !((Codec) o).eq(objects.get(i))) {
                return false;
            } else if (!o.equals(objects.get(i))) {
                return false;
            }
        }
        return true;
    }


}
