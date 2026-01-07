package com.dam.framework.mapping;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.DiscriminatorColumn;
import com.dam.framework.annotations.DiscriminatorValue;
import com.dam.framework.annotations.Entity;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.Inheritance;
import com.dam.framework.annotations.MappedSuperclass;
import com.dam.framework.annotations.Table;
import com.dam.framework.mapping.entity.InheritanceStrategyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityMetadataTest {

    @Test
    @DisplayName("Should scan fields from current class and its MappedSuperclasses")
    void testScanFieldsInheritance() {
        // When
        EntityMetadata metadata = new EntityMetadata(User.class);
        List<ColumnMetadata> columns = metadata.getColumns();

        // Then
        assertEquals(4, columns.size(), "Should have exactly 4 columns");

        assertNotNull(metadata.getIdColumn());
        assertEquals("id", metadata.getIdColumn().columnName());

        assertTrue(hasColumn(columns, "updated_at"));
        assertTrue(hasColumn(columns, "username"));
    }

    @Test
    @DisplayName("Should prioritize child class column over parent class when same name")
    void testColumnOverride() {
        // When
        EntityMetadata metadata = new EntityMetadata(User.class);
        List<ColumnMetadata> columns = metadata.getColumns();

        ColumnMetadata createdAtCol = columns.stream()
                .filter(c -> c.columnName().equals("created_at"))
                .findFirst()
                .orElseThrow();

        // Then
        assertEquals(User.class, createdAtCol.field().getDeclaringClass(),
                "The column 'created_at' should belong to the child class User (Override)");
    }

    @Test
    @DisplayName("Should ignore parent classes that are not marked with @MappedSuperclass")
    void testIgnorePlainParentClasses() {
        class PlainParent {

            @Column(name = "secret")
            private String secret;
        }

        @Table(name = "child")
        @Entity
        class Child extends PlainParent {

            @Id
            @Column(name = "id")
            private Long id;
        }

        // When
        EntityMetadata metadata = new EntityMetadata(Child.class);

        // Then
        assertEquals(1, metadata.getColumns().size());
        assertFalse(hasColumn(metadata.getColumns(), "secret"));
    }

    @Test
    @DisplayName("SingleTable: Should collect all fields from parent Entities")
    void testSingleTableFieldCollection() {
        // When
        EntityMetadata metadata = new EntityMetadata(Truck.class);
        List<ColumnMetadata> columns = metadata.getColumns();

        // Then:
        assertEquals(4, columns.size(), "Truck should have 4 columns inherited from parent Entities");

        assertTrue(hasColumn(columns, "id"));
        assertTrue(hasColumn(columns, "brand"));
        assertTrue(hasColumn(columns, "doors"));
        assertTrue(hasColumn(columns, "payload"));
    }

    @Test
    @DisplayName("SingleTable: Should correctly identify discriminator column from root entity")
    void testSingleTableDiscriminator() {
        // When
        EntityMetadata metadata = new EntityMetadata(Car.class);

        // Then
        assertEquals("v_type", metadata.getDiscriminatorColumn(),
                "Discriminator column should be inherited from the root entity (Vehicle)");
    }

    @Test
    @DisplayName("SingleTable: Table name should always be the root entity table name")
    void testSingleTableTableName() {
        // When
        EntityMetadata carMeta = new EntityMetadata(Car.class);
        EntityMetadata truckMeta = new EntityMetadata(Truck.class);

        assertEquals("vehicles", carMeta.getTableName());
        assertEquals("vehicles", truckMeta.getTableName());
    }

    private boolean hasColumn(List<ColumnMetadata> columns, String name) {
        return columns.stream().anyMatch(c -> c.columnName().equalsIgnoreCase(name));
    }

    @Entity
    @Table(name = "vehicles")
    @Inheritance(strategy = InheritanceStrategyType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "v_type")
    @MappedSuperclass
    static class Vehicle {

        @Id
        @Column(name = "id")
        private Long id;

        @Column(name = "brand")
        private String brand;
    }

    @Entity
    @DiscriminatorValue("CAR")
    @MappedSuperclass
    static class Car extends Vehicle {

        @Column(name = "doors")
        private int doors;
    }

    @Entity
    @DiscriminatorValue("TRUCK")
    static class Truck extends Car {

        @Column(name = "payload")
        private Double payload;
    }

    @MappedSuperclass
    static
    class BaseAudit {

        @Column(name = "created_at")
        private String createdAt;

        @Column(name = "updated_at")
        private String updatedAt;
    }

    @MappedSuperclass
    static
    class BaseUser extends BaseAudit {

        @Id
        @Column(name = "id")
        private Long id;
    }

    @Table(name = "users")
    @Entity
    static
    class User extends BaseUser {

        @Column(name = "username")
        private String username;

        @Column(name = "created_at")
        private String createdAtOverride;
    }
}