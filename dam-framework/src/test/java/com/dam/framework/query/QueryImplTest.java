package com.dam.framework.query;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.Table;
import com.dam.framework.dialect.Dialect;
import com.dam.framework.dialect.Dialect.PaginationFragment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class QueryImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private Dialect mockDialect;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockDialect = mock(Dialect.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockDialect.getPaginationFragment(any(), any()))
                .thenReturn(new PaginationFragment("", List.of()));
    }

    @Table(name = "users")
    public static class User {

        @Column(name = "user_id")
        private Long id;
        private String name;

        public User() {
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    void testBuildSqlBasic() throws SQLException {
        // Given
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(0);
        when(mockResultSet.next()).thenReturn(false);

        QueryImpl<User> query = new QueryImpl<>(User.class, mockConnection, mockDialect);
        query.select("user_id", "name")
                .where("age > ?", 18)
                .orderBy("name", Order.ASC);

        // When
        query.getResultList();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());

        // Then
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("SELECT user_id, name"));
        assertTrue(sql.contains("FROM users"));
        assertTrue(sql.contains("WHERE age > ?"));
        assertTrue(sql.contains("ORDER BY name ASC"));
    }

    @Test
    void testComplexWhereClause() throws SQLException {
        // Given
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(0);
        when(mockResultSet.next()).thenReturn(false);

        QueryImpl<User> query = new QueryImpl<>(User.class, mockConnection, mockDialect);

        query.where("status = ?", "ACTIVE")
                .and("age >= ?", 20)
                .or("role = ?", "ADMIN");
        // When
        query.getResultList();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());

        // Then
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("WHERE status = ? AND age >= ? OR role = ?"));
        verify(mockStatement).setObject(1, "ACTIVE");
        verify(mockStatement).setObject(2, 20);
        verify(mockStatement).setObject(3, "ADMIN");
    }

    @Test
    void testMappingResultSetToEntity() throws SQLException {
        // Given
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(2);

        when(mockMetaData.getColumnLabel(1)).thenReturn("user_id");
        when(mockMetaData.getColumnLabel(2)).thenReturn("name");

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getObject(1)).thenReturn(100L);
        when(mockResultSet.getObject(2)).thenReturn("John Doe");

        // When
        QueryImpl<User> query = new QueryImpl<>(User.class, mockConnection, mockDialect);
        List<User> results = query.getResultList();

        // Then
        assertEquals(1, results.size());
        User user = results.getFirst();
        assertEquals(100L, user.getId());
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testPaginationInteraction() throws SQLException {
        // Given
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(0);
        when(mockResultSet.next()).thenReturn(false);

        when(mockDialect.getPaginationFragment(10, 5))
                .thenReturn(new PaginationFragment(" LIMIT ?, ?", List.of(5, 10)));

        QueryImpl<User> query = new QueryImpl<>(User.class, mockConnection, mockDialect);
        query.limit(10).offset(5);

        // When
        query.getResultList();

        // Then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        var sqlStatement = sqlCaptor.getValue();
        assertTrue(sqlStatement.endsWith(" LIMIT ?, ?"));
        verify(mockStatement).setObject(1, 5);
        verify(mockStatement).setObject(2, 10);
    }

    @Test
    void testGroupByAndHavingClause() throws SQLException {
        // Given
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(0);
        when(mockResultSet.next()).thenReturn(false);

        QueryImpl<User> query = new QueryImpl<>(User.class, mockConnection, mockDialect);

        query.select("name", "COUNT(*) as total")
                .where("status = ?", "ACTIVE")
                .groupBy("name")
                .having("COUNT(*) > ?", 1)
                .orderBy("total", Order.DESC);

        // When
        query.getResultList();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());

        // Then
        String sql = sqlCaptor.getValue();

        assertTrue(sql.indexOf("WHERE") < sql.indexOf("GROUP BY"), "WHERE must be before GROUP BY");
        assertTrue(sql.indexOf("GROUP BY") < sql.indexOf("HAVING"), "GROUP BY must be before HAVING");
        assertTrue(sql.indexOf("HAVING") < sql.indexOf("ORDER BY"), "HAVING must be before ORDER BY");

        assertTrue(sql.contains("GROUP BY name"));
        assertTrue(sql.contains("HAVING COUNT(*) > ?"));

        verify(mockStatement).setObject(1, "ACTIVE");
        verify(mockStatement).setObject(2, 1);
    }

    @Test
    void testFullQueryIntegration() throws SQLException {
        // Given
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(0);
        when(mockResultSet.next()).thenReturn(false);

        when(mockDialect.getPaginationFragment(10, 20))
                .thenReturn(new PaginationFragment(" LIMIT ? OFFSET ?", List.of(10, 20)));

        QueryImpl<User> query = new QueryImpl<>(User.class, mockConnection, mockDialect);

        query.where("department = ?", "IT")
                .groupBy("role")
                .having("salary > ?", 5000)
                .limit(10)
                .offset(20);

        // When
        query.getResultList();

        // Then
        verify(mockStatement).setObject(1, "IT");    // WHERE param
        verify(mockStatement).setObject(2, 5000);  // HAVING param
        verify(mockStatement).setObject(3, 10);    // LIMIT param
        verify(mockStatement).setObject(4, 20);    // OFFSET param
    }

    @Test
    void testSqlClauseOrderImmutability() throws SQLException {
        // Given
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(0);
        when(mockResultSet.next()).thenReturn(false);

        QueryImpl<User> query = new QueryImpl<>(User.class, mockConnection, mockDialect);

        query.orderBy("total", Order.DESC)
                .having("COUNT(*) > ?", 1)
                .groupBy("name")
                .where("status = ?", "ACTIVE")
                .select("name", "COUNT(*) as total");

        // When
        query.getResultList();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());

        // Then
        String sql = sqlCaptor.getValue();

        int selectIdx = sql.indexOf("SELECT");
        int fromIdx   = sql.indexOf("FROM");
        int whereIdx  = sql.indexOf("WHERE");
        int groupIdx  = sql.indexOf("GROUP BY");
        int havingIdx = sql.indexOf("HAVING");
        int orderIdx  = sql.indexOf("ORDER BY");

        assertTrue(selectIdx < fromIdx, "SELECT must be before FROM");
        assertTrue(fromIdx < whereIdx, "FROM must be before WHERE");
        assertTrue(whereIdx < groupIdx, "WHERE must be before GROUP BY");
        assertTrue(groupIdx < havingIdx, "GROUP BY must be before HAVING");
        assertTrue(havingIdx < orderIdx, "HAVING must be before ORDER BY");

        org.mockito.InOrder inOrder = inOrder(mockStatement);

        inOrder.verify(mockStatement).setObject(1, "ACTIVE");
        inOrder.verify(mockStatement).setObject(2, 1);
        inOrder.verify(mockStatement).executeQuery();
    }
}