package org.polkadot.types.metadata.v1;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;

public interface Calls {

    class MetadataCallArg extends Struct {
        public MetadataCallArg(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("type", Type.class)
                    , value);
        }

        /**
         * @description The argument name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * @description The [[Type]]
         */
        public Type getType() {
            return this.getField("type");
        }
    }


    /**
     * @name MetadataCall
     * @description The definition of a call
     */
    class MetadataCall extends Struct {
        public MetadataCall(Object value) {
            super(new Types.ConstructorDef()
                            // id: u16,
                            .add("name", Text.class)
                            .add("args", Vector.with(TypesUtils.getConstructorCodec(MetadataCallArg.class)))
                            .add("docs", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }


        /**
         * @description The [[MetadataCallArg]] for arguments
         */
        public Vector<MetadataCallArg> getArgs() {
            return this.getField("args");
        }

        /**
         * @description The [[Text]] documentation
         */
        public Vector<Text> getDocs() {
            return this.getField("docs");
        }

        // /**
        //  * @description The call function id
        //  */
        // get id () u16 {
        //   return this.getField("id") as u16;
        // }

        /**
         * @description The call name
         */
        public Text getName() {
            return this.getField("name");
        }

    }


}
