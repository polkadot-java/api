package org.polkadot.types.metadata.v6;

import com.google.common.collect.Lists;
import org.polkadot.types.metadata.v7.MetadataV7;
import org.polkadot.types.metadata.v7.StorageMetadata;
import org.polkadot.utils.MapUtils;

import java.util.List;

public class ToV7 {

    public static MetadataV7 toV7(MetadataV6 metadataV6) {

        List<MetadataV7.ModuleMetadataV7> tos = Lists.newArrayList();
        for (MetadataV6.ModuleMetadataV6 module : metadataV6.getVecModules()) {
            MetadataV7.ModuleMetadataV7 moduleMetadataV7 = new MetadataV7.ModuleMetadataV7(
                    MapUtils.ofMap(
                            "calls", module.getCalls(),
                            "constants", module.getConstants(),
                            "events", module.getEvents(),
                            "name", module.getName(),
                            "storage", (module.getStorage().isSome()
                                    ? new StorageMetadata(MapUtils.ofMap(
                                    "prefix", module.getPrefix(), "items", module.getStorage().unwrap()
                            )) : null)
                    )
            );

            tos.add(moduleMetadataV7);
        }

        return new MetadataV7(tos);
    }
}
