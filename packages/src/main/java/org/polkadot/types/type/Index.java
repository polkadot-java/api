package org.polkadot.types.type;

import org.polkadot.types.primitive.U64;

//
// NOTE Nonce is renamed to Index
//export { default as Index } from './Nonce';

/**
 * @name Index
 * @description The Nonce or number of transactions sent by a specific account. Generally used
 * with extrinsics to determine the order of execution. implemented as a Substrate
 * [[U64]]
 * @see Nonce
 */
public class Index extends U64 {
    public Index(Object value) {
        super(value);
    }
}
