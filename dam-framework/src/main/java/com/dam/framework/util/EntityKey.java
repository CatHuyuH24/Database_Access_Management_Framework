package com.dam.framework.util;

public record EntityKey(Class<?> entityClass, Object id) {
    public EntityKey {
        if (entityClass == null || id == null) {
            throw new RuntimeException("Class and id must not be null");
        }
    }
}
