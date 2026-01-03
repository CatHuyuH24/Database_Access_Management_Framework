package com.dam.framework.dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.dam.framework.util.TypeMapper;
import java.util.List;

/**
 * MySQL-specific SQL dialect implementation.
 * <p>
 * Implements Strategy pattern for database-specific SQL generation.
 * Provides MySQL-specific syntax for LIMIT/OFFSET, AUTO_INCREMENT,
 * identifier quoting, and type mappings.
 *
 * <h3>MySQL-Specific Features:</h3>
 * <ul>
 * <li><b>Pagination:</b> LIMIT {limit} OFFSET {offset}</li>
 * <li><b>Auto-increment:</b> AUTO_INCREMENT</li>
 * <li><b>Identifier quotes:</b> Backticks (`)</li>
 * <li><b>Boolean type:</b> TINYINT(1)</li>
 * </ul>
 *
 * <h3>Design Patterns Used:</h3>
 * <ul>
 * <li><b>Strategy Pattern:</b> Implements Dialect interface for MySQL-specific
 * behavior</li>
 * </ul>
 *
 * @author enkay2408
 * @see Dialect
 */
public class MySQLDialect implements Dialect {

  private static final String DIALECT_NAME = "MySQL";
  private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
  private static final String VALIDATION_QUERY = "SELECT 1";
  private static final char QUOTE_CHAR = '`';

  /**
   * Get the name of this dialect.
   *
   * @return "MySQL"
   */
  @Override
  public String getDialectName() {
    return DIALECT_NAME;
  }

  /**
   * Get the MySQL JDBC driver class name.
   *
   * @return "com.mysql.cj.jdbc.Driver"
   */
  @Override
  public String getDriverClassName() {
    return DRIVER_CLASS;
  }

  /**
   * Get MySQL connection validation query.
   *
   * @return "SELECT 1"
   */
  @Override
  public String getValidationQuery() {
    return VALIDATION_QUERY;
  }

  /**
   * Get MySQL LIMIT clause with OFFSET.
   * <p>
   * MySQL syntax: LIMIT {limit} OFFSET {offset}
   *
   * @param limit  the maximum number of rows
   * @param offset the number of rows to skip
   * @return SQL LIMIT clause
   *
   */
  @Override
  public String getLimitClause(int limit, int offset) {
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be non-negative");
    }
    if (offset < 0) {
      throw new IllegalArgumentException("Offset must be non-negative");
    }

