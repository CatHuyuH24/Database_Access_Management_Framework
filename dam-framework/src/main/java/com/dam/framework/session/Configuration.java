package com.dam.framework.session;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.dam.framework.config.DialectDriver;
import com.dam.framework.connection.BasicConnectionManager;
import com.dam.framework.dialect.Dialect;
import com.dam.framework.dialect.DialectFactory;
import com.dam.framework.exception.DAMException;
import com.dam.framework.mapping.EntityMetadata;
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
 *         .setDialectDriverType(new MySQLDialectDriver())
 *         .setUrl("jdbc:mysql://localhost:3306/mydb")
 *         .setUsername("root")
 *         .setPassword("secret")
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
    // is the builder for session factory

    // enforces single session factory per configuration (Singleton)
    private SessionFactory builtFactory;

    // NOT final - builder might be replaced (though currently isn't)
    private BasicConnectionManager.Builder conBuilder = new BasicConnectionManager.Builder();

    private final Properties properties = new Properties();

    // Entity class registry
    private final Set<Class<?>> registeredClasses = new HashSet<>();

    private boolean showSQL = false;
    private String dialectName;

    /**
     * Creates a new Configuration instance.
     */
    public Configuration() {
        // Default constructor

    }

    /**
     * Load configuration from a properties file.
     * <p>
     * Reads properties and applies them to the configuration.
     * Supported properties:
     * <ul>
     * <li>dam.connection.url - JDBC URL</li>
     * <li>dam.connection.username - Database username</li>
     * <li>dam.connection.password - Database password</li>
     * <li>dam.connection.driver - Driver type ("MYSQL", "POSTGRESQL", etc.)</li>
     * <li>dam.show_sql - Show SQL statements (true/false)</li>
     * </ul>
     * 
     * @param resourcePath path to the properties file
     * @return this Configuration for method chaining
     * @throws DAMException if the file is not found
     */
    public Configuration loadConfiguration(String resourcePath) throws IOException {
        try (InputStream is = ClasspathResources.getResourceAsStream(resourcePath)) {
            if (null == is) {
                throw new DAMException("Configuration file not found at: " + resourcePath);
            }

            properties.load(is);

            // Apply properties to configuration
            if (properties.containsKey("dam.connection.url")) {
                setUrl(properties.getProperty("dam.connection.url"));
            }

            if (properties.containsKey("dam.connection.username")) {
                setUsername(properties.getProperty("dam.connection.username"));
            }

            if (properties.containsKey("dam.connection.password")) {
                setPassword(properties.getProperty("dam.connection.password"));
            }

            if (properties.containsKey("dam.connection.driver")) {
                String dialectName = properties.getProperty("dam.connection.driver");
                DialectDriver dialectDriver = DialectDriverConverter.fromString(dialectName);
                setDialectDriver(dialectDriver);
            }

            if (properties.containsKey("dam.show_sql")) {
                setShowSql(Boolean.parseBoolean(properties.getProperty("dam.show_sql")));
            }

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
        conBuilder.url(url);
        return this;
    }

    /**
     * Set the database username.
     * 
     * @param username the username
     * @return this Configuration for method chaining
     */
    public Configuration setUsername(String username) {
        conBuilder.username(username);
        return this;
    }

    /**
     * Set the database password.
     * 
     * @param password the password
     * @return this Configuration for method chaining
     */
    public Configuration setPassword(String password) {
        conBuilder.password(password);
        return this;
    }

    /**
     * Set the database dialect driver.
     * <p>
     * This is the recommended way to configure database support.
     * Automatically sets both the JDBC driver class and dialect name.
     * Adheres to Open-Closed Principle - add new databases by creating new
     * DialectDriver implementations without modifying this class.
     * 
     * @param dialectDriver the dialect driver (e.g., new MySQLDialectDriver())
     * @return this Configuration for method chaining
     * 
     * @example
     * 
     *          <pre>
     * {@code
     * Configuration config = new Configuration()
     *     .setDialectDriver(new MySQLDialectDriver())
     *     .setUrl("jdbc:mysql://localhost:3306/mydb")
     *     .setUsername("root")
     *     .setPassword("secret");
     * }
     * </pre>
     * 
     * @see DialectDriver
     * @see MySQLDialectDriver
     */
    public Configuration setDialectDriver(DialectDriver dialectDriver) {
        if (dialectDriver == null) {
            throw new DAMException("DialectDriver cannot be null");
        }

        // Set both driver class and dialect name from DialectDriver
        conBuilder.driverClass(dialectDriver.getDriverClass());
        this.dialectName = dialectDriver.getDialectName();

        return this;
    }

    /**
     * Add an annotated entity class.
     * 
     * @param entityClass the entity class
     * @return this Configuration for method chaining
     */
    public Configuration addAnnotatedClass(Class<?> entityClass) {
        if (builtFactory != null) {
            throw new DAMException(
                    "Cannot add classes after SessionFactory is built. " +
                            "Create a new Configuration instance.");
        }
        registeredClasses.add(entityClass);
        return this;
    }

    /**
     * Enable or disable SQL logging.
     * 
     * @param show true to show SQL, false to hide
     * @return this Configuration for method chaining
     */
    public Configuration setShowSql(boolean show) {
        showSQL = show;
        return this;
    }

    /**
     * Return a SessionFactory based on the current configuration object. The first
     * time call might cost more time to build
     * 
     * @throws DAMException if missing or pass in wrong arguments
     * @return the configured SessionFactory
     */
    SessionFactory buildSessionFactory() {

        // sessionFactory gets a snapshot of metadata, not a mutable reference
        if (null == builtFactory) {

            try {

                // 1. Build metadata registry from registered entity classes
                Map<Class<?>, EntityMetadata> metadataRegistry = new HashMap<>();
                for (Class<?> entityClass : registeredClasses) {
                    EntityMetadata metadata = new EntityMetadata(entityClass);
                    metadataRegistry.put(entityClass, metadata);
                }
                // 2. Create SessionFactory with metadata registry
                Dialect dialect = DialectFactory.createDialect(dialectName);
                builtFactory = new SessionFactoryImpl(metadataRegistry, conBuilder.build(), dialect, showSQL);

            } catch (IllegalArgumentException e) {
                throw new DAMException(e.getCause());
            }

        }
        // Apply Singleton enforcement to ensure one-one for
        // configuration-sessionFactory
        return builtFactory;
    }
}
