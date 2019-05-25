package org.polkadot.types.metadata.v0;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.U16;

public interface Calls {

    class OuterDispatchCall extends Struct {
        public OuterDispatchCall(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("prefix", Text.class)
                            .add("index", U16.class)
                    , value);
        }


        /**
         * The [[U16]] index for the call
         */
        public U16 getIndex() {
            return this.getField("index");
        }

        /**
         * The name for the call
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * The call prefix (or section)
         */
        public Text getPrefix() {
            return this.getField("prefix");
        }


    }

    class OuterDispatchMetadata extends Struct {
        public OuterDispatchMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("calls", Vector.with(TypesUtils.getConstructorCodec(OuterDispatchCall.class)))
                    , value);
        }


        /**
         * The [[OuterDispathCall]] wrapped
         */
        public Vector<OuterDispatchCall> getCalls() {
            return this.getField("calls");
        }

        /**
         * The name for the dispatch
         */
        public Text getName() {
            return this.getField("name");
        }
    }


}
