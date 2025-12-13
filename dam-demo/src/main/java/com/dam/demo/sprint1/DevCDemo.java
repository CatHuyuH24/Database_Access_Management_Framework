package com.dam.demo.sprint1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.dam.framework.config.DialectType;
import com.dam.framework.connection.BasicConnectionManager;
import com.dam.framework.connection.ConnectionManager;
import com.dam.framework.dialect.Dialect;
import com.dam.framework.dialect.DialectFactory;

/**
 * Demo class chứng minh DAM Framework Sprint 1 - Dev C
 * 
 * <p>
 * Demonstrates:
 * <ul>
 * <li>Connection pool management with BasicConnectionManager
 * <li>MySQL-specific SQL generation with MySQLDialect
 * <li>Dialect factory pattern
 * <li>Connection acquisition and release
 * <li>SQL execution with generated queries
 * </ul>
 * 
 * @author enkay2408
 */
public class DevCDemo {

  public static void main(String[] args) {
    System.out.println("=== DAM Framework Demo - Sprint 1 (Dev C) ===\n");

    // Demo 1: Connection Pool
    demoConnectionPool();

    System.out.println("\n" + "=".repeat(60) + "\n");

    // Demo 2: MySQL Dialect
    demoMySQLDialect();

    System.out.println("\n" + "=".repeat(60) + "\n");

    // Demo 3: Dialect Factory
    demoDialectFactory();

    System.out.println("\n" + "=".repeat(60) + "\n");

    // Demo 4: Full Integration
    demoFullIntegration();

    System.out.println("\n=== Demo Completed Successfully! ===");
  }

  /**
   * Demo 1: Connection Pool Management
   */
  private static void demoConnectionPool() {
    System.out.println("📦 DEMO 1: Connection Pool Management");
    System.out.println("-".repeat(60));

    // Create connection pool using Builder pattern
    ConnectionManager connectionManager = new BasicConnectionManager.Builder()
        .url("jdbc:h2:mem:demodb;MODE=MySQL;DB_CLOSE_DELAY=-1")
        .driverClass("org.h2.Driver")
        .minSize(3)
        .maxSize(10)
        .timeoutMs(5000)
        .validationQuery("SELECT 1")
        .build();

    System.out.println("✅ Connection pool created:");
    System.out.println("   - Min size: 3 connections");
    System.out.println("   - Max size: 10 connections");
    System.out.println("   - Timeout: 5000ms");

    try {
      // Acquire connection
      Connection conn = connectionManager.getConnection();
      System.out.println("\n✅ Connection acquired successfully");
      System.out.println("   - Valid: " + !conn.isClosed());
      System.out.println("   - Auto-commit: " + conn.getAutoCommit());

      // Release connection
      connectionManager.releaseConnection(conn);
      System.out.println("\n✅ Connection released back to pool");

    } catch (SQLException e) {
      System.err.println("❌ Error: " + e.getMessage());
    } finally {
      connectionManager.shutdown();
      System.out.println("\n✅ Connection pool shut down");
    }
  }

  /**
   * Demo 2: MySQL Dialect SQL Generation
   */
  private static void demoMySQLDialect() {
    System.out.println("🔧 DEMO 2: MySQL Dialect SQL Generation");
    System.out.println("-".repeat(60));

    // Create MySQL dialect using factory
    Dialect dialect = DialectFactory.createDialect(DialectType.MYSQL);

    // Demo: Dialect info
    System.out.println("✅ Dialect Name: " + dialect.getName());
    System.out.println("   Driver: " + dialect.getDriverClassName());
    System.out.println("   Validation Query: " + dialect.getValidationQuery());

    // Demo: LIMIT clause
    String limitClause = dialect.getLimitClause(10, 20);
    System.out.println("\n✅ LIMIT clause (limit=10, offset=20):");
    System.out.println("   " + limitClause);

    // Demo: Identifier quoting
    char quoteChar = dialect.getIdentifierQuoteCharacter();
    System.out.println("\n✅ Identifier quoting:");
    System.out.println("   Quote character: '" + quoteChar + "'");
    System.out.println("   Example: " + quoteChar + "user_table" + quoteChar);

    // Demo: Type mapping
    String varcharType = dialect.getTypeName(java.sql.Types.VARCHAR, 100);
    String intType = dialect.getTypeName(java.sql.Types.INTEGER, 0);
    String boolType = dialect.getTypeName(java.sql.Types.BOOLEAN, 0);
    System.out.println("\n✅ Type mapping (JDBC → MySQL):");
    System.out.println("   VARCHAR(100) → " + varcharType);
    System.out.println("   INTEGER → " + intType);
    System.out.println("   BOOLEAN → " + boolType);

    // Demo: AUTO_INCREMENT support
    String autoIncrementSyntax = dialect.getIdentityColumnString();
    boolean supportsSeq = dialect.supportsSequences();
    System.out.println("\n✅ Features:");
    System.out.println("   AUTO_INCREMENT: " + autoIncrementSyntax);
    System.out.println("   Supports Sequences: " + supportsSeq);
  }

