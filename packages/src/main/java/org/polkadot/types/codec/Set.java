package org.polkadot.types.codec;

import com.google.common.primitives.UnsignedBytes;
import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @name Set
 * @description An Set is an array of string values, represented an an encoded type by
 * a bitwise representation of the values.
 */
// FIXME This is a prime candidate to extend the JavaScript built-in Set

public class Set extends Base<List<String>> implements Codec {

    private static final Logger logger = LoggerFactory.getLogger(Set.class);

    public static class SetValues extends LinkedHashMap<String, Integer> {
        public SetValues(Map<String, Integer> map) {
            this.putAll(map);
        }
    }

    private SetValues setValues;

    //  constructor (setValues: SetValues, value?: Array<string> | Uint8Array | number) {
    public Set(SetValues setValues, Object value) {
        super(decodeSet(setValues, value));

        this.setValues = setValues;
    }

    static List<String> decodeSet(SetValues setValues, Object value) {
        if (Utils.isU8a(value)) {
            byte[] bytes = Utils.u8aToU8a(value);
            return decodeSet(setValues, bytes[0]);
        } else if (value instanceof List
                || value.getClass().isArray()) {
            List<Object> list = CodecUtils.arrayLikeToList(value);
            List<String> result = list.stream()
                    .filter(v -> {
                        if (setValues.containsKey(v)) {
                            return true;
                        } else {
                            logger.error("Ignoring invalid {} passed to Set", v);
                            return false;
                        }
                    })
                    .map(v -> v.toString())
                    .collect(Collectors.toList());
            return result;
        }

        long longValue = ((Number) value).longValue();

        List<String> result = setValues.keySet().stream()
                .filter(k -> {
                    if ((longValue & setValues.get(k)) == setValues.get(k)) {
                        return true;
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());

        long computed = Set.encodeSet(setValues, result);

        if (longValue != computed) {
            logger.error("Mismatch decoding {}, computed as {} with {}",
                    value, computed, result);
        }

        return result;
    }

    static long encodeSet(SetValues setValues, List<String> value) {
        long result = 0;
        for (String v : value) {
            result = result | (setValues.containsKey(v) ? setValues.get(v) : 0);
        }
        return result;
    }

    /**
     * @description The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return 1;
    }

    /**
     * @description true is the Set contains no values
     */
    @Override
    public boolean isEmpty() {
        return this.values().isEmpty();
    }

    /**
     * @description The actual set values as a Array<string>
     */
    public List<String> values() {
        return this.raw;
    }


    /**
     * @description The encoded value for the set members
     */
    public long valueEncoded() {
        return Set.encodeSet(this.setValues, this.raw);
    }


    /**
     * @description Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {
        if (other instanceof List
                || other.getClass().isArray()) {
            //List<Object> objectList = CodecUtils.arrayLikeToList(other);
            // we don't actually care about the order, sort the values
            //  TODO    return compareArray(this.values.sort(), other.sort());
            LinkedHashSet<String> set1 = new LinkedHashSet<>(this.values());
            LinkedHashSet<Object> set2 = new LinkedHashSet<>(CodecUtils.arrayLikeToList(other));
            return CodecUtils.compareArray(set1.stream().collect(Collectors.toList()), set2.stream().collect(Collectors.toList()));
        } else if (other instanceof Set) {
            return this.eq(((Set) other).values());
        } else if (other instanceof Number) {
            return this.valueEncoded() == ((Number) other).longValue();
        }
        return false;
    }

    /**
     * @description Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a());
    }


    /**
     * @description Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        return this.values();
    }


    /**
     * @description Returns the string representation of the value
     */
    @Override
    public String toString() {
        return this.values().toString();
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     * @description Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return new byte[]{UnsignedBytes.checkedCast(this.valueEncoded())};
    }


}
