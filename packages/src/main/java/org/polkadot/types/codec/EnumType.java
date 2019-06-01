package org.polkadot.types.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.primitive.Null;
import org.polkadot.utils.Utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.polkadot.utils.Utils.hexToU8a;


/**
 * @name EnumType
 * @description This implements an enum, that based on the value wraps a different type. It is effectively
 * an extension to enum where the value type is determined by the actual index.
 */
// TODO:
//   - As per Enum, actually use TS enum
//   - It should rather probably extend Enum instead of copying code
public class EnumType<T> extends Base<Codec> implements Codec {

    private Types.ConstructorDef def;
    private int index;
    private List<Integer> indexes;

    //  constructor (def: TypesDef, value?: any, index?: number | EnumType<T>, aliasses?: Aliasses) {
    public EnumType(Types.ConstructorDef def, Object value, int index, LinkedHashMap<String, String> aliasses) {
        super(decodeEnumType(def, aliasses, value, index).getValue());

        Pair<Integer, Codec> pair = decodeEnumType(def, aliasses, value, index);
        this.def = def;
        AtomicInteger i = new AtomicInteger();
        this.indexes = def.getNames().stream().map(e -> i.getAndIncrement()).collect(Collectors.toList());

        this.index = this.indexes.indexOf(pair.getLeft());
        if (this.index < 0) {
            this.index = 0;
        }
    }
    //
    //public EnumType(Types.ConstructorDef def, Object value, EnumType<T> index, LinkedHashMap<String, String> aliasses) {
    //    super(decodeEnumType(def, aliasses, value, index).getValue());
    //
    //    Pair<Integer, Codec> pair = decodeEnumType(def, aliasses, value, index);
    //    this.def = def;
    //    AtomicInteger i = new AtomicInteger();
    //    this.indexes = def.getNames().stream().map(e -> i.getAndIncrement()).collect(Collectors.toList());
    //    this.index = this.indexes.indexOf(pair.getLeft());
    //    if (this.index < 0) {
    //        this.index = 0;
    //    }
    //}

    private static <T> Pair<Integer, Codec> decodeEnumType(Types.ConstructorDef def, LinkedHashMap<String, String> aliasses, Object value, EnumType<T> index) {
        // If `index` is set, we parse it.
        if (index != null) {
            return EnumType.createValue(def, index.index, index.raw);
        }

        // Or else, we just look at `value`
        return EnumType.decodeViaValue(def, aliasses, value);
    }

    private static Pair<Integer, Codec> decodeEnumType(Types.ConstructorDef def, LinkedHashMap<String, String> aliasses, Object value, int index) {
        // If `index` is set, we parse it.
        if (index >= 0) {
            return EnumType.createValue(def, index, value);
        }

        // Or else, we just look at `value`
        return EnumType.decodeViaValue(def, aliasses, value);
    }

    private static Pair<Integer, Codec> decodeViaValue(Types.ConstructorDef def, LinkedHashMap<String, String> aliasses, Object value) {
        if (value instanceof EnumType) {
            return EnumType.createValue(def, ((EnumType) value).index, ((EnumType) value).raw);
        } else if (Utils.isU8a(value)) {
            byte[] u8a = (byte[]) value;
            return EnumType.createValue(def, u8a[0], Arrays.copyOfRange(u8a, 1, u8a.length));
        } else if (value instanceof Number) {
            return EnumType.createValue(def, ((Number) value).intValue(), null);
        } else if (value instanceof String) {
            String str = value.toString();
            return Utils.isHex(str) ? EnumType.decodeViaValue(def, aliasses, hexToU8a(str))
                    : EnumType.createViaJSON(def, aliasses, str, null);
        }
        //    } else if (isObject(value)) {
        else if (value instanceof Map) {
            Map value1 = (Map) value;
            Object key = value1.keySet().stream().findFirst().orElse(null);
            return createViaJSON(def, aliasses, (String) key, value1.get(key));
            //const key = Object.keys(value)[0];
            //      return EnumType.createViaJSON(def, aliasses, key, value[key]);
            //TODO 2019-05-07 17:36
            //throw new UnsupportedOperationException(" decodeViaValue " + value);
        }
        // Worst-case scenario, return the first with default
        return EnumType.createValue(def, 0, null);
    }

