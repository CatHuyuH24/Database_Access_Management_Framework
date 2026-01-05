package com.dam.framework.mapping.entity;

public enum InheritanceStrategyType implements MappingStrategyType {
    SINGLE_TABLE,
    JOINED_TABLE,
    PER_TABLE;

    @Override
    public String getName() {
        return this.name();
    }
}