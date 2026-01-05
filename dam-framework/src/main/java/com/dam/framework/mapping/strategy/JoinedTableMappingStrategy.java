package com.dam.framework.mapping.strategy;

import com.dam.framework.mapping.EntityMetadata;

public class JoinedTableMappingStrategy implements MappingStrategy {

    @Override
    public void mapAttributes(EntityMetadata metadata, Class<?> entityClass) {

    }

    @Override
    public String getTableName(Class<?> entityClass) {
        return "";
    }
}