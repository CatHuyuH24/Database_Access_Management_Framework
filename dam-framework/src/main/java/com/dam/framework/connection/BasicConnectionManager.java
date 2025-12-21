package com.dam.framework.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dam.framework.exception.DAMException;

/**
 * Basic implementation of ConnectionManager with connection pooling.
 * <p>
 * Implements Singleton pattern to ensure only one pool per database
 * configuration.
 * Uses BlockingQueue for thread-safe connection pool management.
 * 
 * <h3>Design Patterns Used:</h3>
 * <ul>
 * <li><b>Singleton Pattern:</b> Ensures one connection pool per
 * configuration</li>
 * <li><b>Object Pool Pattern:</b> Reuses database connections for better
 * performance</li>
 * </ul>
 * 
 * <h3>Features:</h3>
 * <ul>
 * <li>Minimum and maximum pool size configuration</li>
 * <li>Connection timeout handling</li>
 * <li>Connection validation before returning to pool</li>
 * <li>Thread-safe connection acquisition and release</li>
 * </ul>
 * 
 * @author enkay2408
 * @see ConnectionManager
 */
public class BasicConnectionManager implements ConnectionManager {

  private static final Logger logger = LoggerFactory.getLogger(BasicConnectionManager.class);

  private final String url;
  private final String username;
  private final String password;
  private final String driverClass;
  private final int minSize;
  private final int maxSize;
  private final long timeoutMs;
  private final String validationQuery;

  private final BlockingQueue<Connection> availableConnections;
  private int currentPoolSize;
  private volatile boolean isShutdown;

  /**
   * Private constructor for Singleton pattern.
   * Initializes the connection pool with minimum connections.
   * 
   * @param url             JDBC connection URL
   * @param username        database username
   * @param password        database password
   * @param driverClass     JDBC driver class name
   * @param minSize         minimum pool size
   * @param maxSize         maximum pool size
   * @param timeoutMs       connection acquisition timeout in milliseconds
   * @param validationQuery SQL query to validate connections
   */
  private BasicConnectionManager(String url, String username, String password,
      String driverClass, int minSize, int maxSize,
      long timeoutMs, String validationQuery) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.driverClass = driverClass;
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.timeoutMs = timeoutMs;
    this.validationQuery = validationQuery;
    this.availableConnections = new LinkedBlockingQueue<>();
    this.currentPoolSize = 0;
    this.isShutdown = false;

