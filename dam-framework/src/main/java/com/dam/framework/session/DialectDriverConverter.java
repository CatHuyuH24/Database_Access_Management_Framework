package com.dam.framework.session;

import com.dam.framework.config.DialectDriver;
import com.dam.framework.config.MySQLDialectDriver;

/**
 * Internal utility class for mapping dialect names to DialectDriver
 * implementations.
 * <p>
 * This class is used internally by Configuration when loading from properties
 * files.
 * End-users should use concrete DialectDriver implementations directly.
 * 
 * @see DialectDriver
 * @see MySQLDialectDriver
 */
class DialectDriverConverter {

    private DialectDriverConverter() {
        // Utility class
    }

    /**
     * Get DialectDriver from string name.
     * Used when loading from properties file.
     * 
     * @param dialectName dialect name (e.g., "mysql", "MYSQL")
     * @return corresponding DialectDriver implementation
     */
    static DialectDriver fromString(String dialectName) {
        if (dialectName == null || dialectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Dialect name cannot be null or empty");
        }

        String normalized = dialectName.trim().toLowerCase();

        switch (normalized) {
            case "mysql":
                return new MySQLDialectDriver();
            default:
                throw new IllegalArgumentException("Unsupported dialect: " + dialectName +
                        ". Use a concrete DialectDriver implementation for custom databases.");
        }
    }
}
