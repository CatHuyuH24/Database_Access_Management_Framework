package com.dam.framework.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dam.framework.exception.DAMException;

/**
 * Default implementation of Transaction interface.
 * <p>
 * Manages JDBC transaction lifecycle by controlling Connection auto-commit
 * state.
 * Follows ACID properties through JDBC's transaction support.
 * 
 * <p>
 * <b>Design Pattern:</b> Command Pattern
 * </p>
 * <p>
 * Encapsulates transaction operations (begin/commit/rollback) as commands.
 * </p>
 * 
 * <p>
 * <b>Thread Safety:</b> NOT thread-safe. Each transaction is bound to a single
 * Session which is also not thread-safe.
 * </p>
 */
public class TransactionImpl implements Transaction {

    private static final Logger logger = LoggerFactory.getLogger(TransactionImpl.class);

    private final Connection connection;
    private boolean active;
    private boolean rollbackOnly;

    /**
     * Create a new Transaction for the given connection.
     * 
     * @param connection the JDBC connection
     */
    public TransactionImpl(Connection connection) {
        this.connection = connection;
        this.active = false;
        this.rollbackOnly = false;
    }

    @Override
    public void begin() {
        if (active) {
            throw new IllegalStateException("Transaction is already active");
        }

        try {
            connection.setAutoCommit(false);
            active = true;
            logger.debug("Transaction started");
        } catch (SQLException e) {
            throw new DAMException("Failed to begin transaction", e);
        }
    }

    @Override
    public void commit() {
        validateActive("commit");

        if (rollbackOnly) {
            throw new DAMException(
                    "Cannot commit transaction marked for rollback-only. " +
                            "Call rollback() instead.");
        }

        try {
            connection.commit();
            connection.setAutoCommit(true);
            active = false;
            logger.debug("Transaction committed");
        } catch (SQLException e) {
            // On commit failure, try to rollback
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException rollbackEx) {
                logger.error("Failed to rollback after commit failure", rollbackEx);
            }
            active = false;
            throw new DAMException("Failed to commit transaction", e);
        }
    }

    @Override
    public void rollback() {
        // Idempotent - safe to call multiple times
        if (!active) {
            logger.debug("Transaction not active, rollback ignored");
            return;
        }

        try {
            connection.rollback();
            connection.setAutoCommit(true);
            active = false;
            logger.debug("Transaction rolled back");
        } catch (SQLException e) {
            // Still mark as inactive even if rollback fails
            active = false;
            throw new DAMException("Failed to rollback transaction", e);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Mark this transaction as rollback-only.
     * <p>
     * Used when an error occurs that should prevent commit.
     * This method is public for Session to call.
     */
    public void setRollbackOnly() {
        if (active) {
            rollbackOnly = true;
            logger.warn("Transaction marked as rollback-only");
        }
    }

    /**
     * Check if transaction is marked rollback-only.
     * 
     * @return true if rollback-only
     */
    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    /**
     * Validate transaction is active.
     * 
     * @param operation the operation name (for error message)
     * @throws IllegalStateException if transaction is not active
     */
    private void validateActive(String operation) {
        if (!active) {
            throw new IllegalStateException(
                    "Cannot " + operation + " - transaction is not active. " +
                            "Call beginTransaction() first.");
        }
    }
}
