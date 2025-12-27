package com.dam.framework.mapping;

public record ColumnMetadata(
        String columnName,
        Class<?> javaType,
        String sqlType,
        boolean nullable,
        boolean unique) {

}
