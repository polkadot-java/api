package org.polkadot.types.type;

import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;


/**
 * @name Nonce
 * The Compact<Nonce> or number of transactions sent by a specific account. Generally used
 * with extrinsics to determine the order of execution.
 */
//export default class NonceCompact extends Compact.with(Nonce) {
public class NonceCompact extends Compact {
    public NonceCompact(Object value) {
        super(TypesUtils.getConstructorCodec(Nonce.class), value);
    }
}

