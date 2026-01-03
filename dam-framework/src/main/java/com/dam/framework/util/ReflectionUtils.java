package com.dam.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.dam.framework.exception.DAMException;

/**
 * Utility class for common reflection operations.
 * <p>
 * Provides methods for working with entity classes and their fields.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Get all fields, including public, private, protected, package and inherited
     * fields
     * 
     * @param type
     * @return List<Field>
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        Class<?> currentClass = type;

        while (currentClass != null && currentClass != Object.class) {
            for (Field f : currentClass.getDeclaredFields()) {
                fields.add(f);
            }
            currentClass = currentClass.getSuperclass();
        }

        return fields;

    }

    public static List<Field> getFieldsWithAnotation(Class<?> type, Class<? extends Annotation> annotation) {
        List<Field> fields = new ArrayList<>();
        for (Field field : getAllFields(type)) {
            if (field.isAnnotationPresent(annotation)) {
                fields.add(field);
            }
        }
        return fields;
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (Exception e) {
            throw new DAMException("Cannot instantiate " + type.getName(), e);
        }
    }

    public static Object getFieldValue(Object target, Field field) {
        try {
            if (!field.canAccess(target)) {
                field.setAccessible(true);
            }

            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new DAMException("Cannot get value of field " + field.getName(), e);
        }
    }

    public static void setFieldValue(Object target, Field field, Object value) {
        try {
            if (!field.canAccess(target)) {
                field.setAccessible(true);
            }

            field.set(target, value);

        } catch (IllegalAccessException e) {
            throw new DAMException("Cannot access to set the value" + value.toString() + " for field " + field.getName()
                    + " of object " + target.toString(), e);
        }
    }

}
