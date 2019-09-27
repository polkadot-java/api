package org.polkadot.types.metadata.v7;

import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.interfaces.metadata.Types.EventMetadataV7;
import org.polkadot.types.interfaces.metadata.Types.FunctionMetadataV7;
import org.polkadot.types.interfaces.metadata.Types.ModuleConstantMetadataV7;
import org.polkadot.types.metadata.Types;
import org.polkadot.types.primitive.Text;


/**
 * @name MetadataV7
 * @description The runtime metadata as a decoded structure
 */
public class MetadataV7 extends Struct implements Types.MetadataInterface<MetadataV7.ModuleMetadataV7> {


    /**
     * @name ModuleMetadataV7
     * @description The definition of a module in the system
     */
    public static class ModuleMetadataV7 extends Struct {
        public ModuleMetadataV7(Object value) {
            super(new ConstructorDef()
                            .add("name", Text.class)
                            .add("storage", Option.with(Vec.with(TypesUtils.getConstructorCodec(StorageMetadata.class))))
                            .add("calls", Option.with(Vec.with(TypesUtils.getConstructorCodec(FunctionMetadataV7.class))))
                            .add("constants", Vec.with(TypesUtils.getConstructorCodec(ModuleConstantMetadataV7.class)))
                            .add("events", Option.with(Vec.with(TypesUtils.getConstructorCodec(EventMetadataV7.class))))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vec<FunctionMetadataV7>> getCalls() {
            return this.getField("calls");
        }

        /**
         * the module events
         */
        public Option<Vec<EventMetadataV7>> getEvents() {
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
        public Option<Vector<StorageMetadata>> getStorage() {
            return this.getField("storage");
        }
    }


    public MetadataV7(Object value) {
        super(new ConstructorDef()
                        .add("modules", Vec.with(TypesUtils.getConstructorCodec(MetadataV7.ModuleMetadataV7.class)))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    @Override
    public Vec<MetadataV7.ModuleMetadataV7> getVecModules() {
        return this.getField("modules");
    }

}