  /**
   * Demo 3: Dialect Factory Pattern
   */
  private static void demoDialectFactory() {
    System.out.println("🏭 DEMO 3: Dialect Factory Pattern");
    System.out.println("-".repeat(60));

    // Get supported dialects
    System.out.println("✅ Supported database dialects:");
    for (DialectType type : DialectFactory.getSupportedDialects()) {
      System.out.println("   - " + type);
    }

    // Create dialect by type
    Dialect mysqlDialect = DialectFactory.createDialect(DialectType.MYSQL);
    System.out.println("\n✅ Created MySQL dialect:");
    System.out.println("   Class: " + mysqlDialect.getClass().getSimpleName());

    // Create dialect by name
    Dialect dialectByName = DialectFactory.createDialect("mysql");
    System.out.println("\n✅ Created dialect by name 'mysql':");
    System.out.println("   Same instance: " + (mysqlDialect.getClass() == dialectByName.getClass()));

    // Check support
    boolean isSupported = DialectFactory.isSupported(DialectType.MYSQL);
    System.out.println("\n✅ MySQL dialect supported: " + isSupported);
  }

  /**
   * Demo 4: Full Integration - Connection Pool + MySQL Dialect
   */
  private static void demoFullIntegration() {
    System.out.println("🎯 DEMO 4: Full Integration Example");
    System.out.println("-".repeat(60));

    // Setup connection pool
    ConnectionManager connectionManager = new BasicConnectionManager.Builder()
        .url("jdbc:h2:mem:integrationdb;MODE=MySQL;DB_CLOSE_DELAY=-1")
        .driverClass("org.h2.Driver")
        .minSize(2)
        .maxSize(5)
        .build();

    // Get MySQL dialect
    Dialect dialect = DialectFactory.createDialect(DialectType.MYSQL);
    char quote = dialect.getIdentifierQuoteCharacter();

    Connection conn = null;
    try {
      // Acquire connection
      conn = connectionManager.getConnection();
      System.out.println("✅ Connection acquired from pool");

      // Create table using MySQL dialect syntax
      String createTableSQL = String.format(
          "CREATE TABLE %cproducts%c (" +
              "  %cid%c %s %s PRIMARY KEY, " +
              "  %cname%c %s NOT NULL, " +
              "  %cprice%c %s, " +
              "  %cavailable%c %s" +
              ")",
          quote, quote,
          quote, quote, dialect.getTypeName(java.sql.Types.INTEGER, 0),
          dialect.getIdentityColumnString(),
          quote, quote, dialect.getTypeName(java.sql.Types.VARCHAR, 100),
          quote, quote, dialect.getTypeName(java.sql.Types.DECIMAL, 10),
          quote, quote, dialect.getTypeName(java.sql.Types.BOOLEAN, 0));

      Statement stmt = conn.createStatement();
      stmt.execute(createTableSQL);
      System.out.println("\n✅ Table created with MySQL syntax:");
      System.out.println("   CREATE TABLE `products` (");
      System.out.println("     `id` INT AUTO_INCREMENT PRIMARY KEY,");
      System.out.println("     `name` VARCHAR(100) NOT NULL,");
      System.out.println("     `price` DECIMAL(10,2),");
      System.out.println("     `available` TINYINT(1)");
      System.out.println("   )");

      // Insert sample data
      String insertSQL = String.format(
          "INSERT INTO %cproducts%c (%cname%c, %cprice%c, %cavailable%c) " +
              "VALUES ('Laptop', 999.99, TRUE), ('Mouse', 25.50, TRUE)",
          quote, quote, quote, quote, quote, quote, quote, quote);
      stmt.execute(insertSQL);
      System.out.println("\n✅ Sample data inserted (2 products)");

      // Query with LIMIT/OFFSET
      String querySQL = String.format(
          "SELECT * FROM %cproducts%c WHERE %cavailable%c = TRUE " +
              "ORDER BY %cprice%c DESC %s",
          quote, quote, quote, quote, quote, quote,
          dialect.getLimitClause(5, 0));

      ResultSet rs = stmt.executeQuery(querySQL);
      System.out.println("\n✅ Query with LIMIT:");
      System.out.println("   " + querySQL.replace("`", "'"));

      System.out.println("\n📊 Query Results:");
      System.out.println("   " + "-".repeat(50));
      while (rs.next()) {
        System.out.printf("   | %-15s | $%-8.2f | Available: %-5s |%n",
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getBoolean("available"));
      }
      System.out.println("   " + "-".repeat(50));

      rs.close();
      stmt.close();

    } catch (SQLException e) {
      System.err.println("❌ Error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (conn != null) {
        connectionManager.releaseConnection(conn);
        System.out.println("\n✅ Connection released back to pool");
      }
      connectionManager.shutdown();
      System.out.println("✅ Connection pool shut down");
    }
  }
}
