package org.polkadot.types.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.polkadot.common.ReflectionUtils;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.primitive.Method;
import org.polkadot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Struct
        //<
        //// The actual Class structure, i.e. key -> Class
        //S extends Map<String, Class<? extends Codec>>,
        //// internal type, instance of classes mapped by key
        //T extends Types.TypeDef,
        //// input values, mapped by key can be anything (construction)
        //V extends Map<String, Object>,
        //// type names, mapped by key, name of Class in S
        //E extends Map<String, Object>>
        extends LinkedHashMap<String, Codec> implements Codec {

    private static final Logger logger = LoggerFactory.getLogger(Method.class);


    Map<String, String> jsonMap;
    ConstructorDef constructorDef;


    public Struct(ConstructorDef constructorDef, Object value, Map<String, String> json) {
        Map<String, Codec> codecMap = decodeStruct(constructorDef, value, json);
        this.putAll(codecMap);


        this.jsonMap = json;
        this.constructorDef = constructorDef;
    }

    public Struct(ConstructorDef constructorDef, Object value) {
        this(constructorDef, value, new HashMap<>());
    }


    /**
     * Decode input to pass into constructor.
     *
     * @param value   - Value to decode, one of:
     *                - null
     *                - undefined
     *                - hex
     *                - Uint8Array
     *                - object with `{ key1: value1, key2: value2 }`, assuming `key1` and `key2`
     *                are also keys in `Types`
     *                - array with `[value1, value2]` assuming the array has the same length as
     *                `Object.keys(Types)`
     * @param jsonMap
     */
    private static Map<String, Codec> decodeStruct(ConstructorDef types, Object value, Map<String, String> jsonMap) {

        if (Utils.isHex(value)) {
            return decodeStruct(types, Utils.hexToU8a((String) value), jsonMap);
        } else if (Utils.isU8a(value)) {
            List<Codec> values = CodecUtils.decodeU8a(Utils.u8aToU8a(value), types);

            LinkedHashMap<String, Codec> ret = Maps.newLinkedHashMap();
            List<String> names = types.getNames();
            for (int i = 0; i < names.size(); i++) {
                ret.put(names.get(i), values.get(i));
            }
            return ret;
        } else if (value == null) {
            return new HashMap<>(0);
        }

        return decodeStructFromObject(types, value, jsonMap);
    }


    private static Map<String, Codec> decodeStructFromObject(ConstructorDef types, Object value, Map<String, String> jsonMap) {
        List<String> names = types.getNames();

        LinkedHashMap<String, Codec> ret = Maps.newLinkedHashMap();

        for (int i = 0; i < names.size(); i++) {
            String key = names.get(i);
            Types.ConstructorCodec type = types.getTypes().get(i);

            // The key in the JSON can be snake_case (or other cases), but in our
            // Types, result or any other maps, it's camelCase


            if (value.getClass().isArray()) {
                List<Object> valueList = CodecUtils.arrayLikeToList(value);
                //Object[] stringArray = Arrays.copyOf(value, value.length, Object[].class);
                Object v = valueList.get(i);
                //if (v instanceof )

                ret.put(key, genInstance(type, v));

                /**
                 * raw[key] = value[index] instanceof Types[key]
                 *           ? value[index]
                 *           : new Types[key](value[index]);
                 */

            } else if (value instanceof Map) {
                Map valueMap = (Map) value;

                String jsonKey = null;
                if (jsonMap.containsKey(key) && !valueMap.containsKey(key)) {
                    jsonKey = jsonMap.get(key);
                } else {
                    jsonKey = key;
                }

                Object mapped = valueMap.get(jsonKey);
                ret.put(key, genInstance(type, mapped));
            } else if (value instanceof List) {
                List valueList = (List) value;
                ret.put(key, genInstance(type, valueList.get(i)));
            } else { //obj
                Object field = ReflectionUtils.getField(value, key);
                ret.put(key, genInstance(type, field));


            }

        }

        return ret;

    }

    private static <T extends Codec> T genInstance(Types.ConstructorCodec<T> type, Object value) {
        Class<T> tClass = type.getTClass();
        //
        if (tClass.isInstance(value) && !Utils.isContainer(value)) {
            return (T) value;
        } else {
            T t1 = type.newInstance(value);
            return t1;
        }
    }


    static class Builder implements Types.ConstructorCodec<Struct> {
        ConstructorDef types;

        Builder(ConstructorDef types) {
            this.types = types;
        }

        @Override
        public Struct newInstance(Object... values) {
            Struct instance = null;
            if (values.length == 1) {
                instance = new Struct(types, values[0]);
            } else {
                instance = new Struct(types, values[0], (Map<String, String>) values[1]);
            }
            return instance;
        }

        @Override
        public Class<Struct> getTClass() {
            return Struct.class;
        }
    }

    public static Types.ConstructorCodec<Struct> with(ConstructorDef types) {
        return new Builder(types);
    }

    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        int allLength = 0;
        for (Codec value : this.values()) {
            allLength += value.getEncodedLength();
        }
        return allLength;
    }

    /**
     * Converts the Object to an standard JavaScript Array
     */
    public List<Codec> toArray() {
        return Lists.newArrayList(this.values());
    }


    /**
     * Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {
        return CodecUtils.compareMap(this, other);
    }

    /**
     * Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        JSONObject jsonObject = new JSONObject();
        this.forEach((k, v) -> jsonObject.put(k, v.toJson()));
        return jsonObject;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    /**
     * Encodes the value as a Uint8Array as per the parity-codec specifications
     *
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     */
    @Override
    public byte[] toU8a(boolean isBare) {


        ///////////
        List<byte[]> collect = Lists.newArrayList();
        for (Codec entry : this.toArray()) {
            byte[] bytes = entry.toU8a(isBare);
            //logger.info(" entry {}, {}", entry.getClass().getSimpleName(), Utils.toU8aString(bytes));
            collect.add(bytes);
        }

        ///////
        //List<byte[]> collect = this.toArray().stream()
        //        .map(entry -> {
        //            byte[] bytes = entry.toU8a(isBare);
        //            if (entry.getClass().getSimpleName().equals("Struct") && bytes.length == 0) {
        //                System.out.println();
        //            }
        //            logger.info(" entry {}, {}", entry.getClass().getSimpleName(), Utils.toU8aString(bytes));
        //            return bytes;
        //        })
        //        .collect(Collectors.toList());
        ///////

        byte[] bytes = Utils.u8aConcat(collect);
        return bytes;

        //    return u8aConcat(
        //  ...this.toArray().map((entry) =>
        //            entry.toU8a(isBare)
        //  )
        //);

    }

    public static Types.ConstructorCodec<Struct> builder() {
        return new Types.ConstructorCodec<Struct>() {

            @Override
            public Struct newInstance(Object... values) {
                if (values.length == 2) {
                    return new Struct(((ConstructorDef) values[0]), values[1], null);
                } else {
                    return new Struct(((ConstructorDef) values[0]), values[1], (Map<String, String>) values[2]);
                }
            }

            @Override
            public Class<Struct> getTClass() {
                return Struct.class;
            }
        };

    }

    public <T> T getField(String key) {
        Codec codec = this.get(key);
        if (codec == null) {
            logger.error(" no such field named {}, current {}", key, this.keySet());
        }
        return (T) codec;
    }

    /**
     * Checks if the value is an empty value
     */
    @Override
    public boolean isEmpty() {
        for (Codec value : this.values()) {
            if (!value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return JSON.toJSONString(typesToMap(this.constructorDef.getAsMap()));
    }

    public static Map<String, String> typesToMap(Map<String, Types.ConstructorCodec> types) {
        Map<String, String> result = Maps.newLinkedHashMap();
        for (String key : types.keySet()) {
            result.put(key, types.get(key).newInstance().toRawType());
        }
        return result;
    }

}
