package com.dam.framework.util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dam.framework.exception.DAMException;

/**
 * Utility class for mapping between Java types and SQL/JDBC types.
 * <p>
 * Provides bidirectional type conversion:
 * <ul>
 * <li>Java types → JDBC types (for INSERT/UPDATE)</li>
 * <li>JDBC types → Java types (for SELECT/query results)</li>
 * <li>ResultSet values → Java objects (for entity population)</li>
 * </ul>
 * 
 * <h3>Design Pattern:</h3>
 * <ul>
 * <li><b>Strategy Pattern:</b> Different type conversion strategies for
 * different
 * Java types</li>
 * </ul>
 * 
 * @author Dev C
 * @see java.sql.Types
 */
public final class TypeMapper {

  private TypeMapper() {
    // Utility class - prevent instantiation
  }

  /**
   * Map of Java class to JDBC type code.
   */
  private static final Map<Class<?>, Integer> JAVA_TO_JDBC = new HashMap<>();

  static {
    // Primitive types and wrappers
    JAVA_TO_JDBC.put(boolean.class, Types.BOOLEAN);
    JAVA_TO_JDBC.put(Boolean.class, Types.BOOLEAN);
    JAVA_TO_JDBC.put(byte.class, Types.TINYINT);
    JAVA_TO_JDBC.put(Byte.class, Types.TINYINT);
    JAVA_TO_JDBC.put(short.class, Types.SMALLINT);
    JAVA_TO_JDBC.put(Short.class, Types.SMALLINT);
    JAVA_TO_JDBC.put(int.class, Types.INTEGER);
    JAVA_TO_JDBC.put(Integer.class, Types.INTEGER);
    JAVA_TO_JDBC.put(long.class, Types.BIGINT);
    JAVA_TO_JDBC.put(Long.class, Types.BIGINT);
    JAVA_TO_JDBC.put(float.class, Types.FLOAT);
    JAVA_TO_JDBC.put(Float.class, Types.FLOAT);
    JAVA_TO_JDBC.put(double.class, Types.DOUBLE);
    JAVA_TO_JDBC.put(Double.class, Types.DOUBLE);

    // String types
    JAVA_TO_JDBC.put(String.class, Types.VARCHAR);
    JAVA_TO_JDBC.put(char.class, Types.CHAR);
    JAVA_TO_JDBC.put(Character.class, Types.CHAR);

    // BigDecimal
    JAVA_TO_JDBC.put(BigDecimal.class, Types.DECIMAL);

    // Date/Time types (java.util)
    JAVA_TO_JDBC.put(Date.class, Types.TIMESTAMP);
    JAVA_TO_JDBC.put(java.sql.Date.class, Types.DATE);
    JAVA_TO_JDBC.put(java.sql.Time.class, Types.TIME);
    JAVA_TO_JDBC.put(java.sql.Timestamp.class, Types.TIMESTAMP);

    // Date/Time types (java.time)
    JAVA_TO_JDBC.put(LocalDate.class, Types.DATE);
    JAVA_TO_JDBC.put(LocalTime.class, Types.TIME);
    JAVA_TO_JDBC.put(LocalDateTime.class, Types.TIMESTAMP);

    // Binary types
    JAVA_TO_JDBC.put(byte[].class, Types.VARBINARY);
  }

  /**
   * Get the JDBC type code for a Java class.
   * <p>
   * This is used when generating SQL or setting PreparedStatement parameters.
   * 
   * @param javaType the Java class
   * @return the JDBC type code from {@link Types}
   * 
   * @example
   * 
   *          <pre>
   *          int jdbcType = TypeMapper.getJdbcType(String.class);
   *          // Returns Types.VARCHAR
   *          </pre>
   */
  public static int getJdbcType(Class<?> javaType) {
    if (javaType == null) {
      throw new IllegalArgumentException("Java type cannot be null");
    }

    Integer jdbcType = JAVA_TO_JDBC.get(javaType);
    if (jdbcType != null) {
      return jdbcType;
    }

    // For enum types
    if (javaType.isEnum()) {
      return Types.VARCHAR;
    }

    // Default to VARCHAR for unknown types
    return Types.VARCHAR;
  }

