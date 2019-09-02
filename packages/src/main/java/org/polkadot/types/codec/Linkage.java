package org.polkadot.types.codec;

import com.google.common.collect.Lists;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;

import java.util.List;

public class Linkage<T extends Codec> extends Struct {
    public Linkage(Types.ConstructorCodec type, Object value) {
        super(
                new Types.ConstructorDef()
                        .add("previous", Option.with(type))
                        .add("next", Option.with(type))
                , value
        );
    }

    static class Builder implements Types.ConstructorCodec<Linkage> {
        Types.ConstructorCodec type;

        public Builder(Types.ConstructorCodec type) {
            this.type = type;
        }

        @Override
        public Linkage newInstance(Object... values) {
            return new Linkage(type, values[0]);
        }

        @Override
        public Class<Linkage> getTClass() {
            return Linkage.class;
        }
    }

    public static <O extends Codec> Types.ConstructorCodec<Linkage> withKey(Types.ConstructorCodec type) {
        return new Builder(type);
    }


    public Option<T> getPrevious() {
        return this.getField("previous");
    }


    public Option<T> getNext() {
        return this.getField("next");
    }


    public static class LinkageResult extends Tuple {
        public LinkageResult(Types.ConstructorCodec typeKey, List<Object> keys, Types.ConstructorCodec typeValue, List<Object> values) {
            super(new Types.ConstructorDef()
                            .add("Keys", Vector.with(typeKey))
                            .add("Values", Vector.with(typeValue))
                    , Lists.newArrayList(keys, values));
        }
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "Linkage<" + this.getNext().toRawType(true) + ">";
    }
}
