package org.polkadot.types.metadata.v0;

import com.google.common.collect.Lists;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Enum;
import org.polkadot.types.codec.*;
import org.polkadot.types.metadata.v1.Storage;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;
import org.polkadot.types.primitive.U16;

import java.util.Map;

public interface Modules {

    class FunctionArgumentMetadata extends Struct {
        public FunctionArgumentMetadata(Object value) {
            super(new Types.ConstructorDef()
                    .add("name", Text.class)
                    .add("type", Type.class), value);
        }

        /**
         * The argument name
         */
        public Text getName() {
            return (Text) this.get("name");
        }

        /**
         * The {@link org.polkadot.types.type}
         */
        public Type getType() {
            return (Type) this.get("type");
        }
    }

    class FunctionMetadata extends Struct {
        public FunctionMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("id", U16.class)
                            .add("name", Text.class)
                            .add("arguments", Vector.with(TypesUtils.getConstructorCodec(FunctionArgumentMetadata.class)))
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }


        /**
         * The FunctionArgumentMetadata for arguments
         */
        public Vector<FunctionArgumentMetadata> getArguments() {
            return this.getField("arguments");
        }

        /**
         * The {@link org.polkadot.types.primitive.Text} documentation
         */
        public Vector<Text> getDocumentation() {
            return this.getField("documentation");
        }

        /**
         * The `[sectionIndex, methodIndex]` call id
         */
        public U16 getId() {
            return this.getField("id");
        }

        /**
         * The call name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

    class CallMetadata extends Struct {
        public CallMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("functions", Vector.with(TypesUtils.getConstructorCodec(FunctionMetadata.class)))
                    , value);
        }


        /**
         * The functions available as FunctionMetadata
         */
        public Vector<FunctionMetadata> getFunctions() {
            return this.getField("functions");
        }

        /**
         * The section name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

    class ModuleMetadata extends Struct {
        public ModuleMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("call", CallMetadata.class)
                    , value);
        }


        /**
         * The calls as CallMetadata
         */
        public CallMetadata getCall() {
            return this.getField("call");
        }

        /**
         * The name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

    class StorageFunctionModifier extends Enum {
        public StorageFunctionModifier(Object value) {
            super(Lists.newArrayList("Optional", "Default", "Required")
                    , value);
        }

        /**
         * `true` if the storage entry is optional
         */
        public boolean isOptional() {
            return this.toNumber() == 0;
        }

    }

    class MapType extends Struct {
        private boolean isLinked = false;

        public MapType(Object value) {
            super(new Types.ConstructorDef()
                            .add("key", Type.class)
                            .add("value", Type.class)
                    , value);



            if (value instanceof Storage.MapType) {
                Boolean linked = ((Storage.MapType) value).isLinked();
                if (linked){
                    this.isLinked = true;
                }
            }

            if (value != null && value instanceof MapType) {
                if (((MapType) value).isLinked) {
                    this.isLinked = true;
                }
            }
        }

        public boolean isLinked() {
            return isLinked;
        }

        public Type getKey() {
            return this.getField("key");
        }

        public Type getValue() {
            return this.getField("value");
        }
    }


    class PlainType extends Type {
        public PlainType(Object value) {
            super(value);
        }
    }

    //export class StorageFunctionType extends EnumType<PlainType | MapType> {
    class StorageFunctionType extends EnumType {
        public StorageFunctionType(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("PlainType", PlainType.class)
                            .add("MapType", MapType.class)
                    , value, index, null);
        }

        public StorageFunctionType(Object value) {
            this(value, -1);
        }

        /**
         * `true` if the storage entry is a map
         */
        public boolean isMap() {
            return this.toNumber() == 1;
        }

        /**
         * The value as a mapped value
         */
        public MapType asMap() {
            return (MapType) this.value();
        }

        /**
         * The value as a {@link org.polkadot.types.type} value
         */
        //TODO 2019-05-08 1819 cast error
        public PlainType asType() {
            return (PlainType) this.value();
        }

        /**
         * Returns the string representation of the value
         */
        @Override
        public String toString() {
            if (this.isMap()) {
                MapType mapType = this.asMap();
                if (mapType.isLinked) {
                    return "(" + mapType.getField("value").toString() + ", Linkage<" + mapType.getField("key").toString() + ">)";
                }
                return mapType.getField("value").toString();
            }
            return this.asType().toString();
        }
    }

    class StorageFunctionMetadataValue {
        /**
         * name string | Text,
         * modifier StorageFunctionModifier | AnyNumber,
         * type StorageFunctionType,
         * default Bytes,
         * documentation Vector<Text> | Array<string>
         */

        String name;
        StorageFunctionModifier modifier;
        StorageFunctionType type;
        Bytes defalut;
        Vector<Text> documentation;

    }

    class StorageFunctionMetadata extends Struct {
        //  constructor (value? StorageFunctionMetadataValue | Uint8Array) {
        public StorageFunctionMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", StorageFunctionModifier.class)
                            .add("type", StorageFunctionType.class)
                            .add("default", Bytes.class)
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        public StorageFunctionType getType() {
            return this.getField("type");
        }

        public Text getName() {
            return this.getField("name");
        }

        public StorageFunctionModifier getModifier() {
            return this.getField("modifier");
        }

        public Bytes getDefault() {
            return this.getField("default");
        }

        public Vector<Text> getDocumentation() {
            return this.getField("documentation");
        }
    }

    class StorageMetadata extends Struct {
        public StorageMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("prefix", Text.class)
                            .add("functions", Vector.with(TypesUtils.getConstructorCodec(StorageFunctionMetadata.class)))
                    , value);
        }

        public Text getPrefix() {
            return this.getField("prefix");
        }

        public Vector<StorageFunctionMetadata> getFunctions() {
            return this.getField("functions");
        }
    }

    class RuntimeModuleMetadata extends Struct {
        public RuntimeModuleMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("prefix", Text.class)
                            .add("module", ModuleMetadata.class)
                            .add("storage", Option.with(TypesUtils.getConstructorCodec(StorageMetadata.class)))
                    , value);
        }

        public ModuleMetadata getModule() {
            return this.getField("module");
        }

        public Text getPrefix() {
            return this.getField("prefix");
        }

        public Option<StorageMetadata> getStorage() {
            return this.getField("storage");
        }
    }


}
