package org.polkadot.type.storage;

import com.google.common.collect.Lists;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.primitive.Text;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Substrate {

    public static class SubstrateMetadata {
        private String documentation;
        private String type;

        public SubstrateMetadata(String documentation, String type) {
            this.documentation = documentation;
            this.type = type;
        }

        public String getDocumentation() {
            return documentation;
        }

        public void setDocumentation(String documentation) {
            this.documentation = documentation;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    // Small helper function to factorize code on this page.
    static StorageKey.StorageFunction createRuntimeFunction(String method, String key, SubstrateMetadata substrateMetadata) {
        Map<String, Object> metaValues = new LinkedHashMap<>();

        metaValues.put("documentation", new Vector<Text>(TypesUtils.getConstructorCodec(Text.class), Lists.newArrayList(substrateMetadata.getDocumentation())));
        metaValues.put("modifier", new Modules.StorageFunctionModifier(1));
        metaValues.put("type", new Modules.StorageFunctionType(substrateMetadata.getType(), 0));

        Modules.StorageFunctionMetadata storageFunctionMetadata = new Modules.StorageFunctionMetadata(metaValues) {
            @Override
            public Object toJson() {
                return key;
            }
        };

        StorageKey.StorageFunction storageFunction = CreateFunction.createFunction(
                "Substrate",
                method,
                storageFunctionMetadata,
                true,
                key
        );

        allFunctions.put(method, storageFunction);

        return storageFunction;
    }

    public static Map<String, StorageKey.StorageFunction> allFunctions = new HashMap<>();


    public static StorageKey.StorageFunction code = createRuntimeFunction(
            "code",
            ":code",
            new SubstrateMetadata("Wasm code of the runtime.",
                    "Bytes")
    );

    public static StorageKey.StorageFunction heapPages = createRuntimeFunction(
            "heapPages",
            ":heappages",
            new SubstrateMetadata(
                    "Number of wasm linear memory pages required for execution of the runtime.",
                    "u64"
            ));

    public static StorageKey.StorageFunction authorityCount = createRuntimeFunction(
            "authorityCount",
            ":auth:len",
            new SubstrateMetadata(
                    "Number of authorities.",
                    "u32"
            ));

    public static StorageKey.StorageFunction authorityPrefix = createRuntimeFunction(
            "authorityPrefix",
            ":auth:",
            new SubstrateMetadata(
                    "Prefix under which authorities are storied.",
                    "u32"
            ));

    public static StorageKey.StorageFunction extrinsicIndex = createRuntimeFunction(
            "extrinsicIndex",
            ":extrinsic_index",
            new SubstrateMetadata(
                    "Current extrinsic index (u32) is stored under this key.",
                    "u32"
            ));

    public static StorageKey.StorageFunction changesTrieConfig = createRuntimeFunction(
            "changesTrieConfig",
            ":changes_trie",
            new SubstrateMetadata(
                    "Changes trie configuration is stored under this key.",
                    "u32"
            ));

}
