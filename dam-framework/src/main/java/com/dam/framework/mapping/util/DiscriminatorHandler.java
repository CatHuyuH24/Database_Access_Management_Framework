package com.dam.framework.mapping.util;

import com.dam.framework.annotations.DiscriminatorColumn;
import com.dam.framework.annotations.DiscriminatorValue;
import com.dam.framework.mapping.EntityMetadata;

/**
 * Helper class for handling discriminator column and value logic
 * in Single Table Inheritance strategy.
 * <p>
 * Extracts discriminator handling responsibility from mapping strategies.
 */
public final class DiscriminatorHandler {

    private static final String DEFAULT_DISCRIMINATOR_COLUMN = "DTYPE";

    private DiscriminatorHandler() {
        // Utility class - prevent instantiation
    }

    /**
     * Handle discriminator configuration for the entity metadata.
     * <p>
     * Sets discriminator column name and value based on annotations
     * or defaults.
     * 
     * @param metadata    the entity metadata to configure
     * @param entityClass the current entity class
     * @param rootEntity  the root entity in the hierarchy
     */
    public static void handleDiscriminator(EntityMetadata metadata, Class<?> entityClass, Class<?> rootEntity) {
        // Get discriminator column name from root entity
        String columnName = DEFAULT_DISCRIMINATOR_COLUMN;
        if (rootEntity.isAnnotationPresent(DiscriminatorColumn.class)) {
            columnName = rootEntity.getAnnotation(DiscriminatorColumn.class).name();
        }
        metadata.setDiscriminatorColumn(columnName);

        // Get discriminator value from current entity
        String value;
        if (entityClass.isAnnotationPresent(DiscriminatorValue.class)) {
            value = entityClass.getAnnotation(DiscriminatorValue.class).value();
        } else {
            value = entityClass.getSimpleName();
        }
        metadata.setDiscriminatorValue(value);
    }

    /**
     * Escape single quotes in discriminator value to prevent SQL injection.
     * 
     * @param value the discriminator value
     * @return escaped value safe for SQL
     */
    public static String escapeDiscriminatorValue(String value) {
        if (value == null) {
            return null;
        }
        // Escape single quotes by doubling them (SQL standard)
        return value.replace("'", "''");
    }
}
