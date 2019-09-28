package org.polkadot.types.metadata.v5;

import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.interfaces.metadata.Types.EventMetadataV5;
import org.polkadot.types.interfaces.metadata.Types.FunctionMetadataV5;
import org.polkadot.types.metadata.Types;
import org.polkadot.types.metadata.v5.Storage.StorageFunctionMetadataV5;
import org.polkadot.types.primitive.Text;


/**
 * @name MetadataV5
 * @description The runtime metadata as a decoded structure
 */
public class MetadataV5 extends Struct implements Types.MetadataInterface<MetadataV5.ModuleMetadataV5> {


    /**
     * @name ModuleMetadataV5
     * @description The definition of a module in the system
     */
    public static class ModuleMetadataV5 extends Struct {
        public ModuleMetadataV5(Object value) {
            super(new ConstructorDef()
                            .add("name", Text.class)
                            .add("prefix", Text.class)
                            .add("storage", Option.with(Vec.with(TypesUtils.getConstructorCodec(Storage.StorageFunctionMetadataV5.class))))
                            .add("calls", Option.with(Vec.with(TypesUtils.getConstructorCodec(FunctionMetadataV5.class))))
                            .add("events", Option.with(Vec.with(TypesUtils.getConstructorCodec(EventMetadataV5.class))))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vec<FunctionMetadataV5>> getCalls() {
            return this.getField("calls");
        }


        /**
         * the module events
         */
        public Option<Vec<EventMetadataV5>> getEvents() {
            return this.getField("events");
        }

        /**
         * the module name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * the module prefix
         */
        public Text getPrefix() {
            return this.getField("prefix");
        }

        /**
         * the associated module storage
         */
        public Option<Vec<StorageFunctionMetadataV5>> getStorage() {
            return this.getField("storage");
        }
    }


    public MetadataV5(Object value) {
        super(new ConstructorDef()
                        .add("modules", Vec.with(TypesUtils.getConstructorCodec(MetadataV5.ModuleMetadataV5.class)))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    @Override
    public Vec<MetadataV5.ModuleMetadataV5> getVecModules() {
        return this.getField("modules");
    }

}
