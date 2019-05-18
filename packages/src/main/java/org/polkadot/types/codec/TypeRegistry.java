package org.polkadot.types.codec;

import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.utils.PackageScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeRegistry {

    private static volatile TypeRegistry defaultRegistry = null;

    private static Map<String, Types.ConstructorCodec> registry = new HashMap<>();

    void registerClass(Class<? extends Codec> clazz) {
        Types.ConstructorCodec builder = TypesUtils.getConstructorCodec(clazz);
        registry.put(clazz.getSimpleName(), builder);
    }

    void register(String name, Types.ConstructorCodec type) {
        registry.put(name, type);
    }

    void registerObject(Types.RegistryTypes object, boolean overwrite) {
//TODO 2019-05-07 14:36
        throw new UnsupportedOperationException();
    }

    public Types.ConstructorCodec get(String name) {
        return registry.get(name);
    }

    public Types.ConstructorCodec getOrThrow(String name, String msg) {
        Types.ConstructorCodec type = get(name);
        if (type == null) {
            throw new RuntimeException(msg);
        }
        return type;
    }

    public static TypeRegistry getDefaultRegistry() {
        if (defaultRegistry == null) {
            synchronized (TypeRegistry.class) {
                if (defaultRegistry == null) {
                    TypeRegistry registry = new TypeRegistry();
                    registerPackage("org.polkadot.types.metadata", registry);
                    registerPackage("org.polkadot.types.primitive", registry);
                    registerPackage("org.polkadot.types.rpc", registry);
                    registerPackage("org.polkadot.types.type", registry);
                    defaultRegistry = registry;
                }
            }
        }
        return defaultRegistry;
    }

    public static void main(String[] args) {
        getDefaultRegistry();
    }

    private static void registerPackage(String packageName, TypeRegistry typeRegistry) {
        Set<Class<?>> classSet = PackageScanner.scan(packageName, true);
        for (Class<?> clazz : classSet) {
            if (Codec.class.isAssignableFrom(clazz)) {
                typeRegistry.registerClass((Class<? extends Codec>) clazz);
            }
        }
    }

}
