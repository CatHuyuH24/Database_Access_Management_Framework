package com.dam.framework.mapping;

import static org.junit.jupiter.api.Assertions.*;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.DiscriminatorColumn;
import com.dam.framework.annotations.DiscriminatorValue;
import com.dam.framework.annotations.Entity;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.Inheritance;
import com.dam.framework.annotations.Table;
import com.dam.framework.mapping.entity.InheritanceStrategyType;
import com.dam.framework.mapping.strategy.SingleTableMappingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class SingleTableMappingStrategyTest {

    private SingleTableMappingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SingleTableMappingStrategy();
    }

    @Test
    @DisplayName("Should return root table name for all subclasses")
    void testGetTableName() {
        // When
        String carTable = strategy.getTableName(Car.class);
        String truckTable = strategy.getTableName(Truck.class);
        String vehicleTable = strategy.getTableName(Vehicle.class);

        // Then
        assertEquals("transportation", carTable);
        assertEquals("transportation", truckTable);
        assertEquals("transportation", vehicleTable);
    }

    @Test
    @DisplayName("Should collect fields from entire hierarchy (Flattening)")
    void testMapAttributesFlattening() {
        // When
        EntityMetadata metadata = new EntityMetadata(Car.class);
        strategy.mapAttributes(metadata, Car.class);

        List<ColumnMetadata> columns = metadata.getColumns();

        // Then
        assertEquals(3, columns.size());

        assertTrue(columns.stream().anyMatch(c -> c.columnName().equals("id")));
        assertTrue(columns.stream().anyMatch(c -> c.columnName().equals("brand")));
        assertTrue(columns.stream().anyMatch(c -> c.columnName().equals("door_count")));
    }

    @Test
    @DisplayName("Should handle discriminator column and explicit value")
    void testDiscriminatorExplicitValue() {
        // When
        EntityMetadata metadata = new EntityMetadata(Car.class);
        strategy.mapAttributes(metadata, Car.class);

        // Then
        assertEquals("vehicle_type", metadata.getDiscriminatorColumn());
        assertEquals("CAR_TYPE", metadata.getDiscriminatorValue());
    }

    @Test
    @DisplayName("Should use class name as default discriminator value when missing annotation")
    void testDiscriminatorDefaultValue() {
        // When
        EntityMetadata metadata = new EntityMetadata(Truck.class);
        strategy.mapAttributes(metadata, Truck.class);

        // Then
        assertEquals("vehicle_type", metadata.getDiscriminatorColumn());
        assertEquals("Truck", metadata.getDiscriminatorValue());
    }

    @Test
    @DisplayName("Should find correctly idColumn even if it's in root class")
    void testIdMapping() {
        // When
        EntityMetadata metadata = new EntityMetadata(Car.class);
        strategy.mapAttributes(metadata, Car.class);

        // Then
        assertNotNull(metadata.getIdColumn());
        assertEquals("id", metadata.getIdColumn().columnName());
        assertEquals(Vehicle.class, metadata.getIdColumn().field().getDeclaringClass());
    }

    @Entity
    @Table(name = "transportation")
    @Inheritance(strategy = InheritanceStrategyType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "vehicle_type")
    static class Vehicle {

        @Id
        @Column(name = "id")
        private Long id;

        @Column(name = "brand")
        private String brand;
    }

    @Entity
    @DiscriminatorValue("CAR_TYPE")
    static class Car extends Vehicle {

        @Column(name = "door_count")
        private int doorCount;
    }

    @Entity
    static class Truck extends Vehicle {

        @Column(name = "load_capacity")
        private double loadCapacity;
    }
}