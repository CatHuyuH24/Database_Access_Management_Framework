package com.dam.demo.sprint2;

import java.lang.reflect.Field;

import com.dam.demo.entity.Order;
import com.dam.demo.entity.Product;
import com.dam.demo.entity.Session;
import com.dam.framework.annotations.GeneratedValue;
import com.dam.framework.annotations.GenerationType;

/**
 * Demo class showcasing different ID generation strategies.
 * <p>
 * This demonstrates the annotation-based configuration for:
 * <ul>
 * <li>IDENTITY strategy (Product) - database auto-increment</li>
 * <li>SEQUENCE strategy (Order) - database sequences (PostgreSQL/Oracle)</li>
 * <li>UUID strategy (Session) - application-generated UUIDs</li>
 * </ul>
 * 
 * <h3>Note:</h3>
 * This is a standalone demo showing annotation configuration.
 * For actual database persistence, configure dam.properties and create tables.
 */
public class IdGenerationDemo {

  public static void main(String[] args) {
    System.out.println("╔════════════════════════════════════════════════════════════════╗");
    System.out.println("║     DAM Framework - ID Generation Strategies Demo             ║");
    System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

    // Demo 1: IDENTITY strategy
    demoIdentityStrategy();

    // Demo 2: SEQUENCE strategy
    demoSequenceStrategy();

    // Demo 3: UUID strategy
    demoUUIDStrategy();

    // Summary
    printSummary();
  }

  /**
   * Demo IDENTITY generation strategy.
   */
  private static void demoIdentityStrategy() {
    System.out.println("┌─ IDENTITY Strategy ─────────────────────────────────────────┐");
    System.out.println("│ Database auto-generates ID using AUTO_INCREMENT/IDENTITY    │");
    System.out.println("│ Supported: MySQL, SQL Server, PostgreSQL                    │");
    System.out.println("└──────────────────────────────────────────────────────────────┘");

    // Create entity
    Product product = new Product("Laptop", 999.99);
    System.out.println("\n📦 Entity created: " + product);

    // Show annotation
    showGenerationStrategy(Product.class);

    System.out.println("\n💡 How it works:");
    System.out.println("   1. Entity persisted WITHOUT setting ID");
    System.out.println("   2. Database generates ID during INSERT");
    System.out.println("   3. Framework retrieves generated ID via JDBC");
    System.out.println("   4. ID set back to entity object\n");
  }

  /**
   * Demo SEQUENCE generation strategy.
   */
  private static void demoSequenceStrategy() {
    System.out.println("┌─ SEQUENCE Strategy ─────────────────────────────────────────┐");
    System.out.println("│ Framework queries database sequence for next value          │");
    System.out.println("│ Supported: PostgreSQL, Oracle, SQL Server 2012+             │");
    System.out.println("│ NOT supported: MySQL                                        │");
    System.out.println("└──────────────────────────────────────────────────────────────┘");

    // Create entity
    Order order = new Order("ORD-001", "John Doe", 1500.00);
    System.out.println("\n📦 Entity created: " + order);

    // Show annotation
    showGenerationStrategy(Order.class);

    System.out.println("\n💡 How it works:");
    System.out.println("   1. Framework executes: SELECT nextval('order_id_seq')");
    System.out.println("   2. ID set to entity BEFORE insert");
    System.out.println("   3. INSERT includes the generated ID");
    System.out.println("   4. No need to retrieve ID after insert\n");
  }

  /**
   * Demo UUID generation strategy.
   */
  private static void demoUUIDStrategy() {
    System.out.println("┌─ UUID Strategy ─────────────────────────────────────────────┐");
    System.out.println("│ Framework generates UUID in application (Java code)         │");
    System.out.println("│ Supported: ALL databases (100% database-independent)        │");
    System.out.println("└──────────────────────────────────────────────────────────────┘");

    // Create entity
    Session session = new Session(1L, "abc123token");
    System.out.println("\n📦 Entity created: " + session);

    // Show annotation
    showGenerationStrategy(Session.class);

    System.out.println("\n💡 How it works:");
    System.out.println("   1. Framework generates: UUID.randomUUID().toString()");
    System.out.println("   2. Example: '550e8400-e29b-41d4-a716-446655440000'");
    System.out.println("   3. ID set to entity BEFORE insert");
    System.out.println("   4. INSERT includes the UUID");
    System.out.println("   5. ✅ Globally unique, safe for distributed systems\n");
  }

  /**
   * Show annotation configuration for a class.
   */
  private static void showGenerationStrategy(Class<?> entityClass) {
    try {
      Field idField = entityClass.getDeclaredField("id");
      if (idField.isAnnotationPresent(GeneratedValue.class)) {
        GeneratedValue gen = idField.getAnnotation(GeneratedValue.class);
        GenerationType strategy = gen.strategy();

        System.out.println("\n📝 Annotation configuration:");
        System.out.println("   @Id");
        System.out.println("   @GeneratedValue(strategy = GenerationType." + strategy + ")");
        System.out.println("   private " + idField.getType().getSimpleName() + " id;");
      }
    } catch (NoSuchFieldException e) {
      System.err.println("⚠️  Could not find 'id' field");
    }
  }

  /**
   * Print summary and comparison.
   */
  private static void printSummary() {
    System.out.println("╔════════════════════════════════════════════════════════════════╗");
    System.out.println("║                      Strategy Comparison                      ║");
    System.out.println("╠════════════════════════════════════════════════════════════════╣");
    System.out.println("║ Strategy  │ When ID Generated │ Database Required              ║");
    System.out.println("╟───────────┼───────────────────┼────────────────────────────────╢");
    System.out.println("║ IDENTITY  │ AFTER insert      │ MySQL, PostgreSQL, SQL Server  ║");
    System.out.println("║ SEQUENCE  │ BEFORE insert     │ PostgreSQL, Oracle             ║");
    System.out.println("║ UUID      │ BEFORE insert     │ ANY (database-independent)     ║");
    System.out.println("╚═══════════╧═══════════════════╧════════════════════════════════╝");

    System.out.println("\n✅ Demo completed successfully!");
    System.out.println("\n📚 For full documentation, see: docs/ID_GENERATION_GUIDE.md");
  }
}
