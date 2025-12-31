package com.dam.framework.session;

/**
 * Factory for creating Session instances.
 * <p>
 * SessionFactory is typically created once during application startup
 * and used to obtain Session instances throughout the application lifecycle.
 * 
 * <pre>
 * {@code
 * Configuration config = new Configuration()
 *         .setUrl("jdbc:mysql://localhost:3306/mydb")
 *         .setUsername("root")
 *         .setPassword("password");
 * 
 * SessionFactory factory = config.buildSessionFactory();
 * 
 * try (Session session = factory.openSession()) {
 *     // Use session
 * }
 * 
 * // On application shutdown
 * factory.close();
 * }
 * </pre>
 * 
 * @see Session
 * @see com.dam.framework.session.Configuration
 */
public interface SessionFactory extends AutoCloseable {

    /**
     * Open a new Session.
     * <p>
     * The caller is responsible for closing the session when done.
     * 
     * @return a new Session instance
     */
    public Session openSession();

    /**
     * Get the current session bound to the context.
     * <p>
     * If no session exists, a new one is created and bound.
     * 
     * @return the current Session
     */
    public Session getCurrentSession();

    /**
     * Check if the factory is still open.
     * 
     * @return true if open, false if closed
     */
    public boolean isOpen();

    /**
     * Close the factory and release all resources.
     * <p>
     * This will close the connection pool and all active sessions.
     */
    @Override
    public void close();

    public int getOpenSessionCount();

}
