package com.dam.framework.mapping;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.dam.framework.annotations.SequenceGenerator;
import com.dam.framework.dialect.Dialect;
import com.dam.framework.exception.DAMException;

/**
 * ID generator using database sequences.
 * <p>
 * This strategy uses database sequence objects to generate unique IDs.
 * Sequences are supported natively by PostgreSQL and Oracle.
 * 
 * <p>
 * <b>Supported by:</b>
 * </p>
 * <ul>
 * <li>PostgreSQL: CREATE SEQUENCE</li>
 * <li>Oracle: CREATE SEQUENCE</li>
 * <li>DB2: CREATE SEQUENCE</li>
 * </ul>
 * 
 * <p>
 * <b>NOT supported by:</b>
 * </p>
 * <ul>
 * <li>MySQL (use IDENTITY instead)</li>
 * <li>SQL Server (use IDENTITY instead, though sequences exist since SQL Server
 * 2012)</li>
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
 *   &#64;GeneratedValue(strategy = GenerationType.SEQUENCE)
 *   @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
 *   private Long id;
 * }
 * </pre>
 */
public class SequenceStyleGenerator implements IdGenerator {

  @Override
  public Object generate(Connection connection, Dialect dialect, EntityMetadata metadata) {
    if (!dialect.supportsSequences()) {
      throw new DAMException(
          "Sequence generation not supported by dialect: " + dialect.getDialectName() +
              ". Use GenerationType.IDENTITY instead.");
    }

    // Get sequence name from @SequenceGenerator annotation
    String sequenceName = getSequenceName(metadata);

    // Get next value from sequence
    String sql = dialect.getSequenceNextValString(sequenceName);

    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      if (rs.next()) {
        Object nextVal = rs.getObject(1);

        // Convert to appropriate type (Long, Integer, etc.)
        Class<?> idType = metadata.getIdColumn().javaType();
        if (idType == Long.class || idType == long.class) {
          return ((Number) nextVal).longValue();
        } else if (idType == Integer.class || idType == int.class) {
          return ((Number) nextVal).intValue();
        } else {
          return nextVal;
        }
      } else {
        throw new DAMException("Failed to retrieve next value from sequence: " + sequenceName);
      }

    } catch (SQLException e) {
      throw new DAMException("Error generating ID from sequence: " + sequenceName, e);
    }
  }

  @Override
  public boolean isPostInsertGenerator() {
    // Sequence value is generated BEFORE insert
    return false;
  }

  /**
   * Extract sequence name from @SequenceGenerator annotation or use default.
   */
  private String getSequenceName(EntityMetadata metadata) {
    Field idField = metadata.getIdColumn().field();

    if (idField.isAnnotationPresent(SequenceGenerator.class)) {
      SequenceGenerator seqGen = idField.getAnnotation(SequenceGenerator.class);

      // Use explicit sequenceName if provided
      if (!seqGen.sequenceName().isBlank()) {
        return seqGen.sequenceName();
      }
    }

    // Default: <table_name>_<id_column>_seq
    return metadata.getTableName().toLowerCase() + "_" +
        metadata.getIdColumn().columnName().toLowerCase() + "_seq";
  }
}
