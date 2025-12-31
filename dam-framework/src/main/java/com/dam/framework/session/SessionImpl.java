package com.dam.framework.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.ColumnMetadata;
import com.dam.framework.mapping.EntityKey;
import com.dam.framework.mapping.EntityMetadata;
import com.dam.framework.query.Query;
import com.dam.framework.transaction.Transaction;
import com.dam.framework.util.ReflectionUtils;

class SessionImpl implements Session {

    private final InternalSessionFactory factory;
    private final Map<EntityKey, Object> attachedEntities; // L1 cache - stores actual entity instances
    private final Connection connection;
    private final Map<EntityKey, Map<String, Object>> originalSnapshots;

    public SessionImpl(InternalSessionFactory factory,
            Connection connection) {
        this.factory = factory;
        this.attachedEntities = new HashMap<>(); // Initialize L1 cache as empty
        this.connection = connection;
        originalSnapshots = new HashMap<>();
    }

    @Override
    public void close() throws Exception {
        // clear L1 cache (session-specific data)
        attachedEntities.clear();
        originalSnapshots.clear();

        // notify the internal factory to release connection and remove from tracking
        factory.sessionClosed(this);
    }

    @Override
    public <T> void persist(T entity) {
        // 1. Get metadata for this entity class
        EntityMetadata metadata = factory.getMetadata(entity.getClass());
        if (metadata == null) {
            throw new DAMException("Entity class not registered: " + entity.getClass().getName());
        }

        // 2. Get the ID value from the entity using reflection
        ColumnMetadata idColumn = metadata.getIdColumn();
        Object id = ReflectionUtils.getFieldValue(entity, idColumn.field());

        if (id == null) {
            // TODO: Handle @GeneratedValue - for now throw exception
            throw new DAMException("Entity ID is null. @GeneratedValue not yet supported.");
        }

        // 3. Check if already in L1 cache (Identity Map pattern)
        EntityKey key = new EntityKey(entity.getClass(), id);
        if (attachedEntities.containsKey(key)) {
            return; // Already persisted in this session
        }

        // TODO: 4. Generate INSERT SQL using metadata
        // String sql = generateInsertSQL(metadata);

        // TODO: 5. Execute SQL using JDBC PreparedStatement
        // executeInsert(sql, entity, metadata);

        // 6. Add to L1 cache
        attachedEntities.put(key, entity);
    }

    private <T> T mapResultSetToEntity(ResultSet rs, EntityMetadata entityMetadata) {
        @SuppressWarnings("unchecked")
        T entity = ReflectionUtils.newInstance((Class<T>) entityMetadata.getEntityClass());

        for (ColumnMetadata column : entityMetadata.getColumns()) {
            Object value;
            try {
                value = rs.getObject(column.columnName());
            } catch (SQLException e) {
                throw new DAMException(e.getCause());
            }
            ReflectionUtils.setFieldValue(entity, column.field(), value);
        }

        return entity;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object id) {
        // 1. Check L1 cache first (Identity Map pattern)
        EntityKey key = new EntityKey(entityClass, id);
        Object cached = attachedEntities.get(key);
        if (cached != null) {
            return entityClass.cast(cached);
        }

        // 2. Query database if not in cache
        EntityMetadata metadata = factory.getMetadata(entityClass);
        String sql = "SELECT * FROM " + metadata.getTableName() + " WHERE " + metadata.getIdColumn().columnName()
                + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    T entity = mapResultSetToEntity(rs, metadata);

                    // 3. Take snapshot (whenever loaded from DB) - along with L1 cache, helps to
                    // reduce number of DB queries, while ensure data integrity
                    Map<String, Object> snapshot = new HashMap<>();
                    for (ColumnMetadata col : metadata.getColumns()) {
                        Object value = ReflectionUtils.getFieldValue(entity, col.field());
                        snapshot.put(col.field().getName(), value);
                    }

                    // saving snapshot
                    originalSnapshots.put(key, snapshot);

                    // 3. Add to L1 cache before returning
                    attachedEntities.put(key, entity);

                    return entity;
                }

            }
        } catch (Exception e) {
            throw new DAMException(e.getCause());
        }

        return null;
    }

    @Override
    public <T> T merge(T entity) {
        return null;
        // Set<Map.Entry<EntityKey, Object>> entities = _attachedObjects.entrySet();
        // for (Map.Entry<EntityKey, Object> e : entities) {
        // if (e.getClass().equals(entity.getClass()) && e.equals(entity)) {
        // e.setValue(entity);
        // return ((T) e);
        // }
        // }
        // _attachedObjects.put(new EntityKey(entity.getClass(), UUID.randomUUID()),
        // entity);
        // return entity;
    }

    @Override
    public void remove(Object entity) {
        // TODO: Implement DELETE operation
        // 1. Get metadata
        // 2. Generate DELETE SQL
        // 3. Execute SQL
        // 4. Remove from L1 cache
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public <T> Query<T> createQuery(Class<T> resultClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createQuery'");
    }

    @Override
    public Transaction beginTransaction() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'beginTransaction'");
    }

    private List<ColumnMetadata> getChangedColumns(Map.Entry<EntityKey, Object> currentAttachedObject) {
        List<ColumnMetadata> changedColumns = new ArrayList<>();
        Object entity = currentAttachedObject.getValue();
        EntityMetadata metadata = factory.getMetadata(entity.getClass());

        Map<String, Object> originalSnapshot = originalSnapshots
                .get(attachedEntities.get(currentAttachedObject.getKey()));
        for (ColumnMetadata col : metadata.getColumns()) {
            Object currentValue = ReflectionUtils.getFieldValue(entity, col.field());
            Object originalValue = originalSnapshot.get(col.field().getName());

            if (!Objects.equals(currentValue, originalValue)) {
                changedColumns.add(col);
            }
        }

        return changedColumns;

    }

    @Override
    public void flush() {
        // dirty checking
        for (Map.Entry<EntityKey, Object> entry : attachedEntities.entrySet()) {
            // Compare current values with snapshot
            List<ColumnMetadata> changedColumns = getChangedColumns(entry);
            if (!changedColumns.isEmpty()) {
                // TODO: generate SQL
                // String sql = sqlGenerator.generateUpdate(metadata, changedColumns);
                // executeUpdate(sql, entity, changedColumns);
            }

        }
        // TODO still not supported
        throw new UnsupportedOperationException("Unimplemented method 'flush'");
    }

};
