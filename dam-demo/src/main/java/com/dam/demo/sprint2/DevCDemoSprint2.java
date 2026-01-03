// package com.dam.demo.sprint2;

// import java.math.BigDecimal;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Types;
// import java.time.LocalDate;
// import java.time.LocalDateTime;

// import com.dam.framework.config.DialectDriverType;
// import com.dam.framework.connection.BasicConnectionManager;
// import
// com.dam.framework.connection.BasicConnectionManager.ConnectionPoolStats;
// import com.dam.framework.dialect.DialectFactory;
// import com.dam.framework.dialect.MySQLDialect;
// import com.dam.framework.util.TypeMapper;

// /**
// * Demo class chứng minh DAM Framework Sprint 2
// *
// * <p>
// * Demonstrates:
// * <ul>
// * <li>TypeMapper utility for bidirectional type conversion
// * <li>Enhanced MySQLDialect with type mapping integration
// * <li>Connection pool monitoring (statistics, health checks)
// * <li>PreparedStatement parameter handling with type safety
// * </ul>
// *
// * @author enkay2408
// */
// public class DevCDemoSprint2 {

// public static void main(String[] args) {
// System.out.println("=== DAM Framework Demo - Sprint 2 (Dev C) ===\n");

// // Demo 1: TypeMapper
// demoTypeMapper();

// System.out.println("\n" + "=".repeat(60) + "\n");

// // Demo 2: Enhanced MySQLDialect
// demoEnhancedMySQLDialect();

// System.out.println("\n" + "=".repeat(60) + "\n");

// // Demo 3: Connection Pool Monitoring
// demoConnectionPoolMonitoring();

// System.out.println("\n" + "=".repeat(60) + "\n");

// // Demo 4: Full Integration
// demoFullIntegration();

// System.out.println("\n=== Demo Completed Successfully! ===");
// }

// /**
// * Demo 1: TypeMapper Utility
// */
// private static void demoTypeMapper() {
// System.out.println("🔄 DEMO 1: TypeMapper - Bidirectional Type Conversion");
// System.out.println("-".repeat(60));

// // Java → JDBC Type Mapping
// System.out.println("✅ Java → JDBC Type Mapping:");
// System.out.println(" String.class → " + TypeMapper.getJdbcType(String.class)
// + " (VARCHAR)");
// System.out.println(" Integer.class → " +
// TypeMapper.getJdbcType(Integer.class) + " (INTEGER)");
// System.out.println(" Boolean.class → " +
// TypeMapper.getJdbcType(Boolean.class) + " (BOOLEAN)");
// System.out.println(" LocalDate.class → " +
// TypeMapper.getJdbcType(LocalDate.class) + " (DATE)");
// System.out.println(" LocalDateTime.class→ " +
// TypeMapper.getJdbcType(LocalDateTime.class) + " (TIMESTAMP)");
// System.out.println(" BigDecimal.class → " +
// TypeMapper.getJdbcType(BigDecimal.class) + " (DECIMAL)");

// // JDBC → Java Type Mapping
// System.out.println("\n✅ JDBC → Java Type Mapping:");
// System.out.println(" VARCHAR (12) → " +
// TypeMapper.getJavaType(Types.VARCHAR).getSimpleName());
// System.out.println(" INTEGER (4) → " +
// TypeMapper.getJavaType(Types.INTEGER).getSimpleName());
// System.out.println(" BOOLEAN (16) → " +
// TypeMapper.getJavaType(Types.BOOLEAN).getSimpleName());
// System.out.println(" DATE (91) → " +
// TypeMapper.getJavaType(Types.DATE).getSimpleName());
// System.out.println(" TIMESTAMP (93) → " +
// TypeMapper.getJavaType(Types.TIMESTAMP).getSimpleName());
// System.out.println(" DECIMAL (3) → " +
// TypeMapper.getJavaType(Types.DECIMAL).getSimpleName());

// // Type Checkers
// System.out.println("\n✅ Type Checkers:");
// System.out.println(" isPrimitiveOrWrapper(int.class) → " +
// TypeMapper.isPrimitiveOrWrapper(int.class));
// System.out.println(" isNumericType(BigDecimal.class) → " +
// TypeMapper.isNumericType(BigDecimal.class));
// System.out.println(" isStringType(String.class) → " +
// TypeMapper.isStringType(String.class));
// System.out.println(" isDateTimeType(LocalDate.class) → " +
// TypeMapper.isDateTimeType(LocalDate.class));
// }

