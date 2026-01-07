package com.dam.framework.mapping.util;

import com.dam.framework.annotations.Entity;

/**
 * Helper class for traversing and resolving entity class hierarchies.
 * <p>
 * Extracts hierarchy traversal logic from mapping strategies.
 */
public final class HierarchyResolver {

    private HierarchyResolver() {
        // Utility class - prevent instantiation
    }

    /**
     * Find the root entity in an inheritance hierarchy.
     * <p>
     * Traverses up the class hierarchy to find the topmost class
     * annotated with @Entity.
     * 
     * @param clazz the starting class
     * @return the root entity class in the hierarchy
     */
    public static Class<?> findRootEntity(Class<?> clazz) {
        Class<?> root = clazz;
        Class<?> current = clazz.getSuperclass();

        while (current != null && current.isAnnotationPresent(Entity.class)) {
            root = current;
            current = current.getSuperclass();
        }

        return root;
    }

    /**
     * Check if a class is part of an entity hierarchy.
     * 
     * @param clazz the class to check
     * @return true if class or any parent is annotated with @Entity
     */
    public static boolean isInEntityHierarchy(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            if (current.isAnnotationPresent(Entity.class)) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }
}
