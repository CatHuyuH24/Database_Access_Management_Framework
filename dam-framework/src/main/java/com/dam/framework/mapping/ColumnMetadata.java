package com.dam.framework.mapping;

import java.lang.reflect.Field;

public record ColumnMetadata(
        Field field,
        String columnName,
        Class<?> javaType,
        String sqlType,
        boolean nullable,
        boolean unique) {

}
