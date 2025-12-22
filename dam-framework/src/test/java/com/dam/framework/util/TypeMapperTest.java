package com.dam.framework.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.dam.framework.exception.DAMException;

/**
 * Unit tests for TypeMapper.
 * <p>
 * Tests type mapping functionality including:
 * - Java type to JDBC type mapping
 * - JDBC type to Java type mapping
 * - ResultSet value extraction
 * - Type conversion
 * - Type checking utilities
 * 
 * @author Dev C
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TypeMapperTest {

  /**
   * Test enum for testing enum type mapping.
   */
  enum TestEnum {
    VALUE1, VALUE2
  }

  /**
   * Test 1: Map Java primitive types to JDBC types.
   */
  @Test
  @Order(1)
  @DisplayName("Should map Java primitive types to JDBC types")
  void testJavaPrimitiveToJdbcMapping() {
    assertEquals(Types.BOOLEAN, TypeMapper.getJdbcType(boolean.class));
    assertEquals(Types.BOOLEAN, TypeMapper.getJdbcType(Boolean.class));
    assertEquals(Types.TINYINT, TypeMapper.getJdbcType(byte.class));
    assertEquals(Types.TINYINT, TypeMapper.getJdbcType(Byte.class));
    assertEquals(Types.SMALLINT, TypeMapper.getJdbcType(short.class));
    assertEquals(Types.SMALLINT, TypeMapper.getJdbcType(Short.class));
    assertEquals(Types.INTEGER, TypeMapper.getJdbcType(int.class));
    assertEquals(Types.INTEGER, TypeMapper.getJdbcType(Integer.class));
    assertEquals(Types.BIGINT, TypeMapper.getJdbcType(long.class));
    assertEquals(Types.BIGINT, TypeMapper.getJdbcType(Long.class));
    assertEquals(Types.FLOAT, TypeMapper.getJdbcType(float.class));
    assertEquals(Types.FLOAT, TypeMapper.getJdbcType(Float.class));
    assertEquals(Types.DOUBLE, TypeMapper.getJdbcType(double.class));
    assertEquals(Types.DOUBLE, TypeMapper.getJdbcType(Double.class));
  }

  /**
   * Test 2: Map Java string types to JDBC types.
   */
  @Test
  @Order(2)
  @DisplayName("Should map Java string types to JDBC types")
  void testJavaStringToJdbcMapping() {
    assertEquals(Types.VARCHAR, TypeMapper.getJdbcType(String.class));
    assertEquals(Types.CHAR, TypeMapper.getJdbcType(char.class));
    assertEquals(Types.CHAR, TypeMapper.getJdbcType(Character.class));
  }

  /**
   * Test 3: Map Java date/time types to JDBC types.
   */
  @Test
  @Order(3)
  @DisplayName("Should map Java date/time types to JDBC types")
  void testJavaDateTimeToJdbcMapping() {
    assertEquals(Types.TIMESTAMP, TypeMapper.getJdbcType(Date.class));
    assertEquals(Types.DATE, TypeMapper.getJdbcType(java.sql.Date.class));
    assertEquals(Types.TIME, TypeMapper.getJdbcType(java.sql.Time.class));
    assertEquals(Types.TIMESTAMP, TypeMapper.getJdbcType(java.sql.Timestamp.class));
    assertEquals(Types.DATE, TypeMapper.getJdbcType(LocalDate.class));
    assertEquals(Types.TIME, TypeMapper.getJdbcType(LocalTime.class));
    assertEquals(Types.TIMESTAMP, TypeMapper.getJdbcType(LocalDateTime.class));
  }

  /**
   * Test 4: Map Java BigDecimal to JDBC type.
   */
  @Test
  @Order(4)
  @DisplayName("Should map BigDecimal to JDBC DECIMAL")
  void testBigDecimalMapping() {
    assertEquals(Types.DECIMAL, TypeMapper.getJdbcType(BigDecimal.class));
  }

  /**
   * Test 5: Map Java array types to JDBC types.
   */
  @Test
  @Order(5)
  @DisplayName("Should map byte array to JDBC VARBINARY")
  void testByteArrayMapping() {
    assertEquals(Types.VARBINARY, TypeMapper.getJdbcType(byte[].class));
  }

  /**
   * Test 6: Map enum types to VARCHAR.
   */
  @Test
  @Order(6)
  @DisplayName("Should map enum types to JDBC VARCHAR")
  void testEnumMapping() {
    assertEquals(Types.VARCHAR, TypeMapper.getJdbcType(TestEnum.class));
  }

  /**
   * Test 7: Null Java type throws exception.
   */
  @Test
  @Order(7)
  @DisplayName("Should throw exception for null Java type")
  void testNullJavaType() {
    assertThrows(IllegalArgumentException.class, () -> TypeMapper.getJdbcType(null));
  }

  /**
   * Test 8: Map JDBC types to Java types.
   */
  @Test
  @Order(8)
  @DisplayName("Should map JDBC types to Java types")
  void testJdbcToJavaMapping() {
    assertEquals(Boolean.class, TypeMapper.getJavaType(Types.BOOLEAN));
    assertEquals(Boolean.class, TypeMapper.getJavaType(Types.BIT));
    assertEquals(Byte.class, TypeMapper.getJavaType(Types.TINYINT));
    assertEquals(Short.class, TypeMapper.getJavaType(Types.SMALLINT));
    assertEquals(Integer.class, TypeMapper.getJavaType(Types.INTEGER));
    assertEquals(Long.class, TypeMapper.getJavaType(Types.BIGINT));
    assertEquals(Float.class, TypeMapper.getJavaType(Types.FLOAT));
    assertEquals(Float.class, TypeMapper.getJavaType(Types.REAL));
    assertEquals(Double.class, TypeMapper.getJavaType(Types.DOUBLE));
    assertEquals(BigDecimal.class, TypeMapper.getJavaType(Types.DECIMAL));
    assertEquals(BigDecimal.class, TypeMapper.getJavaType(Types.NUMERIC));
  }

  /**
   * Test 9: Map JDBC string types to Java String.
   */
  @Test
  @Order(9)
  @DisplayName("Should map JDBC string types to Java String")
  void testJdbcStringToJavaMapping() {
    assertEquals(Character.class, TypeMapper.getJavaType(Types.CHAR));
    assertEquals(String.class, TypeMapper.getJavaType(Types.VARCHAR));
    assertEquals(String.class, TypeMapper.getJavaType(Types.LONGVARCHAR));
    assertEquals(String.class, TypeMapper.getJavaType(Types.CLOB));
  }

  /**
   * Test 10: Map JDBC date/time types to Java SQL types.
   */
  @Test
  @Order(10)
  @DisplayName("Should map JDBC date/time types to Java SQL types")
  void testJdbcDateTimeToJavaMapping() {
    assertEquals(java.sql.Date.class, TypeMapper.getJavaType(Types.DATE));
    assertEquals(java.sql.Time.class, TypeMapper.getJavaType(Types.TIME));
    assertEquals(java.sql.Timestamp.class, TypeMapper.getJavaType(Types.TIMESTAMP));
  }

  /**
   * Test 11: Map JDBC binary types to byte array.
   */
  @Test
  @Order(11)
  @DisplayName("Should map JDBC binary types to byte array")
  void testJdbcBinaryToJavaMapping() {
    assertEquals(byte[].class, TypeMapper.getJavaType(Types.BINARY));
    assertEquals(byte[].class, TypeMapper.getJavaType(Types.VARBINARY));
    assertEquals(byte[].class, TypeMapper.getJavaType(Types.LONGVARBINARY));
    assertEquals(byte[].class, TypeMapper.getJavaType(Types.BLOB));
  }

  /**
   * Test 12: Unknown JDBC type returns Object.
   */
  @Test
  @Order(12)
  @DisplayName("Should return Object for unknown JDBC types")
  void testUnknownJdbcType() {
    assertEquals(Object.class, TypeMapper.getJavaType(9999));
  }

  /**
   * Test 13: Extract String value from ResultSet.
   */
  @Test
  @Order(13)
  @DisplayName("Should extract String value from ResultSet")
  void testExtractStringFromResultSet() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("name")).thenReturn("John Doe");

    String value = TypeMapper.getResultSetValue(rs, "name", String.class);
    assertEquals("John Doe", value);
  }

  /**
   * Test 14: Extract Integer value from ResultSet.
   */
  @Test
  @Order(14)
  @DisplayName("Should extract Integer value from ResultSet")
  void testExtractIntegerFromResultSet() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("age")).thenReturn(25);

    Integer value = TypeMapper.getResultSetValue(rs, "age", Integer.class);
    assertEquals(25, value);
  }

  /**
   * Test 15: Extract null value from ResultSet.
   */
  @Test
  @Order(15)
  @DisplayName("Should extract null value from ResultSet")
  void testExtractNullFromResultSet() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("optional")).thenReturn(null);

    String value = TypeMapper.getResultSetValue(rs, "optional", String.class);
    assertNull(value);
  }

  /**
   * Test 16: Extract value by index from ResultSet.
   */
  @Test
  @Order(16)
  @DisplayName("Should extract value by column index from ResultSet")
  void testExtractByIndexFromResultSet() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject(1)).thenReturn("Test");

    String value = TypeMapper.getResultSetValue(rs, 1, String.class);
    assertEquals("Test", value);
  }

  /**
   * Test 17: Type conversion from Integer to Long.
   */
  @Test
  @Order(17)
  @DisplayName("Should convert Integer to Long")
  void testIntegerToLongConversion() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("id")).thenReturn(123);

    Long value = TypeMapper.getResultSetValue(rs, "id", Long.class);
    assertEquals(123L, value);
  }

  /**
   * Test 18: Type conversion from Number to BigDecimal.
   */
  @Test
  @Order(18)
  @DisplayName("Should convert Number to BigDecimal")
  void testNumberToBigDecimalConversion() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("price")).thenReturn(99.99);

    BigDecimal value = TypeMapper.getResultSetValue(rs, "price", BigDecimal.class);
    assertNotNull(value);
    assertEquals(0, BigDecimal.valueOf(99.99).compareTo(value));
  }

  /**
   * Test 19: Type conversion from Integer to Boolean.
   */
  @Test
  @Order(19)
  @DisplayName("Should convert Integer to Boolean")
  void testIntegerToBooleanConversion() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("active")).thenReturn(1);

    Boolean value = TypeMapper.getResultSetValue(rs, "active", Boolean.class);
    assertTrue(value);

    when(rs.getObject("active")).thenReturn(0);
    value = TypeMapper.getResultSetValue(rs, "active", Boolean.class);
    assertFalse(value);
  }

  /**
   * Test 20: Type conversion from String to Boolean.
   */
  @Test
  @Order(20)
  @DisplayName("Should convert String to Boolean")
  void testStringToBooleanConversion() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("flag")).thenReturn("true");

    Boolean value = TypeMapper.getResultSetValue(rs, "flag", Boolean.class);
    assertTrue(value);
  }

  /**
   * Test 21: Type conversion from Object to String.
   */
  @Test
  @Order(21)
  @DisplayName("Should convert any Object to String")
  void testObjectToStringConversion() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("value")).thenReturn(12345);

    String value = TypeMapper.getResultSetValue(rs, "value", String.class);
    assertEquals("12345", value);
  }

  /**
   * Test 22: Invalid column index throws exception.
   */
  @Test
  @Order(22)
  @DisplayName("Should throw exception for invalid column index")
  void testInvalidColumnIndex() throws SQLException {
    ResultSet rs = mock(ResultSet.class);

    assertThrows(IllegalArgumentException.class,
        () -> TypeMapper.getResultSetValue(rs, 0, String.class));
  }

  /**
   * Test 23: Null ResultSet throws exception.
   */
  @Test
  @Order(23)
  @DisplayName("Should throw exception for null ResultSet")
  void testNullResultSet() {
    assertThrows(IllegalArgumentException.class,
        () -> TypeMapper.getResultSetValue(null, "column", String.class));
  }

  /**
   * Test 24: isPrimitiveOrWrapper correctly identifies types.
   */
  @Test
  @Order(24)
  @DisplayName("Should correctly identify primitive and wrapper types")
  void testIsPrimitiveOrWrapper() {
    assertTrue(TypeMapper.isPrimitiveOrWrapper(int.class));
    assertTrue(TypeMapper.isPrimitiveOrWrapper(Integer.class));
    assertTrue(TypeMapper.isPrimitiveOrWrapper(boolean.class));
    assertTrue(TypeMapper.isPrimitiveOrWrapper(Boolean.class));
    assertTrue(TypeMapper.isPrimitiveOrWrapper(double.class));
    assertTrue(TypeMapper.isPrimitiveOrWrapper(Double.class));

    assertFalse(TypeMapper.isPrimitiveOrWrapper(String.class));
    assertFalse(TypeMapper.isPrimitiveOrWrapper(BigDecimal.class));
    assertFalse(TypeMapper.isPrimitiveOrWrapper(Date.class));
    assertFalse(TypeMapper.isPrimitiveOrWrapper(null));
  }

  /**
   * Test 25: isNumericType correctly identifies numeric types.
   */
  @Test
  @Order(25)
  @DisplayName("Should correctly identify numeric types")
  void testIsNumericType() {
    assertTrue(TypeMapper.isNumericType(byte.class));
    assertTrue(TypeMapper.isNumericType(Byte.class));
    assertTrue(TypeMapper.isNumericType(int.class));
    assertTrue(TypeMapper.isNumericType(Integer.class));
    assertTrue(TypeMapper.isNumericType(long.class));
    assertTrue(TypeMapper.isNumericType(Long.class));
    assertTrue(TypeMapper.isNumericType(double.class));
    assertTrue(TypeMapper.isNumericType(Double.class));
    assertTrue(TypeMapper.isNumericType(BigDecimal.class));

    assertFalse(TypeMapper.isNumericType(String.class));
    assertFalse(TypeMapper.isNumericType(Boolean.class));
    assertFalse(TypeMapper.isNumericType(Date.class));
    assertFalse(TypeMapper.isNumericType(null));
  }

  /**
   * Test 26: isStringType correctly identifies string types.
   */
  @Test
  @Order(26)
  @DisplayName("Should correctly identify string types")
  void testIsStringType() {
    assertTrue(TypeMapper.isStringType(String.class));
    assertTrue(TypeMapper.isStringType(char.class));
    assertTrue(TypeMapper.isStringType(Character.class));

    assertFalse(TypeMapper.isStringType(Integer.class));
    assertFalse(TypeMapper.isStringType(Boolean.class));
    assertFalse(TypeMapper.isStringType(Date.class));
    assertFalse(TypeMapper.isStringType(null));
  }

  /**
   * Test 27: isDateTimeType correctly identifies date/time types.
   */
  @Test
  @Order(27)
  @DisplayName("Should correctly identify date/time types")
  void testIsDateTimeType() {
    assertTrue(TypeMapper.isDateTimeType(Date.class));
    assertTrue(TypeMapper.isDateTimeType(java.sql.Date.class));
    assertTrue(TypeMapper.isDateTimeType(java.sql.Time.class));
    assertTrue(TypeMapper.isDateTimeType(java.sql.Timestamp.class));
    assertTrue(TypeMapper.isDateTimeType(LocalDate.class));
    assertTrue(TypeMapper.isDateTimeType(LocalTime.class));
    assertTrue(TypeMapper.isDateTimeType(LocalDateTime.class));

    assertFalse(TypeMapper.isDateTimeType(String.class));
    assertFalse(TypeMapper.isDateTimeType(Integer.class));
    assertFalse(TypeMapper.isDateTimeType(null));
  }

  /**
   * Test 28: getJdbcTypeName returns correct names.
   */
  @Test
  @Order(28)
  @DisplayName("Should return correct JDBC type names")
  void testGetJdbcTypeName() {
    assertEquals("VARCHAR", TypeMapper.getJdbcTypeName(Types.VARCHAR));
    assertEquals("INTEGER", TypeMapper.getJdbcTypeName(Types.INTEGER));
    assertEquals("BOOLEAN", TypeMapper.getJdbcTypeName(Types.BOOLEAN));
    assertEquals("DATE", TypeMapper.getJdbcTypeName(Types.DATE));
    assertEquals("TIMESTAMP", TypeMapper.getJdbcTypeName(Types.TIMESTAMP));
    assertEquals("BLOB", TypeMapper.getJdbcTypeName(Types.BLOB));
    assertTrue(TypeMapper.getJdbcTypeName(9999).startsWith("UNKNOWN"));
  }

  /**
   * Test 29: Type conversion fails for incompatible types.
   */
  @Test
  @Order(29)
  @DisplayName("Should throw DAMException for incompatible type conversions")
  void testIncompatibleTypeConversion() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("data")).thenReturn(new Object());

    assertThrows(DAMException.class,
        () -> TypeMapper.getResultSetValue(rs, "data", Integer.class));
  }

  /**
   * Test 30: Character conversion works correctly.
   */
  @Test
  @Order(30)
  @DisplayName("Should convert String to Character")
  void testStringToCharacterConversion() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getObject("initial")).thenReturn("A");

    Character value = TypeMapper.getResultSetValue(rs, "initial", Character.class);
    assertEquals('A', value);
  }
}
