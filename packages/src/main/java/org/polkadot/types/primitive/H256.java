package org.polkadot.types.primitive;

import org.polkadot.types.codec.U8aFixed;

/**
 * @name H256
 * Hash containing 256 bits (32 bytes), typically used in blocks, extrinsics and
 * as a sane default for fixed-length hash representations.
 */
public class H256 extends U8aFixed {
    //  constructor (value?: AnyU8a) {
    public H256(Object value) {
        super(value, 256);
    }

}
