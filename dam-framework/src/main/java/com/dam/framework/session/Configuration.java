package com.dam.framework.session;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.dam.framework.connection.BasicConnectionManager;
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
    // is the builder for session factory

    // enforces single session factory per configuration (Singleton)
    private SessionFactory builtFactory;

    // NOT final - builder might be replaced (though currently isn't)
    private BasicConnectionManager.Builder conBuilder = new BasicConnectionManager.Builder();

    private final Properties properties = new Properties();

    // Entity class registry
    private final Set<Class<?>> registeredClasses = new HashSet<>();

    private boolean showSQL = false;

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

            properties.load(is);
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
        // TODO: Implement
        conBuilder.password(password);
        return this;
    }

    /**
     * Set the JDBC driver class name.
     * 
     * @param driverClass the driver class name
     * @return this Configuration for method chaining
     */
    public Configuration setDriver(String driverClass) {
        conBuilder.driverClass(driverClass);
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
        if (builtFactory != null) {
            throw new DAMException(
                    "Cannot add classes after SessionFactory is built. " +
                            "Create a new Configuration instance.");
        }
        registeredClasses.add(entityClass);
        return this;
    }

    /**
     * Scan a package for entity classes.
     * 
     * @param packageName the package to scan
     * @return this Configuration for method chaining
     */
    // public Configuration scanPackage(String packageName) {
    // // TODO: Implement
    // return this;
    // }

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
                builtFactory = new SessionFactoryImpl(metadataRegistry, conBuilder.build());

            } catch (IllegalArgumentException e) {
                throw new DAMException(e.getCause());
            }

        }
        // Apply Singleton enforcement to ensure one-one for
        // configuration-sessionFactory
        return builtFactory;

        // TODO: Add ConnectionManager and Dialect later
    }
}
