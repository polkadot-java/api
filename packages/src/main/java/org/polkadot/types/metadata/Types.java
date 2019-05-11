package org.polkadot.types.metadata;

import java.util.List;

public interface Types {
    interface MetadataInterface {
        List<String> getUniqTypes(boolean throwError);
    }

}
