package org.polkadot.types.type;

import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.Method;

/**
 * @name Proposal
 * @description A proposal in the system. It just extends [[Method]] (Proposal = Call in Rust)
 */
public class Proposal extends Method {
    public Proposal(Object value, Modules.FunctionMetadata meta) {
        super(value, meta);
    }
}
