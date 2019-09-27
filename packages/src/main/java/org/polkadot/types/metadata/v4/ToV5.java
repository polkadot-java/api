package org.polkadot.types.metadata.v4;

import com.google.common.collect.Lists;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.metadata.v4.Storage.StorageFunctionMetadataV4;
import org.polkadot.types.metadata.v5.MetadataV5;
import org.polkadot.types.metadata.v5.Storage.StorageFunctionMetadataV5;
import org.polkadot.types.primitive.StorageHasher;
import org.polkadot.types.primitive.Text;
import org.polkadot.utils.MapUtils;

import java.util.List;
import java.util.Map;

import static org.polkadot.types.codec.CreateType.createType;

public class ToV5 {

    static final Map<String, String> hasherMap = MapUtils.ofMap(
            "blake2_128", "Blake2_128",
            "blake2_256", "Blake2_256",
            "twox_128", "Twox128",
            "twox_256", "Twox256",
            "twox_64_concat", "Twox64Concat"
    );


    static StorageHasher toStorageHasher(Text text) {
        String mapped = hasherMap.get(text.toString());
        //assert (mapped,`Invalid Storage hasher: ${text.toString()}`);
        return new StorageHasher(mapped);
    }


    static StorageFunctionMetadataV5 toV5StorageFunction(StorageFunctionMetadataV4 storageFn) {

        Object newType;
        int index;
        if (storageFn.getType().isPlainType()) {
            newType = storageFn.getType();
            index = 0;
        } else {
            if (storageFn.getType().isMap()) {
                newType = storageFn.getType().asMap();
                index = 1;
            } else {
                newType = createType("DoubleMapTypeV5",
                        MapUtils.ofMap("hasher", storageFn.getType().asDoubleMap().hasher,
                                "key1", storageFn.getType().asDoubleMap().key1,
                                "key2", storageFn.getType().asDoubleMap().key2,
                                "value", storageFn.getType().asDoubleMap().value,
                                "key2Hasher", toStorageHasher(storageFn.getType().asDoubleMap().key2Hasher))
                );
                index = 2;
            }
        }

        return new StorageFunctionMetadataV5(
                MapUtils.ofMap(
                        "documentation", storageFn.getDocumentation(),
                        "fallback", storageFn.getFallback(),
                        "name", storageFn.getName(),
                        "modifier", storageFn.getModifier(),
                        "type", new org.polkadot.types.metadata.v5.Storage.StorageFunctionTypeV5(newType, index)
                )
        );
    }

    /**
     * Convert from MetadataV4 to MetadataV5
     * See https://github.com/paritytech/substrate/pull/2836/files for details
     */
    public static MetadataV5 toV5(MetadataV4 metadataV4) {

        List<MetadataV5.ModuleMetadataV5> tos = Lists.newArrayList();
        for (MetadataV4.ModuleMetadataV4 module : metadataV4.getVecModules()) {
            MetadataV5.ModuleMetadataV5 moduleMetadataV6 = new MetadataV5.ModuleMetadataV5(
                    MapUtils.ofMap(
                            "calls", module.getCalls(),
                            "events", module.getEvents(),
                            "name", module.getName(),
                            "prefix", module.getPrefix(),
                            "storage", module.getStorage().isSome()
                                    ? new Option(Vec.with(TypesUtils.getConstructorCodec(StorageFunctionMetadataV5.class)), module.getStorage().unwrap().stream().map(ToV5::toV5StorageFunction))
                                    : null
                    )
            );
            tos.add(moduleMetadataV6);
        }
        return new MetadataV5(tos);
    }
}
