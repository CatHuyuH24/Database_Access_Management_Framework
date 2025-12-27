package com.dam.framework.session;

import java.util.Map;

import com.dam.framework.mapping.EntityKey;
import com.dam.framework.mapping.EntityMetadata;

public class SessionFactoryImpl implements SessionFactory {

    // This map holds the "blueprint" for every entity in your app
    private final Map<EntityKey, EntityMetadata> metadataMap;

    // In the future, this will also hold the ConnectionPool

    public SessionFactoryImpl(Map<EntityKey, EntityMetadata> metadataMap) {
        this.metadataMap = metadataMap;
    }

    @Override
    public Session openSession() {
        // Pass the metadata to the session so it knows how to save things
        return new SessionImpl(this, metadataMap);
    }

    @Override
    public Session getCurrentSession() {
        // For MVP 1, simple openSession is enough
        return openSession();
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() {
        // TODO: Close connection pool here later
        // connection pool logic
    }
}