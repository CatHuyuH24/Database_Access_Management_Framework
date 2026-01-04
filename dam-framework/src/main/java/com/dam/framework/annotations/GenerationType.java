package com.dam.framework.annotations;

/**
 * Defines strategies for generating primary key values.
 * 
 * @see GeneratedValue
 */
public enum GenerationType {

    /**
     * Indicates that the database will generate the primary key using an identity
     * column.
     * This is the most common strategy and works with AUTO_INCREMENT (MySQL)
     * or IDENTITY (SQL Server) columns.
     */
    IDENTITY,

    /**
     * Indicates that the database sequence should be used to generate the primary
     * key.
     * Mainly used with PostgreSQL and Oracle.
     * Requires @SequenceGenerator annotation to specify sequence details.
     */
    SEQUENCE,

    /**
     * Indicates that a database table will be used to generate unique keys.
     * A separate table stores the next available ID for each entity.
     */
    TABLE,

    /**
     * Indicates that a UUID (Universally Unique Identifier) should be generated.
     * This strategy is database-independent and generates 36-character UUIDs.
     * Best for distributed systems where database-generated IDs may conflict.
     */
    UUID,

    /**
     * Indicates that the primary key value will be assigned manually.
     * No automatic generation occurs.
     */
    NONE
}
