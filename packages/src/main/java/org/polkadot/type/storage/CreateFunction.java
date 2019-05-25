package org.polkadot.type.storage;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.polkadot.types.codec.CreateType;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.metadata.v2.Storage;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.utils.CryptoUtils;
import org.polkadot.utils.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class CreateFunction {

    public static class CreateItemOptions {
        private boolean isUnhashed;
        private String key;


        public CreateItemOptions(boolean isUnhashed, String key) {
            this.isUnhashed = isUnhashed;
            this.key = key;
        }

        public boolean isUnhashed() {
            return isUnhashed;
        }

        public void setUnhashed(boolean unhashed) {
            isUnhashed = unhashed;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    /**
     * From the schema of a function in the module's storage, generate the function
     * that will return the correct storage key.
     *
     * @param schema  - The function's definition schema to create the function from.
     *                The schema is taken from state_getMetadata.
     * @param options - Additional options when creating the function. These options
     *                are not known at runtime (from state_getMetadata), they need to be supplied
     *                by us manually at compile time.
     */
    public static StorageKey.StorageFunction createFunction(String section,
                                                            String method,
                                                            Modules.StorageFunctionMetadata meta,
                                                            boolean isUnhashed, //false
                                                            String key) {
        String stringKey = StringUtils.isNotEmpty(key)
                ? key
                : section + " " + method;
        byte[] rawKey = Utils.stringToU8a(stringKey);


        // Can only have zero or one argument:
        // - storage.balances.freeBalance(address)
        // - storage.timestamp.blockPeriod()
        StorageKey.StorageFunction storageFn = new StorageKey.StorageFunction() {
            String strKey = stringKey;
            @Override
            public byte[] apply(Object... args) {
                byte[] key = rawKey;
                if (meta.getType().isMap()) {
                    assert args == null || args.length != 1
                            : meta.getName() + "expects one argument";

                    String type = meta.getType().asMap().getKey().toString();
                    byte[] param = CreateType.createType(type, args[0]).toU8a(false);

                    key = Utils.u8aConcat(Lists.newArrayList(key, param));
                }

                // StorageKey is a Bytes, so is length-prefixed
                return Utils.compactAddLength(
                        isUnhashed
                                ? key
                                : CryptoUtils.xxhashAsU8a(key, 128)
                );
            }

            @Override
            public Object toJson() {
                return this.getMeta().toJson();
            }
        };

        if (meta.getType().isMap() && meta.getType().asMap().isLinked()) {
            byte[] keyHash = CryptoUtils.xxhashAsU8a(("head of " + stringKey).getBytes(), 128);
            StorageKey.StorageFunction keyFn = new StorageKey.StorageFunction() {
                @Override
                public byte[] apply(Object... args) {
                    return keyHash;
                }

                @Override
                public Object toJson() {
                    return this.getMeta().toJson();
                }
            };

            Map<String, Object> metaValues = new LinkedHashMap<>();
            metaValues.put("name", meta.getName());
            metaValues.put("modifier", new Modules.StorageFunctionModifier("Required"));
            metaValues.put("type", new Modules.StorageFunctionType(new Storage.PlainType(meta.getType().asMap().getKey()), 0));
            //metaValues.put("default", new Bytes(new byte[0]));
            metaValues.put("default", new Bytes(null));
            metaValues.put("documentation", meta.getDocumentation());

            keyFn.setMeta(new Modules.StorageFunctionMetadata(metaValues));

            storageFn.setHeadKey(new StorageKey(keyFn));
        }

        storageFn.setMeta(meta);
        storageFn.setMethod(Utils.stringLowerFirst(method));
        storageFn.setSection(Utils.stringLowerFirst(section));

        return storageFn;
    }
}
