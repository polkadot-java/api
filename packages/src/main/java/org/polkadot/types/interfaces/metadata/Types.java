package org.polkadot.types.interfaces.metadata;

import org.polkadot.types.Codec;
import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.*;

import java.util.List;
import java.util.Map;

public interface Types {

    /**
     * Struct
     */
    class CallMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Vec<FunctionMetadataV0>
         */
        public Vec<FunctionMetadataV0> functions;

        public CallMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class DoubleMapTypeV3 extends Struct {
        /**
         * Type
         */
        public Type key1;
        /**
         * Type
         */
        public Type key2;
        /**
         * Type
         */
        public Type value;
        /**
         * Text
         */
        public Text key2Hasher;

        public DoubleMapTypeV3(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class DoubleMapTypeV4 extends Struct {
        /**
         * StorageHasher
         */
        public StorageHasher hasher;
        /**
         * Type
         */
        public Type key1;
        /**
         * Type
         */
        public Type key2;
        /**
         * Type
         */
        public Type value;
        /**
         * Text
         */
        public Text key2Hasher;

        public DoubleMapTypeV4(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class DoubleMapTypeV5 extends Struct {
        /**
         * StorageHasher
         */
        public StorageHasher hasher;
        /**
         * Type
         */
        public Type key1;
        /**
         * Type
         */
        public Type key2;
        /**
         * Type
         */
        public Type value;
        /**
         * StorageHasher
         */
        public StorageHasher key2Hasher;

        public DoubleMapTypeV5(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * DoubleMapTypeV5
     */
    class DoubleMapTypeV6 extends DoubleMapTypeV5 {

        public DoubleMapTypeV6(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * DoubleMapTypeV6
     */
    class DoubleMapTypeV7 extends DoubleMapTypeV6 {
        public DoubleMapTypeV7(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class EventMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Vec<Type>
         */
        public Vec<Type> args;
        /**
         * Vec<Text>
         */
        public Vec<Text> documentation;

        public EventMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * EventMetadataV0
     */
    class EventMetadataV1 extends EventMetadataV0 {
        public EventMetadataV1(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * EventMetadataV1
     */
    class EventMetadataV2 extends EventMetadataV1 {
        public EventMetadataV2(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * EventMetadataV2
     */
    class EventMetadataV3 extends EventMetadataV2 {
        public EventMetadataV3(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * EventMetadataV3
     */
    class EventMetadataV4 extends EventMetadataV3 {
        public EventMetadataV4(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * EventMetadataV4
     */
    class EventMetadataV5 extends EventMetadataV4 {
        public EventMetadataV5(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * EventMetadataV5
     */
    class EventMetadataV6 extends EventMetadataV5 {
        public EventMetadataV6(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * EventMetadataV6
     */
    class EventMetadataV7 extends EventMetadataV6 {
        public EventMetadataV7(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class FunctionArgumentMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Type
         */
        public Type type;

        public FunctionArgumentMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionArgumentMetadataV0
     */
    class FunctionArgumentMetadataV1 extends FunctionArgumentMetadataV0 {
        public FunctionArgumentMetadataV1(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionArgumentMetadataV1
     */
    class FunctionArgumentMetadataV2 extends FunctionArgumentMetadataV1 {
        public FunctionArgumentMetadataV2(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionArgumentMetadataV2
     */
    class FunctionArgumentMetadataV3 extends FunctionArgumentMetadataV2 {
        public FunctionArgumentMetadataV3(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionArgumentMetadataV3
     */
    class FunctionArgumentMetadataV4 extends FunctionArgumentMetadataV3 {
        public FunctionArgumentMetadataV4(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionArgumentMetadataV4
     */
    class FunctionArgumentMetadataV5 extends FunctionArgumentMetadataV4 {
        public FunctionArgumentMetadataV5(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionArgumentMetadataV5
     */
    class FunctionArgumentMetadataV6 extends FunctionArgumentMetadataV5 {
        public FunctionArgumentMetadataV6(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionArgumentMetadataV6
     */
    class FunctionArgumentMetadataV7 extends FunctionArgumentMetadataV6 {
        public FunctionArgumentMetadataV7(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class FunctionMetadataV0 extends Struct {
        /**
         * u16
         */
        public U16 id;
        /**
         * Text
         */
        public Text name;
        /**
         * Vec<FunctionArgumentMetadataV0>
         */
        public Vec<FunctionArgumentMetadataV0> args;
        /**
         * Vec<Text>
         */
        public Vec<Text> documentation;

        public FunctionMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class FunctionMetadataV1 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Vec<FunctionArgumentMetadataV1>
         */
        public Vec<FunctionArgumentMetadataV1> args;
        /**
         * Vec<Text>
         */
        public Vec<Text> documentation;

        public FunctionMetadataV1(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionMetadataV1
     */
    class FunctionMetadataV2 extends FunctionMetadataV1 {
        public FunctionMetadataV2(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionMetadataV2
     */
    class FunctionMetadataV3 extends FunctionMetadataV2 {
        public FunctionMetadataV3(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionMetadataV3
     */
    class FunctionMetadataV4 extends FunctionMetadataV3 {
        public FunctionMetadataV4(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionMetadataV4
     */
    class FunctionMetadataV5 extends FunctionMetadataV4 {
        public FunctionMetadataV5(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionMetadataV5
     */
    class FunctionMetadataV6 extends FunctionMetadataV5 {
        public FunctionMetadataV6(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * FunctionMetadataV6
     */
    class FunctionMetadataV7 extends FunctionMetadataV6 {
        public FunctionMetadataV7(Object value) {
            //TODO 2019-09-28 04:13 check
            super(null, value, null);
        }
        //TODO 2019-09-28 04:18 check
        /**
         * The FunctionArgumentMetadata for arguments
         */
        public Vector<Modules.FunctionArgumentMetadata> getArguments() {
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

    /**
     * Struct
     */
    class MapTypeV0 extends Struct {
        /**
         * Type
         */
        public Type key;
        /**
         * Type
         */
        public Type value;

        public MapTypeV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class MapTypeV2 extends Struct {
        /**
         * Type
         */
        public Type key;
        /**
         * Type
         */
        public Type value;
        /**
         * bool
         */
        public boolean linked;

        public MapTypeV2(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * MapTypeV2
     */
    class MapTypeV3 extends MapTypeV2 {
        public MapTypeV3(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class MapTypeV4 extends Struct {
        /**
         * StorageHasher
         */
        public StorageHasher hasher;
        /**
         * Type
         */
        public Type key;
        /**
         * Type
         */
        public Type value;
        /**
         * bool
         */
        public boolean linked;

        public MapTypeV4(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * MapTypeV4
     */
    class MapTypeV5 extends MapTypeV4 {
        public MapTypeV5(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * MapTypeV5
     */
    class MapTypeV6 extends MapTypeV5 {
        public MapTypeV6(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * MapTypeV6
     */
    class MapTypeV7 extends MapTypeV6 {
        public MapTypeV7(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class MetadataV0 extends Struct {
        /**
         * OuterEventMetadataV0
         */
        public OuterEventMetadataV0 outerEvent;
        /**
         * Vec<RuntimeModuleMetadataV0>
         */
        public Vec<RuntimeModuleMetadataV0> modules;
        /**
         * OuterDispatchMetadataV0
         */
        public OuterDispatchMetadataV0 outerDispatch;

        public MetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class MetadataV1 extends Struct {
        /**
         * Vec<ModuleMetadataV1>
         */
        public Vec<ModuleMetadataV1> modules;

        public MetadataV1(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class ModuleConstantMetadataV6 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Type
         */
        public Type type;
        /**
         * Bytes
         */
        public Bytes value;
        /**
         * Vec<Text>
         */
        public Vec<Text> documentation;

        public ModuleConstantMetadataV6(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * ModuleConstantMetadataV6
     */
    class ModuleConstantMetadataV7 extends ModuleConstantMetadataV6 {
        public ModuleConstantMetadataV7(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class ModuleMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * CallMetadataV0
         */
        public CallMetadataV0 call;

        public ModuleMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class ModuleMetadataV1 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Text
         */
        public Text prefix;
        /**
         * Option<Vec<StorageFunctionMetadataV1>>
         */
        public Option<Vec<StorageFunctionMetadataV1>> storage;
        /**
         * Option<Vec<FunctionMetadataV1>>
         */
        public Option<Vec<FunctionMetadataV1>> calls;
        /**
         * Option<Vec<EventMetadataV1>>
         */
        public Option<Vec<EventMetadataV1>> events;

        public ModuleMetadataV1(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class OuterDispatchCallV0 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Text
         */
        public Text prefix;
        /**
         * u16
         */
        public U16 index;

        public OuterDispatchCallV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Struct
     */
    class OuterDispatchMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Vec<OuterDispatchCallV0>
         */
        public Vec<OuterDispatchCallV0> calls;

        public OuterDispatchMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Vec<EventMetadataV0>
     */
    class OuterEventEventMetadataEventsV0 extends Vec<EventMetadataV0> {
        public OuterEventEventMetadataEventsV0(org.polkadot.types.Types.ConstructorCodec<EventMetadataV0> type, Object value) {
            super(type, value);
        }
    }

    /**
     * [Text, OuterEventEventMetadataEventsV0] & Codec
     */
    //class OuterEventEventMetadataV0 extends [Text, OuterEventEventMetadataEventsV0] & Codec;
    class OuterEventEventMetadataV0 extends OuterEventEventMetadataEventsV0 implements Codec {

        public OuterEventEventMetadataV0(org.polkadot.types.Types.ConstructorCodec<EventMetadataV0> type, Object value) {
            super(type, value);
        }
    }


    /**
     * Struct
     */
    class OuterEventMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * Vec<OuterEventEventMetadataV0>
         */
        public Vec<OuterEventEventMetadataV0> events;

        public OuterEventMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Type
     */
    class PlainTypeV0 extends Type {
        public PlainTypeV0(Object value) {
            super(value);
        }
    }

    /**
     * Type
     */
    class PlainTypeV2 extends Type {
        public PlainTypeV2(Object value) {
            super(value);
        }
    }

    /**
     * Type
     */
    class PlainTypeV3 extends Type {
        public PlainTypeV3(Object value) {
            super(value);
        }
    }

    /**
     * Type
     */
    class PlainTypeV4 extends Type {
        public PlainTypeV4(Object value) {
            super(value);
        }
    }

    /**
     * Type
     */
    class PlainTypeV5 extends Type {
        public PlainTypeV5(Object value) {
            super(value);
        }
    }

    /**
     * Type
     */
    class PlainTypeV6 extends Type {
        public PlainTypeV6(Object value) {
            super(value);
        }
    }

    /**
     * Type
     */
    class PlainTypeV7 extends Type {
        public PlainTypeV7(Object value) {
            super(value);
        }
    }

    /**
     * Struct
     */
    class RuntimeModuleMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text prefix;
        /**
         * ModuleMetadataV0
         */
        public ModuleMetadataV0 module;
        /**
         * Option<StorageMetadataV0>
         */
        public Option<StorageMetadataV0> storage;

        public RuntimeModuleMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * StorageFunctionModifierV5
     */
    class StorageEntryModifierV6 extends StorageFunctionModifierV5 {
        public StorageEntryModifierV6(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * StorageEntryModifierV6
     */
    class StorageEntryModifierV7 extends StorageEntryModifierV6 {
        public StorageEntryModifierV7(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * Struct
     */
    class StorageFunctionMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text name;
        /**
         * StorageFunctionModifierV0
         */
        public StorageFunctionModifierV0 modifier;
        /**
         * StorageFunctionTypeV0
         */
        public StorageFunctionTypeV0 type;
        /**
         * Bytes
         */
        public Bytes fallback;
        /**
         * Vec<Text>
         */
        public Vec<Text> documentation;

        public StorageFunctionMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * StorageFunctionMetadataV0
     */
    class StorageFunctionMetadataV1 extends StorageFunctionMetadataV0 {
        public StorageFunctionMetadataV1(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

    /**
     * Enum
     */
    class StorageFunctionModifierV0 extends org.polkadot.types.codec.Enum {
        /**
         * 0 Optional
         */
        public boolean isOptional;
        /**
         * 1 Default
         */
        public boolean isDefault;
        /**
         * 2 Required
         */
        public boolean isRequired;

        public StorageFunctionModifierV0(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * StorageFunctionModifierV0
     */
    class StorageFunctionModifierV1 extends StorageFunctionModifierV0 {
        public StorageFunctionModifierV1(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * StorageFunctionModifierV1
     */
    class StorageFunctionModifierV2 extends StorageFunctionModifierV1 {
        public StorageFunctionModifierV2(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * StorageFunctionModifierV2
     */
    class StorageFunctionModifierV3 extends StorageFunctionModifierV2 {
        public StorageFunctionModifierV3(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * StorageFunctionModifierV3
     */
    class StorageFunctionModifierV4 extends StorageFunctionModifierV3 {
        public StorageFunctionModifierV4(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * StorageFunctionModifierV4
     */
    class StorageFunctionModifierV5 extends StorageFunctionModifierV4 {
        public StorageFunctionModifierV5(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * Enum
     */
    class StorageFunctionTypeV0 extends org.polkadot.types.codec.Enum {
        /**
         * 0 Type(PlainTypeV0)
         */
        public boolean isType;
        /**
         * PlainTypeV0
         */
        public PlainTypeV0 asType;
        /**
         * 1 Map(MapTypeV0)
         */
        public boolean isMap;
        /**
         * MapTypeV0
         */
        public MapTypeV0 asMap;

        public StorageFunctionTypeV0(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * StorageFunctionTypeV0
     */
    class StorageFunctionTypeV1 extends StorageFunctionTypeV0 {
        public StorageFunctionTypeV1(List<String> def, Object value) {
            super(def, value);
        }
    }

    /**
     * Struct
     */
    class StorageMetadataV0 extends Struct {
        /**
         * Text
         */
        public Text prefix;
        /**
         * Vec<StorageFunctionMetadataV0>
         */
        public Vec<StorageFunctionMetadataV0> functions;

        public StorageMetadataV0(ConstructorDef constructorDef, Object value, Map<String, String> json) {
            super(constructorDef, value, json);
        }
    }

}
