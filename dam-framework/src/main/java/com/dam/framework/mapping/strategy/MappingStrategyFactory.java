package com.dam.framework.mapping.strategy;

import com.dam.framework.annotations.Inheritance;
import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.entity.InheritanceStrategyType;

/**
 * Factory for creating mapping strategies based on entity configuration.
 * <p>
 * Supported strategies:
 * <ul>
 * <li>SINGLE_TABLE - Fully implemented</li>
 * <li>Default (no @Inheritance) - Fully implemented</li>
 * </ul>
 * <p>
 * JOINED_TABLE and PER_TABLE are not implemented and will throw
 * UnsupportedOperationException.
 */
public class MappingStrategyFactory {

    public static MappingStrategy getStrategy(Class<?> entityClass) {
        Inheritance inheritance = findInheritanceAnnotation(entityClass);

        if (inheritance != null) {
            InheritanceStrategyType type = inheritance.strategy();
            return switch (type) {
                case SINGLE_TABLE -> new SingleTableMappingStrategy();
                case JOINED_TABLE -> throw new UnsupportedOperationException(
                        "JOINED_TABLE inheritance strategy is not implemented. " +
                                "Use SINGLE_TABLE or extend this framework by implementing MappingStrategy.");
                case PER_TABLE -> throw new UnsupportedOperationException(
                        "PER_TABLE inheritance strategy is not implemented. " +
                                "Use SINGLE_TABLE or extend this framework by implementing MappingStrategy.");
            };
        }

        return new DefaultMappingStrategy();
    }

    private static Inheritance findInheritanceAnnotation(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            if (current.isAnnotationPresent(Inheritance.class)) {
                return current.getAnnotation(Inheritance.class);
            }
            current = current.getSuperclass();
        }
        return null;
    }
}