    initializePool();
  }

  /**
   * Initialize the connection pool with minimum number of connections.
   */
  private void initializePool() {
    try {
      // Load JDBC driver
      Class.forName(driverClass);
      logger.info("Loaded JDBC driver: {}", driverClass);

      // Create minimum number of connections
      for (int i = 0; i < minSize; i++) {
        Connection conn = createNewConnection();
        availableConnections.offer(conn);
        currentPoolSize++;
      }

      logger.info("Connection pool initialized with {} connections (min={}, max={})",
          currentPoolSize, minSize, maxSize);
    } catch (ClassNotFoundException e) {
      throw new DAMException("Failed to load JDBC driver: " + driverClass, e);
    } catch (SQLException e) {
      throw new DAMException("Failed to initialize connection pool", e);
    }
  }

  /**
   * Create a new physical database connection.
   * 
   * @return new database connection
   * @throws SQLException if connection fails
   */
  private Connection createNewConnection() throws SQLException {
    logger.debug("Creating new database connection to: {}", url);
    if (username != null && !username.isEmpty()) {
      return DriverManager.getConnection(url, username, password);
    } else {
      return DriverManager.getConnection(url);
    }
  }

  /**
   * Get a connection from the pool or create a new one.
   * <p>
   * Thread-safe implementation:
   * <ol>
   * <li>Try to get idle connection from pool</li>
   * <li>If pool empty and can grow, create new connection</li>
   * <li>If pool at max size, wait for available connection</li>
   * <li>Validate connection before returning</li>
   * </ol>
   * 
   * @return a database connection
   * @throws DAMException if cannot acquire connection within timeout
   */
  @Override
  public synchronized Connection getConnection() {
    if (isShutdown) {
      throw new DAMException("Connection pool has been shutdown");
    }

    try {
      Connection conn = availableConnections.poll();

      if (conn == null) {
        // No available connection in pool
        if (currentPoolSize < maxSize) {
          // Pool can grow - create new connection
          logger.debug("Creating new connection (current pool size: {})", currentPoolSize);
          conn = createNewConnection();
          currentPoolSize++;
        } else {
          // Pool at max size - wait for available connection
          logger.debug("Pool at max size, waiting for available connection...");
          conn = availableConnections.poll(timeoutMs, TimeUnit.MILLISECONDS);

          if (conn == null) {
            throw new DAMException(
                String.format("Connection acquisition timeout after %d ms. Pool size: %d/%d",
                    timeoutMs, currentPoolSize, maxSize));
          }
        }
      }

      // Validate connection before returning
      if (!isConnectionValid(conn)) {
        logger.warn("Connection validation failed, creating new connection");
        try {
          conn.close();
        } catch (SQLException e) {
          logger.warn("Error closing invalid connection", e);
        }
        currentPoolSize--;
        conn = createNewConnection();
        currentPoolSize++;
      }

      logger.debug("Connection acquired. Available: {}, Total: {}",
          availableConnections.size(), currentPoolSize);
      return conn;

    } catch (SQLException e) {
      throw new DAMException("Failed to create database connection", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new DAMException("Connection acquisition interrupted", e);
    }
  }

  /**
   * Validate a connection using the validation query.
   * 
   * @param conn connection to validate
   * @return true if connection is valid
   */
  private boolean isConnectionValid(Connection conn) {
    if (conn == null) {
      return false;
    }

    try {
      // Check if connection is closed
      if (conn.isClosed()) {
        return false;
      }

      // Execute validation query if provided
      if (validationQuery != null && !validationQuery.isEmpty()) {
        var stmt = conn.createStatement();
        var rs = stmt.executeQuery(validationQuery);
        rs.close();
        stmt.close();
      }

      return true;
    } catch (SQLException e) {
      logger.warn("Connection validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Release a connection back to the pool.
   * <p>
   * Resets connection state and returns to pool if valid.
   * Closes connection if validation fails.
   * 
   * @param connection the connection to release
   */
  @Override
  public synchronized void releaseConnection(Connection connection) {
    if (connection == null || isShutdown) {
      return;
    }

    try {
      // Reset connection state
      if (!connection.getAutoCommit()) {
        connection.rollback();
        connection.setAutoCommit(true);
      }

      // Validate and return to pool
      if (isConnectionValid(connection)) {
        boolean added = availableConnections.offer(connection);
        if (added) {
          logger.debug("Connection returned to pool. Available: {}",
              availableConnections.size());
        } else {
          logger.warn("Failed to return connection to pool, closing it");
          connection.close();
          currentPoolSize--;
        }
      } else {
        logger.warn("Invalid connection not returned to pool, closing it");
        connection.close();
        currentPoolSize--;
      }
    } catch (SQLException e) {
      logger.error("Error releasing connection", e);
      try {
        connection.close();
        currentPoolSize--;
      } catch (SQLException ex) {
        logger.error("Error closing connection during release", ex);
      }
    }
  }

  /**
   * Close all connections and shutdown the pool.
   * <p>
   * Should be called when application is shutting down.
   */
  @Override
  public synchronized void shutdown() {
    if (isShutdown) {
      return;
    }

    logger.info("Shutting down connection pool...");
    isShutdown = true;

    // Close all available connections
    Connection conn;
    int closed = 0;
    while ((conn = availableConnections.poll()) != null) {
      try {
        conn.close();
        closed++;
      } catch (SQLException e) {
        logger.error("Error closing connection during shutdown", e);
      }
    }

    logger.info("Connection pool shutdown complete. Closed {} connections", closed);
    currentPoolSize = 0;
  }

  /**
   * Get current number of available connections in pool.
   * 
   * @return number of available connections
   */
  public int getAvailableConnectionCount() {
    return availableConnections.size();
  }

  /**
   * Get total number of connections in pool.
   * 
   * @return total pool size
   */
  public int getTotalConnectionCount() {
    return currentPoolSize;
  }

  /**
   * Builder for creating BasicConnectionManager instances.
   * <p>
   * Implements Builder pattern for flexible object construction.
   * 
   * @author Dev C
   */
  public static class Builder {
    private String url;
    private String username;
    private String password;
    private String driverClass;
    private int minSize = 5;
    private int maxSize = 20;
    private long timeoutMs = 30000; // 30 seconds default
    private String validationQuery = "SELECT 1";

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder driverClass(String driverClass) {
      this.driverClass = driverClass;
      return this;
    }

    public Builder minSize(int minSize) {
      this.minSize = minSize;
      return this;
    }

    public Builder maxSize(int maxSize) {
      this.maxSize = maxSize;
      return this;
    }

    public Builder timeoutMs(long timeoutMs) {
      this.timeoutMs = timeoutMs;
      return this;
    }

    public Builder validationQuery(String validationQuery) {
      this.validationQuery = validationQuery;
      return this;
    }

    /**
     * Build and return the ConnectionManager instance.
     * 
     * @return configured ConnectionManager
     * @throws IllegalArgumentException if required parameters are missing
     */
    public ConnectionManager build() {
      if (url == null || url.isEmpty()) {
        throw new IllegalArgumentException("Database URL is required");
      }
      if (driverClass == null || driverClass.isEmpty()) {
        throw new IllegalArgumentException("JDBC driver class is required");
      }
      if (minSize < 1 || minSize > maxSize) {
        throw new IllegalArgumentException("Invalid pool size: minSize=" + minSize + ", maxSize=" + maxSize);
      }

      return new BasicConnectionManager(url, username, password, driverClass,
          minSize, maxSize, timeoutMs, validationQuery);
    }
  }
}
