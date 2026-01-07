package com.dam.framework.mapping.entity;

/**
 * Supported inheritance mapping strategies.
 * <p>
 * Currently only SINGLE_TABLE is implemented.
 * JOINED_TABLE and PER_TABLE are reserved for future extensions.
 */
public enum InheritanceStrategyType {
    /**
     * Single Table Inheritance - all classes in hierarchy map to one table.
     * Uses discriminator column to differentiate between types.
     * <p>
     * Status: ✅ Fully implemented
     */
    SINGLE_TABLE,

    /**
     * Joined Table Inheritance - each class gets its own table.
     * <p>
     * Status: ❌ Not implemented (reserved for future)
     */
    JOINED_TABLE,

    /**
     * Table Per Class Inheritance - complete table per concrete class.
     * <p>
     * Status: ❌ Not implemented (reserved for future)
     */
    PER_TABLE
}