  /**
   * Get the default Java class for a JDBC type code.
   * <p>
   * This is used when creating entity instances from database results.
   * 
   * @param jdbcType the JDBC type code from {@link Types}
   * @return the corresponding Java class
   * 
   * @example
   * 
   *          <pre>
   *          Class<?> javaType = TypeMapper.getJavaType(Types.VARCHAR);
   *          // Returns String.class
   *          </pre>
   */
  public static Class<?> getJavaType(int jdbcType) {
    switch (jdbcType) {
      case Types.BOOLEAN:
      case Types.BIT:
        return Boolean.class;

      case Types.TINYINT:
        return Byte.class;

      case Types.SMALLINT:
        return Short.class;

      case Types.INTEGER:
        return Integer.class;

      case Types.BIGINT:
        return Long.class;

      case Types.FLOAT:
      case Types.REAL:
        return Float.class;

      case Types.DOUBLE:
        return Double.class;

      case Types.DECIMAL:
      case Types.NUMERIC:
        return BigDecimal.class;

      case Types.CHAR:
        return Character.class;

      case Types.VARCHAR:
      case Types.LONGVARCHAR:
      case Types.CLOB:
        return String.class;

      case Types.DATE:
        return java.sql.Date.class;

      case Types.TIME:
        return java.sql.Time.class;

      case Types.TIMESTAMP:
        return java.sql.Timestamp.class;

      case Types.BINARY:
      case Types.VARBINARY:
      case Types.LONGVARBINARY:
      case Types.BLOB:
        return byte[].class;

      default:
        return Object.class;
    }
  }

  /**
   * Extract a value from ResultSet and convert to the specified Java type.
   * <p>
   * Handles type conversion and null values appropriately.
   * 
   * @param rs         the ResultSet
   * @param columnName the column name
   * @param targetType the target Java class
   * @return the converted value, or null if the database value is NULL
   * @throws SQLException if database access error occurs
   * 
   * @example
   * 
   *          <pre>
   *          String name = TypeMapper.getResultSetValue(rs, "name", String.class);
   *          Integer age = TypeMapper.getResultSetValue(rs, "age", Integer.class);
   *          </pre>
   */
  @SuppressWarnings("unchecked")
  public static <T> T getResultSetValue(ResultSet rs, String columnName, Class<T> targetType)
      throws SQLException {
    if (rs == null || columnName == null || targetType == null) {
      throw new IllegalArgumentException("ResultSet, column name, and target type cannot be null");
    }

    Object value = rs.getObject(columnName);
    if (value == null) {
      return null;
    }

    // If already the correct type, return as-is
    if (targetType.isInstance(value)) {
      return (T) value;
    }

    // Type conversion
    try {
      return convertValue(value, targetType);
    } catch (Exception e) {
      throw new DAMException(
          "Failed to convert value from column '" + columnName + "' to type " + targetType.getName(),
          e);
    }
  }

  /**
   * Extract a value from ResultSet by column index and convert to the specified
   * Java type.
   * <p>
   * Handles type conversion and null values appropriately.
   * 
   * @param rs          the ResultSet
   * @param columnIndex the column index (1-based)
   * @param targetType  the target Java class
   * @return the converted value, or null if the database value is NULL
   * @throws SQLException if database access error occurs
   * 
   * @example
   * 
   *          <pre>
   *          String name = TypeMapper.getResultSetValue(rs, 1, String.class);
   *          Integer age = TypeMapper.getResultSetValue(rs, 2, Integer.class);
   *          </pre>
   */
  @SuppressWarnings("unchecked")
  public static <T> T getResultSetValue(ResultSet rs, int columnIndex, Class<T> targetType)
      throws SQLException {
    if (rs == null || targetType == null) {
      throw new IllegalArgumentException("ResultSet and target type cannot be null");
    }
    if (columnIndex < 1) {
      throw new IllegalArgumentException("Column index must be >= 1");
    }

    Object value = rs.getObject(columnIndex);
    if (value == null) {
      return null;
    }

    // If already the correct type, return as-is
    if (targetType.isInstance(value)) {
      return (T) value;
    }

    // Type conversion
    try {
      return convertValue(value, targetType);
    } catch (Exception e) {
      throw new DAMException(
          "Failed to convert value from column index " + columnIndex + " to type " + targetType.getName(),
          e);
    }
  }

