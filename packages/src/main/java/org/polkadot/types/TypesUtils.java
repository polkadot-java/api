package org.polkadot.types;

import org.polkadot.types.primitive.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TypesUtils {

    private static final Logger logger = LoggerFactory.getLogger(TypesUtils.class);

    private static <T extends Codec> Types.ConstructorCodec<T> getBuilderConstructorCodec(Class<T> clazz) {
        try {
            Method builderMethod = clazz.getDeclaredMethod("builder");
            Object builder = builderMethod.invoke(null, null);
            return (Types.ConstructorCodec) builder;
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        }
        //logger.debug(" no builder for {} ", clazz);
        return null;
    }

    public static Map<Class, Types.ConstructorCodec> constructorCodecMap = new ConcurrentHashMap<>();

    public static <T extends Codec> Types.ConstructorCodec<T> getConstructorCodec(Class<T> clazz) {
        return constructorCodecMap.computeIfAbsent(clazz, clz -> getConstructorCodecInner(clz));
    }

    private static <T extends Codec> Types.ConstructorCodec<T> getConstructorCodecInner(Class<T> clazz) {


        Types.ConstructorCodec<T> builderConstructorCodec = getBuilderConstructorCodec(clazz);
        if (builderConstructorCodec != null) {
            return builderConstructorCodec;
        }

        return new Types.ConstructorCodec<T>() {
            @Override
            public T newInstance(Object... values) {


                Constructor<?>[] constructors = clazz.getConstructors();
                if (constructors.length < 1) {
                    logger.error(" no constructor {}, {}", clazz, constructors);
                    return null;
                }


                Constructor<?> constructor = null;

                //TODO match type
                for (Constructor<?> con : constructors) {
                    int parameterCount = con.getParameterCount();
                    if (values.length == parameterCount) {
                        constructor = con;
                    }
                }

                if (constructor == null) {
                    if (!Null.class.isAssignableFrom(clazz)) {
                        logger.debug("can not find match constructor {}, {}, {}, {}"
                                , clazz, constructors, values.length,
                                Arrays.stream(values).map(v -> v.getClass().getSimpleName()).collect(Collectors.toList()));
                    }
                    constructor = constructors[0];
                }


                if (constructors.length > 1) {
                    //logger.error(" codec class has move than one constructor {}, {}", clazz, constructors);
                }

                int parameterCount = constructor.getParameterCount();
                Class<?>[] parameterTypes = constructor.getParameterTypes();

                Object[] params;
                if (values != null && parameterCount > values.length) {
                    params = new Object[parameterCount];
                    for (int i = 0; i < values.length; i++) {
                        params[i] = values[i];
                    }
                } else {
                    params = values;
                }
                //TODO 2019-05-10 17:27  append null
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                }

                T ret = null;
                try {
                    Object o;
                    if (parameterCount > 0) {
                        o = constructor.newInstance(params);
                    } else {
                        o = constructor.newInstance();
                    }
                    ret = (T) o;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ret == null) {
                    logger.error(" newInstance fail : {}, {}",
                            constructors, values);
                }
                return ret;
            }

            @Override
            public Class<T> getTClass() {
                return clazz;
            }
        };

    }
}
