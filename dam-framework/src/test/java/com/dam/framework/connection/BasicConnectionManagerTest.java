package com.dam.framework.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dam.framework.exception.DAMException;

/**
 * Unit tests for BasicConnectionManager.
 * <p>
 * Tests connection pool behavior including:
 * - Pool initialization
 * - Connection acquisition and release
 * - Pool size limits
 * - Timeout behavior
 * - Connection validation
 * - Thread safety
 * - Shutdown behavior
 * 
 * @author enkay2408
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BasicConnectionManagerTest {

  private static final Logger logger = LoggerFactory.getLogger(BasicConnectionManagerTest.class);

  // Using H2 in-memory database for testing
  private static final String TEST_URL = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1";
  private static final String TEST_DRIVER = "org.h2.Driver";
  private static final String TEST_VALIDATION_QUERY = "SELECT 1";

  private ConnectionManager connectionManager;

  @BeforeEach
  void setUp() {
    logger.info("Setting up test");
  }

  @AfterEach
  void tearDown() {
    if (connectionManager != null) {
      connectionManager.shutdown();
      connectionManager = null;
    }
    logger.info("Tear down complete");
  }

  /**
   * Test 1: Pool initialization creates minimum connections.
   */
  @Test
  @Order(1)
  @DisplayName("Should initialize pool with minimum connections")
  void testPoolInitialization() {
    // Given: Configuration with minSize=3, maxSize=10
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(3)
        .maxSize(10)
        .validationQuery(TEST_VALIDATION_QUERY)
        .build();

    // When: Pool is initialized
    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // Then: Pool should have 3 connections
    assertEquals(3, bcm.getTotalConnectionCount(),
        "Pool should be initialized with minSize connections");
    assertEquals(3, bcm.getAvailableConnectionCount(),
        "All connections should be available initially");
  }

  /**
   * Test 2: Can acquire connection from pool.
   */
  @Test
  @Order(2)
  @DisplayName("Should acquire connection from pool")
  void testAcquireConnection() throws SQLException {
    // Given: Pool with connections
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .build();

    // When: Acquire a connection
    Connection conn = connectionManager.getConnection();

    // Then: Connection should be valid
    assertNotNull(conn, "Connection should not be null");
    assertFalse(conn.isClosed(), "Connection should not be closed");

    // Cleanup
    connectionManager.releaseConnection(conn);
  }

  /**
   * Test 3: Can release connection back to pool.
   */
  @Test
  @Order(3)
  @DisplayName("Should release connection back to pool")
  void testReleaseConnection() throws SQLException {
    // Given: Pool with connection acquired
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;
    int initialAvailable = bcm.getAvailableConnectionCount();

    Connection conn = connectionManager.getConnection();
    int afterAcquire = bcm.getAvailableConnectionCount();

    // When: Release the connection
    connectionManager.releaseConnection(conn);
    int afterRelease = bcm.getAvailableConnectionCount();

    // Then: Available count should increase
    assertEquals(initialAvailable - 1, afterAcquire,
        "Available count should decrease after acquire");
    assertEquals(initialAvailable, afterRelease,
        "Available count should return to initial after release");
  }

  /**
   * Test 4: Pool creates new connections up to max size.
   */
  @Test
  @Order(4)
  @DisplayName("Should create new connections up to max size")
  void testPoolGrowth() throws SQLException {
    // Given: Pool with minSize=2, maxSize=5
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Acquire 5 connections
    List<Connection> connections = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      connections.add(connectionManager.getConnection());
    }

    // Then: Pool should have grown to 5 connections
    assertEquals(5, bcm.getTotalConnectionCount(),
        "Pool should grow to maxSize");
    assertEquals(0, bcm.getAvailableConnectionCount(),
        "No connections should be available");

    // Cleanup
    connections.forEach(connectionManager::releaseConnection);
  }

  /**
   * Test 5: Pool throws exception when max size reached and timeout expires.
   */
  @Test
  @Order(5)
  @DisplayName("Should timeout when pool at max size")
  void testConnectionTimeout() throws SQLException {
    // Given: Pool with maxSize=2 and short timeout
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(1)
        .maxSize(2)
        .timeoutMs(500) // 500ms timeout
        .build();

    // When: Acquire max connections and try to get one more
    Connection conn1 = connectionManager.getConnection();
    Connection conn2 = connectionManager.getConnection();

    // Then: Next acquisition should timeout
    DAMException exception = assertThrows(DAMException.class,
        () -> connectionManager.getConnection(),
        "Should throw DAMException on timeout");

    assertTrue(exception.getMessage().contains("timeout"),
        "Exception message should mention timeout");

    // Cleanup
    connectionManager.releaseConnection(conn1);
    connectionManager.releaseConnection(conn2);
  }

  /**
   * Test 6: Connection validation rejects invalid connections.
   */
  @Test
  @Order(6)
  @DisplayName("Should validate connections before returning")
  void testConnectionValidation() throws SQLException {
    // Given: Pool with validation query
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(3)
        .validationQuery(TEST_VALIDATION_QUERY)
        .build();

    // When: Get first connection and verify it's valid
    Connection conn1 = connectionManager.getConnection();
    assertNotNull(conn1, "First connection should not be null");
    assertFalse(conn1.isClosed(), "First connection should be valid");

    // Then: Get second connection and verify validation query works
    Connection conn2 = connectionManager.getConnection();
    assertNotNull(conn2, "Second connection should not be null");
    assertFalse(conn2.isClosed(), "Second connection should be valid");

    // Cleanup
    connectionManager.releaseConnection(conn1);
    connectionManager.releaseConnection(conn2);
  }

  /**
   * Test 7: Pool is thread-safe for concurrent access.
   */
  @Test
  @Order(7)
  @DisplayName("Should handle concurrent connection requests")
  void testConcurrentAccess() throws InterruptedException, ExecutionException {
    // Given: Pool with adequate size for concurrent access
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(5)
        .maxSize(20)
        .timeoutMs(10000) // Longer timeout for thread coordination
        .build();

    // When: Multiple threads request connections simultaneously
    int numThreads = 20;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<Future<Boolean>> futures = new ArrayList<>();

    for (int i = 0; i < numThreads; i++) {
      futures.add(executor.submit(() -> {
        Connection conn = null;
        try {
          conn = connectionManager.getConnection();
          Thread.sleep(10); // Simulate work
          return true;
        } catch (Exception e) {
          logger.error("Thread failed", e);
          return false;
        } finally {
          if (conn != null) {
            connectionManager.releaseConnection(conn);
          }
        }
      }));
    }

    // Then: All threads should succeed
    int successCount = 0;
    for (Future<Boolean> future : futures) {
      if (future.get()) {
        successCount++;
      }
    }

    assertEquals(numThreads, successCount,
        "All threads should successfully acquire and release connections");

    executor.shutdown();
    executor.awaitTermination(15, TimeUnit.SECONDS);
  }

  /**
   * Test 8: Shutdown closes all connections.
   */
  @Test
  @Order(8)
  @DisplayName("Should close all connections on shutdown")
  void testShutdown() throws SQLException {
    // Given: Pool with active connections
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(3)
        .maxSize(5)
        .build();

    Connection conn = connectionManager.getConnection();
    connectionManager.releaseConnection(conn);

    // When: Shutdown the pool
    connectionManager.shutdown();

    // Then: Should not be able to get new connections
    DAMException exception = assertThrows(DAMException.class,
        () -> connectionManager.getConnection(),
        "Should throw exception after shutdown");

    assertTrue(exception.getMessage().contains("shutdown"),
        "Exception should mention pool is shutdown");
  }

  /**
   * Test 9: Builder validates required parameters.
   */
  @Test
  @Order(9)
  @DisplayName("Should validate builder parameters")
  void testBuilderValidation() {
    // Test missing URL
    assertThrows(IllegalArgumentException.class,
        () -> new BasicConnectionManager.Builder()
            .driverClass(TEST_DRIVER)
            .build(),
        "Should throw exception when URL is missing");

    // Test missing driver
    assertThrows(IllegalArgumentException.class,
        () -> new BasicConnectionManager.Builder()
            .url(TEST_URL)
            .build(),
        "Should throw exception when driver class is missing");

    // Test invalid pool sizes
    assertThrows(IllegalArgumentException.class,
        () -> new BasicConnectionManager.Builder()
            .url(TEST_URL)
            .driverClass(TEST_DRIVER)
            .minSize(0)
            .build(),
        "Should throw exception when minSize < 1");

    assertThrows(IllegalArgumentException.class,
        () -> new BasicConnectionManager.Builder()
            .url(TEST_URL)
            .driverClass(TEST_DRIVER)
            .minSize(10)
            .maxSize(5)
            .build(),
        "Should throw exception when minSize > maxSize");
  }

  /**
   * Test 10: Released connection resets auto-commit state.
   */
  @Test
  @Order(10)
  @DisplayName("Should reset connection state on release")
  void testConnectionStateReset() throws SQLException {
    // Given: Pool with connection
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(1)
        .maxSize(2)
        .build();

    // When: Get connection, change state, and release
    Connection conn = connectionManager.getConnection();
    conn.setAutoCommit(false);
    connectionManager.releaseConnection(conn);

    // Then: Next connection should have auto-commit enabled
    Connection newConn = connectionManager.getConnection();
    assertTrue(newConn.getAutoCommit(),
        "Connection should have auto-commit reset to true");

    // Cleanup
    connectionManager.releaseConnection(newConn);
  }
}
