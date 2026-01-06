package com.dam.framework.mapping;

import java.sql.Connection;

import com.dam.framework.dialect.Dialect;

/**
 * No-operation ID generator for manual ID assignment (NONE strategy).
 * <p>
 * Implements the Null Object Pattern to handle cases where the user
 * wants to manually set the ID value before persisting.
 * <p>
 * This generator does nothing - it's the responsibility of the application
 * to set the ID value before calling persist().
 * 
 * <p>
 * <b>Usage:</b>
 * </p>
 * 
 * <pre>
 * {
 *     &#64;code
 *     &#64;Id
 *     // No @GeneratedValue annotation, or:
 *     @GeneratedValue(strategy = GenerationType.NONE)
 *     private String id;
 * 
 *     // Application must set ID before persist:
 *     entity.setId("custom-id-123");
 *     session.persist(entity);
 * }
 * </pre>
 * 
 * @see com.dam.framework.annotations.GenerationType#NONE
 */
public class NoOpGenerator implements IdGenerator {

    /**
     * Singleton instance - NoOpGenerator is stateless and can be shared.
     * 
     */
    public static final NoOpGenerator INSTANCE = new NoOpGenerator();

    /**
     * Private constructor to enforce singleton pattern.
     * Use {@link #INSTANCE} instead.
     */
    private NoOpGenerator() {
        // Private to enforce singleton
    }

    /**
     * Does not generate any ID value.
     * <p>
     * For NONE strategy, the application is responsible for setting the ID.
     * 
     * @param connection ignored - not needed for manual ID assignment
     * @param dialect    ignored - not needed for manual ID assignment
     * @param metadata   ignored - not needed for manual ID assignment
     * @return null always - ID must be set manually by application
     */
    @Override
    public Object generate(Connection connection, Dialect dialect, EntityMetadata metadata) {
        // No-op: ID must be manually assigned by user
        return null;
    }

    /**
     * Returns false since there's no post-insert ID retrieval needed.
     * <p>
     * If the ID is null after persist() with NONE strategy, it means
     * the user forgot to set the ID, which will be caught by validation.
     * 
     * @return false - no database-generated ID to retrieve
     */
    @Override
    public boolean isPostInsertGenerator() {
        return false;
    }
}
