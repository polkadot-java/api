package org.polkadot.types.primitive.extrinsic;

import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.extrinsic.Extrinsic;

/**
 * A {@link org.polkadot.types.codec.Vector} of {@link org.polkadot.type.extrinsics}
 */
public class Extrinsics extends Vector<Extrinsic> {
    public Extrinsics(Object value) {
        super(TypesUtils.getConstructorCodec(Extrinsic.class), value);
    }
}