  /**
   * Convert a value to the target type.
   * <p>
   * Supports various type conversions including primitives, wrappers, strings,
   * and dates.
   * 
   * @param value      the value to convert
   * @param targetType the target Java class
   * @return the converted value
   */
  @SuppressWarnings("unchecked")
  private static <T> T convertValue(Object value, Class<T> targetType) {
    if (value == null) {
      return null;
    }

    // String conversion
    if (targetType == String.class) {
      return (T) value.toString();
    }

    // Enum conversion
    if (targetType.isEnum()) {
      if (value instanceof String) {
        return (T) Enum.valueOf((Class<Enum>) targetType, (String) value);
      }
    }

    // Number conversions
    if (value instanceof Number) {
      Number num = (Number) value;

      if (targetType == Integer.class || targetType == int.class) {
        return (T) Integer.valueOf(num.intValue());
      }
      if (targetType == Long.class || targetType == long.class) {
        return (T) Long.valueOf(num.longValue());
      }
      if (targetType == Double.class || targetType == double.class) {
        return (T) Double.valueOf(num.doubleValue());
      }
      if (targetType == Float.class || targetType == float.class) {
        return (T) Float.valueOf(num.floatValue());
      }
      if (targetType == Short.class || targetType == short.class) {
        return (T) Short.valueOf(num.shortValue());
      }
      if (targetType == Byte.class || targetType == byte.class) {
        return (T) Byte.valueOf(num.byteValue());
      }
      if (targetType == BigDecimal.class) {
        if (value instanceof BigDecimal) {
          return (T) value;
        }
        return (T) BigDecimal.valueOf(num.doubleValue());
      }
    }

    // Boolean conversions
    if (targetType == Boolean.class || targetType == boolean.class) {
      if (value instanceof Boolean) {
        return (T) value;
      }
      if (value instanceof Number) {
        return (T) Boolean.valueOf(((Number) value).intValue() != 0);
      }
      if (value instanceof String) {
        return (T) Boolean.valueOf((String) value);
      }
    }

    // Character conversion
    if (targetType == Character.class || targetType == char.class) {
      String str = value.toString();
      if (!str.isEmpty()) {
        return (T) Character.valueOf(str.charAt(0));
      }
    }

    // Date/Time conversions
    if (targetType == LocalDate.class && value instanceof java.sql.Date) {
      return (T) ((java.sql.Date) value).toLocalDate();
    }
    if (targetType == LocalTime.class && value instanceof java.sql.Time) {
      return (T) ((java.sql.Time) value).toLocalTime();
    }
    if (targetType == LocalDateTime.class && value instanceof java.sql.Timestamp) {
      return (T) ((java.sql.Timestamp) value).toLocalDateTime();
    }
    if (targetType == Date.class && value instanceof java.sql.Timestamp) {
      return (T) new Date(((java.sql.Timestamp) value).getTime());
    }

    // If no conversion found, try casting
    try {
      return targetType.cast(value);
    } catch (ClassCastException e) {
      throw new DAMException(
          "Cannot convert value of type " + value.getClass().getName() + " to " + targetType.getName(),
          e);
    }
  }

  /**
   * Check if a Java type is a primitive or primitive wrapper.
   * 
   * @param type the Java class to check
   * @return true if primitive or wrapper type
   * 
   * @example
   * 
   *          <pre>
   *          boolean isPrimitive = TypeMapper.isPrimitiveOrWrapper(Integer.class); // true
   *          boolean isPrimitive = TypeMapper.isPrimitiveOrWrapper(String.class); // false
   *          </pre>
   */
  public static boolean isPrimitiveOrWrapper(Class<?> type) {
    if (type == null) {
      return false;
    }

    return type.isPrimitive() ||
        type == Boolean.class ||
        type == Byte.class ||
        type == Short.class ||
        type == Integer.class ||
        type == Long.class ||
        type == Float.class ||
        type == Double.class ||
        type == Character.class;
  }

