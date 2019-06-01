package org.polkadot.types.type;


import org.polkadot.types.codec.U8aFixed;

/**
 * @name LockIdentifier
 * @description
 * The Substrate LockIdentifier for staking
 */
public class LockIdentifier extends U8aFixed {
    public LockIdentifier(Object value) {
        super(value, 64);
    }
}