    private static Pair<Integer, Codec> createViaJSON(Types.ConstructorDef def, LinkedHashMap<String, String> aliasses, String key, Object value) {

        // JSON comes in the form of { "<type (lowercased)>": "<value for type>" }, here we
        // additionally force to lower to ensure forward compat
        //const keys = Object.keys(def).map((k) => k.toLowerCase());
        List<String> keys = def.getNames().stream().map(k -> k.toLowerCase()).collect(Collectors.toList());
        String keyLower = key.toLowerCase();
        Map<String, String> aliasLower = aliasses.entrySet().stream()
                .map(e -> new String[]{e.getKey().toLowerCase(), e.getValue().toLowerCase()})
                .collect(Collectors.toMap((sa) -> sa[0], (sa) -> sa[1]));

        String aliasKey = aliasLower.getOrDefault(keyLower, keyLower);
        int index = keys.indexOf(aliasKey);

        //assert(index !== -1, `Cannot map input on JSON, unable to find '${key}' in ${keys.join(', ')}`);

        return EnumType.createValue(def, index, value);
    }

    private static Pair<Integer, Codec> createValue(Types.ConstructorDef def, int index, Object value) {
        Types.ConstructorCodec constructorCodec = def.getTypes().get(index);
        Codec codec = constructorCodec.newInstance(value);
        return Pair.of(index, codec);
    }

    public interface EnumConstructor<T extends Codec> {
        T newInstance(Object value, int index);
    }

    static class Builder implements Types.ConstructorCodec<EnumType> {
        Types.ConstructorDef def;

        Builder(Types.ConstructorDef def) {
            this.def = def;
        }

        //@Override
        //public EnumType<Types.ConstructorDef> newInstance(Object value, int index) {
        //    return new EnumType<>(def, value, index, null);
        //}

        @Override
        public EnumType<Types.ConstructorDef> newInstance(Object... value) {
            return new EnumType<>(def, value[0], (Integer) value[1], null);
        }

        @Override
        public Class<EnumType> getTClass() {
            return EnumType.class;
        }
    }

    //public static EnumConstructor<EnumType<Types.ConstructorDef>> with(Types.ConstructorDef def) {
    //    return new Builder(def);
    //}

    public static Types.ConstructorCodec<EnumType> with(Types.ConstructorDef def) {
        return new Builder(def);
    }

    /**
     * Returns the number representation for the value
     */
    public int toNumber() {
        return this.index;
    }


    /**
     * The value of the enum
     */
    public Codec value() {
        return this.raw;
    }

    /**
     * The name of the type this enum value represents
     */
    public String getType() {
        return this.def.getNames().get(this.index);
    }

  /**
   * The index of the metadata value
   */
    public int index() {
        return this.index;
    }

    /**
     * Checks if the Enum points to a [[Null]] type (deprecated, use isNone)
     */
    public boolean isNull() {
        return this.raw instanceof Null;
    }

  /**
   * The length of the value when encoded as a Uint8Array
   */
    @Override
    public int getEncodedLength() {
        return 1 + this.raw.getEncodedLength();
    }

  /**
   * Checks if the value is an empty value
   */
    @Override
    public boolean isEmpty() {
        return this.raw.isEmpty();
    }

  /**
   * Checks if the Enum points to a [[Null]] type
   */
    public boolean isNone() {
        return this.isNull();
    }

  /**
   * Compares the value of the input to see if there is a match
   */
    @Override
    public boolean eq(Object other) {

        // cater for the case where we only pass the enum index
        if (other instanceof Number) {
            return this.toNumber() == ((Number) other).intValue();
        }


        // compare the actual wrapper value
        return this.value().eq(other);
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
        JSONObject jsonObject = new JSONObject();
        String type = this.getType();
        jsonObject.put(type, this.raw.toJson());
        return jsonObject;
    }

  /**
   * Returns the string representation of the value
   */
    @Override
    public String toString() {
        return this.isNull()
                ? this.getType()
                : JSON.toJSONString(this.toJson());
    }

  /**
   * Encodes the value as a Uint8Array as per the parity-codec specifications
   * @param isBare true when the value has none of the type-specific prefixes (internal)
   */
    @Override
    public byte[] toU8a(boolean isBare) {
        Integer index = this.indexes.get(this.index);
        return Utils.u8aConcat(Lists.newArrayList(new byte[]{
                index.byteValue()
        }, this.raw.toU8a(isBare)));
    }


    public boolean isType(String value) {
        return this.getType().equals(value);
    }
}
