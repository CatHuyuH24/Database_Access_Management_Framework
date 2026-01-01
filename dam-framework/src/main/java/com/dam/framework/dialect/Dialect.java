package com.dam.framework.dialect;

import java.util.List;

/**
 * Interface for database-specific SQL generation.
 * <p>
 * Implements the Strategy Pattern to support multiple database systems. Each supported database has
 * its own Dialect implementation.
 *
 * @see MySQLDialect
 */
public interface Dialect {

    /**
     * Get the name of this dialect.
     *
     * @return the dialect name
     */
    String getName();

    /**
     * Get the default JDBC driver class name.
     *
     * @return the driver class name
     */
    String getDriverClassName();

    /**
     * Get a SQL query to validate connections.
     *
     * @return the validation query
     */
    String getValidationQuery();

    /**
     * Get the LIMIT/OFFSET clause for pagination.
     *
     * @param limit  the maximum number of rows
     * @param offset the number of rows to skip
     * @return the pagination SQL clause
     */
    String getLimitClause(int limit, int offset);

    /**
     * Get the SQL for an identity/auto-increment column.
     *
     * @return the identity column SQL
     */
    String getIdentityColumnString();

    /**
     * Get the SQL type name for a JDBC type.
     *
     * @param sqlType the JDBC type code
     * @param length  the column length
     * @return the SQL type name
     */
    String getTypeName(int sqlType, int length);

    /**
     * Check if this database supports sequences.
     *
     * @return true if sequences are supported
     */
    boolean supportsSequences();

    /**
     * Get the SQL to get the next value from a sequence.
     *
     * @param sequenceName the sequence name
     * @return the SQL for next sequence value
     */
    String getSequenceNextValString(String sequenceName);

    /**
     * Get the character used to quote identifiers.
     *
     * @return the quote character
     */
    char getIdentifierQuoteCharacter();

    PaginationFragment getPaginationFragment(Integer limit, Integer offset);

    record PaginationFragment(String sql, List<Object> parameters) {

    }
}