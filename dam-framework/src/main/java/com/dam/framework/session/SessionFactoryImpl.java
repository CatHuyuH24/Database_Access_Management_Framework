package com.dam.framework.session;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.dam.framework.connection.ConnectionManager;
import com.dam.framework.dialect.Dialect;
import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.EntityMetadata;
import com.dam.framework.sql.SQLGenerator;
import com.dam.framework.sql.SQLGeneratorImpl;

class SessionFactoryImpl implements SessionFactory, InternalSessionFactory {

    // This map holds the "blueprint" for every entity in your app
    // Key: Entity class (e.g., User.class), Value: Metadata for that class
    private final Map<Class<?>, EntityMetadata> metadataRegistry;

    // Connection pool
    private final ConnectionManager connectionManager;

    // Track which connection belongs to which session (for proper cleanup)
    private final Map<Session, Connection> sessionConnections = new HashMap<>();

    // ThreadLocal for session-per-thread pattern
    private final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();

    private boolean isOpen = true;
    private final SQLGenerator sqlGenerator = new SQLGeneratorImpl();
    private final Dialect dialect;
    private final boolean showSQL;

    SessionFactoryImpl(Map<Class<?>, EntityMetadata> metadataRegistry,
            ConnectionManager connectionManager, Dialect dialect, boolean showSQL) {
        this.metadataRegistry = metadataRegistry;
        this.connectionManager = connectionManager;
        this.dialect = dialect;
        this.showSQL = showSQL;
    }

    @Override
    public Session openSession() {
        // Get connection from pool
        Connection conn = connectionManager.getConnection();

        // Create session with read-only metadata access
        Session session = new SessionImpl(this, conn, sqlGenerator, dialect, showSQL);

        // Track session and its connection
        sessionConnections.put(session, conn);

        return session;
    }

    @Override
    public Session getCurrentSession() {
        // Check if current thread already has a session
        Session session = threadLocalSession.get();

        if (session != null && !isClosed(session)) {
            return session;
        }

        // No session for current thread - create new one
        session = openSession();
        threadLocalSession.set(session);

        return session;
    }

    /**
     * Check if a session is closed.
     * Helper method for getCurrentSession().
     */
    private boolean isClosed(Session session) {
        // Session is closed if it's not in our tracking map
        return !sessionConnections.containsKey(session);
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

        // Clean up ThreadLocal if this was the current thread's session
        if (threadLocalSession.get() == session) {
            threadLocalSession.remove();
        }
    }

    @Override
    public EntityMetadata getMetadata(Class<?> entityClass) {
        return metadataRegistry.get(entityClass);
    }
}