// /**
// * Demo 2: Enhanced MySQLDialect with Type Mapping
// */
// private static void demoEnhancedMySQLDialect() {
// System.out.println("🔧 DEMO 2: Enhanced MySQLDialect - Type Mapping
// Integration");
// System.out.println("-".repeat(60));

// MySQLDialect dialect = (MySQLDialect)
// DialectFactory.createDialect(DialectDriverType.MYSQL);

// // JDBC → Java Type
// System.out.println("\n✅ JDBC → Java Type (via MySQLDialect):");
// System.out.println(" JDBC VARCHAR → " +
// dialect.getJavaTypeForJdbcType(Types.VARCHAR).getSimpleName());
// System.out.println(" JDBC INTEGER → " +
// dialect.getJavaTypeForJdbcType(Types.INTEGER).getSimpleName());
// System.out.println(" JDBC DATE → " +
// dialect.getJavaTypeForJdbcType(Types.DATE).getSimpleName());

// // Java → MySQL Type (one-step conversion)
// System.out.println("\n✅ Java → MySQL Type (direct conversion):");
// System.out.println(" String.class (100) → " +
// dialect.getMySQLTypeForJavaType(String.class, 100));
// System.out.println(" Integer.class → " +
// dialect.getMySQLTypeForJavaType(Integer.class, 0));
// System.out.println(" Boolean.class → " +
// dialect.getMySQLTypeForJavaType(Boolean.class, 0));
// System.out.println(" LocalDate.class → " +
// dialect.getMySQLTypeForJavaType(LocalDate.class, 0));
// System.out.println(" BigDecimal.class → " +
// dialect.getMySQLTypeForJavaType(BigDecimal.class, 10));
// }

// /**
// * Demo 3: Connection Pool Monitoring
// */
// private static void demoConnectionPoolMonitoring() {
// System.out.println("📊 DEMO 3: Connection Pool Monitoring");
// System.out.println("-".repeat(60));

// BasicConnectionManager connectionManager = (BasicConnectionManager) new
// BasicConnectionManager.Builder()
// .url("jdbc:h2:mem:monitoringdb;MODE=MySQL;DB_CLOSE_DELAY=-1")
// .driverClass("org.h2.Driver")
// .minSize(3)
// .maxSize(10)
// .timeoutMs(5000)
// .build();

// try {
// // Initial statistics
// ConnectionPoolStats stats = connectionManager.getStatistics();
// System.out.println("✅ Initial Pool Statistics:");
// System.out.println(" Current Size: " + stats.totalConnections);
// System.out.println(" Available: " + stats.availableConnections);
// System.out.println(" Min Size: " + stats.minSize);
// System.out.println(" Max Size: " + stats.maxSize);

// // Acquire and release connections
// System.out.println("\n📝 Acquiring 3 connections...");
// Connection conn1 = connectionManager.getConnection();
// Connection conn2 = connectionManager.getConnection();
// Connection conn3 = connectionManager.getConnection();

// stats = connectionManager.getStatistics();
// System.out.println("✅ After acquiring 3 connections:");
// System.out.println(" Available: " + stats.availableConnections);
// System.out.println(" Total Acquired: " + stats.connectionsAcquired);

// connectionManager.releaseConnection(conn1);
// connectionManager.releaseConnection(conn2);
// connectionManager.releaseConnection(conn3);

// stats = connectionManager.getStatistics();
// System.out.println("\n✅ After releasing 3 connections:");
// System.out.println(" Available: " + stats.availableConnections);
// System.out.println(" Total Released: " + stats.connectionsReleased);

// // Health check
// boolean healthy = connectionManager.isHealthy();
// System.out.println("\n✅ Health Check:");
// System.out.println(" Pool is healthy: " + healthy);

// // Status report
// String statusReport = connectionManager.getStatusReport();
// System.out.println("\n✅ Detailed Status Report:");
// System.out.println(statusReport);

// } catch (Exception e) {
// System.err.println("❌ Error: " + e.getMessage());
// } finally {
// connectionManager.shutdown();
// System.out.println("\n✅ Connection pool shut down");
// }
// }

// /**
// * Demo 4: Full Integration - TypeMapper + Enhanced Dialect + Monitoring
// */
// private static void demoFullIntegration() {
// System.out.println("🎯 DEMO 4: Full Integration Example");
// System.out.println("-".repeat(60));

// BasicConnectionManager connectionManager = (BasicConnectionManager) new
// BasicConnectionManager.Builder()
// .url("jdbc:h2:mem:integrationdb;MODE=MySQL;DB_CLOSE_DELAY=-1")
// .driverClass("org.h2.Driver")
// .minSize(2)
// .maxSize(5)
// .build();

