package com.dam.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.dam.framework.connection.BasicConnectionManager;
import com.dam.framework.exception.DAMException;
import com.dam.framework.session.SessionFactory;
import com.dam.framework.util.ClasspathResources;

/**
 * Configuration class for the DAM Framework.
 * <p>
 * Provides fluent API for configuring database connection and framework
 * settings.
 * 
 * <pre>
 * {@code
 * Configuration config = new Configuration()
 *         .setUrl("jdbc:mysql://localhost:3306/mydb")
 *         .setUsername("root")
 *         .setPassword("secret")
 *         .setDriver("com.mysql.cj.jdbc.Driver")
 *         .setDialect(DialectType.MYSQL)
 *         .addAnnotatedClass(User.class)
 *         .addAnnotatedClass(Order.class);
 * 
 * SessionFactory factory = config.buildSessionFactory();
 * }
 * </pre>
 * 
 * @see SessionFactory
 */
public class Configuration {

    // TODO: Implement configuration properties and methods
    private BasicConnectionManager.Builder _conBuilder;
    Properties _properties = new Properties();

    /**
     * Creates a new Configuration instance.
     */
    public Configuration() {
        // Default constructor
    }

    /**
     * Load configuration from a properties file.
     * 
     * @param resourcePath path to the properties file
     * @return this Configuration for method chaining
     * @throws DAMException if the file is not found
     */
    public Configuration loadConfiguration(String resourcePath) throws IOException {
        // TODO: Implement properties file loading

        try (InputStream is = ClasspathResources.getResourceAsStream(resourcePath)) {
            if (null == is) {
                throw new DAMException("Configuration file not found at: " + resourcePath);
            }

            _properties.load(is);
            return this;
        }
    }

    /**
     * Set the JDBC connection URL.
     * 
     * @param url the JDBC URL
     * @return this Configuration for method chaining
     */
    public Configuration setUrl(String url) {
        _conBuilder.url(url);
        return this;
    }

    /**
     * Set the database username.
     * 
     * @param username the username
     * @return this Configuration for method chaining
     */
    public Configuration setUsername(String username) {
        _conBuilder.username(username);
        return this;
    }

    /**
     * Set the database password.
     * 
     * @param password the password
     * @return this Configuration for method chaining
     */
    public Configuration setPassword(String password) {
        // TODO: Implement
        _conBuilder.password(password);
        return this;
    }

    /**
     * Set the JDBC driver class name.
     * 
     * @param driverClass the driver class name
     * @return this Configuration for method chaining
     */
    public Configuration setDriver(String driverClass) {
        _conBuilder.driverClass(driverClass);
        return this;
    }

    // /**
    // * Set the database dialect.
    // *
    // * @param dialect the dialect type
    // * @return this Configuration for method chaining
    // */
    // public Configuration setDialect(DialectType dialect) {
    // _conBuilder
    // return this;
    // }

    /**
     * Add an annotated entity class.
     * 
     * @param entityClass the entity class
     * @return this Configuration for method chaining
     */
    public Configuration addAnnotatedClass(Class<?> entityClass) {
        // TODO: Implement
        return this;
    }

    /**
     * Scan a package for entity classes.
     * 
     * @param packageName the package to scan
     * @return this Configuration for method chaining
     */
    public Configuration scanPackage(String packageName) {
        // TODO: Implement
        return this;
    }

    /**
     * Enable or disable SQL logging.
     * 
     * @param show true to show SQL, false to hide
     * @return this Configuration for method chaining
     */
    public Configuration setShowSql(boolean show) {
        // TODO: Implement
        return this;
    }

    /**
     * Build and return a SessionFactory based on this configuration.
     * 
     * @return the configured SessionFactory
     */
    public SessionFactory buildSessionFactory() {
        // TODO: Implement SessionFactory creation
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
