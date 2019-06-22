package org.polkadot.common;

import java.lang.reflect.Field;

public class ReflectionUtils {
    /**
     * Find DeclaredField recursively.
     *
     * @param object    : the child object
     * @param fieldName : the field name in the parent object
     * @return the field object in the parent object
     */
    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;

        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
            }
        }

        return null;
    }

    /**
     * Read field value directly, ignore private/protected and getter
     *
     * @param object    : the child object
     * @param fieldName : the field name in the parent object
     * @return : the field value in the parent object
     */

    public static <T> T getField(Object object, String fieldName) {

        Field field = getDeclaredField(object, fieldName);
        field.setAccessible(true);

        try {
            return (T) field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
