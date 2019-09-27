package org.polkadot.types.metadata.v5;

import com.google.common.collect.Lists;
import org.polkadot.types.metadata.v6.MetadataV6;
import org.polkadot.utils.MapUtils;

import java.util.List;

public class ToV6 {

    public static MetadataV6 toV6(MetadataV5 metadataV5) {

        List<MetadataV6.ModuleMetadataV6> tos = Lists.newArrayList();
        for (MetadataV5.ModuleMetadataV5 module : metadataV5.getVecModules()) {
            MetadataV6.ModuleMetadataV6 moduleMetadataV6 = new MetadataV6.ModuleMetadataV6(
                    MapUtils.ofMap(
                            "calls", module.getCalls(),
                            "constants", new Object[0],
                            "events", module.getEvents(),
                            "name", module.getName(),
                            "storage", module.getStorage()
                    )
            );
            tos.add(moduleMetadataV6);
        }
        return new MetadataV6(tos);
    }
}
