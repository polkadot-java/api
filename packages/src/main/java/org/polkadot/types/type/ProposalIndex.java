package org.polkadot.types.type;


import org.polkadot.types.primitive.U32;

/**
 * @name ProposalIndex
 * @description
 * An increasing number that represents a specific council proposal index in
 * the system, implemented as [[U32]]
 */
public class ProposalIndex extends U32 {
    public ProposalIndex(Object value) {
        super(value);
    }
}
