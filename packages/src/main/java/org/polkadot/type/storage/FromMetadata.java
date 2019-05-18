package org.polkadot.type.storage;

import org.polkadot.types.metadata.v0.MetadataV0;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.Text;
import org.polkadot.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Extend a storage object with the storage modules & module functions present
 * in the metadata.
 *
 * @param storage  - A storage object to be extended.
 * @param metadata - The metadata to extend the storage object against.
 */
public class FromMetadata {
    public static Types.Storage fromMetadata(MetadataV0 metadata) {

        Map<String, Types.ModuleStorage> storageModules = new HashMap<>();

        for (Modules.RuntimeModuleMetadata moduleMetadata : metadata.getModules()) {
            if (moduleMetadata.getStorage().isNone()) {
                continue;
            }
            Modules.StorageMetadata storageMetadata = moduleMetadata.getStorage().unwrap();

            Text prefix = storageMetadata.getPrefix();

            Types.ModuleStorage newModule = new Types.ModuleStorage();
            // For access, we change the index names, i.e. Balances.FreeBalance -> balances.freeBalance
            for (Modules.StorageFunctionMetadata func : storageMetadata.getFunctions()) {
                newModule.addFunction(
                        Utils.stringLowerFirst(func.getName().toString()),
                        CreateFunction.createFunction(prefix.toString(), func.getName().toString(), func, false, null)
                );
            }

            storageModules.put(Utils.stringLowerFirst(prefix.toString()), newModule);
        }

        return new Types.Storage() {
            Map<String, Types.ModuleStorage> modules = storageModules;

            @Override
            public Types.ModuleStorage section(String section) {
                return modules.get(section);
            }

            @Override
            public Set<String> sectionNames() {
                return modules.keySet();
            }

            @Override
            public Types.ModuleStorage substrate() {
                return Substrate.substrate;
            }
        };
    }
}
