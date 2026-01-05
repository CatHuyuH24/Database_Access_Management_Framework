package com.dam.framework.mapping.strategy;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.DiscriminatorColumn;
import com.dam.framework.annotations.Table;
import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.EntityMetadata;
import com.dam.framework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.List;

public class SingleTableMappingStrategy implements MappingStrategy {

    @Override
    public void mapAttributes(EntityMetadata metadata, Class<?> entityClass) {
        // @Id
        List<Field> idFields = ReflectionUtils.getFieldsWithAnotation(entityClass, Id.class);
        validateId(idFields, entityClass);

        for (Field idField : idFields) {
            if (isValidForSingleTable(metadata, idField.getDeclaringClass())) {
                var idCol = metadata.createColumnMetadata(idField);
                metadata.addColumn(idCol);
                metadata.setIdColumn(idCol);
                break;
            }
        }

        // @Column
        List<Field> fields = ReflectionUtils.getFieldsWithAnotation(entityClass, Column.class);
        for (Field field : fields) {
            if (metadata.getIdColumn() != null && field.equals(metadata.getIdColumn().field())) {
                continue;
            }

            if (isValidForSingleTable(metadata, field.getDeclaringClass())) {
                metadata.addColumn(metadata.createColumnMetadata(field));
            }
        }

        // @DiscriminatorColumn
        handleDiscriminator(metadata, entityClass);

        if (metadata.getIdColumn() == null) {
            throw new DAMException(
                    "No valid @Id found for Single Table hierarchy: " + entityClass.getSimpleName());
        }
    }

    @Override
    public String getTableName(Class<?> entityClass) {
        Class<?> root = findRootEntity(entityClass);
        if (root.isAnnotationPresent(Table.class)) {
            Table tableAnno = root.getAnnotation(Table.class);
            return tableAnno.name().isBlank() ? root.getSimpleName() : tableAnno.name();
        }
        return root.getSimpleName();
    }

    private boolean isValidForSingleTable(EntityMetadata metadata, Class<?> declaringClass) {
        // Parent must be annotated with MappedSuperClass n Entity
        return declaringClass == metadata.getEntityClass() ||
                declaringClass.isAnnotationPresent(com.dam.framework.annotations.MappedSuperclass.class) ||
                declaringClass.isAnnotationPresent(com.dam.framework.annotations.Entity.class);
    }

    private void handleDiscriminator(EntityMetadata metadata, Class<?> entityClass) {
        Class<?> root = findRootEntity(entityClass);

        String discoColName = "DTYPE";
        if (root.isAnnotationPresent(DiscriminatorColumn.class)) {
            discoColName = root.getAnnotation(DiscriminatorColumn.class).name();
        }

        metadata.setDiscriminatorColumn(discoColName);

        String discoValue;
        if (entityClass.isAnnotationPresent(com.dam.framework.annotations.DiscriminatorValue.class)) {
            discoValue = entityClass.getAnnotation(com.dam.framework.annotations.DiscriminatorValue.class)
                    .value();
        } else {
            discoValue = entityClass.getSimpleName();
        }
        metadata.setDiscriminatorValue(discoValue);
    }

    private Class<?> findRootEntity(Class<?> clazz) {
        Class<?> root = clazz;
        Class<?> current = clazz.getSuperclass();
        while (current != null && current.isAnnotationPresent(
                com.dam.framework.annotations.Entity.class)) {
            root = current;
            current = current.getSuperclass();
        }
        return root;
    }

    private void validateId(List<Field> fields, Class<?> clazz) {
        if (fields.isEmpty()) {
            throw new DAMException("No @Id set for " + clazz.getSimpleName());
        }
    }
}