package com.dam.framework.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.dam.framework.config.DialectType;

/**
 * Unit tests for DialectFactory.
 * <p>
 * Tests Factory pattern implementation for creating Dialect instances.
 * 
 * @author enkay2408
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DialectFactoryTest {

  /**
   * Test 1: Create MySQL dialect by DialectType.
   */
  @Test
  @Order(1)
  @DisplayName("Should create MySQL dialect from DialectType")
  void testCreateMySQLDialect() {
    Dialect dialect = DialectFactory.createDialect(DialectType.MYSQL);

    assertNotNull(dialect);
    assertInstanceOf(MySQLDialect.class, dialect);
    assertEquals("MySQL", dialect.getName());
  }

  /**
   * Test 2: Create MySQL dialect by string name.
   */
  @Test
  @Order(2)
  @DisplayName("Should create MySQL dialect from string name")
  void testCreateDialectFromString() {
    Dialect mysql1 = DialectFactory.createDialect("mysql");
    Dialect mysql2 = DialectFactory.createDialect("MYSQL");
    Dialect mysql3 = DialectFactory.createDialect("MySQL");

    assertNotNull(mysql1);
    assertNotNull(mysql2);
    assertNotNull(mysql3);

    assertInstanceOf(MySQLDialect.class, mysql1);
    assertInstanceOf(MySQLDialect.class, mysql2);
    assertInstanceOf(MySQLDialect.class, mysql3);
  }

  /**
   * Test 3: Null DialectType throws exception.
   */
  @Test
  @Order(3)
  @DisplayName("Should throw exception for null DialectType")
  void testNullDialectType() {
    assertThrows(IllegalArgumentException.class,
        () -> DialectFactory.createDialect((DialectType) null));
  }

  /**
   * Test 4: Null or empty string name throws exception.
   */
  @Test
  @Order(4)
  @DisplayName("Should throw exception for null or empty string name")
  void testNullOrEmptyStringName() {
    assertThrows(IllegalArgumentException.class,
        () -> DialectFactory.createDialect((String) null));

    assertThrows(IllegalArgumentException.class,
        () -> DialectFactory.createDialect(""));

    assertThrows(IllegalArgumentException.class,
        () -> DialectFactory.createDialect("   "));
  }

  /**
   * Test 5: PostgreSQL not yet implemented.
   */
  @Test
  @Order(5)
  @DisplayName("Should throw exception for PostgreSQL (not yet implemented)")
  void testPostgreSQLNotImplemented() {
    UnsupportedOperationException exception = assertThrows(
        UnsupportedOperationException.class,
        () -> DialectFactory.createDialect(DialectType.POSTGRESQL));

    assertTrue(exception.getMessage().contains("PostgreSQL"));
    assertTrue(exception.getMessage().contains("Sprint 5"));
  }

  /**
   * Test 6: SQL Server not yet implemented.
   */
  @Test
  @Order(6)
  @DisplayName("Should throw exception for SQL Server (not yet implemented)")
  void testSQLServerNotImplemented() {
    UnsupportedOperationException exception = assertThrows(
        UnsupportedOperationException.class,
        () -> DialectFactory.createDialect(DialectType.SQLSERVER));

    assertTrue(exception.getMessage().contains("SQL Server"));
    assertTrue(exception.getMessage().contains("Sprint 5"));
  }

  /**
   * Test 7: SQLite not yet implemented.
   */
  @Test
  @Order(7)
  @DisplayName("Should throw exception for SQLite (not yet implemented)")
  void testSQLiteNotImplemented() {
    assertThrows(UnsupportedOperationException.class,
        () -> DialectFactory.createDialect(DialectType.SQLITE));
  }

  /**
   * Test 8: Invalid string name throws exception.
   */
  @Test
  @Order(8)
  @DisplayName("Should throw exception for invalid dialect name")
  void testInvalidDialectName() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> DialectFactory.createDialect("invalid"));

    assertTrue(exception.getMessage().contains("Unsupported dialect name"));
  }

  /**
   * Test 9: isSupported returns correct values.
   */
  @Test
  @Order(9)
  @DisplayName("Should correctly report supported dialects")
  void testIsSupported() {
    assertTrue(DialectFactory.isSupported(DialectType.MYSQL),
        "MySQL should be supported");

    assertFalse(DialectFactory.isSupported(DialectType.POSTGRESQL),
        "PostgreSQL should not be supported yet");

    assertFalse(DialectFactory.isSupported(DialectType.SQLSERVER),
        "SQL Server should not be supported yet");

    assertFalse(DialectFactory.isSupported(DialectType.SQLITE),
        "SQLite should not be supported yet");

    assertFalse(DialectFactory.isSupported(null),
        "Null should return false");
  }

  /**
   * Test 10: getSupportedDialects returns correct array.
   */
  @Test
  @Order(10)
  @DisplayName("Should return array of supported dialects")
  void testGetSupportedDialects() {
    DialectType[] supported = DialectFactory.getSupportedDialects();

    assertNotNull(supported);
    assertEquals(1, supported.length, "Only MySQL should be supported in Sprint 1");
    assertEquals(DialectType.MYSQL, supported[0]);
  }

  /**
   * Test 11: getSupportedDialectsString returns correct string.
   */
  @Test
  @Order(11)
  @DisplayName("Should return comma-separated string of supported dialects")
  void testGetSupportedDialectsString() {
    String supported = DialectFactory.getSupportedDialectsString();

    assertNotNull(supported);
    assertEquals("MYSQL", supported);
  }

  /**
   * Test 12: Cannot instantiate DialectFactory.
   */
  @Test
  @Order(12)
  @DisplayName("Should not allow instantiation of DialectFactory")
  void testCannotInstantiate() throws Exception {
    var constructor = DialectFactory.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("Should throw AssertionError");
    } catch (java.lang.reflect.InvocationTargetException e) {
      assertTrue(e.getCause() instanceof AssertionError);
      assertTrue(e.getCause().getMessage().contains("utility class"));
    }
  }
}
