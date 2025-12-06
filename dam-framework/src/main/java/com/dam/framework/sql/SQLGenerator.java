package com.dam.framework.sql;

import com.dam.framework.mapping.EntityMetadata;

/**
 * Generates SQL statements from entity metadata.
 * <p>
 * This class is responsible for converting entity operations into SQL statements.
 * 
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
}
