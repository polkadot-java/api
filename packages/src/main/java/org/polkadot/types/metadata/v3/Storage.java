package org.polkadot.types.metadata.v3;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v1.Storage.MetadataStorageModifier;
import org.polkadot.types.metadata.v2.Storage.MapType;
import org.polkadot.types.metadata.v2.Storage.PlainType;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Text;

public interface Storage {


    class DoubleMapType extends Struct {
        public DoubleMapType(Object value) {
            super(new Types.ConstructorDef()
                            .add("key1", Text.class)
                            .add("key2", Text.class)
                            .add("value", Text.class)
                            .add("keyHasher", Text.class)
                    , value);
        }

        /**
         * @description The mapped key as [[Text]]
         */
        public Text getKey1() {
            return this.getField("key1");
        }

        /**
         * @description The mapped key as [[Text]]
         */
        public Text getKey2() {
            return this.getField("key2");
        }

        /**
         * @description The mapped key as [[Text]]
         */
        public Text getKeyHasher() {
            return this.getField("keyHasher");
        }

        /**
         * @description The mapped key as [[Text]]
         */
        public Text getValue() {
            return this.getField("value");
        }
    }


    //EnumType<PlainType | MapType | DoubleMapType>
    class MetadataStorageType extends EnumType {
        public MetadataStorageType(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("PlainType", PlainType.class)
                            .add("MapType", MapType.class)
                            .add("DoubleMapType", DoubleMapType.class)
                    , value, index, null);
        }

        public MetadataStorageType(Object value) {
            this(value, -1);
        }


        /**
         * @description The value as a mapped value
         */
        public DoubleMapType asDoubleMap() {
            return (DoubleMapType) this.value();
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
         * @description The value as a mapped value
         */
        public MapType asMap() {
            return (MapType) this.value();
        }

        /**
         * @description The value as a [[Type]] value
         */
        public PlainType asType() {
            return (PlainType) this.value();
        }

        /**
         * @description Returns the string representation of the value
         */


        @Override
        public String toString() {

            if (this.isDoubleMap()) {
                return this.asDoubleMap().toString();
            }

            return this.isMap()
                    ? this.asMap().getValue().toString()
                    : this.asType().toString();
        }
    }




    /**
     * @name MetadataModule
     * @description The definition of a storage function
     */
    class MetadataStorage extends Struct {
        public MetadataStorage(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", MetadataStorageModifier.class)
                            .add("type", MetadataStorageType.class)
                            .add("fallback", Bytes.class)
                            .add("docs", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * @description The [[Text]] documentation
         */
        public Vector<Text> getDocs() {
            return this.getField("docs");
        }

        /**
         * @description The [[Bytes]] fallback default
         */
        public Bytes getFallback() {
            return this.getField("fallback");
        }

        /**
         * @description The [[MetadataArgument]] for arguments
         */
        public MetadataStorageModifier getModifier() {
            return this.getField("modifier");
        }

        /**
         * @description The call name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * @description The [[MetadataStorageType]]
         */
        public MetadataStorageType getType() {
            return this.getField("type");
        }
    }


}
