package com.dam.framework.mapping.strategy;

import com.dam.framework.mapping.EntityMetadata;

public interface MappingStrategy {

    void mapAttributes(EntityMetadata metadata, Class<?> entityClass);

    String getTableName(Class<?> entityClass);
}