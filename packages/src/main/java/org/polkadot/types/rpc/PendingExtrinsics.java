package org.polkadot.types.rpc;

import org.polkadot.types.primitive.extrinsic.Extrinsics;

/**
 * A list of pending {@link Extrinsics}
 */
public class PendingExtrinsics extends Extrinsics {
    public PendingExtrinsics(Object value) {
        super(value);
    }
}
