package org.polkadot.types.type;

import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Vector;

/**
 * @name Extrinsics
 * @description A [[Vector]] of [[Extrinsic]]
 */
public class Extrinsics extends Vector<Extrinsic> {
    public Extrinsics(Object value) {
        super(TypesUtils.getConstructorCodec(Extrinsic.class), value);
    }
}
