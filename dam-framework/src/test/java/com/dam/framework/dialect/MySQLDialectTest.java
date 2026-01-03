package com.dam.framework.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Unit tests for MySQLDialect.
 * <p>
 * Tests MySQL-specific SQL generation including:
 * - LIMIT/OFFSET clause generation
 * - Type mapping from JDBC to MySQL types
 * - Identifier quoting
 * - Auto-increment support
 * - Sequence support (not supported in MySQL)
 * - Helper methods for type checking
 * 
 * @author enkay2408
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySQLDialectTest {

  private MySQLDialect dialect;

  @BeforeEach
  void setUp() {
    dialect = new MySQLDialect();
  }

  /**
   * Test 1: Dialect name is correct.
   */
  @Test
  @Order(1)
  @DisplayName("Should return correct dialect name")
  void testGetName() {
    assertEquals("MySQL", dialect.getDialectName());
  }

  /**
   * Test 2: Driver class name is correct.
   */
  @Test
  @Order(2)
  @DisplayName("Should return correct JDBC driver class")
  void testGetDriverClassName() {
    assertEquals("com.mysql.cj.jdbc.Driver", dialect.getDriverClassName());
  }

  /**
   * Test 3: Validation query is correct.
   */
  @Test
  @Order(3)
  @DisplayName("Should return correct validation query")
  void testGetValidationQuery() {
    assertEquals("SELECT 1", dialect.getValidationQuery());
  }

  /**
   * Test 4: LIMIT clause with offset.
   */
  @Test
  @Order(4)
  @DisplayName("Should generate correct LIMIT clause with offset")
  void testGetLimitClauseWithOffset() {
    assertEquals("LIMIT 10 OFFSET 20", dialect.getLimitClause(10, 20));
    assertEquals("LIMIT 100 OFFSET 50", dialect.getLimitClause(100, 50));
  }

  /**
   * Test 5: LIMIT clause without offset.
   */
  @Test
  @Order(5)
  @DisplayName("Should generate correct LIMIT clause without offset")
  void testGetLimitClauseWithoutOffset() {
    // When offset is 0, should only use LIMIT
    assertEquals("LIMIT 10", dialect.getLimitClause(10, 0));
    assertEquals("LIMIT 1", dialect.getLimitClause(1, 0));
  }

  /**
   * Test 6: LIMIT clause validation.
   */
  @Test
  @Order(6)
  @DisplayName("Should validate LIMIT clause parameters")
  void testGetLimitClauseValidation() {
    // Negative limit
    assertThrows(IllegalArgumentException.class,
        () -> dialect.getLimitClause(-1, 0));

    // Negative offset
    assertThrows(IllegalArgumentException.class,
        () -> dialect.getLimitClause(10, -5));
  }

  /**
   * Test 7: Auto-increment column string.
   */
  @Test
  @Order(7)
  @DisplayName("Should return correct auto-increment column string")
  void testGetIdentityColumnString() {
    assertEquals("AUTO_INCREMENT", dialect.getIdentityColumnString());
  }

  /**
   * Test 8: Identifier quote character.
   */
  @Test
  @Order(8)
  @DisplayName("Should return correct identifier quote character")
  void testGetIdentifierQuoteCharacter() {
    assertEquals('`', dialect.getIdentifierQuoteCharacter());
  }

  /**
   * Test 9: Quote identifier helper.
   */
  @Test
  @Order(9)
  @DisplayName("Should quote identifiers correctly")
  void testQuoteIdentifier() {
    assertEquals("`users`", dialect.quoteIdentifier("users"));
    assertEquals("`first_name`", dialect.quoteIdentifier("first_name"));
    assertEquals("`order_id`", dialect.quoteIdentifier("order_id"));

    // Edge cases
    assertNull(dialect.quoteIdentifier(null));
    assertEquals("", dialect.quoteIdentifier(""));
  }

  /**
   * Test 10: Sequences are not supported.
   */
  @Test
  @Order(10)
  @DisplayName("Should indicate sequences are not supported")
  void testSupportsSequences() {
    assertFalse(dialect.supportsSequences());
  }

  /**
   * Test 11: Sequence next value throws exception.
   */
  @Test
  @Order(11)
  @DisplayName("Should throw exception for sequence next value")
  void testGetSequenceNextValString() {
    assertThrows(UnsupportedOperationException.class,
        () -> dialect.getSequenceNextValString("test_seq"));
  }

  /**
   * Test 12: Numeric type mapping.
   */
  @Test
  @Order(12)
  @DisplayName("Should map numeric types correctly")
  void testNumericTypeMapping() {
    assertEquals("TINYINT(1)", dialect.getTypeName(Types.BOOLEAN, 0));
    assertEquals("TINYINT(1)", dialect.getTypeName(Types.BIT, 0));
    assertEquals("TINYINT", dialect.getTypeName(Types.TINYINT, 0));
    assertEquals("SMALLINT", dialect.getTypeName(Types.SMALLINT, 0));
    assertEquals("INT", dialect.getTypeName(Types.INTEGER, 0));
    assertEquals("BIGINT", dialect.getTypeName(Types.BIGINT, 0));
    assertEquals("FLOAT", dialect.getTypeName(Types.FLOAT, 0));
    assertEquals("FLOAT", dialect.getTypeName(Types.REAL, 0));
    assertEquals("DOUBLE", dialect.getTypeName(Types.DOUBLE, 0));
    assertEquals("DECIMAL", dialect.getTypeName(Types.DECIMAL, 0));
    assertEquals("DECIMAL", dialect.getTypeName(Types.NUMERIC, 0));
  }

  /**
   * Test 13: String type mapping.
   */
  @Test
  @Order(13)
  @DisplayName("Should map string types correctly")
  void testStringTypeMapping() {
    assertEquals("CHAR(1)", dialect.getTypeName(Types.CHAR, 0));
    assertEquals("CHAR(10)", dialect.getTypeName(Types.CHAR, 10));
    assertEquals("VARCHAR(255)", dialect.getTypeName(Types.VARCHAR, 255));
    assertEquals("VARCHAR(1000)", dialect.getTypeName(Types.VARCHAR, 1000));
    assertEquals("TEXT", dialect.getTypeName(Types.VARCHAR, 70000)); // > 65535
    assertEquals("TEXT", dialect.getTypeName(Types.LONGVARCHAR, 0));
    assertEquals("TEXT", dialect.getTypeName(Types.CLOB, 0));
  }

  /**
   * Test 14: Binary type mapping.
   */
  @Test
  @Order(14)
  @DisplayName("Should map binary types correctly")
  void testBinaryTypeMapping() {
    assertEquals("BINARY(1)", dialect.getTypeName(Types.BINARY, 0));
    assertEquals("BINARY(16)", dialect.getTypeName(Types.BINARY, 16));
    assertEquals("VARBINARY(100)", dialect.getTypeName(Types.VARBINARY, 100));
    assertEquals("BLOB", dialect.getTypeName(Types.VARBINARY, 70000)); // > 65535
    assertEquals("BLOB", dialect.getTypeName(Types.LONGVARBINARY, 0));
    assertEquals("BLOB", dialect.getTypeName(Types.BLOB, 0));
  }

  /**
   * Test 15: Date/Time type mapping.
   */
  @Test
  @Order(15)
  @DisplayName("Should map date/time types correctly")
  void testDateTimeTypeMapping() {
    assertEquals("DATE", dialect.getTypeName(Types.DATE, 0));
    assertEquals("TIME", dialect.getTypeName(Types.TIME, 0));
    assertEquals("DATETIME", dialect.getTypeName(Types.TIMESTAMP, 0));
  }

  /**
   * Test 16: Unknown type defaults to VARCHAR.
   */
  @Test
  @Order(16)
  @DisplayName("Should default unknown types to VARCHAR")
  void testUnknownTypeMapping() {
    // Unknown JDBC type code should default to VARCHAR(255)
    assertEquals("VARCHAR(255)", dialect.getTypeName(9999, 0));
  }

  /**
   * Test 17: LAST_INSERT_ID query.
   */
  @Test
  @Order(17)
  @DisplayName("Should return correct LAST_INSERT_ID query")
  void testGetLastInsertIdQuery() {
    assertEquals("SELECT LAST_INSERT_ID()", dialect.getLastInsertIdQuery());
  }

  /**
   * Test 18: CONCAT function.
   */
  @Test
  @Order(18)
  @DisplayName("Should generate correct CONCAT function")
  void testGetConcatFunction() {
    assertEquals("CONCAT(a, b, c)",
        dialect.getConcatFunction("a", "b", "c"));
    assertEquals("CONCAT(first_name, ' ', last_name)",
        dialect.getConcatFunction("first_name", "' '", "last_name"));

    // Edge cases
    assertEquals("", dialect.getConcatFunction());
    assertEquals("", dialect.getConcatFunction((String[]) null));
  }

  /**
   * Test 19: Type checking helpers - numeric types.
   */
  @Test
  @Order(19)
  @DisplayName("Should correctly identify numeric types")
  void testIsNumericType() {
    // Numeric types
    assertTrue(dialect.isNumericType(Types.TINYINT));
    assertTrue(dialect.isNumericType(Types.SMALLINT));
    assertTrue(dialect.isNumericType(Types.INTEGER));
    assertTrue(dialect.isNumericType(Types.BIGINT));
    assertTrue(dialect.isNumericType(Types.FLOAT));
    assertTrue(dialect.isNumericType(Types.DOUBLE));
    assertTrue(dialect.isNumericType(Types.DECIMAL));

    // Non-numeric types
    assertFalse(dialect.isNumericType(Types.VARCHAR));
    assertFalse(dialect.isNumericType(Types.DATE));
    assertFalse(dialect.isNumericType(Types.BLOB));
  }

  /**
   * Test 20: Type checking helpers - string types.
   */
  @Test
  @Order(20)
  @DisplayName("Should correctly identify string types")
  void testIsStringType() {
    // String types
    assertTrue(dialect.isStringType(Types.CHAR));
    assertTrue(dialect.isStringType(Types.VARCHAR));
    assertTrue(dialect.isStringType(Types.LONGVARCHAR));
    assertTrue(dialect.isStringType(Types.CLOB));

    // Non-string types
    assertFalse(dialect.isStringType(Types.INTEGER));
    assertFalse(dialect.isStringType(Types.DATE));
    assertFalse(dialect.isStringType(Types.BLOB));
  }

  /**
   * Test 21: Type checking helpers - date/time types.
   */
  @Test
  @Order(21)
  @DisplayName("Should correctly identify date/time types")
  void testIsDateTimeType() {
    // Date/Time types
    assertTrue(dialect.isDateTimeType(Types.DATE));
    assertTrue(dialect.isDateTimeType(Types.TIME));
    assertTrue(dialect.isDateTimeType(Types.TIMESTAMP));

    // Non-date/time types
    assertFalse(dialect.isDateTimeType(Types.INTEGER));
    assertFalse(dialect.isDateTimeType(Types.VARCHAR));
    assertFalse(dialect.isDateTimeType(Types.BLOB));
  }

  /**
   * Test 22: toString provides useful representation.
   */
  @Test
  @Order(22)
  @DisplayName("Should provide useful toString representation")
  void testToString() {
    String toString = dialect.toString();
    assertTrue(toString.contains("MySQL"));
    assertTrue(toString.contains("com.mysql.cj.jdbc.Driver"));
  }
}
