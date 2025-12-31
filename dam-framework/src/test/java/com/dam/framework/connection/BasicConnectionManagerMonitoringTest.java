package com.dam.framework.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dam.framework.connection.BasicConnectionManager.ConnectionPoolStats;

/**
 * Tests for Sprint 2 connection pool monitoring features.
 * <p>
 * Tests new monitoring and statistics capabilities added to
 * BasicConnectionManager in Sprint 2.
 * 
 * @author Dev C
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BasicConnectionManagerMonitoringTest {

  private static final Logger logger = LoggerFactory.getLogger(BasicConnectionManagerMonitoringTest.class);

  private static final String TEST_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private static final String TEST_DRIVER = "org.h2.Driver";

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
   * Test 1: getStatistics returns valid statistics.
   */
  @Test
  @Order(1)
  @DisplayName("Should return connection pool statistics")
  void testGetStatistics() {
    // Given: Pool with 3 connections
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(3)
        .maxSize(10)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Get statistics
    ConnectionPoolStats stats = bcm.getStatistics();

    // Then: Statistics should be populated
    assertNotNull(stats);
    assertEquals(3, stats.totalConnections);
    assertEquals(3, stats.availableConnections);
    assertEquals(10, stats.maxSize);
    assertEquals(3, stats.minSize);
    assertEquals(3, stats.connectionsCreated);
    assertEquals(0, stats.connectionsAcquired);
    assertEquals(0, stats.connectionsReleased);
    assertEquals(0, stats.connectionTimeouts);
    assertEquals(0, stats.validationFailures);
  }

  /**
   * Test 2: Statistics track connection acquisitions.
   */
  @Test
  @Order(2)
  @DisplayName("Should track connection acquisitions")
  void testTrackAcquisitions() throws SQLException {
    // Given: Pool with connections
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Acquire connections
    Connection conn1 = connectionManager.getConnection();
    Connection conn2 = connectionManager.getConnection();

    ConnectionPoolStats stats = bcm.getStatistics();

    // Then: Acquisitions should be tracked
    assertEquals(2, stats.connectionsAcquired);
    assertEquals(0, stats.availableConnections);

    // Cleanup
    connectionManager.releaseConnection(conn1);
    connectionManager.releaseConnection(conn2);
  }

  /**
   * Test 3: Statistics track connection releases.
   */
  @Test
  @Order(3)
  @DisplayName("Should track connection releases")
  void testTrackReleases() throws SQLException {
    // Given: Pool with acquired connections
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    Connection conn1 = connectionManager.getConnection();
    Connection conn2 = connectionManager.getConnection();

    // When: Release connections
    connectionManager.releaseConnection(conn1);
    connectionManager.releaseConnection(conn2);

    ConnectionPoolStats stats = bcm.getStatistics();

    // Then: Releases should be tracked
    assertEquals(2, stats.connectionsReleased);
    assertEquals(2, stats.availableConnections);
  }

  /**
   * Test 4: Statistics track connection creation.
   */
  @Test
  @Order(4)
  @DisplayName("Should track total connections created")
  void testTrackConnectionCreation() throws SQLException {
    // Given: Pool that can grow
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Acquire more connections than minSize
    Connection conn1 = connectionManager.getConnection();
    Connection conn2 = connectionManager.getConnection();
    Connection conn3 = connectionManager.getConnection(); // Creates new connection

    ConnectionPoolStats stats = bcm.getStatistics();

    // Then: Total created should include initial + grown
    assertEquals(3, stats.connectionsCreated);
    assertEquals(3, stats.totalConnections);

    // Cleanup
    connectionManager.releaseConnection(conn1);
    connectionManager.releaseConnection(conn2);
    connectionManager.releaseConnection(conn3);
  }

  /**
   * Test 5: isHealthy returns true for healthy pool.
   */
  @Test
  @Order(5)
  @DisplayName("Should report pool as healthy")
  void testHealthyPool() {
    // Given: Normal pool
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(3)
        .maxSize(10)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Check health
    boolean healthy = bcm.isHealthy();

    // Then: Should be healthy
    assertTrue(healthy);
  }

  /**
   * Test 6: isHealthy returns false after shutdown.
   */
  @Test
  @Order(6)
  @DisplayName("Should report pool as unhealthy after shutdown")
  void testUnhealthyAfterShutdown() {
    // Given: Pool that will be shutdown
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(3)
        .maxSize(10)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Shutdown pool
    connectionManager.shutdown();

    // Then: Should be unhealthy
    assertFalse(bcm.isHealthy());
  }

  /**
   * Test 7: getStatusReport provides detailed information.
   */
  @Test
  @Order(7)
  @DisplayName("Should provide detailed status report")
  void testStatusReport() {
    // Given: Pool with some activity
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(3)
        .maxSize(10)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Get status report
    String report = bcm.getStatusReport();

    // Then: Report should contain key information
    assertNotNull(report);
    assertTrue(report.contains("Connection Pool Status"));
    assertTrue(report.contains("HEALTHY"));
    assertTrue(report.contains("Total Connections: 3/10"));
    assertTrue(report.contains("Available: 3"));
    assertTrue(report.contains("Total Created: 3"));
  }

  /**
   * Test 8: Statistics toString provides useful output.
   */
  @Test
  @Order(8)
  @DisplayName("Should provide useful statistics toString")
  void testStatisticsToString() {
    // Given: Pool with statistics
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(3)
        .maxSize(10)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;
    ConnectionPoolStats stats = bcm.getStatistics();

    // When: Get toString
    String statsString = stats.toString();

    // Then: Should contain key metrics
    assertNotNull(statsString);
    assertTrue(statsString.contains("total=3"));
    assertTrue(statsString.contains("available=3"));
    assertTrue(statsString.contains("max=10"));
    assertTrue(statsString.contains("min=3"));
  }

  /**
   * Test 9: Statistics track multiple acquire/release cycles.
   */
  @Test
  @Order(9)
  @DisplayName("Should track multiple acquire/release cycles")
  void testMultipleCycles() throws SQLException {
    // Given: Pool
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Multiple cycles of acquire/release
    for (int i = 0; i < 5; i++) {
      Connection conn = connectionManager.getConnection();
      connectionManager.releaseConnection(conn);
    }

    ConnectionPoolStats stats = bcm.getStatistics();

    // Then: Should track all operations
    assertEquals(5, stats.connectionsAcquired);
    assertEquals(5, stats.connectionsReleased);
    assertEquals(2, stats.availableConnections);
  }

  /**
   * Test 10: Status report shows unhealthy state correctly.
   */
  @Test
  @Order(10)
  @DisplayName("Should show unhealthy status in report")
  void testUnhealthyStatusReport() {
    // Given: Pool that will be shutdown
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(3)
        .maxSize(10)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // When: Shutdown and get report
    connectionManager.shutdown();
    String report = bcm.getStatusReport();

    // Then: Report should show unhealthy
    assertTrue(report.contains("UNHEALTHY"));
  }

  /**
   * Test 11: Statistics accurately reflect pool state after growth.
   */
  @Test
  @Order(11)
  @DisplayName("Should accurately track pool growth")
  void testPoolGrowthTracking() throws SQLException {
    // Given: Small pool that can grow
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .build();

    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    ConnectionPoolStats initialStats = bcm.getStatistics();
    assertEquals(2, initialStats.connectionsCreated);

    // When: Acquire enough to trigger growth
    Connection conn1 = connectionManager.getConnection();
    Connection conn2 = connectionManager.getConnection();
    Connection conn3 = connectionManager.getConnection(); // Triggers growth

    ConnectionPoolStats grownStats = bcm.getStatistics();

    // Then: Should reflect growth
    assertEquals(3, grownStats.connectionsCreated);
    assertEquals(3, grownStats.totalConnections);
    assertEquals(0, grownStats.availableConnections);

    // Cleanup
    connectionManager.releaseConnection(conn1);
    connectionManager.releaseConnection(conn2);
    connectionManager.releaseConnection(conn3);
  }

  /**
   * Test 12: Verify Sprint 2 monitoring methods are accessible.
   */
  @Test
  @Order(12)
  @DisplayName("Should have all Sprint 2 monitoring methods")
  void testSprint2MonitoringMethodsPresent() throws NoSuchMethodException {
    // Verify new methods exist
    assertNotNull(BasicConnectionManager.class.getMethod("getStatistics"));
    assertNotNull(BasicConnectionManager.class.getMethod("isHealthy"));
    assertNotNull(BasicConnectionManager.class.getMethod("getStatusReport"));
  }
}
