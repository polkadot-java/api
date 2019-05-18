package org.polkadot.type.extrinsics;

import org.polkadot.types.metadata.v0.Calls;
import org.polkadot.types.metadata.v0.MetadataV0;
import org.polkadot.types.primitive.Method;
import org.polkadot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class FromMetadata {
    private static final Logger logger = LoggerFactory.getLogger(FromMetadata.class);

    /**
     * Extend a storage object with the storage modules & module functions present
     * in the metadata.
     *
     * @param extrinsics - An extrinsics object to be extended.
     * @param metadata   - The metadata to extend the storage object against.
     */
    public static Method.ModulesWithMethods fromMetadata(MetadataV0 metadata) {
        AtomicInteger indexCount = new AtomicInteger(-1);

        Function<String, Integer> findIndex = prefix -> {
            indexCount.getAndIncrement();

            Calls.OuterDispatchCall mod = metadata.getCalls().stream().filter((item) -> item.getPrefix().toString().equals(prefix)).findFirst().orElse(null);
            if (mod == null) {
                logger.error("Unable to find module index for {}", prefix);

                // compatible with old versions
                return indexCount.get();
            }
            return mod.getIndex().intValue();
        };


        metadata.getModules()
                .stream()
                .filter(
                        (meta) -> {
                            if (meta.getModule().getCall() == null
                                    || meta.getModule().getCall().getFunctions().length() <= 0) {
                                return false;
                            }
                            return true;
                        }
                )
                .forEach(
                        (meta) -> {
                            String prefix = Utils.stringCamelCase(meta.getPrefix().toString());
                            Integer index = findIndex.apply(meta.getPrefix().toString());


                            Method.Methods methods = new Method.Methods();
                            meta.getModule().getCall().getFunctions().stream().forEach(
                                    (funcMeta) -> {
                                        // extrinsics.balances.set_balance -> extrinsics.balances.setBalance
                                        String funcName = Utils.stringCamelCase(funcMeta.getName().toString());
                                        methods.put(funcName, CreateUnchecked.createDescriptor(prefix, funcName, index, funcMeta));
                                    }
                            );
                            Index.extrinsics.put(prefix, methods);
                        }
                );


        return Index.extrinsics;
    }


}
