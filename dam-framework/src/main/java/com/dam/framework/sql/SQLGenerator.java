package com.dam.framework.sql;

import java.util.List;

import com.dam.framework.mapping.ColumnMetadata;
import com.dam.framework.session.Session;
import com.dam.framework.query.Query;

import com.dam.framework.mapping.EntityMetadata;

/**
 * Generates SQL statements from entity metadata.
 * <p>
 * <b>Note:</b> This is an internal framework interface.
 * Application developers should use {@link Session} and {@link Query} instead.
 * Direct use of SQLGenerator is not recommended.
 *
 * @see Session
 * @see Query
 * @see EntityMetadata
 */
public interface SQLGenerator {

    /**
     * Generate an INSERT statement for the entity.
     * 
     * @param metadata the entity metadata
     * @return the INSERT SQL statement
     */
    String generateInsert(EntityMetadata metadata);

    /**
     * Generate a SELECT statement for all records.
     * 
     * @param metadata the entity metadata
     * @return the SELECT SQL statement
     */
    String generateSelect(EntityMetadata metadata);

    /**
     * Generate a SELECT statement by primary key.
     * 
     * @param metadata the entity metadata
     * @return the SELECT SQL statement with WHERE clause
     */
    String generateSelectById(EntityMetadata metadata);

    /**
     * Generate an UPDATE statement for the entity.
     * 
     * @param metadata the entity metadata
     * @return the UPDATE SQL statement
     */
    String generateUpdate(EntityMetadata metadata);

    /**
     * Generate a DELETE statement for the entity.
     * 
     * @param metadata the entity metadata
     * @return the DELETE SQL statement
     */
    String generateDelete(EntityMetadata metadata);

    /**
     * Generate a partial UPDATE statement for the entity. Mainly for dirty-checking
     * and updating
     * 
     * @param metadata       the entity metadata
     * @param changedColumns the list of changed columns
     * @return the UPDATE SQL statement
     */
    String generatePartialUpdate(EntityMetadata metadata, List<ColumnMetadata> changedColumns);
}
