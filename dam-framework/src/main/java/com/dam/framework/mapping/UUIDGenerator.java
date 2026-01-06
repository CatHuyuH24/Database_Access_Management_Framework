package com.dam.framework.mapping;

import java.sql.Connection;
import java.util.UUID;

import com.dam.framework.dialect.Dialect;
import com.dam.framework.exception.DAMException;

/**
 * ID generator that creates UUID (Universally Unique Identifier) values.
 * <p>
 * Generates 36-character string UUIDs in the format:
 * {@code 550e8400-e29b-41d4-a716-446655440000}
 * 
 * <p>
 * <b>Advantages:</b>
 * </p>
 * <ul>
 * <li>Database-independent (no database-specific features needed)</li>
 * <li>Globally unique across all tables and databases</li>
 * <li>No round-trip to database needed</li>
 * <li>Safe for distributed systems and database replication</li>
 * </ul>
 * 
 * <p>
 * <b>Disadvantages:</b>
 * </p>
 * <ul>
 * <li>Larger storage size (36 bytes for string, 16 bytes for binary)</li>
 * <li>Non-sequential, which may impact index performance</li>
 * <li>Less human-readable than integers</li>
 * </ul>
 * 
 * <p>
 * <b>Usage:</b>
 * </p>
 * 
 * <pre>
 * {
 *   &#64;code
 *   &#64;Id
 *   @GeneratedValue(strategy = GenerationType.UUID)
 *   private String id; // or use UUID type if supported
 * }
 * </pre>
 */
public class UUIDGenerator implements IdGenerator {

  @Override
  public Object generate(Connection connection, Dialect dialect, EntityMetadata metadata) {
    Class<?> idType = metadata.getIdColumn().javaType();
    if (idType != String.class && idType != java.util.UUID.class) {
      throw new DAMException(
          "UUID strategy requires String or UUID field type, but found: " + idType);
    }
    /*
     * UUID generator always returns String, but doesn't validate that the field
     * type is compatible. If user annotates a `Long id` field with
     * `@GeneratedValue(strategy = UUID)`, runtime error occurs.
     */
    return UUID.randomUUID().toString();
  }

  @Override
  public boolean isPostInsertGenerator() {
    // UUID is generated BEFORE insert by application
    return false;
  }
}
