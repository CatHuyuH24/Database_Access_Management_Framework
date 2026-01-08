package com.dam.demo;

import com.dam.demo.entity.User;
import com.dam.demo.entity.Product;
import com.dam.framework.config.MySQLDialectDriver;
import com.dam.framework.session.Configuration;
import com.dam.framework.session.Session;
import com.dam.framework.session.SessionFactory;
import com.dam.framework.transaction.Transaction;

/**
 * Simple Demo Application showcasing DAM Framework core features.
 * 
 * Prerequisites:
 * 1. MySQL server running on localhost:3306
 * 2. Create database: CREATE DATABASE dam_demo;
 * 3. Create tables:
 * 
 * CREATE TABLE users (
 * id BIGINT AUTO_INCREMENT PRIMARY KEY,
 * username VARCHAR(255) NOT NULL,
 * email VARCHAR(255),
 * age INT,
 * status VARCHAR(50)
 * );
 * 
 * CREATE TABLE products (
 * id BIGINT AUTO_INCREMENT PRIMARY KEY,
 * name VARCHAR(255) NOT NULL,
 * price DOUBLE
 * );
 */
public class SimpleDemoApplication {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         DAM Framework - Simple Feature Demo                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        SessionFactory sessionFactory = null;

        try {
            // 1. Initialize Framework
            System.out.println("─── 1. Framework Initialization ───");
            Configuration config = new Configuration()
                    .setDialectDriver(new MySQLDialectDriver())
                    .setUrl("jdbc:mysql://localhost:3306/dam_demo")
                    .setUsername("root")
                    .setPassword("password")
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Product.class);

            sessionFactory = config.buildSessionFactory();
            System.out.println("✓ SessionFactory created\n");

            // 2. CRUD Operations
            System.out.println("─── 2. CRUD Operations ───");
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = session.beginTransaction();

                // CREATE
                User user = new User("demo_user", "demo@example.com", 25);
                session.persist(user);
                System.out.println("✓ Created user: " + user.getUsername());

                Product product = new Product("Demo Product", 99.99);
                session.persist(product);
                System.out.println("✓ Created product: " + product.getName());

                tx.commit();
                System.out.println("✓ Transaction committed");

                // READ
                Long userId = user.getId();
                User foundUser = session.find(User.class, userId);
                System.out.println("✓ Found user with ID " + userId + ": " + foundUser.getUsername());

                // UPDATE
                Transaction tx2 = session.beginTransaction();
                foundUser.setAge(26);
                session.merge(foundUser);
                tx2.commit();
                System.out.println("✓ Updated user age to: " + foundUser.getAge());

                // DELETE
                Transaction tx3 = session.beginTransaction();
                session.remove(foundUser);
                session.remove(product);
                tx3.commit();
                System.out.println("✓ Deleted user and product\n");
            }

            // 3. Transaction Rollback
            System.out.println("─── 3. Transaction Rollback ───");
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = session.beginTransaction();
                User user = new User("temp_user", "temp@example.com", 30);
                session.persist(user);
                System.out.println("✓ Created temporary user");

                tx.rollback();
                System.out.println("✓ Rolled back transaction (user not saved)\n");
            }

            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║              ✓ All demos completed successfully!             ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("\n✗ Demo failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (sessionFactory != null) {
                try {
                    sessionFactory.close();
                    System.out.println("\n✓ SessionFactory closed");
                } catch (Exception e) {
                    System.err.println("Failed to close SessionFactory: " + e.getMessage());
                }
            }
        }
    }
}
