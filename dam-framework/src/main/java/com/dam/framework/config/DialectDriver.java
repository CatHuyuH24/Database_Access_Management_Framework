package com.dam.framework.config;

/**
 * Interface for database driver and dialect configuration.
 * <p>
 * Implements Strategy Pattern to support extensibility without modifying
 * Configuration class (Open-Closed Principle).
 * 
 * <h3>Design Pattern:</h3>
 * <ul>
 * <li><b>Strategy Pattern:</b> Different driver detection strategies</li>
 * <li><b>Open-Closed Principle:</b> Extensible without modification</li>
 * </ul>
 * 
 */
public interface DialectDriver {

    /**
     * Get the JDBC driver class name.
     * 
     * @return driver class name (e.g., "com.mysql.cj.jdbc.Driver")
     */
    public String getDriverClass();

    /**
     * Get the dialect name for this database.
     * 
     * @return dialect name (e.g., "mysql", "postgresql")
     */
    public String getDialectName();
}