  /**
   * Check if a Java type is a numeric type.
   * 
   * @param type the Java class to check
   * @return true if numeric type
   * 
   * @example
   * 
   *          <pre>
   *          boolean isNumeric = TypeMapper.isNumericType(Integer.class); // true
   *          boolean isNumeric = TypeMapper.isNumericType(String.class); // false
   *          </pre>
   */
  public static boolean isNumericType(Class<?> type) {
    if (type == null) {
      return false;
    }

    return type == byte.class || type == Byte.class ||
        type == short.class || type == Short.class ||
        type == int.class || type == Integer.class ||
        type == long.class || type == Long.class ||
        type == float.class || type == Float.class ||
        type == double.class || type == Double.class ||
        type == BigDecimal.class;
  }

  /**
   * Check if a Java type is a string type.
   * 
   * @param type the Java class to check
   * @return true if string type
   * 
   * @example
   * 
   *          <pre>
   *          boolean isString = TypeMapper.isStringType(String.class); // true
   *          boolean isString = TypeMapper.isStringType(Integer.class); // false
   *          </pre>
   */
  public static boolean isStringType(Class<?> type) {
    if (type == null) {
      return false;
    }

    return type == String.class ||
        type == char.class ||
        type == Character.class;
  }

  /**
   * Check if a Java type is a date/time type.
   * 
   * @param type the Java class to check
   * @return true if date/time type
   * 
   * @example
   * 
   *          <pre>
   *          boolean isDateTime = TypeMapper.isDateTimeType(LocalDate.class); // true
   *          boolean isDateTime = TypeMapper.isDateTimeType(String.class); // false
   *          </pre>
   */
  public static boolean isDateTimeType(Class<?> type) {
    if (type == null) {
      return false;
    }

    return type == Date.class ||
        type == java.sql.Date.class ||
        type == java.sql.Time.class ||
        type == java.sql.Timestamp.class ||
        type == LocalDate.class ||
        type == LocalTime.class ||
        type == LocalDateTime.class;
  }

  /**
   * Get a descriptive name for a JDBC type code.
   * 
   * @param jdbcType the JDBC type code
   * @return the type name (e.g., "VARCHAR", "INTEGER")
   * 
   * @example
   * 
   *          <pre>
   *          String typeName = TypeMapper.getJdbcTypeName(Types.VARCHAR);
   *          // Returns "VARCHAR"
   *          </pre>
   */
  public static String getJdbcTypeName(int jdbcType) {
    switch (jdbcType) {
      case Types.BOOLEAN:
        return "BOOLEAN";
      case Types.BIT:
        return "BIT";
      case Types.TINYINT:
        return "TINYINT";
      case Types.SMALLINT:
        return "SMALLINT";
      case Types.INTEGER:
        return "INTEGER";
      case Types.BIGINT:
        return "BIGINT";
      case Types.FLOAT:
        return "FLOAT";
      case Types.REAL:
        return "REAL";
      case Types.DOUBLE:
        return "DOUBLE";
      case Types.DECIMAL:
        return "DECIMAL";
      case Types.NUMERIC:
        return "NUMERIC";
      case Types.CHAR:
        return "CHAR";
      case Types.VARCHAR:
        return "VARCHAR";
      case Types.LONGVARCHAR:
        return "LONGVARCHAR";
      case Types.CLOB:
        return "CLOB";
      case Types.DATE:
        return "DATE";
      case Types.TIME:
        return "TIME";
      case Types.TIMESTAMP:
        return "TIMESTAMP";
      case Types.BINARY:
        return "BINARY";
      case Types.VARBINARY:
        return "VARBINARY";
      case Types.LONGVARBINARY:
        return "LONGVARBINARY";
      case Types.BLOB:
        return "BLOB";
      case Types.NULL:
        return "NULL";
      default:
        return "UNKNOWN(" + jdbcType + ")";
    }
  }
}
