package com.dam.framework.mapping.strategy;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.Table;
import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.EntityMetadata;
import com.dam.framework.mapping.util.MappingValidationUtils;
import com.dam.framework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Default mapping strategy for entities without inheritance.
 */
public class DefaultMappingStrategy implements MappingStrategy {

    @Override
    public void mapAttributes(EntityMetadata metadata, Class<?> entityClass) {
        // @Id
        List<Field> idFields = ReflectionUtils.getFieldsWithAnotation(entityClass, Id.class);
        MappingValidationUtils.validateSingleId(idFields, entityClass);
        for (Field idField : idFields) {
            if (metadata.isValidForMapping(idField.getDeclaringClass())) {
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
            if (metadata.isValidForMapping(field.getDeclaringClass())) {
                metadata.addColumn(metadata.createColumnMetadata(field));
            }
        }

        if (metadata.getIdColumn() == null) {
            throw new DAMException(
                    "No valid @Id found in " + entityClass.getSimpleName() + " or its MappedSuperclasses");
        }
    }

    @Override
    public String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnno = entityClass.getAnnotation(Table.class);
            return tableAnno.name().isBlank() ? entityClass.getSimpleName() : tableAnno.name();
        }
        return entityClass.getSimpleName();
    }
}