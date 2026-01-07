package com.dam.framework.mapping.util;

import java.lang.reflect.Field;
import java.util.List;

import com.dam.framework.exception.DAMException;

/**
 * Utility class for validating entity mapping constraints.
 * <p>
 * Centralizes validation logic to avoid code duplication across
 * mapping strategies.
 */
public final class MappingValidationUtils {

    private MappingValidationUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Validate that exactly one @Id field exists.
     * 
     * @param fields the list of @Id annotated fields
     * @param clazz  the entity class being validated
     * @throws DAMException if no @Id or multiple @Id fields found
     */
    public static void validateSingleId(List<Field> fields, Class<?> clazz) {
        if (fields.isEmpty()) {
            throw new DAMException("No @Id set for " + clazz.getSimpleName());
        }
        if (fields.size() > 1) {
            throw new DAMException("Multiple @Id fields in " + clazz.getSimpleName()
                    + ". Only one @Id field is allowed.");
        }
    }

    /**
     * Validate that at least one @Id field exists (for inheritance scenarios).
     * 
     * @param fields the list of @Id annotated fields
     * @param clazz  the entity class being validated
     * @throws DAMException if no @Id field found
     */
    public static void validateIdExists(List<Field> fields, Class<?> clazz) {
        if (fields.isEmpty()) {
            throw new DAMException("No @Id set for " + clazz.getSimpleName());
        }
    }
}
