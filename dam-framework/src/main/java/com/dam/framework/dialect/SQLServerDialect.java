package com.dam.framework.dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dam.framework.util.TypeMapper;

/**
 * Microsoft SQL Server dialect implementation.
 * <p>
 * SQL Server is Microsoft's enterprise RDBMS, widely used in Windows
 * environments.
 * It supports T-SQL (Transact-SQL) extensions and has good integration with
 * .NET.
 * 
 * <h3>SQL Server-Specific Features:</h3>
 * <ul>
 * <li><b>Pagination:</b> OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY (SQL
 * Server 2012+)</li>
 * <li><b>Auto-increment:</b> IDENTITY</li>
 * <li><b>Identifier quotes:</b> Square brackets ([])</li>
 * <li><b>Boolean type:</b> BIT (0/1)</li>
 * <li><b>No native UUID:</b> Use UNIQUEIDENTIFIER</li>
 * <li><b>Sequences:</b> Supported since SQL Server 2012</li>
 * </ul>
 * 
 * @see Dialect
 */
public class SQLServerDialect implements Dialect {

  private static final String DIALECT_NAME = "SQL Server";
  private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  private static final String VALIDATION_QUERY = "SELECT 1";
  private static final char QUOTE_CHAR = '['; // SQL Server uses []

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

    // SQL Server 2012+ syntax
    // NOTE: SQL Server requires ORDER BY for OFFSET/FETCH
    // The ORDER BY clause must be added by the query builder
    if (offset == 0) {
      return "OFFSET 0 ROWS FETCH NEXT " + limit + " ROWS ONLY";
    }
    return "OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
  }

  @Override
  public String getIdentityColumnString() {
    return "IDENTITY";
  }

  @Override
  public String getTypeName(int sqlType, int length) {
    switch (sqlType) {
      // Numeric types
      case Types.BOOLEAN:
      case Types.BIT:
        return "BIT";

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
        return "REAL";

      case Types.DOUBLE:
        return "FLOAT";

      case Types.DECIMAL:
      case Types.NUMERIC:
        return "DECIMAL";

      // String types
      case Types.CHAR:
        return length > 0 ? "CHAR(" + length + ")" : "CHAR(1)";

      case Types.VARCHAR:
      case Types.LONGVARCHAR:
        return length > 0 ? "VARCHAR(" + length + ")" : "VARCHAR(255)";

      case Types.CLOB:
        return "TEXT";

      // Unicode string types (SQL Server specific)
      case Types.NCHAR:
        return length > 0 ? "NCHAR(" + length + ")" : "NCHAR(1)";

      case Types.NVARCHAR:
        return length > 0 ? "NVARCHAR(" + length + ")" : "NVARCHAR(255)";

      // Date/Time types
      case Types.DATE:
        return "DATE";

      case Types.TIME:
        return "TIME";

      case Types.TIMESTAMP:
        return "DATETIME2"; // More precise than DATETIME

      // Binary types
      case Types.BINARY:
      case Types.VARBINARY:
      case Types.LONGVARBINARY:
        return "VARBINARY(MAX)";

      case Types.BLOB:
        return "VARBINARY(MAX)";

      // Special types
      case Types.OTHER:
        return "UNIQUEIDENTIFIER"; // SQL Server's UUID type

      default:
        return "VARCHAR(255)";
    }
  }

  @Override
  public boolean supportsSequences() {
    // SQL Server 2012+ supports sequences
    return true;
  }

  @Override
  public String getSequenceNextValString(String sequenceName) {
    // SQL Server syntax for sequences
    return "SELECT NEXT VALUE FOR " + sequenceName;
  }

  @Override
  public char getIdentifierQuoteCharacter() {
    return QUOTE_CHAR;
  }

  @Override
  public PaginationFragment getPaginationFragment(Integer limit, Integer offset) {
    List<Object> params = new ArrayList<>();
    StringBuilder sql = new StringBuilder();

    // SQL Server requires OFFSET even if it's 0
    int actualOffset = (offset != null && offset > 0) ? offset : 0;

    sql.append(" OFFSET ? ROWS");
    params.add(actualOffset);

    if (limit != null) {
      sql.append(" FETCH NEXT ? ROWS ONLY");
      params.add(limit);
    }

    return new PaginationFragment(sql.toString(), params);
  }

  @Override
  public void setParameter(PreparedStatement stmt, int parameterIndex, Object value, Class<?> javaType)
      throws SQLException {

    if (value == null) {
      int jdbcType = TypeMapper.getJdbcType(javaType);
      stmt.setNull(parameterIndex, jdbcType);
      return;
    }

    // Handle boolean as BIT (0/1) for SQL Server
    if (javaType == Boolean.class || javaType == boolean.class) {
      stmt.setBoolean(parameterIndex, (Boolean) value);
    }
    // Handle UUID as UNIQUEIDENTIFIER
    else if (javaType == java.util.UUID.class) {
      stmt.setString(parameterIndex, value.toString());
    }
    // For all other types, use standard setObject
    else {
      stmt.setObject(parameterIndex, value);
    }
  }
}
