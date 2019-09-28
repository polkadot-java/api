package org.polkadot.types.metadata.v6;

import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.interfaces.metadata.Types.EventMetadataV6;
import org.polkadot.types.interfaces.metadata.Types.FunctionMetadataV6;
import org.polkadot.types.interfaces.metadata.Types.ModuleConstantMetadataV6;
import org.polkadot.types.metadata.Types;
import org.polkadot.types.metadata.v5.Storage.StorageFunctionMetadataV5;
import org.polkadot.types.primitive.Text;


/**
 * @name MetadataV6
 * @description The runtime metadata as a decoded structure
 */
public class MetadataV6 extends Struct implements Types.MetadataInterface<MetadataV6.ModuleMetadataV6> {


    /**
     * @name ModuleMetadataV6
     * @description The definition of a module in the system
     */
    public static class ModuleMetadataV6 extends Struct {
        public ModuleMetadataV6(Object value) {
            super(new ConstructorDef()
                            .add("name", Text.class)
                            .add("prefix", Text.class)
                            .add("storage", Option.with(Vec.with(TypesUtils.getConstructorCodec(StorageFunctionMetadataV5.class))))
                            .add("calls", Option.with(Vec.with(TypesUtils.getConstructorCodec(FunctionMetadataV6.class))))
                            .add("constants", Vec.with(TypesUtils.getConstructorCodec(ModuleConstantMetadataV6.class)))
                            .add("events", Option.with(Vec.with(TypesUtils.getConstructorCodec(EventMetadataV6.class))))

                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vec<FunctionMetadataV6>> getCalls() {
            return this.getField("calls");
        }


        /**
         * the module constants
         */
        public Option<Vec<ModuleConstantMetadataV6>> getConstants() {
            return this.getField("constants");
        }

        /**
         * the module events
         */
        public Option<Vec<EventMetadataV6>> getEvents() {
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


    public MetadataV6(Object value) {
        super(new ConstructorDef()
                        .add("modules", Vec.with(TypesUtils.getConstructorCodec(MetadataV6.ModuleMetadataV6.class)))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    @Override
    public Vec<MetadataV6.ModuleMetadataV6> getVecModules() {
        return this.getField("modules");
    }

}
