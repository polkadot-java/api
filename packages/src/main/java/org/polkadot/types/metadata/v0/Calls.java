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

    }

    class OuterDispatchMetadata extends Struct {
        public OuterDispatchMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("calls", Vector.with(TypesUtils.getConstructorCodec(OuterDispatchCall.class)))
                    , value);
        }
    }


}
