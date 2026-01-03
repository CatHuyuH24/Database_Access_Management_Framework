package com.dam.framework.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
   * Test 1: Create MySQL dialect from string name.
   */
  @Test
  @Order(1)
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
   * Test 2: Null or empty string name throws exception.
   */
  @Test
  @Order(2)
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
   * Test 3: Invalid string name throws exception.
   */
  @Test
  @Order(3)
  @DisplayName("Should throw exception for invalid/unsupported dialect name")
  void testInvalidDialectName() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> DialectFactory.createDialect("invalid"));

    assertTrue(exception.getMessage().contains("Unsupported dialect name"));
  }

  /**
   * Test 4: getSupportedDialects returns correct array.
   */
  @Test
  @Order(4)
  @DisplayName("Should return array of supported dialects")
  void testGetSupportedDialects() {
    String[] supported = DialectFactory.getSupportedDialects();

    assertNotNull(supported);
    assertEquals(1, supported.length, "Only MySQL should be supported currently");
    assertEquals("mysql", supported[0]);
  }

  /**
   * Test 5: Cannot instantiate DialectFactory.
   */
  @Test
  @Order(5)
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
