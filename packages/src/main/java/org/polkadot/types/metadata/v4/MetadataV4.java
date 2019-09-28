package org.polkadot.types.metadata.v4;

import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.interfaces.metadata.Types.EventMetadataV4;
import org.polkadot.types.interfaces.metadata.Types.EventMetadataV5;
import org.polkadot.types.interfaces.metadata.Types.FunctionMetadataV4;
import org.polkadot.types.interfaces.metadata.Types.FunctionMetadataV5;
import org.polkadot.types.metadata.Types;
import org.polkadot.types.metadata.v4.Storage.StorageFunctionMetadataV4;
import org.polkadot.types.metadata.v5.Storage;
import org.polkadot.types.metadata.v5.Storage.StorageFunctionMetadataV5;
import org.polkadot.types.primitive.Text;


/**
 * @name MetadataV4
 * @description The runtime metadata as a decoded structure
 */
public class MetadataV4 extends Struct implements Types.MetadataInterface<MetadataV4.ModuleMetadataV4> {


    /**
     * @name ModuleMetadataV4
     * @description
     * The definition of a module in the system
     */
    public static class ModuleMetadataV4 extends Struct {
        public ModuleMetadataV4(Object value) {
            super(new ConstructorDef()
                            .add("name", Text.class)
                            .add("prefix", Text.class)
                            .add("storage", Option.with(Vec.with(TypesUtils.getConstructorCodec(StorageFunctionMetadataV4.class))))
                            .add("calls", Option.with(Vec.with(TypesUtils.getConstructorCodec(FunctionMetadataV4.class))))
                            .add("events", Option.with(Vec.with(TypesUtils.getConstructorCodec(EventMetadataV4.class))))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vec<FunctionMetadataV4>> getCalls() {
            return this.getField("calls");
        }


        /**
         * the module events
         */
        public Option<Vec<EventMetadataV4>> getEvents() {
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
        public Option<Vec<StorageFunctionMetadataV4>> getStorage() {
            return this.getField("storage");
        }
    }


    public MetadataV4(Object value) {
        super(new ConstructorDef()
                        .add("modules", Vec.with(TypesUtils.getConstructorCodec(MetadataV4.ModuleMetadataV4.class)))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    @Override
    public Vec<MetadataV4.ModuleMetadataV4> getVecModules() {
        return this.getField("modules");
    }

}
