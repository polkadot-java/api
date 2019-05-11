package org.polkadot.types.metadata.v2;

import org.polkadot.types.metadata.v1.MetadataV1;

public class ToV1 {

    public static MetadataV1 toV1(MetadataV2 v2) {
        return new MetadataV1(v2);
    }
}
