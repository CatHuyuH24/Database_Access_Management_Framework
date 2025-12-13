package com.dam.framework.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dam.framework.config.DialectType;
import com.dam.framework.connection.BasicConnectionManager;
import com.dam.framework.connection.ConnectionManager;
import com.dam.framework.dialect.DialectFactory;
import com.dam.framework.dialect.MySQLDialect;

/**
 * Integration tests for MySQL connectivity and SQL execution.
 * <p>
 * These tests require a running MySQL database instance.
 * Uses H2 database in MySQL compatibility mode for testing.
 * 
 * <h3>Test Configuration:</h3>
 * <ul>
 * <li>Database: H2 in-memory (MySQL mode)</li>
 * <li>URL: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1</li>
 * <li>Driver: org.h2.Driver</li>
 * </ul>
 * 
 * <h3>Note for Production:</h3>
 * For testing with real MySQL:
 * <ul>
 * <li>URL: jdbc:mysql://localhost:3306/test_db</li>
 * <li>Driver: com.mysql.cj.jdbc.Driver</li>
 * <li>Username/Password: Set via environment variables or properties</li>
 * </ul>
 * 
 * @author enkay2408
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySQLIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(MySQLIntegrationTest.class);

  // H2 database in MySQL mode for testing
  private static final String TEST_URL = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1";
  private static final String TEST_DRIVER = "org.h2.Driver";
  private static final String TEST_USER = "sa";
  private static final String TEST_PASSWORD = "";

  private ConnectionManager connectionManager;
  private MySQLDialect dialect;

  @BeforeAll
  static void setUpClass() {
    logger.info("Starting MySQL Integration Tests");
    logger.info("Using H2 database in MySQL compatibility mode");
  }

  @BeforeEach
  void setUp() {
    // Initialize connection manager
    connectionManager = new BasicConnectionManager.Builder()
        .url(TEST_URL)
        .username(TEST_USER)
        .password(TEST_PASSWORD)
        .driverClass(TEST_DRIVER)
        .minSize(2)
        .maxSize(5)
        .timeoutMs(5000)
        .validationQuery("SELECT 1")
        .build();

    // Initialize MySQL dialect
    dialect = (MySQLDialect) DialectFactory.createDialect(DialectType.MYSQL);

    logger.info("Test setup complete");
  }

  @AfterEach
  void tearDown() {
    if (connectionManager != null) {
      connectionManager.shutdown();
    }
    logger.info("Test cleanup complete");
  }

  /**
   * Test 1: Connection manager can connect to database.
   */
  @Test
  @Order(1)
  @DisplayName("Should successfully connect to database")
  void testDatabaseConnection() throws SQLException {
    Connection conn = connectionManager.getConnection();

    assertNotNull(conn, "Connection should not be null");
    assertFalse(conn.isClosed(), "Connection should be open");

    DatabaseMetaData metadata = conn.getMetaData();
    logger.info("Connected to: {} {}",
        metadata.getDatabaseProductName(),
        metadata.getDatabaseProductVersion());

    connectionManager.releaseConnection(conn);
  }

  /**
   * Test 2: Can create table with MySQL dialect types.
   */
  @Test
  @Order(2)
  @DisplayName("Should create table with MySQL-specific types")
  void testCreateTable() throws SQLException {
    Connection conn = connectionManager.getConnection();

    try {
      // Create table using MySQL dialect types
      String createTableSQL = String.format(
          "CREATE TABLE %s (" +
              "  %s %s %s PRIMARY KEY," +
              "  %s %s NOT NULL," +
              "  %s %s," +
              "  %s %s," +
              "  %s %s" +
              ")",
          dialect.quoteIdentifier("users"),
          dialect.quoteIdentifier("id"),
          dialect.getTypeName(Types.INTEGER, 0),
          dialect.getIdentityColumnString(),
          dialect.quoteIdentifier("username"),
          dialect.getTypeName(Types.VARCHAR, 50),
          dialect.quoteIdentifier("email"),
          dialect.getTypeName(Types.VARCHAR, 100),
          dialect.quoteIdentifier("age"),
          dialect.getTypeName(Types.INTEGER, 0),
          dialect.quoteIdentifier("active"),
          dialect.getTypeName(Types.BOOLEAN, 0));

      logger.info("Executing SQL: {}", createTableSQL);

      Statement stmt = conn.createStatement();
      stmt.execute(createTableSQL);
      stmt.close();

      // Verify table exists (H2 stores table names in uppercase by default)
      DatabaseMetaData metadata = conn.getMetaData();
      ResultSet tables = metadata.getTables(null, null, "USERS", null);
      if (!tables.next()) {
        // Try lowercase
        tables.close();
        tables = metadata.getTables(null, null, "users", null);
      }
      assertTrue(tables.next(), "Table 'users' should exist");
      tables.close();

      logger.info("Table created successfully");

    } finally {
      connectionManager.releaseConnection(conn);
    }
  }

  /**
   * Test 3: Can insert data into table.
   */
  @Test
  @Order(3)
  @DisplayName("Should insert data into table")
  void testInsertData() throws SQLException {
    Connection conn = connectionManager.getConnection();

    try {
      // Create table first
      createUsersTable(conn);

      // Insert data
      String insertSQL = "INSERT INTO `users` (`username`, `email`, `age`, `active`) VALUES (?, ?, ?, ?)";
      PreparedStatement pstmt = conn.prepareStatement(insertSQL);

      pstmt.setString(1, "johndoe");
      pstmt.setString(2, "john@example.com");
      pstmt.setInt(3, 30);
      pstmt.setBoolean(4, true);

      int rowsAffected = pstmt.executeUpdate();
      assertEquals(1, rowsAffected, "Should insert 1 row");

      pstmt.close();
      logger.info("Data inserted successfully");

    } finally {
      connectionManager.releaseConnection(conn);
    }
  }

  /**
   * Test 4: Can query data with LIMIT clause.
   */
  @Test
  @Order(4)
  @DisplayName("Should query data with LIMIT clause")
  void testQueryWithLimit() throws SQLException {
    Connection conn = connectionManager.getConnection();

    try {
      // Create table and insert test data
      createUsersTable(conn);
      insertTestData(conn, 10);

      // Query with LIMIT
      String querySQL = "SELECT * FROM `users` " + dialect.getLimitClause(5, 0);
      logger.info("Executing query: {}", querySQL);

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(querySQL);

      int count = 0;
      while (rs.next()) {
        count++;
        logger.debug("Row {}: id={}, username={}",
            count, rs.getInt("id"), rs.getString("username"));
      }

      assertEquals(5, count, "Should return 5 rows");

      rs.close();
      stmt.close();

    } finally {
      connectionManager.releaseConnection(conn);
    }
  }

  /**
   * Test 5: Can query data with LIMIT and OFFSET.
   */
  @Test
  @Order(5)
  @DisplayName("Should query data with LIMIT and OFFSET")
  void testQueryWithLimitAndOffset() throws SQLException {
    Connection conn = connectionManager.getConnection();

    try {
      // Create table and insert test data
      createUsersTable(conn);
      insertTestData(conn, 10);

      // Query with LIMIT and OFFSET
      String querySQL = "SELECT * FROM `users` ORDER BY `id` " +
          dialect.getLimitClause(3, 5);
      logger.info("Executing query: {}", querySQL);

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(querySQL);

      int count = 0;
      int firstId = -1;
      while (rs.next()) {
        count++;
        int id = rs.getInt("id");
        if (firstId == -1) {
          firstId = id;
        }
        logger.debug("Row {}: id={}", count, id);
      }

      assertEquals(3, count, "Should return 3 rows");
      assertTrue(firstId >= 6, "First row should have id >= 6 (after offset of 5)");

      rs.close();
      stmt.close();

    } finally {
      connectionManager.releaseConnection(conn);
    }
  }

  /**
   * Test 6: Connection pool reuses connections.
   */
  @Test
  @Order(6)
  @DisplayName("Should reuse connections from pool")
  void testConnectionPooling() throws SQLException {
    BasicConnectionManager bcm = (BasicConnectionManager) connectionManager;

    // Get initial pool state
    int initialTotal = bcm.getTotalConnectionCount();

    // Acquire and release multiple connections
    for (int i = 0; i < 10; i++) {
      Connection conn = connectionManager.getConnection();
      assertNotNull(conn);
      connectionManager.releaseConnection(conn);
    }

    // Pool size should not have grown significantly
    int finalTotal = bcm.getTotalConnectionCount();
    assertTrue(finalTotal <= initialTotal + 2,
        "Pool should reuse connections, not create many new ones");

    logger.info("Pool stats - Initial: {}, Final: {}", initialTotal, finalTotal);
  }

  /**
   * Test 7: Quoted identifiers work correctly.
   */
  @Test
  @Order(7)
  @DisplayName("Should handle quoted identifiers correctly")
  void testQuotedIdentifiers() throws SQLException {
    Connection conn = connectionManager.getConnection();

    try {
      // Create table with quoted identifiers
      String createSQL = String.format(
          "CREATE TABLE %s (%s INT, %s VARCHAR(50))",
          dialect.quoteIdentifier("test_table"),
          dialect.quoteIdentifier("id"),
          dialect.quoteIdentifier("name"));

      Statement stmt = conn.createStatement();
      stmt.execute(createSQL);

      // Insert data with quoted identifiers
      String insertSQL = String.format(
          "INSERT INTO %s (%s, %s) VALUES (1, 'Test')",
          dialect.quoteIdentifier("test_table"),
          dialect.quoteIdentifier("id"),
          dialect.quoteIdentifier("name"));

      stmt.execute(insertSQL);

      // Query with quoted identifiers
      String querySQL = String.format(
          "SELECT * FROM %s WHERE %s = 1",
          dialect.quoteIdentifier("test_table"),
          dialect.quoteIdentifier("id"));

      ResultSet rs = stmt.executeQuery(querySQL);
      assertTrue(rs.next(), "Should find inserted row");
      assertEquals("Test", rs.getString("name"));

      rs.close();
      stmt.close();
      logger.info("Quoted identifiers work correctly");

    } finally {
      connectionManager.releaseConnection(conn);
    }
  }

  /**
   * Test 8: Transaction rollback works.
   */
  @Test
  @Order(8)
  @DisplayName("Should rollback transaction on error")
  void testTransactionRollback() throws SQLException {
    Connection conn = connectionManager.getConnection();

    try {
      createUsersTable(conn);

      // Start transaction
      conn.setAutoCommit(false);

      // Insert data
      String insertSQL = "INSERT INTO `users` (`username`, `email`, `age`, `active`) VALUES (?, ?, ?, ?)";
      PreparedStatement pstmt = conn.prepareStatement(insertSQL);
      pstmt.setString(1, "testuser");
      pstmt.setString(2, "test@example.com");
      pstmt.setInt(3, 25);
      pstmt.setBoolean(4, true);
      pstmt.executeUpdate();
      pstmt.close();

      // Rollback
      conn.rollback();
      conn.setAutoCommit(true);

      // Verify data was rolled back
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `users`");
      rs.next();
      int count = rs.getInt(1);
      assertEquals(0, count, "Data should be rolled back");

      rs.close();
      stmt.close();
      logger.info("Transaction rollback successful");

    } finally {
      connectionManager.releaseConnection(conn);
    }
  }

  // ===== Helper Methods =====

  private void createUsersTable(Connection conn) throws SQLException {
    String dropSQL = "DROP TABLE IF EXISTS `users`";
    Statement stmt = conn.createStatement();
    stmt.execute(dropSQL);

    String createSQL = String.format(
        "CREATE TABLE %s (" +
            "  %s %s %s PRIMARY KEY," +
            "  %s %s NOT NULL," +
            "  %s %s," +
            "  %s %s," +
            "  %s %s" +
            ")",
        dialect.quoteIdentifier("users"),
        dialect.quoteIdentifier("id"),
        dialect.getTypeName(Types.INTEGER, 0),
        dialect.getIdentityColumnString(),
        dialect.quoteIdentifier("username"),
        dialect.getTypeName(Types.VARCHAR, 50),
        dialect.quoteIdentifier("email"),
        dialect.getTypeName(Types.VARCHAR, 100),
        dialect.quoteIdentifier("age"),
        dialect.getTypeName(Types.INTEGER, 0),
        dialect.quoteIdentifier("active"),
        dialect.getTypeName(Types.BOOLEAN, 0));

    stmt.execute(createSQL);
    stmt.close();
  }

  private void insertTestData(Connection conn, int count) throws SQLException {
    String insertSQL = "INSERT INTO `users` (`username`, `email`, `age`, `active`) VALUES (?, ?, ?, ?)";
    PreparedStatement pstmt = conn.prepareStatement(insertSQL);

    for (int i = 1; i <= count; i++) {
      pstmt.setString(1, "user" + i);
      pstmt.setString(2, "user" + i + "@example.com");
      pstmt.setInt(3, 20 + i);
      pstmt.setBoolean(4, i % 2 == 0);
      pstmt.executeUpdate();
    }

    pstmt.close();
    logger.debug("Inserted {} test records", count);
  }
}
