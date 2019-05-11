package org.polkadot.type.storage;

import org.polkadot.direct.IModule;
import org.polkadot.direct.ISection;
import org.polkadot.types.primitive.StorageKey;

public interface Types {

    class ModuleStorage extends ISection<StorageKey.StorageFunction> {

    }

    interface Storage extends IModule<ModuleStorage> {
        //[key: string]: ModuleStorage; // Will hold modules returned by state_getMetadata
        //      substrate: { [key in Substrate]: StorageFunction };
        ModuleStorage substrate();
    }
}
