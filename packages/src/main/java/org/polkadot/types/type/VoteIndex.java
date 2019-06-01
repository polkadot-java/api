package org.polkadot.types.type;


import org.polkadot.types.primitive.U32;

/**
 * @name VoteIndex
 * @description
 * Voting index, implemented as a [[U32]]
 */
public class VoteIndex extends U32 {
    public VoteIndex(Object value) {
        super(value);
    }
}
