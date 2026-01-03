package com.dam.framework.config;

/**
 * MySQL dialect driver implementation.
 * 
 * @author Dev C
 */
public class MySQLDialectDriver implements DialectDriver {

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getDialectName() {
        return "mysql";
    }
}
