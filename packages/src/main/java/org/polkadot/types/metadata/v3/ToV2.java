package org.polkadot.types.metadata.v3;

import org.polkadot.types.metadata.v2.MetadataV2;

public class ToV2 {
    public static MetadataV2 toV2(MetadataV3 v3) {
        return new MetadataV2(v3);
    }
}
