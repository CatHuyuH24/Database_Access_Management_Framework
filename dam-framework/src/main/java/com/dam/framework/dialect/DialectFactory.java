package com.dam.framework.dialect;

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
 * @see DialectDriverType
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
      switch (normalized) {
        case "mysql":
          return new MySQLDialect();

        default:
          return null;
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Unsupported dialect name: " + dialectName +
              ". Supported values: MYSQL, POSTGRESQL, SQLSERVER, SQLITE",
          e);
    }
  }

  /**
   * Get an array of currently supported dialect types in string.
   * 
   * @return array of supported dialect types
   * 
   */
  public static String[] getSupportedDialects() {
    // Currently only MySQL is supported in Sprint 1
    // This will expand in Sprint 5
    return new String[] { "mysql" };
  }
}
