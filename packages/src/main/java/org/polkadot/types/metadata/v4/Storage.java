package org.polkadot.types.metadata.v4;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.interfaces.metadata.Types.DoubleMapTypeV4;
import org.polkadot.types.interfaces.metadata.Types.MapTypeV4;
import org.polkadot.types.interfaces.metadata.Types.PlainTypeV4;
import org.polkadot.types.interfaces.metadata.Types.StorageFunctionModifierV4;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Text;

public interface Storage {

    class StorageFunctionTypeV4 extends EnumType {
        public StorageFunctionTypeV4(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("Type", PlainTypeV4.class)
                            .add("Map", MapTypeV4.class)
                            .add("DoubleMap", DoubleMapTypeV4.class)
                    , value, index,
                    null);
        }

        /**
         * @description The value as a mapped value
         */
        public DoubleMapTypeV4 asDoubleMap() {

            return (DoubleMapTypeV4) this.value();
        }

        /**
         * @description The value as a mapped value
         */
        public MapTypeV4 asMap() {
            return (MapTypeV4) this.value();
        }

        /**
         * @description The value as a [[Type]] value
         */
        public PlainTypeV4 asType() {

            return (PlainTypeV4) this.value();
        }

        /**
         * @description `true` if the storage entry is a doublemap
         */
        public boolean isDoubleMap() {
            return this.toNumber() == 2;
        }

        /**
         * @description `true` if the storage entry is a map
         */
        public boolean isMap() {
            return this.toNumber() == 1;
        }

        /**
         * @description `true` if the storage entry is a plain type
         */
        public boolean isPlainType() {
            return this.toNumber() == 0;
        }

        /**
         * @description Returns the string representation of the value
         */
        @Override
        public String toString() {
            if (this.isDoubleMap()) {
                return "DoubleMap<" + this.asDoubleMap().value.toString() + ">";
            }

            if (this.isMap()) {
                if (this.asMap().linked) {
                    return "(" + this.asMap().value.toString() + ", Linkage<" + this.asMap().key.toString() + ">)";
                }
                return this.asMap().value.toString();
            }
            return this.asType().toString();
        }
    }


    class StorageFunctionMetadataValueV4 {
        //name string | Text;
        //modifier StorageFunctionModifierV5 | AnyNumber;
        //type StorageFunctionTypeV5;
        //fallback Bytes;
        //documentation Vec<Text> | string[];
        String name;
        Object modifier;
        StorageFunctionTypeV4 type;
        Bytes fallback;
        Object documentation;
    }

    /*


public class StorageHasher extends Enum {
    public StorageHasher(Object value) {
        super(Lists.newArrayList(
                "Blake2_128",
                "Blake2_256",
                "Twox128",
                "Twox256",
                "Twox64Concat"),
                value);
    }


     *
     */


    /**
     * @name StorageFunctionMetadataV4
     * @description The definition of a storage function
     */
    class StorageFunctionMetadataV4 extends Struct {


        public StorageFunctionMetadataV4(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", StorageFunctionModifierV4.class)
                            .add("type", StorageFunctionTypeV4.class)
                            .add("fallback", Bytes.class)
                            .add("documentation", Vec.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }


        /**
         * @description The default value of the storage function
         */
        public Bytes getFallback() {
            return this.getField("fallback");
        }

        /**
         * @description The [[Text]] documentation
         */
        public Vec<Text> getDocumentation() {
            return this.getField("documentation");
        }

        /**
         * @description The key name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * @description The modifier
         */
        public StorageFunctionModifierV4 getModifier() {
            return this.getField("modifier");
        }

        /**
         * @description The [[StorageFunctionTypeV5]]
         */
        public StorageFunctionTypeV4 getType() {
            return this.getField("type");
        }

    }


}
