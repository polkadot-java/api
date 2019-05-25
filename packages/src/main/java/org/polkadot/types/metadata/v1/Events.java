package org.polkadot.types.metadata.v1;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;

public interface Events {


    /**
     * @name MetadataEvent
     * The definition of an event
     */
    class MetadataEvent extends Struct {

        public MetadataEvent(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("args", Vector.with(TypesUtils.getConstructorCodec(Type.class)))
                            .add("docs", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }


        /**
         * The [[Type]] for args
         */
        public Vector<Type> getArgs() {
            return this.getField("args");
        }

        /**
         * The [[Text]] documentation
         */
        public Vector<Text> getDocs() {
            return this.getField("docs");
        }

        /**
         * The call name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

}
