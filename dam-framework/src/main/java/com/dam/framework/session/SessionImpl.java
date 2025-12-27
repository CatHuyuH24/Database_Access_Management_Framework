package com.dam.framework.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.dam.framework.mapping.EntityKey;
import com.dam.framework.mapping.EntityMetadata;
import com.dam.framework.query.Query;
import com.dam.framework.transaction.Transaction;

class SessionImpl implements Session {
    private final SessionFactory _sessionFactory;
    private final Map<EntityKey, EntityMetadata> _attachedEntities;

    public SessionImpl(SessionFactory sessionFactory, Map<EntityKey, EntityMetadata> metadaMap) {
        _sessionFactory = sessionFactory;
        _attachedEntities = metadaMap;
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

    @Override
    public <T> void persist(T entity) {
        Collection<Object> values = _attachedObjects.values();

        for (Object v : values) {
            if (v.getClass().equals(entity.getClass())) {
                return;
            }
        }
        _attachedObjects.put(new EntityKey(entity.getClass(), UUID.randomUUID()), entity);

    }

    @Override
    public <T> T find(Class<T> entityClass, Object id) {
        Object entity = _attachedObjects.get(new EntityKey(entityClass, id));
        return entityClass.cast(entity);
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
        Set<Map.Entry<EntityKey, Object>> entities = _attachedObjects.entrySet();
        for (Map.Entry<EntityKey, Object> e : entities) {
            if (e.getClass().equals(entity.getClass()) && e.equals(entity)) {
                entities.remove(e);
                return;
            }
        }

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

    @Override
    public void flush() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'flush'");
    }

};
