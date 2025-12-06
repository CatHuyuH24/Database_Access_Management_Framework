package com.dam.framework.connection;

import java.sql.Connection;

/**
 * Interface for managing database connections.
 * <p>
 * Implementations may provide connection pooling for better performance.
 */
public interface ConnectionManager {
    
    /**
     * Get a connection from the pool or create a new one.
     * 
     * @return a database connection
     */
    Connection getConnection();
    
    /**
     * Release a connection back to the pool.
     * 
     * @param connection the connection to release
     */
    void releaseConnection(Connection connection);
    
    /**
     * Close all connections and shutdown the pool.
     */
    void shutdown();
}
