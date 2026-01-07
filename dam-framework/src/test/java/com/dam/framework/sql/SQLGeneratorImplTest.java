package com.dam.framework.sql;

import com.dam.framework.mapping.ColumnMetadata;
import com.dam.framework.mapping.EntityMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SQLGeneratorImplTest {

    private SQLGeneratorImpl sqlGenerator;
    private EntityMetadata mockMetadata;

    @BeforeEach
    void setUp() {
        sqlGenerator = new SQLGeneratorImpl();
        mockMetadata = mock(EntityMetadata.class);

        ColumnMetadata idColumn = mock(ColumnMetadata.class);
        when(idColumn.columnName()).thenReturn("user_id");

        ColumnMetadata nameColumn = mock(ColumnMetadata.class);
        when(nameColumn.columnName()).thenReturn("username");

        ColumnMetadata emailColumn = mock(ColumnMetadata.class);
        when(emailColumn.columnName()).thenReturn("email");

        when(mockMetadata.getTableName()).thenReturn("users");
        when(mockMetadata.getSchema()).thenReturn("hr");
        when(mockMetadata.getIdColumn()).thenReturn(idColumn);
        when(mockMetadata.getColumns()).thenReturn(List.of(idColumn, nameColumn, emailColumn));
    }

    @Test
    void testGenerateInsert() {
        // When
        String sql = sqlGenerator.generateInsert(mockMetadata);

        // Then
        assertEquals("INSERT INTO hr.users (user_id, username, email) VALUES (?, ?, ?)", sql);
    }

    @Test
    void testGenerateSelect() {
        String sql = sqlGenerator.generateSelect(mockMetadata);

        assertEquals("SELECT * FROM hr.users", sql);
    }

    @Test
    void testGenerateSelectById() {
        String sql = sqlGenerator.generateSelectById(mockMetadata);

        assertEquals("SELECT * FROM hr.users WHERE user_id = ?", sql);
    }

    @Test
    void testGenerateUpdate() {
        // When
        String sql = sqlGenerator.generateUpdate(mockMetadata);

        // Then
        assertEquals("UPDATE hr.users SET username = ?, email = ? WHERE user_id = ?", sql);
        assertFalse(sql.contains("SET user_id = ?"),
                "Update statement should not update the ID column");
    }

    @Test
    void testGenerateDelete() {
        // When
        String sql = sqlGenerator.generateDelete(mockMetadata);

        // Then
        assertEquals("DELETE FROM hr.users WHERE user_id = ?", sql);
    }

    @Test
    void testGetFullTableNameWithDefaultSchema() {
        // Given
        when(mockMetadata.getSchema()).thenReturn("public");
        // When
        String sql = sqlGenerator.generateSelect(mockMetadata);
        // Then
        assertEquals("SELECT * FROM users", sql);
    }

    @Test
    @DisplayName("SingleTable: generateInsert should include discriminator column and hardcoded value")
    void testGenerateInsertSingleTable() {
        // Given
        when(mockMetadata.getDiscriminatorColumn()).thenReturn("v_type");
        when(mockMetadata.getDiscriminatorValue()).thenReturn("CAR");

        // When
        String sql = sqlGenerator.generateInsert(mockMetadata);

        // Then
        assertEquals("INSERT INTO hr.users (user_id, username, email, v_type) VALUES (?, ?, ?, 'CAR')", sql);
    }

    @Test
    @DisplayName("SingleTable: generateSelect should include WHERE clause for discriminator")
    void testGenerateSelectSingleTable() {
        // Given
        when(mockMetadata.getDiscriminatorColumn()).thenReturn("v_type");
        when(mockMetadata.getDiscriminatorValue()).thenReturn("CAR");

        // When
        String sql = sqlGenerator.generateSelect(mockMetadata);

        // Then
        assertEquals("SELECT * FROM hr.users WHERE v_type = 'CAR'", sql);
    }

    @Test
    @DisplayName("SingleTable: generateSelectById should include AND condition for discriminator")
    void testGenerateSelectByIdSingleTable() {
        // Given
        when(mockMetadata.getDiscriminatorColumn()).thenReturn("v_type");
        when(mockMetadata.getDiscriminatorValue()).thenReturn("CAR");

        // When
        String sql = sqlGenerator.generateSelectById(mockMetadata);

        // Then
        assertEquals("SELECT * FROM hr.users WHERE user_id = ? AND v_type = 'CAR'", sql);
    }

    @Test
    @DisplayName("SingleTable: generateUpdate should NOT include discriminator in SET clause but might include in WHERE")
    void testGenerateUpdateSingleTable() {
        // Given
        when(mockMetadata.getDiscriminatorColumn()).thenReturn("v_type");
        when(mockMetadata.getDiscriminatorValue()).thenReturn("CAR");

        // When
        String sql = sqlGenerator.generateUpdate(mockMetadata);

        // Then
        assertEquals("UPDATE hr.users SET username = ?, email = ? WHERE user_id = ?", sql);
    }
}