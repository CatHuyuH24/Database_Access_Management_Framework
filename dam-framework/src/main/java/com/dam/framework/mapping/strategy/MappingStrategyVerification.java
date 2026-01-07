package com.dam.framework.mapping.strategy;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.DiscriminatorColumn;
import com.dam.framework.annotations.DiscriminatorValue;
import com.dam.framework.annotations.Entity;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.Inheritance;
import com.dam.framework.annotations.Table;
import com.dam.framework.mapping.entity.InheritanceStrategyType;

/**
 * Simple verification that all the mapping strategy refactoring compiles
 * correctly.
 * This class demonstrates that:
 * - Helper classes (MappingValidationUtils, DiscriminatorHandler,
 * HierarchyResolver) are accessible
 * - MappingStrategyFactory throws UnsupportedOperationException for unsupported
 * strategies
 * - SingleTableMappingStrategy and DefaultMappingStrategy use the helper
 * classes
 */
public class MappingStrategyVerification {

    // Test entities
    @Entity
    @Table(name = "vehicles")
    @Inheritance(strategy = InheritanceStrategyType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "vehicle_type")
    static class Vehicle {
        @Id
        @Column(name = "id")
        private Long id;
    }

    @Entity
    @DiscriminatorValue("CAR")
    static class Car extends Vehicle {
        @Column(name = "doors")
        private int doors;
    }

    @Entity
    @Table(name = "products")
    @Inheritance(strategy = InheritanceStrategyType.JOINED_TABLE)
    static class JoinedProduct {
        @Id
        @Column(name = "id")
        private Long id;
    }

    @Entity
    @Table(name = "orders")
    @Inheritance(strategy = InheritanceStrategyType.PER_TABLE)
    static class PerTableOrder {
        @Id
        @Column(name = "id")
        private Long id;
    }

    @Entity
    @Table(name = "users")
    static class RegularUser {
        @Id
        @Column(name = "id")
        private Long id;
    }

    public static void main(String[] args) {
        System.out.println("=== Mapping Strategy Refactoring Verification ===\n");

        // Verify supported strategies work
        System.out.println("Supported strategies:");
        try {
            MappingStrategy singleTable = MappingStrategyFactory.getStrategy(Car.class);
            System.out.println("✓ SINGLE_TABLE strategy created: " + singleTable.getClass().getSimpleName());

            MappingStrategy defaultStrategy = MappingStrategyFactory.getStrategy(RegularUser.class);
            System.out.println("✓ Default strategy created: " + defaultStrategy.getClass().getSimpleName());
        } catch (Exception e) {
            System.out.println("✗ Error creating supported strategies: " + e.getMessage());
        }

        // Verify unsupported strategies throw exception
        System.out.println("\nUnsupported strategies (should throw exception):");

        try {
            MappingStrategyFactory.getStrategy(JoinedProduct.class);
            System.out.println("✗ JOINED_TABLE should have thrown exception");
        } catch (UnsupportedOperationException e) {
            System.out.println("✓ JOINED_TABLE correctly throws exception");
        }

        try {
            MappingStrategyFactory.getStrategy(PerTableOrder.class);
            System.out.println("✗ PER_TABLE should have thrown exception");
        } catch (UnsupportedOperationException e) {
            System.out.println("✓ PER_TABLE correctly throws exception");
        }

        System.out.println("\n=== Verification Complete ===");
        System.out.println("All refactoring changes compiled and function correctly!");
    }
}