// MySQLDialect dialect = (MySQLDialect)
// DialectFactory.createDialect(DialectDriverType.MYSQL);
// Connection conn = null;

// try {
// conn = connectionManager.getConnection();
// System.out.println("✅ Connection acquired from pool");

// // Create table with type-mapped columns
// String createTableSQL = "CREATE TABLE `employees` (" +
// " `id` INT AUTO_INCREMENT PRIMARY KEY, " +
// " `name` VARCHAR(100) NOT NULL, " +
// " `salary` DECIMAL(10,2), " +
// " `hire_date` DATE, " +
// " `last_login` TIMESTAMP, " +
// " `is_active` TINYINT(1)" +
// ")";

// conn.createStatement().execute(createTableSQL);
// System.out.println("\n✅ Table 'employees' created with typed columns");

// // Insert data using PreparedStatement with type-safe parameters
// String insertSQL = "INSERT INTO `employees` (`name`, `salary`, `hire_date`,
// `last_login`, `is_active`) " +
// "VALUES (?, ?, ?, ?, ?)";
// PreparedStatement pstmt = conn.prepareStatement(insertSQL);

// // Use MySQLDialect.setParameter for type-safe parameter setting
// dialect.setParameter(pstmt, 1, "John Doe", String.class);
// dialect.setParameter(pstmt, 2, new BigDecimal("75000.50"), BigDecimal.class);
// dialect.setParameter(pstmt, 3, LocalDate.of(2023, 1, 15), LocalDate.class);
// dialect.setParameter(pstmt, 4, LocalDateTime.now(), LocalDateTime.class);
// dialect.setParameter(pstmt, 5, true, Boolean.class);
// pstmt.executeUpdate();

// dialect.setParameter(pstmt, 1, "Jane Smith", String.class);
// dialect.setParameter(pstmt, 2, new BigDecimal("82000.00"), BigDecimal.class);
// dialect.setParameter(pstmt, 3, LocalDate.of(2022, 6, 1), LocalDate.class);
// dialect.setParameter(pstmt, 4, LocalDateTime.now(), LocalDateTime.class);
// dialect.setParameter(pstmt, 5, true, Boolean.class);
// pstmt.executeUpdate();

// pstmt.close();
// System.out.println("✅ 2 employees inserted using type-safe
// PreparedStatement");

// // Query with TypeMapper for result extraction
// ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM `employees`
// ORDER BY `salary` DESC");

// System.out.println("\n📊 Query Results (using TypeMapper for extraction):");
// System.out.println(" " + "-".repeat(80));
// System.out.printf(" | %-4s | %-15s | %-10s | %-12s | %-8s |%n",
// "ID", "Name", "Salary", "Hire Date", "Active");
// System.out.println(" " + "-".repeat(80));

// while (rs.next()) {
// Integer id = TypeMapper.getResultSetValue(rs, "id", Integer.class);
// String name = TypeMapper.getResultSetValue(rs, "name", String.class);
// BigDecimal salary = TypeMapper.getResultSetValue(rs, "salary",
// BigDecimal.class);
// LocalDate hireDate = TypeMapper.getResultSetValue(rs, "hire_date",
// LocalDate.class);
// Boolean active = TypeMapper.getResultSetValue(rs, "is_active",
// Boolean.class);

// System.out.printf(" | %-4d | %-15s | $%-9.2f | %-12s | %-8s |%n",
// id, name, salary, hireDate, active);
// }
// System.out.println(" " + "-".repeat(80));

// rs.close();

// // Display final pool statistics
// ConnectionPoolStats stats = connectionManager.getStatistics();
// System.out.println("\n📊 Final Pool Statistics:");
// System.out.println(" Total Connections Created: " +
// stats.connectionsCreated);
// System.out.println(" Total Connections Acquired: " +
// stats.connectionsAcquired);
// System.out.println(" Total Connections Released: " +
// stats.connectionsReleased);
// System.out
// .println(" Pool Health: " + (connectionManager.isHealthy() ? "✅ HEALTHY" : "❌
// UNHEALTHY"));

// } catch (SQLException e) {
// System.err.println("❌ Error: " + e.getMessage());
// e.printStackTrace();
// } finally {
// if (conn != null) {
// connectionManager.releaseConnection(conn);
// System.out.println("\n✅ Connection released back to pool");
// }
// connectionManager.shutdown();
// System.out.println("✅ Connection pool shut down");
// }
// }
// }
