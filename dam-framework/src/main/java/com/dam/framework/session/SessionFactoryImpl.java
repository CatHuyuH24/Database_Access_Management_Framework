package com.dam.framework.session;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.dam.framework.connection.ConnectionManager;
import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.EntityMetadata;

class SessionFactoryImpl implements SessionFactory, InternalSessionFactory {

    // This map holds the "blueprint" for every entity in your app
    // Key: Entity class (e.g., User.class), Value: Metadata for that class
    private final Map<Class<?>, EntityMetadata> metadataRegistry;

    // Connection pool
    private final ConnectionManager connectionManager;

    // Track which connection belongs to which session (for proper cleanup)
    private final Map<Session, Connection> sessionConnections = new HashMap<>();

    private boolean isOpen = true;

    SessionFactoryImpl(Map<Class<?>, EntityMetadata> metadataRegistry, ConnectionManager connectionManager) {
        this.metadataRegistry = metadataRegistry;
        this.connectionManager = connectionManager;
    }

    @Override
    public Session openSession() {
        // Get connection from pool
        Connection conn = connectionManager.getConnection();

        // Create session with read-only metadata access
        Session session = new SessionImpl(this, conn); // Pass 'this' as both InternalSessionFactory and
                                                       // MetadataRegistry

        // Track session and its connection
        sessionConnections.put(session, conn);

        return session;
    }

    @Override
    public Session getCurrentSession() {
        // For MVP 1, simple openSession is enough
        return openSession();
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() {
        for (Map.Entry<Session, Connection> entry : sessionConnections.entrySet()) {
            try {
                entry.getKey().close();
                connectionManager.releaseConnection(entry.getValue());
            } catch (Exception e) {
                throw new DAMException(e.getCause());
            }
        }

        sessionConnections.clear();
        connectionManager.shutdown();
        isOpen = false;
    }

    @Override
    public int getOpenSessionCount() {
        return sessionConnections.size();
    }

    @Override
    public void sessionClosed(Session session) {
        connectionManager.releaseConnection(sessionConnections.remove(session));
    }

    @Override
    public EntityMetadata getMetadata(Class<?> entityClass) {
        return metadataRegistry.get(entityClass);
    }
}
