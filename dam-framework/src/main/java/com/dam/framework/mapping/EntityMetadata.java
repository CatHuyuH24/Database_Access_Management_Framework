package com.dam.framework.mapping;

import com.dam.framework.annotations.MappedSuperclass;
import com.dam.framework.mapping.strategy.MappingStrategy;
import com.dam.framework.mapping.strategy.MappingStrategyFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dam.framework.annotations.Column;
import com.dam.framework.exception.DAMException;

/**
 * Holds metadata information about an entity class.
 * <p>
 * This class is populated by parsing annotations on entity classes and is cached for performance.
 *
 * @see com.dam.framework.annotations.Entity
 */
public class EntityMetadata {

    protected Class<?> entityClass;
    protected String tableName;
    protected String tableSchema;
    protected ColumnMetadata idColumn;
    protected List<ColumnMetadata> columns;
    private String discriminatorColumn;
    private String discriminatorValue;

    public EntityMetadata(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(com.dam.framework.annotations.Entity.class)) {
            throw new DAMException(
                    "Class " + entityClass.getSimpleName() + " is not marked with @Entity");
        }

        this.entityClass = entityClass;

        // Attributes
        // Get strategy
        MappingStrategy mappingStrategy = MappingStrategyFactory.getStrategy(entityClass);
        this.tableName = mappingStrategy.getTableName(entityClass);
        mappingStrategy.mapAttributes(this, entityClass);
    }

    public ColumnMetadata createColumnMetadata(Field field) {
        String colName = field.getName();
        boolean nullable = true;
        boolean unique = false;

        if (field.isAnnotationPresent(Column.class)) {
            Column colAnno = field.getAnnotation(Column.class);
            colName = colAnno.name().isBlank() ? field.getName() : colAnno.name();
            nullable = colAnno.nullable();
            unique = colAnno.unique();
        }
        field.setAccessible(true);

        return new ColumnMetadata(
                field,
                colName,
                field.getType(),
                null,
                nullable,
                unique
        );
    }

    public boolean isValidForMapping(Class<?> declaringClass) {
        if (declaringClass == this.entityClass) {
            return true;
        }
        return declaringClass.isAnnotationPresent(MappedSuperclass.class) &&
                !declaringClass.isAnnotationPresent(com.dam.framework.annotations.Entity.class);
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public ColumnMetadata getIdColumn() {
        return idColumn;
    }

    public List<ColumnMetadata> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public String getSchema() {
        return tableSchema;
    }

    public void addColumn(ColumnMetadata column) {
        if (this.columns == null) {
            this.columns = new ArrayList<>();
        }
        if (isNotAlreadyMapped(column)) {
            this.columns.add(column);
        }
    }

    public void setDiscriminatorColumn(String name) {
        this.discriminatorColumn = name;
    }

    public void setDiscriminatorValue(String value) {
        this.discriminatorValue = value;
    }

    public String getDiscriminatorColumn() {
        return discriminatorColumn;
    }

    public String getDiscriminatorValue() {
        return discriminatorValue;
    }

    public void setIdColumn(ColumnMetadata idCol) {
        this.idColumn = idCol;
    }

    private boolean isNotAlreadyMapped(ColumnMetadata newCol) {
        return columns.stream()
                .noneMatch(c -> c.columnName().equalsIgnoreCase(newCol.columnName()));
    }

}