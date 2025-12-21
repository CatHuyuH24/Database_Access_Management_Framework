package com.dam.framework.dialect;

import com.dam.framework.config.DialectType;

/**
 * Factory for creating database-specific Dialect instances.
 * <p>
 * Implements Factory pattern to centralize Dialect object creation.
 * Provides static factory method for obtaining appropriate Dialect
 * based on DialectType.
 * 
 * <h3>Design Patterns Used:</h3>
 * <ul>
 * <li><b>Factory Pattern:</b> Centralized creation of Dialect objects</li>
 * <li><b>Strategy Pattern:</b> Each Dialect is a different strategy for SQL
 * generation</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * 
 * <pre>
 * {@code
 * Dialect dialect = DialectFactory.createDialect(DialectType.MYSQL);
 * String limitClause = dialect.getLimitClause(10, 0);
 * }
 * </pre>
 * 
 * @author enkay2408
 * @see Dialect
 * @see DialectType
 */
public class DialectFactory {

  /**
   * Private constructor to prevent instantiation.
   * This is a utility class with only static methods.
   */
  private DialectFactory() {
    throw new AssertionError("DialectFactory is a utility class and should not be instantiated");
  }

  /**
   * Create a Dialect instance based on the specified type.
   * <p>
   * Factory method that returns appropriate Dialect implementation.
   * Currently supports:
   * <ul>
   * <li>MySQL - {@link MySQLDialect}</li>
   * <li>PostgreSQL - PostgreSQLDialect (to be implemented in Sprint 5)</li>
   * <li>SQL Server - SQLServerDialect (to be implemented in Sprint 5)</li>
   * <li>SQLite - SQLiteDialect (optional, future)</li>
   * </ul>
   * 
   * @param type the dialect type
   * @return appropriate Dialect implementation
   * @throws IllegalArgumentException if type is null or unsupported
   * 
   * @example
   * 
   *          <pre>
   *          Dialect mysql = DialectFactory.createDialect(DialectType.MYSQL);
   *          Dialect postgres = DialectFactory.createDialect(DialectType.POSTGRESQL);
   *          </pre>
   */
  public static Dialect createDialect(DialectType type) {
    if (type == null) {
      throw new IllegalArgumentException("Dialect type cannot be null");
    }

    switch (type) {
      case MYSQL:
        return new MySQLDialect();

      case POSTGRESQL:
        // TODO: Implement in Sprint 5
        throw new UnsupportedOperationException(
            "PostgreSQL dialect not yet implemented. Will be available in Sprint 5.");

      case SQLSERVER:
        // TODO: Implement in Sprint 5
        throw new UnsupportedOperationException(
            "SQL Server dialect not yet implemented. Will be available in Sprint 5.");

      case SQLITE:
        // Optional - may implement in future
        throw new UnsupportedOperationException(
            "SQLite dialect not yet implemented.");

      default:
        throw new IllegalArgumentException("Unsupported dialect type: " + type);
    }
  }

  /**
   * Create a Dialect instance from a string name.
   * <p>
   * Alternative factory method that accepts string dialect names.
   * Case-insensitive matching.
   * 
   * @param dialectName the dialect name (e.g., "mysql", "PostgreSQL",
   *                    "SQLSERVER")
   * @return appropriate Dialect implementation
   * @throws IllegalArgumentException if dialectName is null, empty, or
   *                                  unsupported
   * 
   * @example
   * 
   *          <pre>
   *          Dialect mysql = DialectFactory.createDialect("mysql");
   *          Dialect postgres = DialectFactory.createDialect("PostgreSQL");
   *          </pre>
   */
  public static Dialect createDialect(String dialectName) {
    if (dialectName == null || dialectName.trim().isEmpty()) {
      throw new IllegalArgumentException("Dialect name cannot be null or empty");
    }

    String normalized = dialectName.trim().toUpperCase();

    try {
      DialectType type = DialectType.valueOf(normalized);
      return createDialect(type);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Unsupported dialect name: " + dialectName +
              ". Supported values: MYSQL, POSTGRESQL, SQLSERVER, SQLITE",
          e);
    }
  }

  /**
   * Check if a dialect type is currently supported.
   * <p>
   * Useful for validation before attempting to create a dialect.
   * 
   * @param type the dialect type to check
   * @return true if the dialect is implemented and supported
   * 
   * @example
   * 
   *          <pre>
   *          if (DialectFactory.isSupported(DialectType.MYSQL)) {
   *            Dialect dialect = DialectFactory.createDialect(DialectType.MYSQL);
   *          }
   *          </pre>
   */
  public static boolean isSupported(DialectType type) {
    if (type == null) {
      return false;
    }

    // Currently only MySQL is supported in Sprint 1
    // PostgreSQL and SQL Server will be added in Sprint 5
    return type == DialectType.MYSQL;
  }

  /**
   * Get an array of currently supported dialect types.
   * <p>
   * Returns the list of DialectTypes that are fully implemented
   * and can be created via this factory.
   * 
   * @return array of supported dialect types
   * 
   * @example
   * 
   *          <pre>
   *          DialectType[] supported = DialectFactory.getSupportedDialects();
   *          for (DialectType type : supported) {
   *            System.out.println("Supported: " + type);
   *          }
   *          </pre>
   */
  public static DialectType[] getSupportedDialects() {
    // Currently only MySQL is supported in Sprint 1
    // This will expand in Sprint 5
    return new DialectType[] { DialectType.MYSQL };
  }

  /**
   * Get a descriptive string of all supported dialects.
   * <p>
   * Useful for error messages and user information.
   * 
   * @return comma-separated list of supported dialect names
   * 
   * @example
   * 
   *          <pre>
   *          String supported = DialectFactory.getSupportedDialectsString();
   *          // Returns: "MYSQL"
   *          // In Sprint 5, will return: "MYSQL, POSTGRESQL, SQLSERVER"
   *          </pre>
   */
  public static String getSupportedDialectsString() {
    DialectType[] supported = getSupportedDialects();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < supported.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(supported[i].name());
    }
    return sb.toString();
  }
}
