package com.dam.framework.dialect;

import java.sql.Types;

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
  public String getName() {
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
   * @example
   * 
   *          <pre>
   * getLimitClause(10, 0)  → "LIMIT 10 OFFSET 0"
   * getLimitClause(20, 40) → "LIMIT 20 OFFSET 40"
   *          </pre>
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
   * @example
   * 
   *          <pre>
   * `user_table`
   * `first_name`
   *          </pre>
   */
  @Override
  public char getIdentifierQuoteCharacter() {
    return QUOTE_CHAR;
  }

  /**
   * Quote a SQL identifier (table or column name) using MySQL backticks.
   * <p>
   * Helper method for properly quoting identifiers.
   * 
   * @param identifier the identifier to quote
   * @return quoted identifier
   * 
   * @example
   * 
   *          <pre>
   * quoteIdentifier("users")      → "`users`"
   * quoteIdentifier("first_name") → "`first_name`"
   *          </pre>
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
   * 
   * @example
   * 
   *          <pre>
   * getConcatFunction("first_name", "' '", "last_name") 
   *   → "CONCAT(first_name, ' ', last_name)"
   *          </pre>
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

  @Override
  public String toString() {
    return "MySQLDialect{" +
        "name='" + DIALECT_NAME + '\'' +
        ", driver='" + DRIVER_CLASS + '\'' +
        '}';
  }
}
