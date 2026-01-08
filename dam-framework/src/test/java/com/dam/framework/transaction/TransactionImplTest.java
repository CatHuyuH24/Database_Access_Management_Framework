package com.dam.framework.transaction;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dam.framework.exception.DAMException;

/**
 * Integration tests for TransactionImpl using real H2 database connection.
 */
class TransactionImplTest {

    private Connection connection;
    private TransactionImpl transaction;

    @BeforeEach
    void setUp() throws Exception {
        // Create in-memory H2 database connection
        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        // Create test table
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, data VARCHAR(50))");
        }

        connection.setAutoCommit(true); // Reset to default
        transaction = new TransactionImpl(connection);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS test_table");
            }
            connection.close();
        }
    }

    @Test
    void testBeginSetsAutoCommitFalse() throws Exception {
        transaction.begin();

        assertFalse(connection.getAutoCommit());
        assertTrue(transaction.isActive());
    }

    @Test
    void testBeginTwiceThrowsException() {
        transaction.begin();

        assertThrows(IllegalStateException.class, () -> transaction.begin());
    }

    @Test
    void testCommitSuccess() throws Exception {
        transaction.begin();

        // Insert data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO test_table VALUES (1, 'test')");
        }

        transaction.commit();

        assertTrue(connection.getAutoCommit());
        assertFalse(transaction.isActive());

        // Verify data was committed
        try (Statement stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM test_table")) {
            rs.next();
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    void testCommitWithoutBeginThrowsException() {
        assertThrows(IllegalStateException.class, () -> transaction.commit());
    }

    @Test
    void testCommitRollbackOnlyThrowsException() {
        transaction.begin();
        transaction.setRollbackOnly();

        assertThrows(DAMException.class, () -> transaction.commit());
        assertTrue(transaction.isRollbackOnly());
    }

    @Test
    void testRollbackSuccess() throws Exception {
        transaction.begin();

        // Insert data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO test_table VALUES (2, 'test2')");
        }

        transaction.rollback();

        assertTrue(connection.getAutoCommit());
        assertFalse(transaction.isActive());

        // Verify data was rolled back
        try (Statement stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM test_table")) {
            rs.next();
            assertEquals(0, rs.getInt(1));
        }
    }

    @Test
    void testRollbackIsIdempotent() throws Exception {
        transaction.begin();
        transaction.rollback();
        transaction.rollback(); // Should not throw

        assertFalse(transaction.isActive());
    }

    @Test
    void testRollbackWithoutBeginDoesNothing() {
        transaction.rollback(); // Should not throw
        assertFalse(transaction.isActive());
    }

    @Test
    void testSetRollbackOnly() {
        transaction.begin();
        transaction.setRollbackOnly();

        assertTrue(transaction.isRollbackOnly());
    }

    @Test
    void testMultipleOperationsInTransaction() throws Exception {
        transaction.begin();

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO test_table VALUES (10, 'value1')");
            stmt.executeUpdate("INSERT INTO test_table VALUES (11, 'value2')");
            stmt.executeUpdate("INSERT INTO test_table VALUES (12, 'value3')");
        }

        transaction.commit();

        // Verify all data was committed
        try (Statement stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM test_table")) {
            rs.next();
            assertEquals(3, rs.getInt(1));
        }
    }

    @Test
    void testRollbackAfterError() throws Exception {
        transaction.begin();

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO test_table VALUES (20, 'value1')");
            stmt.executeUpdate("INSERT INTO test_table VALUES (21, 'value2')");
        }

        transaction.rollback();

        // Verify no data was committed
        try (Statement stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM test_table")) {
            rs.next();
            assertEquals(0, rs.getInt(1));
        }
    }
}
