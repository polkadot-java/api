package org.polkadot.types.metadata;

import org.polkadot.types.Codec;
import org.polkadot.types.codec.Vec;


public interface Types {
    interface MetadataInterface<T extends Codec> {
        //List<String> getUniqTypes(boolean throwError);
        //Vec<T> getModules();

        Vec<T> getVecModules();
    }

}
