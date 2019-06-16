package org.polkadot.types.codec;

import com.google.common.collect.Sets;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.primitive.*;
import org.polkadot.utils.PackageScanner;

import java.util.HashMap;
import java.util.HashSet;
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
                    registerAlias(registry);
                    defaultRegistry = registry;
                }
            }
        }
        return defaultRegistry;
    }


    private static void registerAlias(TypeRegistry typeRegistry) {
        HashSet<Class<? extends Codec>> aliasClass =
                Sets.newHashSet(U8.class, U16.class, U32.class,
                        U64.class, U128.class, U256.class,
                        USize.class, Bool.class,
                        I8.class, I16.class, I32.class,
                        I64.class, I128.class, I256.class);

        for (Class<? extends Codec> aClass : aliasClass) {
            Types.ConstructorCodec builder = TypesUtils.getConstructorCodec(aClass);
            registry.put(aClass.getSimpleName().toLowerCase(), builder);
        }
    }

    public static void registerTypes(Map<String, Types.ConstructorCodec> types) {
        TypeRegistry defaultRegistry = getDefaultRegistry();
        TypeRegistry.registry.putAll(types);
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
