package com.dam.framework.session;

import java.util.List;
import com.dam.framework.query.Query;
import com.dam.framework.transaction.Transaction;

/**
 * The main interface for interacting with the database.
 * <p>
 * A Session represents a single unit of work with the database.
 * It provides methods for CRUD operations and query building.
 * 
 * <pre>
 * {@code
 * try (Session session = sessionFactory.openSession()) {
 *     User user = session.find(User.class, 1L);
 *     user.setName("Updated Name");
 *     session.update(user);
 * }
 * }
 * </pre>
 * 
 * <p>Sessions are not thread-safe. Each thread should obtain its own Session instance.</p>
 * 
 * @see SessionFactory
 * @see Transaction
 */
public interface Session extends AutoCloseable {
    
    /**
     * Find an entity by its primary key.
     * 
     * @param <T> the entity type
     * @param entityClass the class of the entity
     * @param id the primary key value
     * @return the found entity or null if not found
     */
    <T> T find(Class<T> entityClass, Object id);
    
    /**
     * Find all entities of the given type.
     * 
     * @param <T> the entity type
     * @param entityClass the class of the entity
     * @return list of all entities
     */
    <T> List<T> findAll(Class<T> entityClass);
    
    /**
     * Save a new entity to the database.
     * 
     * @param entity the entity to save
     */
    void save(Object entity);
    
    /**
     * Update an existing entity in the database.
     * 
     * @param entity the entity to update
     */
    void update(Object entity);
    
    /**
     * Delete an entity from the database.
     * 
     * @param entity the entity to delete
     */
    void delete(Object entity);
    
    /**
     * Create a query for the given entity type.
     * 
     * @param <T> the entity type
     * @param entityClass the class of the entity
     * @return a new Query instance
     */
    <T> Query<T> createQuery(Class<T> entityClass);
    
    /**
     * Begin a new transaction.
     * 
     * @return the Transaction object
     */
    Transaction beginTransaction();
    
    /**
     * Get the current active transaction, if any.
     * 
     * @return the active transaction or null
     */
    Transaction getTransaction();
    
    /**
     * Check if the session is still open.
     * 
     * @return true if open, false if closed
     */
    boolean isOpen();
    
    /**
     * Close the session and release resources.
     */
    @Override
    void close();
}
