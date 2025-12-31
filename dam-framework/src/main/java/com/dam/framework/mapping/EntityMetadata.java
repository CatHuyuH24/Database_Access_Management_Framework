package com.dam.framework.mapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.Table;
import com.dam.framework.exception.DAMException;
import com.dam.framework.util.ReflectionUtils;

/**
 * Holds metadata information about an entity class.
 * <p>
 * This class is populated by parsing annotations on entity classes
 * and is cached for performance.
 * 
 * @see com.dam.framework.annotations.Entity
 */
public class EntityMetadata {

    private Class<?> entityClass;
    private String tableName;
    private String tableSchema;
    private ColumnMetadata idColumn;
    private List<ColumnMetadata> columns;

    // - Relationship metadata
    // private Map<String, RelationshipMetadata> relationships;

    public EntityMetadata(Class<?> entityClass) {
        this.entityClass = entityClass;

        // Reflection scanning to process table name and schema
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            this.tableName = tableAnnotation.name().isBlank() ? entityClass.getSimpleName() : tableAnnotation.name();
            this.tableSchema = tableAnnotation.schema().isBlank() ? "public" : tableAnnotation.schema();
        } else {
            this.tableName = entityClass.getSimpleName();
            this.tableSchema = "public";
        }

        // Reflection scanning to process columns
        List<Field> fields = ReflectionUtils.getFieldsWithAnotation(entityClass, Id.class);
        if (fields.size() > 1) {
            throw new DAMException(
                    "More than one column is annotated with @Id in class " + entityClass.getSimpleName());
        } else if (fields.size() < 1) {
            throw new DAMException("No annotation @Id is set for class " + entityClass.getSimpleName());
        }
        Field idField = fields.getFirst();
        String idColName = idField.getName();
        if (idField.isAnnotationPresent(Column.class)) {
            Column colAnnotation = idField.getAnnotation(Column.class);
            idColName = colAnnotation.name().isBlank() ? idField.getName() : colAnnotation.name();
        }
        this.idColumn = new ColumnMetadata(idField, idColName, idField.getType(), null, false, true);

        this.columns = new ArrayList<>();
        this.columns.add(idColumn);// also include id column
        fields = ReflectionUtils.getFieldsWithAnotation(entityClass, Column.class);
        for (Field field : fields) {
            Column colAnnotation = field.getAnnotation(Column.class);
            String colName = colAnnotation.name().isBlank() ? field.getName() : colAnnotation.name();
            this.columns.add(
                    new ColumnMetadata(
                            field,
                            colName, field.getType(),
                            null, colAnnotation.nullable(),
                            colAnnotation.unique()));// `field` is the value of the column in Java program, so that we
                                                     // can retrieve
            // later and process in db layer
        }

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

}
