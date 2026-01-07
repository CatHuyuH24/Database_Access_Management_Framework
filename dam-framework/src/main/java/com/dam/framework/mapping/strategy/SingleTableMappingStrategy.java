package com.dam.framework.mapping.strategy;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.Id;
import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.EntityMetadata;
import com.dam.framework.mapping.util.DiscriminatorHandler;
import com.dam.framework.mapping.util.HierarchyResolver;
import com.dam.framework.mapping.util.MappingValidationUtils;
import com.dam.framework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Mapping strategy for Single Table Inheritance.
 * <p>
 * All classes in the hierarchy are mapped to a single table.
 * A discriminator column is used to differentiate between types.
 */
public class SingleTableMappingStrategy implements MappingStrategy {

    @Override
    public void mapAttributes(EntityMetadata metadata, Class<?> entityClass) {
        // @Id
        List<Field> idFields = ReflectionUtils.getFieldsWithAnotation(entityClass, Id.class);
        MappingValidationUtils.validateIdExists(idFields, entityClass);

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

        // @DiscriminatorColumn and @DiscriminatorValue
        Class<?> root = HierarchyResolver.findRootEntity(entityClass);
        DiscriminatorHandler.handleDiscriminator(metadata, entityClass, root);

        if (metadata.getIdColumn() == null) {
            throw new DAMException(
                    "No valid @Id found for Single Table hierarchy: " + entityClass.getSimpleName());
        }
    }

    @Override
    public String getTableName(Class<?> entityClass) {
        Class<?> root = HierarchyResolver.findRootEntity(entityClass);
        if (root.isAnnotationPresent(com.dam.framework.annotations.Table.class)) {
            var tableAnno = root.getAnnotation(com.dam.framework.annotations.Table.class);
            return tableAnno.name().isBlank() ? root.getSimpleName() : tableAnno.name();
        }
        return root.getSimpleName();
    }

    private boolean isValidForSingleTable(EntityMetadata metadata, Class<?> declaringClass) {
        // Parent must be annotated with MappedSuperClass or Entity
        return declaringClass == metadata.getEntityClass() ||
                declaringClass.isAnnotationPresent(com.dam.framework.annotations.MappedSuperclass.class) ||
                declaringClass.isAnnotationPresent(com.dam.framework.annotations.Entity.class);
    }
}