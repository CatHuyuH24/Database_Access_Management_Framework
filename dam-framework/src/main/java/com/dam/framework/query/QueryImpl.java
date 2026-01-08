package com.dam.framework.query;

import com.dam.framework.dialect.Dialect;
import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.ColumnMetadata;
import com.dam.framework.mapping.EntityMetadata;
import com.dam.framework.util.ReflectionUtils;
import com.dam.framework.util.TypeMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryImpl<T> implements Query<T> {

    private final Class<T> entityClass;
    private final QueryContext context = new QueryContext();
    private final Dialect dialect;
    private final Connection connection;
    private final EntityMetadata metadata;
    // private static final Logger logger =
    // LoggerFactory.getLogger(QueryImpl.class);

    public QueryImpl(Class<T> entityClass, Connection connection, Dialect dialect, EntityMetadata metadata) {
        this.entityClass = entityClass;
        this.connection = connection;
        this.dialect = dialect;
        this.metadata = metadata;
    }

    @Override
    public Query<T> select(String... columns) {
        context.selectColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public Query<T> where(String condition, Object... params) {
        if (!context.whereConditions.isEmpty()) {
            return and(condition, params);
        }
        context.whereConditions.add(condition);
        context.parameters.addAll(Arrays.asList(params));
        return this;
    }

    @Override
    public Query<T> and(String condition, Object... params) {
        context.whereConditions.add("AND " + condition);
        context.parameters.addAll(Arrays.asList(params));
        return this;
    }

    @Override
    public Query<T> or(String condition, Object... params) {
        context.whereConditions.add("OR " + condition);
        context.parameters.addAll(Arrays.asList(params));
        return this;
    }

    @Override
    public Query<T> groupBy(String... columns) {
        context.groupByColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public Query<T> having(String condition, Object... params) {
        context.havingConditions.add(condition);
        if (params != null) {
            context.parameters.addAll(Arrays.asList(params));
        }
        return this;
    }

    @Override
    public Query<T> orderBy(String column, Order order) {
        context.orderByClauses.add(column + " " + order.name());
        return this;
    }

    @Override
    public Query<T> limit(int limit) {
        context.limit = limit;
        return this;
    }

    @Override
    public Query<T> offset(int offset) {
        context.offset = offset;
        return this;
    }

    @Override
    public List<T> getResultList() {
        List<Object> effectiveParameters = new ArrayList<>(context.parameters);
        String sql = buildSql(effectiveParameters);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < effectiveParameters.size(); i++) {
                stmt.setObject(i + 1, effectiveParameters.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToEntities(rs);
            }
        } catch (SQLException e) {
            throw new DAMException("Error executing query: " + sql, e);
        }
    }

    private String buildSql(List<Object> effectiveParameters) {
        StringBuilder sql = new StringBuilder("SELECT ");

        // 1. Columns
        sql.append(context.selectColumns.isEmpty() ? "*" : String.join(", ", context.selectColumns));

        // 2. FROM
        sql.append(" FROM ").append(metadata.getTableName());

        // 3. WHERE
        if (!context.whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" ", context.whereConditions));
        }

        // 4. GROUP BY
        if (!context.groupByColumns.isEmpty()) {
            sql.append(" GROUP BY ");
            sql.append(String.join(", ", context.groupByColumns));
        }

        // 5. HAVING
        if (!context.havingConditions.isEmpty()) {
            sql.append(" HAVING ");
            sql.append(String.join(" AND ", context.havingConditions));
        }

        // 4. ORDER BY
        if (!context.orderByClauses.isEmpty()) {
            sql.append(" ORDER BY ").append(String.join(", ", context.orderByClauses));
        }

        // 5. Pagination
        var pagination = dialect.getPaginationFragment(context.limit, context.offset);
        if (!pagination.sql().isEmpty()) {
            sql.append(pagination.sql());
            effectiveParameters.addAll(pagination.parameters());
        }
        return sql.toString();
    }

    private List<T> mapResultSetToEntities(ResultSet rs) throws SQLException {
        List<T> results = new ArrayList<>();

        // Get list of field returned from db
        // java.sql.ResultSetMetaData metaData = rs.getMetaData();
        // int columnCount = metaData.getColumnCount();

        try {
            while (rs.next()) {
                // 1. New entity instance
                T entity = entityClass.getDeclaredConstructor().newInstance();

                // 2. Loop through fields in metadata, map value into field if exist in the
                // resultSet
                for (ColumnMetadata col : metadata.getColumns()) {
                    try {
                        Object value = TypeMapper.getResultSetValue(rs, col.columnName(), col.field().getType());
                        ReflectionUtils.setFieldValue(entity, col.field(), value);
                    } catch (SQLException e) {
                        // Column might not be in SELECT list - skip
                    }
                }

                // for (int i = 1; i <= columnCount; i++) {
                // String columnName = metaData.getColumnLabel(i);
                // Object value = rs.getObject(i);

                // if (value == null) {
                // continue;
                // }

                // ReflectionUtils.setFieldValue(entity, null, value);

                //
                // setProperty(entity, rs, i, columnName);
                // }
                results.add(entity);
            }
        } catch (Exception e) {
            throw new DAMException("Failed to map ResultSet to " + entityClass.getSimpleName(), e);
        }
        return results;
    }

    // private String getTableName() {
    // if (entityClass.isAnnotationPresent(Table.class)) {
    // String name = entityClass.getAnnotation(Table.class).name();
    // if (!name.isEmpty()) {
    // return name;
    // }
    // }
    // return entityClass.getSimpleName().toLowerCase();
    // }

    // private void setProperty(T entity, ResultSet rs, int columnIndex, String
    // columnName) {
    // try {
    // java.lang.reflect.Field field = findFieldByColumnName(entityClass,
    // columnName);

    // if (field != null) {
    // field.setAccessible(true);
    // Object convertedValue = com.dam.framework.util.TypeMapper.getResultSetValue(
    // rs, columnIndex, field.getType());
    // field.set(entity, convertedValue);
    // }
    // } catch (Exception e) {
    // logger.error("Could not map column: {} to entity {}", columnName,
    // entityClass.getSimpleName());
    // }
    // }

    // private java.lang.reflect.Field findFieldByColumnName(Class<?> clazz, String
    // columnName) {
    // for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
    // if (field.isAnnotationPresent(Column.class)) {
    // String annotatedName = field.getAnnotation(Column.class)
    // .name();
    // if (columnName.equalsIgnoreCase(annotatedName)) {
    // return field;
    // }
    // }
    // }

    // String fieldName = convertToCamelCase(columnName);
    // try {
    // return clazz.getDeclaredField(fieldName);
    // } catch (NoSuchFieldException e) {
    // if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
    // return findFieldByColumnName(clazz.getSuperclass(), columnName);
    // }
    // }
    // return null;
    // }

    // private String convertToCamelCase(String input) {
    // StringBuilder result = new StringBuilder();
    // boolean nextUpper = false;
    // for (char c : input.toLowerCase().toCharArray()) {
    // if (c == '_') {
    // nextUpper = true;
    // } else {
    // if (nextUpper) {
    // result.append(Character.toUpperCase(c));
    // nextUpper = false;
    // } else {
    // result.append(c);
    // }
    // }
    // }
    // return result.toString();
    // }

    @Override
    public T getSingleResult() {
        List<T> list = getResultList();
        if (list.isEmpty()) {
            throw new DAMException("Query returned no results");
        }
        if (list.size() > 1) {
            throw new DAMException("Multiple results found");
        }
        return list.getFirst();
    }

    static class QueryContext {

        public List<String> selectColumns = new ArrayList<>();
        public List<String> whereConditions = new ArrayList<>();
        public List<Object> parameters = new ArrayList<>();
        public List<String> orderByClauses = new ArrayList<>();
        public List<String> groupByColumns = new ArrayList<>();
        public List<String> havingConditions = new ArrayList<>();
        public Integer limit;
        public Integer offset;
    }
}