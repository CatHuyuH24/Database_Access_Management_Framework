package com.dam.framework.query;

/**
 * Defines the sort order for ORDER BY clauses.
 * 
 * @see Query#orderBy(String, Order)
 */
public enum Order {
    
    /**
     * Ascending order (smallest to largest).
     */
    ASC,
    
    /**
     * Descending order (largest to smallest).
     */
    DESC
}
