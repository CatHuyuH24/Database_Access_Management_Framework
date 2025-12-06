package com.dam.framework.annotations;

/**
 * Defines strategies for generating primary key values.
 * 
 * @see GeneratedValue
 */
public enum GenerationType {
    
    /**
     * Indicates that the database will generate the primary key using an identity column.
     * This is the most common strategy and works with AUTO_INCREMENT (MySQL) 
     * or IDENTITY (SQL Server) columns.
     */
    IDENTITY,
    
    /**
     * Indicates that the database sequence should be used to generate the primary key.
     * Mainly used with PostgreSQL and Oracle.
     */
    SEQUENCE,
    
    /**
     * Indicates that the primary key value will be assigned manually.
     * No automatic generation occurs.
     */
    NONE
}
