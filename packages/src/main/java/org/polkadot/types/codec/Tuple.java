package org.polkadot.types.codec;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Tuple defines an anonymous fixed-length array, where each element has its
 * own type. It extends the base JS `Array` object.
 */
public class Tuple extends AbstractArray<Codec> {
    //private _Types: TupleConstructors;
    private Types.ConstructorDef types;

    //public Tuple(List<Types.ConstructorCodec> types, Object value) {
    //    this(new Types.ConstructorDef(types), value);
    //
    //}

    public Tuple(Types.ConstructorDef types, Object value) {
        List<Codec> codecs = decodeTuple(types.getTypes(), value);
        this.addAll(codecs);
        this.types = types;
    }


    private static List<Codec> decodeTuple(List<Types.ConstructorCodec> types, Object value) {
        if (Utils.isU8a(value)) {
            ArrayList<Codec> results = Lists.newArrayList();
            CodecUtils.decodeU8a((byte[]) value, types, results);
            return results;
        } else if (Utils.isHex(value)) {
            return decodeTuple(types, Utils.hexToU8a((String) value));
        }

        List<?> list = CodecUtils.arrayLikeToList(value);
        ArrayList<Codec> ret = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < types.size(); i++) {
                ret.add(types.get(i).newInstance(list.get(i)));
            }
        } else {
            for (int i = 0; i < types.size(); i++) {
                ret.add(types.get(i).newInstance(value));
            }
        }
        return ret;
    }

    static class Builder implements Types.ConstructorCodec<Tuple> {

        Types.ConstructorDef types;

        public Builder(Types.ConstructorDef types) {
            this.types = types;
        }

        @Override
        public Tuple newInstance(Object... values) {
            return new Tuple(types, values[0]);
        }

        @Override
        public Class<Tuple> getTClass() {
            return Tuple.class;
        }
    }

    static Types.ConstructorCodec<Tuple> with(List<Types.ConstructorCodec> types) {
        return with(new Types.ConstructorDef(types));
    }

    static Types.ConstructorCodec<Tuple> with(Types.ConstructorDef types) {
        return new Builder(types);
    }

    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return this.stream().mapToInt(e -> e.getEncodedLength()).sum();
    }

    /**
     * The types definition of the tuple
     */
    public List<String> getTypes() {
        return this.types.getNames();
    }

    /**
     * Returns the string representation of the value
     */
    @Override
    public String toString() {
        // Overwrite the default toString representation of Array.
        return JSON.toJSONString(this.toJson());
    }


    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     * Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return Utils.u8aConcat(this.stream().map(e -> e.toU8a(isBare)).collect(Collectors.toList()));
    }
}
