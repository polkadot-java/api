package org.polkadot.common;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static <T> T getField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception t) {
            t.printStackTrace();
            return null; // NotReached
        }
    }
}
