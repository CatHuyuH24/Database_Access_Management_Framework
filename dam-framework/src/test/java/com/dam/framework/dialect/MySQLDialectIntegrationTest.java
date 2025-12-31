package com.dam.framework.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Integration tests for MySQLDialect Sprint 2 enhancements.
 * <p>
 * Tests new type mapping and PreparedStatement integration features
 * added in Sprint 2.
 * 
 * @author Dev C
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySQLDialectIntegrationTest {

  /**
   * Test enum for testing enum parameter setting.
   */
  enum Status {
    ACTIVE, INACTIVE
  }

  private MySQLDialect dialect;

  @BeforeEach
  void setUp() {
    dialect = new MySQLDialect();
  }

  /**
   * Test 1: getJdbcTypeForJavaType returns correct JDBC types.
   */
  @Test
  @Order(1)
  @DisplayName("Should map Java types to JDBC types")
  void testGetJdbcTypeForJavaType() {
    assertEquals(Types.VARCHAR, dialect.getJdbcTypeForJavaType(String.class));
    assertEquals(Types.INTEGER, dialect.getJdbcTypeForJavaType(Integer.class));
    assertEquals(Types.BIGINT, dialect.getJdbcTypeForJavaType(Long.class));
    assertEquals(Types.BOOLEAN, dialect.getJdbcTypeForJavaType(Boolean.class));
    assertEquals(Types.DOUBLE, dialect.getJdbcTypeForJavaType(Double.class));
    assertEquals(Types.DECIMAL, dialect.getJdbcTypeForJavaType(BigDecimal.class));
    assertEquals(Types.DATE, dialect.getJdbcTypeForJavaType(LocalDate.class));
    assertEquals(Types.TIME, dialect.getJdbcTypeForJavaType(LocalTime.class));
    assertEquals(Types.TIMESTAMP, dialect.getJdbcTypeForJavaType(LocalDateTime.class));
  }

  /**
   * Test 2: getJavaTypeForJdbcType returns correct Java types.
   */
  @Test
  @Order(2)
  @DisplayName("Should map JDBC types to Java types")
  void testGetJavaTypeForJdbcType() {
    assertEquals(String.class, dialect.getJavaTypeForJdbcType(Types.VARCHAR));
    assertEquals(Integer.class, dialect.getJavaTypeForJdbcType(Types.INTEGER));
    assertEquals(Long.class, dialect.getJavaTypeForJdbcType(Types.BIGINT));
    assertEquals(Boolean.class, dialect.getJavaTypeForJdbcType(Types.BOOLEAN));
    assertEquals(Double.class, dialect.getJavaTypeForJdbcType(Types.DOUBLE));
    assertEquals(BigDecimal.class, dialect.getJavaTypeForJdbcType(Types.DECIMAL));
    assertEquals(java.sql.Date.class, dialect.getJavaTypeForJdbcType(Types.DATE));
    assertEquals(java.sql.Time.class, dialect.getJavaTypeForJdbcType(Types.TIME));
    assertEquals(java.sql.Timestamp.class, dialect.getJavaTypeForJdbcType(Types.TIMESTAMP));
  }

  /**
   * Test 3: getMySQLTypeForJavaType returns correct MySQL types.
   */
  @Test
  @Order(3)
  @DisplayName("Should get MySQL type for Java type")
  void testGetMySQLTypeForJavaType() {
    assertEquals("VARCHAR(100)", dialect.getMySQLTypeForJavaType(String.class, 100));
    assertEquals("INT", dialect.getMySQLTypeForJavaType(Integer.class, 0));
    assertEquals("BIGINT", dialect.getMySQLTypeForJavaType(Long.class, 0));
    assertEquals("TINYINT(1)", dialect.getMySQLTypeForJavaType(Boolean.class, 0));
    assertEquals("DOUBLE", dialect.getMySQLTypeForJavaType(Double.class, 0));
    assertEquals("DECIMAL", dialect.getMySQLTypeForJavaType(BigDecimal.class, 0));
    assertEquals("DATE", dialect.getMySQLTypeForJavaType(LocalDate.class, 0));
    assertEquals("TIME", dialect.getMySQLTypeForJavaType(LocalTime.class, 0));
    assertEquals("DATETIME", dialect.getMySQLTypeForJavaType(LocalDateTime.class, 0));
  }

  /**
   * Test 4: setParameter with String value.
   */
  @Test
  @Order(4)
  @DisplayName("Should set String parameter")
  void testSetStringParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);

    dialect.setParameter(stmt, 1, "John Doe", String.class);

    verify(stmt).setObject(eq(1), eq("John Doe"));
  }

  /**
   * Test 5: setParameter with Integer value.
   */
  @Test
  @Order(5)
  @DisplayName("Should set Integer parameter")
  void testSetIntegerParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);

    dialect.setParameter(stmt, 1, 25, Integer.class);

    verify(stmt).setObject(eq(1), eq(25));
  }

  /**
   * Test 6: setParameter with Boolean value converts to int.
   */
  @Test
  @Order(6)
  @DisplayName("Should convert Boolean to int for MySQL")
  void testSetBooleanParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);

    dialect.setParameter(stmt, 1, true, Boolean.class);
    verify(stmt).setInt(eq(1), eq(1));

    dialect.setParameter(stmt, 2, false, Boolean.class);
    verify(stmt).setInt(eq(2), eq(0));
  }

  /**
   * Test 7: setParameter with null value.
   */
  @Test
  @Order(7)
  @DisplayName("Should set null parameter with correct JDBC type")
  void testSetNullParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);

    dialect.setParameter(stmt, 1, null, String.class);
    verify(stmt).setNull(eq(1), eq(Types.VARCHAR));

    dialect.setParameter(stmt, 2, null, Integer.class);
    verify(stmt).setNull(eq(2), eq(Types.INTEGER));
  }

  /**
   * Test 8: setParameter with LocalDate converts to java.sql.Date.
   */
  @Test
  @Order(8)
  @DisplayName("Should convert LocalDate to java.sql.Date")
  void testSetLocalDateParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);
    LocalDate date = LocalDate.of(2024, 12, 20);

    dialect.setParameter(stmt, 1, date, LocalDate.class);

    verify(stmt).setDate(eq(1), eq(java.sql.Date.valueOf(date)));
  }

  /**
   * Test 9: setParameter with LocalTime converts to java.sql.Time.
   */
  @Test
  @Order(9)
  @DisplayName("Should convert LocalTime to java.sql.Time")
  void testSetLocalTimeParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);
    LocalTime time = LocalTime.of(14, 30, 0);

    dialect.setParameter(stmt, 1, time, LocalTime.class);

    verify(stmt).setTime(eq(1), eq(java.sql.Time.valueOf(time)));
  }

  /**
   * Test 10: setParameter with LocalDateTime converts to java.sql.Timestamp.
   */
  @Test
  @Order(10)
  @DisplayName("Should convert LocalDateTime to java.sql.Timestamp")
  void testSetLocalDateTimeParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);
    LocalDateTime dateTime = LocalDateTime.of(2024, 12, 20, 14, 30, 0);

    dialect.setParameter(stmt, 1, dateTime, LocalDateTime.class);

    verify(stmt).setTimestamp(eq(1), eq(java.sql.Timestamp.valueOf(dateTime)));
  }

  /**
   * Test 11: setParameter with Enum converts to String.
   */
  @Test
  @Order(11)
  @DisplayName("Should convert Enum to String")
  void testSetEnumParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);

    dialect.setParameter(stmt, 1, Status.ACTIVE, Status.class);

    verify(stmt).setString(eq(1), eq("ACTIVE"));
  }

  /**
   * Test 12: setParameter with BigDecimal.
   */
  @Test
  @Order(12)
  @DisplayName("Should set BigDecimal parameter")
  void testSetBigDecimalParameter() throws SQLException {
    PreparedStatement stmt = mock(PreparedStatement.class);
    BigDecimal price = new BigDecimal("99.99");

    dialect.setParameter(stmt, 1, price, BigDecimal.class);

    verify(stmt).setObject(eq(1), eq(price));
  }

  /**
   * Test 13: Complete type mapping round-trip.
   */
  @Test
  @Order(13)
  @DisplayName("Should complete Java -> JDBC -> MySQL type round-trip")
  void testCompleteTypeMappingRoundTrip() {
    // Java type -> JDBC type
    int jdbcType = dialect.getJdbcTypeForJavaType(String.class);
    assertEquals(Types.VARCHAR, jdbcType);

    // JDBC type -> MySQL type
    String mysqlType = dialect.getTypeName(jdbcType, 100);
    assertEquals("VARCHAR(100)", mysqlType);

    // JDBC type -> Java type
    Class<?> javaType = dialect.getJavaTypeForJdbcType(jdbcType);
    assertEquals(String.class, javaType);
  }

  /**
   * Test 14: Type mapping consistency across methods.
   */
  @Test
  @Order(14)
  @DisplayName("Should maintain type mapping consistency")
  void testTypeMappingConsistency() {
    Class<?>[] testTypes = {
        String.class, Integer.class, Long.class, Boolean.class,
        Double.class, BigDecimal.class, LocalDate.class
    };

    for (Class<?> type : testTypes) {
      // Get JDBC type
      int jdbcType = dialect.getJdbcTypeForJavaType(type);

      // Get MySQL type name
      String mysqlType = dialect.getTypeName(jdbcType, 100);
      assertNotNull(mysqlType, "MySQL type should not be null for " + type.getSimpleName());

      // Verify getMySQLTypeForJavaType gives same result
      String directMysqlType = dialect.getMySQLTypeForJavaType(type, 100);
      assertEquals(mysqlType, directMysqlType,
          "Direct MySQL type mapping should match for " + type.getSimpleName());
    }
  }

  /**
   * Test 15: Verify all Sprint 2 enhancements are accessible.
   */
  @Test
  @Order(15)
  @DisplayName("Should have all Sprint 2 enhancement methods")
  void testSprint2EnhancementsPresent() throws NoSuchMethodException {
    // Verify new methods exist
    assertNotNull(MySQLDialect.class.getMethod("getJdbcTypeForJavaType", Class.class));
    assertNotNull(MySQLDialect.class.getMethod("getJavaTypeForJdbcType", int.class));
    assertNotNull(MySQLDialect.class.getMethod("getMySQLTypeForJavaType", Class.class, int.class));
    assertNotNull(MySQLDialect.class.getMethod("setParameter",
        PreparedStatement.class, int.class, Object.class, Class.class));
  }
}
