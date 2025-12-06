package com.dam.framework.transaction;

/**
 * Interface for managing database transactions.
 * <p>
 * Transactions ensure that a group of operations are executed atomically.
 * 
 * <pre>
 * {@code
 * Transaction tx = session.beginTransaction();
 * try {
 *     session.save(entity1);
 *     session.save(entity2);
 *     tx.commit();
 * } catch (Exception e) {
 *     tx.rollback();
 *     throw e;
 * }
 * }
 * </pre>
 * 
 * @see com.dam.framework.session.Session#beginTransaction()
 */
public interface Transaction {
    
    /**
     * Begin the transaction.
     */
    void begin();
    
    /**
     * Commit the transaction, making all changes permanent.
     */
    void commit();
    
    /**
     * Rollback the transaction, undoing all changes.
     */
    void rollback();
    
    /**
     * Check if the transaction is currently active.
     * 
     * @return true if active, false otherwise
     */
    boolean isActive();
}
