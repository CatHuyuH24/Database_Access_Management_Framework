package com.dam.demo;

import com.dam.demo.entity.User;
import com.dam.demo.entity.Product;
import com.dam.demo.entity.Order;
import com.dam.demo.entity.Session;
import com.dam.framework.config.MySQLDialectDriver;
import com.dam.framework.session.Configuration;
import com.dam.framework.session.SessionFactory;
import com.dam.framework.transaction.Transaction;
import com.dam.framework.query.Query;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive Demo Application for DAM Framework
 * Navigate using number keys to explore different features.
 */
public class InteractiveDemoApplication {

    private static SessionFactory sessionFactory;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        try {
            initializeFramework();
            showMainMenu();
        } catch (Exception e) {
            System.err.println("\n❌ Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private static void initializeFramework() {
        printBanner();
        System.out.println("Initializing DAM Framework...");

        try {
            // Wait a bit for MySQL to be ready
            Thread.sleep(2000);

            Configuration config = new Configuration()
                    .setDialectDriver(new MySQLDialectDriver())
                    .setUrl("jdbc:mysql://localhost:3306/dam_demo")
                    .setUsername("root")
                    .setPassword("rootpassword")
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(Session.class);

            sessionFactory = config.buildSessionFactory();
            System.out.println("Framework initialized successfully!\n");

        } catch (Exception e) {
            System.err.println("Failed to initialize: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void showMainMenu() {
        while (true) {
            printSeparator();
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              DAM FRAMEWORK - MAIN MENU                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("  [1] User Management");
            System.out.println("  [2] Product Management");
            System.out.println("  [3] Demo All Features");
            System.out.println("  [4] Query & Search");
            System.out.println("  [5] Transaction Examples");
            System.out.println("  [6] View All Data");
            System.out.println("  [0] Exit");
            System.out.println();
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    userManagementMenu();
                    break;
                case "2":
                    productManagementMenu();
                    break;
                case "3":
                    demoAllFeatures();
                    break;
                case "4":
                    queryMenu();
                    break;
                case "5":
                    transactionExamples();
                    break;
                case "6":
                    viewAllData();
                    break;
                case "0":
                    System.out.println("\nGoodbye!");
                    return;
                default:
                    System.out.println("\nInvalid option. Try again.");
            }
        }
    }

    private static void userManagementMenu() {
        while (true) {
            printSeparator();
            System.out.println("═══  USER MANAGEMENT ═══");
            System.out.println("[1] Create User");
            System.out.println("[2] List All Users");
            System.out.println("[3] Update User");
            System.out.println("[4] Delete User");
            System.out.println("[0] Back to Main Menu");
            System.out.print("\n Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createUser();
                    break;
                case "2":
                    listAllUsers();
                    break;
                case "3":
                    updateUser();
                    break;
                case "4":
                    deleteUser();
                    break;
                case "0":
                    return;
                default:
                    System.out.println(" Invalid option");
            }
        }
    }

    private static void productManagementMenu() {
        while (true) {
            printSeparator();
            System.out.println("═══  PRODUCT MANAGEMENT ═══");
            System.out.println("[1] Create Product");
            System.out.println("[2] List All Products");
            System.out.println("[3] Update Product Price");
            System.out.println("[4] Delete Product");
            System.out.println("[0] Back to Main Menu");
            System.out.print("\nChoose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createProduct();
                    break;
                case "2":
                    listAllProducts();
                    break;
                case "3":
                    updateProduct();
                    break;
                case "4":
                    deleteProduct();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    // ==================== User CRUD Operations ====================

    private static void createUser() {
        System.out.print("\nUsername: ");
        String username = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            User user = new User(username, email, age);
            session.persist(user);

            tx.commit();
            System.out.println("User created with ID: " + user.getId());
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    private static void listAllUsers() {
        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery(User.class);
            List<User> users = query.getResultList();

            System.out.println("\nALL USERS (" + users.size() + " found)");
            System.out.println("─".repeat(80));

            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                for (User user : users) {
                    System.out.printf("ID: %-5s | Username: %-20s | Email: %-30s | Age: %d%n",
                            user.getId(), user.getUsername(), user.getEmail(), user.getAge());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    private static void updateUser() {
        System.out.print("\nEnter User ID to update: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            User user = session.find(User.class, id);

            if (user == null) {
                System.out.println("User not found!");
                pause();
                return;
            }

            System.out.println("Current: " + user);
            System.out.print("New Email (or press Enter to skip): ");
            String email = scanner.nextLine().trim();
            System.out.print("New Age (or press Enter to skip): ");
            String ageStr = scanner.nextLine().trim();

            Transaction tx = session.beginTransaction();

            if (!email.isEmpty())
                user.setEmail(email);
            if (!ageStr.isEmpty())
                user.setAge(Integer.parseInt(ageStr));

            session.merge(user);
            tx.commit();

            System.out.println("User updated!");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    private static void deleteUser() {
        System.out.print("\nEnter User ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            User user = session.find(User.class, id);

            if (user == null) {
                System.out.println("User not found!");
                pause();
                return;
            }

            System.out.println("Found: " + user);
            System.out.print("Confirm delete? (yes/no): ");
            String confirm = scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("yes")) {
                Transaction tx = session.beginTransaction();
                session.remove(user);
                tx.commit();
                System.out.println("User deleted!");
            } else {
                System.out.println("Cancelled");
            }
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    // ==================== Product CRUD Operations ====================

    private static void createProduct() {
        System.out.print("\nProduct Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Product product = new Product(name, price);
            session.persist(product);

            tx.commit();
            System.out.println("Product created with ID: " + product.getId());
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    private static void listAllProducts() {
        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(Product.class);
            List<Product> products = query.getResultList();

            System.out.println("\nALL PRODUCTS (" + products.size() + " found)");
            System.out.println("─".repeat(80));

            if (products.isEmpty()) {
                System.out.println("No products found.");
            } else {
                for (Product product : products) {
                    System.out.printf("ID: %-5s | Name: %-40s | Price: $%.2f%n",
                            product.getId(), product.getName(), product.getPrice());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    private static void updateProduct() {
        System.out.print("\nEnter Product ID to update: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Product product = session.find(Product.class, id);

            if (product == null) {
                System.out.println("Product not found!");
                pause();
                return;
            }

            System.out.println("Current: " + product);
            System.out.print("New Price: ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            Transaction tx = session.beginTransaction();
            product.setPrice(price);
            session.merge(product);
            tx.commit();

            System.out.println("Product updated!");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    private static void deleteProduct() {
        System.out.print("\nEnter Product ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Product product = session.find(Product.class, id);

            if (product == null) {
                System.out.println("Product not found!");
                pause();
                return;
            }

            System.out.println("Found: " + product);
            System.out.print("Confirm delete? (yes/no): ");
            String confirm = scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("yes")) {
                Transaction tx = session.beginTransaction();
                session.remove(product);
                tx.commit();
                System.out.println("Product deleted!");
            } else {
                System.out.println("Cancelled");
            }
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    // ==================== Demo & Query Features ====================

    private static void demoAllFeatures() {
        System.out.println("\nRunning comprehensive demo...\n");

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            // Create sample data
            System.out.println("[1] Creating sample users...");
            User user1 = new User("alice", "alice@example.com", 25);
            User user2 = new User("bob", "bob@example.com", 30);
            session.persist(user1);
            session.persist(user2);
            System.out.println("    Users created");

            System.out.println("\n[2] Creating sample products...");
            Product p1 = new Product("Laptop", 1299.99);
            Product p2 = new Product("Mouse", 29.99);
            Product p3 = new Product("Keyboard", 79.99);
            session.persist(p1);
            session.persist(p2);
            session.persist(p3);
            System.out.println("    Products created");

            tx.commit();
            System.out.println("\n[3] Transaction committed");

            // Read operations
            System.out.println("\n[4] Reading data...");
            User foundUser = session.find(User.class, user1.getId());
            System.out.println("    Found user: " + foundUser.getUsername());

            // Update operations
            System.out.println("\n[5] Updating data...");
            Transaction tx2 = session.beginTransaction();
            foundUser.setAge(26);
            session.merge(foundUser);
            tx2.commit();
            System.out.println("    User age updated to: " + foundUser.getAge());

            System.out.println("\nDemo completed successfully!");

        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }

        pause();
    }

    private static void queryMenu() {
        System.out.println("\nQUERY EXAMPLES");
        System.out.println("[1] Find users by age range");
        System.out.println("[2] Find products by price range");
        System.out.print("\nChoose: ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            queryUsersByAge();
        } else if (choice.equals("2")) {
            queryProductsByPrice();
        }
    }

    private static void queryUsersByAge() {
        System.out.print("\nMinimum age: ");
        int minAge = Integer.parseInt(scanner.nextLine().trim());

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery(User.class)
                    .where("age >= ?", minAge);
            List<User> users = query.getResultList();

            System.out.println("\nFound " + users.size() + " users:");
            for (User user : users) {
                System.out.println("  - " + user.getUsername() + " (age: " + user.getAge() + ")");
            }
        } catch (Exception e) {
            System.err.println("Query failed: " + e.getMessage());
        }

        pause();
    }

    private static void queryProductsByPrice() {
        System.out.print("\nMaximum price: ");
        double maxPrice = Double.parseDouble(scanner.nextLine().trim());

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(Product.class)
                    .where("price <= ?", maxPrice);
            List<Product> products = query.getResultList();

            System.out.println("\nFound " + products.size() + " products:");
            for (Product product : products) {
                System.out.println("  - " + product.getName() + " ($" + product.getPrice() + ")");
            }
        } catch (Exception e) {
            System.err.println("Query failed: " + e.getMessage());
        }

        pause();
    }

    private static void transactionExamples() {
        System.out.println("\nTRANSACTION EXAMPLES");
        System.out.println("[1] Successful commit");
        System.out.println("[2] Rollback example");
        System.out.print("\nChoose: ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            demoCommit();
        } else if (choice.equals("2")) {
            demoRollback();
        }
    }

    private static void demoCommit() {
        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            User user = new User("tx_user_" + System.currentTimeMillis(), "tx@example.com", 35);
            session.persist(user);

            tx.commit();
            System.out.println("Transaction committed! User ID: " + user.getId());
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    private static void demoRollback() {
        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            User user = new User("rollback_user", "rollback@example.com", 40);
            session.persist(user);
            System.out.println("User created (not yet committed)");

            tx.rollback();
            System.out.println("Transaction rolled back! User was NOT saved.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    private static void viewAllData() {
        System.out.println("\nDATABASE OVERVIEW");

        try (com.dam.framework.session.Session session = sessionFactory.openSession()) {
            // Count users
            Query<User> userQuery = session.createQuery(User.class);
            List<User> users = userQuery.getResultList();

            // Count products
            Query<Product> productQuery = session.createQuery(Product.class);
            List<Product> products = productQuery.getResultList();

            System.out.println("─".repeat(60));
            System.out.println("Users:    " + users.size());
            System.out.println("Products: " + products.size());
            System.out.println("─".repeat(60));

        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }

        pause();
    }

    // ==================== Utilities ====================

    private static void cleanup() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
                System.out.println("\nSessionFactory closed");
            } catch (Exception e) {
                System.err.println("Cleanup failed: " + e.getMessage());
            }
        }
        if (scanner != null) {
            scanner.close();
        }
    }

    private static void printBanner() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║        DAM Framework - Interactive Demo Application          ║");
        System.out.println("║        Database Access Management Framework v1.0              ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printSeparator() {
        System.out.println("\n" + "═".repeat(65) + "\n");
    }

    private static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
