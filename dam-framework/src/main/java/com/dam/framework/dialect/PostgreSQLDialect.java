package com.dam.framework.dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dam.framework.util.TypeMapper;

/**
 * PostgreSQL-specific SQL dialect implementation.
 * <p>
 * PostgreSQL supports advanced features like sequences, advanced data types,
 * and robust ACID compliance. It's widely used for enterprise applications.
 * 
 * <h3>PostgreSQL-Specific Features:</h3>
 * <ul>
 * <li><b>Pagination:</b> LIMIT {limit} OFFSET {offset}</li>
 * <li><b>Sequences:</b> Native sequence support via nextval()</li>
 * <li><b>Auto-increment:</b> SERIAL (internally uses sequences)</li>
 * <li><b>Identifier quotes:</b> Double quotes (")</li>
 * <li><b>Boolean type:</b> Native BOOLEAN</li>
 * <li><b>UUID type:</b> Native UUID type</li>
 * </ul>
 * 
 * @see Dialect
 */
public class PostgreSQLDialect implements Dialect {

  private static final String DIALECT_NAME = "PostgreSQL";
  private static final String DRIVER_CLASS = "org.postgresql.Driver";
  private static final String VALIDATION_QUERY = "SELECT 1";
  private static final char QUOTE_CHAR = '"';

  @Override
  public String getDialectName() {
    return DIALECT_NAME;
  }

  @Override
  public String getDriverClassName() {
    return DRIVER_CLASS;
  }

  @Override
  public String getValidationQuery() {
    return VALIDATION_QUERY;
  }

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

  @Override
  public String getIdentityColumnString() {
    // PostgreSQL uses SERIAL (which is syntactic sugar for sequences)
    return "SERIAL";
  }

  @Override
  public String getTypeName(int sqlType, int length) {
    switch (sqlType) {
      // Numeric types
      case Types.BOOLEAN:
      case Types.BIT:
        return "BOOLEAN";

      case Types.TINYINT:
      case Types.SMALLINT:
        return "SMALLINT";

      case Types.INTEGER:
        return "INTEGER";

      case Types.BIGINT:
        return "BIGINT";

      case Types.FLOAT:
      case Types.REAL:
        return "REAL";

      case Types.DOUBLE:
        return "DOUBLE PRECISION";

      case Types.DECIMAL:
      case Types.NUMERIC:
        return "NUMERIC";

      // String types
      case Types.CHAR:
        return length > 0 ? "CHAR(" + length + ")" : "CHAR(1)";

      case Types.VARCHAR:
      case Types.LONGVARCHAR:
        return length > 0 ? "VARCHAR(" + length + ")" : "VARCHAR(255)";

      case Types.CLOB:
        return "TEXT";

      // Date/Time types
      case Types.DATE:
        return "DATE";

      case Types.TIME:
        return "TIME";

      case Types.TIMESTAMP:
        return "TIMESTAMP";

      // Binary types
      case Types.BINARY:
      case Types.VARBINARY:
      case Types.LONGVARBINARY:
      case Types.BLOB:
        return "BYTEA";

      // Special types
      case Types.OTHER:
        return "UUID"; // PostgreSQL has native UUID type

      default:
        return "TEXT";
    }
  }

  @Override
  public boolean supportsSequences() {
    // PostgreSQL has excellent native sequence support
    return true;
  }

  @Override
  public String getSequenceNextValString(String sequenceName) {
    // PostgreSQL uses nextval() function
    return "SELECT nextval('" + sequenceName + "')";
  }

  @Override
  public char getIdentifierQuoteCharacter() {
    return QUOTE_CHAR;
  }

  @Override
  public PaginationFragment getPaginationFragment(Integer limit, Integer offset) {
    List<Object> params = new ArrayList<>();
    StringBuilder sql = new StringBuilder();

    if (limit != null) {
      sql.append(" LIMIT ?");
      params.add(limit);
    }

    if (offset != null && offset > 0) {
      sql.append(" OFFSET ?");
      params.add(offset);
    }

    return new PaginationFragment(sql.toString(), params);
  }

  @Override
  public void setParameter(PreparedStatement stmt, int parameterIndex, Object value, Class<?> javaType)
      throws SQLException {

    if (value == null) {
      // Use TypeMapper to get the correct JDBC type
      int jdbcType = TypeMapper.getJdbcType(javaType);
      stmt.setNull(parameterIndex, jdbcType);
      return;
    }

    // Handle boolean specially for PostgreSQL
    if (javaType == Boolean.class || javaType == boolean.class) {
      stmt.setBoolean(parameterIndex, (Boolean) value);
    }
    // Handle UUID type
    else if (javaType == java.util.UUID.class) {
      stmt.setObject(parameterIndex, value, Types.OTHER);
    }
    // For all other types, use standard setObject
    else {
      stmt.setObject(parameterIndex, value);
    }
  }
}