    if (offset == 0) {
      return "LIMIT " + limit;
    }
    return "LIMIT " + limit + " OFFSET " + offset;
  }

  /**
   * Get MySQL auto-increment column definition.
   *
   * @return "AUTO_INCREMENT"
   */
  @Override
  public String getIdentityColumnString() {
    return "AUTO_INCREMENT";
  }

  /**
   * Get MySQL-specific type name for a JDBC type.
   * <p>
   * Maps Java SQL types to MySQL column types.
   *
   * @param sqlType the JDBC type code (from java.sql.Types)
   * @param length  the column length (for string types)
   * @return MySQL type name
   *
   * @see java.sql.Types
   */
  @Override
  public String getTypeName(int sqlType, int length) {
    switch (sqlType) {
      // Numeric types
      case Types.BOOLEAN:
      case Types.BIT:
        return "TINYINT(1)";

      case Types.TINYINT:
        return "TINYINT";

      case Types.SMALLINT:
        return "SMALLINT";

      case Types.INTEGER:
        return "INT";

      case Types.BIGINT:
        return "BIGINT";

      case Types.FLOAT:
      case Types.REAL:
        return "FLOAT";

      case Types.DOUBLE:
        return "DOUBLE";

      case Types.DECIMAL:
      case Types.NUMERIC:
        return "DECIMAL";

      // String types
      case Types.CHAR:
        return length > 0 ? "CHAR(" + length + ")" : "CHAR(1)";

      case Types.VARCHAR:
      case Types.LONGVARCHAR:
        if (length <= 0 || length > 65535) {
          return "TEXT";
        }
        return "VARCHAR(" + length + ")";

      case Types.CLOB:
        return "TEXT";

      // Binary types
      case Types.BINARY:
        return length > 0 ? "BINARY(" + length + ")" : "BINARY(1)";

      case Types.VARBINARY:
      case Types.LONGVARBINARY:
        if (length <= 0 || length > 65535) {
          return "BLOB";
        }
        return "VARBINARY(" + length + ")";

      case Types.BLOB:
        return "BLOB";

      // Date/Time types
      case Types.DATE:
        return "DATE";

      case Types.TIME:
        return "TIME";

      case Types.TIMESTAMP:
        return "DATETIME";

      // Other types
      case Types.NULL:
        return "NULL";

      default:
        return "VARCHAR(255)"; // Default fallback
    }
  }

  /**
   * Check if MySQL supports sequences.
   * <p>
   * MySQL does not support sequences (uses AUTO_INCREMENT instead).
   *
   * @return false
   */
  @Override
  public boolean supportsSequences() {
    return false;
  }

  /**
   * Get SQL for next sequence value.
   * <p>
   * MySQL does not support sequences, so this throws
   * UnsupportedOperationException.
   *
   * @param sequenceName the sequence name (ignored)
   * @return not applicable
   * @throws UnsupportedOperationException always thrown
   */
  @Override
  public String getSequenceNextValString(String sequenceName) {
    throw new UnsupportedOperationException("MySQL does not support sequences. Use AUTO_INCREMENT instead.");
  }

  /**
   * Get MySQL identifier quote character.
   * <p>
   * MySQL uses backticks (`) to quote identifiers (table/column names).
   *
   * @return '`' (backtick)
   *
   */
  @Override
  public char getIdentifierQuoteCharacter() {
    return QUOTE_CHAR;
  }

  @Override
  public PaginationFragment getPaginationFragment(Integer limit, Integer offset) {
    if (limit == null)
      return new PaginationFragment("", List.of());

    if (offset != null && offset > 0) {
      return new PaginationFragment(" LIMIT ? OFFSET ?", List.of(limit, offset));
    } else {
      return new PaginationFragment(" LIMIT ?", List.of(limit));
    }
  }

  /**
   * Quote a SQL identifier (table or column name) using MySQL backticks.
   * <p>
   * Helper method for properly quoting identifiers.
   *
   * @param identifier the identifier to quote
   * @return quoted identifier
   *
   */
  public String quoteIdentifier(String identifier) {
    if (identifier == null || identifier.isEmpty()) {
      return identifier;
    }
    return QUOTE_CHAR + identifier + QUOTE_CHAR;
  }

  /**
   * Get the LAST_INSERT_ID() function call for MySQL.
   * <p>
   * Used to retrieve the auto-generated ID after INSERT.
   *
   * @return "SELECT LAST_INSERT_ID()"
   */
  public String getLastInsertIdQuery() {
    return "SELECT LAST_INSERT_ID()";
  }

  /**
   * Get MySQL-specific CONCAT function.
   * <p>
   * Concatenates multiple string values.
   *
   * @param values the values to concatenate
   * @return CONCAT SQL function call
   */
  public String getConcatFunction(String... values) {
    if (values == null || values.length == 0) {
      return "";
    }
    return "CONCAT(" + String.join(", ", values) + ")";
  }

  /**
   * Check if a given SQL type is a numeric type.
   *
   * @param sqlType the JDBC type code
   * @return true if numeric type
   */
  public boolean isNumericType(int sqlType) {
    switch (sqlType) {
      case Types.TINYINT:
      case Types.SMALLINT:
      case Types.INTEGER:
      case Types.BIGINT:
      case Types.FLOAT:
      case Types.REAL:
      case Types.DOUBLE:
      case Types.DECIMAL:
      case Types.NUMERIC:
        return true;
      default:
        return false;
    }
  }

  /**
   * Check if a given SQL type is a string type.
   *
   * @param sqlType the JDBC type code
   * @return true if string type
   */
  public boolean isStringType(int sqlType) {
    switch (sqlType) {
      case Types.CHAR:
      case Types.VARCHAR:
      case Types.LONGVARCHAR:
      case Types.CLOB:
        return true;
      default:
        return false;
    }
  }

  /**
   * Check if a given SQL type is a date/time type.
   *
   * @param sqlType the JDBC type code
   * @return true if date/time type
   */
  public boolean isDateTimeType(int sqlType) {
    switch (sqlType) {
      case Types.DATE:
      case Types.TIME:
      case Types.TIMESTAMP:
        return true;
      default:
        return false;
    }
  }

  /**
   * Get the JDBC type code for a Java class using TypeMapper.
   * <p>
   * This method delegates to TypeMapper for consistent type mapping
   * across the framework.
   *
   * @param javaType the Java class
   * @return the JDBC type code
   *
   * @see TypeMapper#getJdbcType(Class)
   */
  public int getJdbcTypeForJavaType(Class<?> javaType) {
    return TypeMapper.getJdbcType(javaType);
  }

  /**
   * Get the default Java class for a JDBC type code.
   * <p>
   * This method delegates to TypeMapper for consistent type mapping
   * across the framework.
   *
   * @param jdbcType the JDBC type code
   * @return the corresponding Java class
   *
   * @see TypeMapper#getJavaType(int)
   */
  public Class<?> getJavaTypeForJdbcType(int jdbcType) {
    return TypeMapper.getJavaType(jdbcType);
  }

  /**
   * Set a parameter value in PreparedStatement with proper type handling.
   * <p>
   * This is a convenience method that handles null values and type-specific
   * parameter setting for MySQL.
   *
   * @param stmt           the PreparedStatement
   * @param parameterIndex the parameter index (1-based)
   * @param value          the parameter value (can be null)
   * @param javaType       the Java type of the value
   * @throws SQLException if parameter setting fails
   */
  public void setParameter(PreparedStatement stmt, int parameterIndex, Object value, Class<?> javaType)
      throws SQLException {
    if (value == null) {
      int jdbcType = TypeMapper.getJdbcType(javaType);
      stmt.setNull(parameterIndex, jdbcType);
      return;
    }

    // Handle specific MySQL type conversions
    if (value instanceof Boolean) {
      // MySQL uses TINYINT(1) for booleans
      stmt.setInt(parameterIndex, ((Boolean) value) ? 1 : 0);
    } else if (value instanceof java.time.LocalDate) {
      stmt.setDate(parameterIndex, java.sql.Date.valueOf((java.time.LocalDate) value));
    } else if (value instanceof java.time.LocalTime) {
      stmt.setTime(parameterIndex, java.sql.Time.valueOf((java.time.LocalTime) value));
    } else if (value instanceof java.time.LocalDateTime) {
      stmt.setTimestamp(parameterIndex, java.sql.Timestamp.valueOf((java.time.LocalDateTime) value));
    } else if (value instanceof Enum) {
      // Store enums as strings
      stmt.setString(parameterIndex, ((Enum<?>) value).name());
    } else {
      // Use default setObject for other types
      stmt.setObject(parameterIndex, value);
    }
  }

  /**
   * Get the MySQL-specific type name for a Java class.
   * <p>
   * Combines TypeMapper and getTypeName for convenient Java-to-MySQL type
   * mapping.
   *
   * @param javaType the Java class
   * @param length   the column length (for string types)
   * @return MySQL type name
   */
  public String getMySQLTypeForJavaType(Class<?> javaType, int length) {
    int jdbcType = TypeMapper.getJdbcType(javaType);
    return getTypeName(jdbcType, length);
  }

  @Override
  public String toString() {
    return "MySQLDialect{" +
        "name='" + DIALECT_NAME + '\'' +
        ", driver='" + DRIVER_CLASS + '\'' +
        '}';
  }
}