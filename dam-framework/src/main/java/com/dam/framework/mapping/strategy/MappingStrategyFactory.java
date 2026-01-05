package com.dam.framework.mapping.strategy;

import com.dam.framework.annotations.Inheritance;
import com.dam.framework.mapping.entity.InheritanceStrategyType;

public class MappingStrategyFactory {

    public static MappingStrategy getStrategy(Class<?> entityClass) {
        Inheritance inheritance = findInheritanceAnnotation(entityClass);

        if (inheritance != null) {
            InheritanceStrategyType type = inheritance.strategy();
            return switch (type) {
                case SINGLE_TABLE -> new SingleTableMappingStrategy();
                case JOINED_TABLE -> new JoinedTableMappingStrategy();
                case PER_TABLE -> new PerTableMappingStrategy();
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