package com.dam.framework.session;

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
 * <p>
 * Sessions are not thread-safe. Each thread should obtain its own Session
 * instance.
 * </p>
 * 
 * @see SessionFactory
 * @see Transaction
 */
public interface Session extends AutoCloseable {

    <T> void persist(T entity);

    <T> T find(Class<T> entityClass, Object id);

    <T> T merge(T entity); // Update or insert

    void remove(Object entity);

    <T> Query<T> createQuery(Class<T> resultClass);

    Transaction beginTransaction();

    void flush(); // Force synchronization

}
