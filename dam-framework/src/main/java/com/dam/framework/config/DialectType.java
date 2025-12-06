package com.dam.framework.config;

/**
 * Enumeration of supported database dialects.
 * 
 * @see Configuration#setDialect(DialectType)
 */
public enum DialectType {
    
    /**
     * MySQL database dialect.
     */
    MYSQL("com.mysql.cj.jdbc.Driver"),
    
    /**
     * PostgreSQL database dialect.
     */
    POSTGRESQL("org.postgresql.Driver"),
    
    /**
     * Microsoft SQL Server dialect.
     */
    SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    
    /**
     * SQLite database dialect.
     */
    SQLITE("org.sqlite.JDBC");
    
    private final String defaultDriverClass;
    
    DialectType(String defaultDriverClass) {
        this.defaultDriverClass = defaultDriverClass;
    }
    
    /**
     * Get the default JDBC driver class for this dialect.
     * 
     * @return the driver class name
     */
    public String getDefaultDriverClass() {
        return defaultDriverClass;
    }
}
