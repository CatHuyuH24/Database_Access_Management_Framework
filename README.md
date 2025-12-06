# Database Access Management (DAM) Framework

[![Java](https://img.shields.io/badge/Java-11%2B-orange)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-blue)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

A lightweight Object-Relational Mapping (ORM) framework for Java, similar to Hibernate. This framework provides a simple and intuitive API for mapping Java objects to relational database tables.

---

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage Examples](#usage-examples)
- [Design Patterns](#design-patterns)
- [Supported Databases](#supported-databases)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **Annotation-based ORM mapping** - Map Java classes to database tables using annotations
- **CRUD operations** - Full support for Create, Read, Update, Delete operations
- **Query Builder** - Fluent API for building complex queries
- **WHERE clause** - Filter data with conditions and parameters
- **GROUP BY & HAVING** - Aggregate data with grouping and filtering
- **Multi-database support** - Works with MySQL, PostgreSQL, and SQL Server
- **Connection pooling** - Efficient connection management
- **Transaction support** - ACID-compliant transaction handling
- **Design Pattern implementation** - Uses Factory, Singleton, Strategy, and Builder patterns

---

## Prerequisites

Before you begin, ensure you have the following installed:

| Requirement | Version | Check Command |
|------------|---------|---------------|
| **Java JDK** | 11 or higher | `java -version` |
| **Maven** | 3.8 or higher | `mvn -version` |
| **Git** | Any recent version | `git --version` |
| **Database** | MySQL 8.0+ / PostgreSQL 13+ / SQL Server 2019+ | - |

### Installing Prerequisites

**Java JDK 11+:**
```bash
# Ubuntu/Debian
sudo apt update && sudo apt install openjdk-11-jdk

# macOS (using Homebrew)
brew install openjdk@11

# Windows - Download from https://adoptium.net/
```

**Maven:**
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven

# Windows - Download from https://maven.apache.org/download.cgi
```

---

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/CatHuyuH24/Database_Access_Management_Framework.git
cd Database_Access_Management_Framework
```

### 2. Build the Framework

```bash
cd dam-framework
mvn clean install
```

### 3. Create Your Entity

```java
import com.dam.framework.annotations.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "email")
    private String email;
    
    // Getters and setters...
}
```

### 4. Configure and Use

```java
import com.dam.framework.config.Configuration;
import com.dam.framework.session.Session;
import com.dam.framework.session.SessionFactory;

// Configure the framework
Configuration config = new Configuration()
    .setUrl("jdbc:mysql://localhost:3306/mydb")
    .setUsername("root")
    .setPassword("password")
    .setDialect(DialectType.MYSQL)
    .addAnnotatedClass(User.class);

// Create SessionFactory
SessionFactory factory = config.buildSessionFactory();

// Use Session for database operations
try (Session session = factory.openSession()) {
    // Create
    User user = new User();
    user.setUsername("john");
    user.setEmail("john@example.com");
    session.save(user);
    
    // Read
    User found = session.find(User.class, 1L);
    
    // Update
    found.setEmail("newemail@example.com");
    session.update(found);
    
    // Delete
    session.delete(found);
    
    // Query
    List<User> adults = session.createQuery(User.class)
        .where("age > ?", 18)
        .orderBy("username", Order.ASC)
        .getResultList();
}
```

---

## Project Structure

```
Database_Access_Management_Framework/
│
├── docs/                              # Documentation
│   ├── DP-Dac_ta_Do_an-*.md          # Core requirements document
│   ├── project_breakdown.md           # Technical analysis & breakdown
│   ├── user_stories.md                # User stories & acceptance criteria
│   ├── functional_requirements.md     # Detailed FRD document
│   └── project_plan.md                # Project timeline & milestones
│
├── dam-framework/                     # Main framework module
│   ├── pom.xml                        # Maven configuration
│   └── src/
│       ├── main/
│       │   ├── java/com/dam/framework/
│       │   │   ├── annotations/       # @Entity, @Table, @Column, @Id
│       │   │   ├── config/            # Configuration & DialectType
│       │   │   ├── connection/        # Connection pooling
│       │   │   ├── dialect/           # Database dialects
│       │   │   ├── engine/            # Core ORM engine
│       │   │   ├── exception/         # Custom exceptions
│       │   │   ├── mapping/           # Entity metadata
│       │   │   ├── query/             # Query builder API
│       │   │   ├── session/           # Session & SessionFactory
│       │   │   ├── sql/               # SQL generation
│       │   │   ├── transaction/       # Transaction management
│       │   │   └── util/              # Utility classes
│       │   └── resources/
│       │       ├── dam.properties.example
│       │       └── logback.xml
│       └── test/
│           └── java/com/dam/framework/
│
├── dam-demo/                          # Demo application
│   ├── pom.xml
│   └── src/
│       └── main/java/com/dam/demo/
│           ├── DemoApplication.java
│           └── entity/
│               └── User.java
│
├── .gitignore
└── README.md
```

### Package Descriptions

| Package | Description |
|---------|-------------|
| `annotations` | ORM annotations (@Entity, @Table, @Column, @Id, @GeneratedValue) |
| `config` | Framework configuration and dialect type enumeration |
| `connection` | Database connection management and pooling |
| `dialect` | Database-specific SQL generation (MySQL, PostgreSQL, SQL Server) |
| `engine` | Core ORM engine components |
| `exception` | Custom exception classes (DAMException, etc.) |
| `mapping` | Entity metadata parsing and storage |
| `query` | Fluent query builder API |
| `session` | Session and SessionFactory interfaces and implementations |
| `sql` | SQL statement generation |
| `transaction` | Transaction management |
| `util` | Utility classes (reflection, etc.) |

---

## Installation

### Option 1: Build from Source

```bash
# Clone the repository
git clone https://github.com/CatHuyuH24/Database_Access_Management_Framework.git
cd Database_Access_Management_Framework

# Build and install to local Maven repository
cd dam-framework
mvn clean install

# Run tests
mvn test
```

### Option 2: Add as Dependency (after building)

Add to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.dam</groupId>
    <artifactId>dam-framework</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Also add the appropriate JDBC driver:

```xml
<!-- MySQL -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>

<!-- SQL Server -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.2.0.jre11</version>
</dependency>
```

---

## Configuration

### Properties File Configuration

Create `dam.properties` in your resources folder:

```properties
# Database Connection
dam.connection.url=jdbc:mysql://localhost:3306/mydb
dam.connection.username=root
dam.connection.password=secret
dam.connection.driver=com.mysql.cj.jdbc.Driver

# Dialect (mysql, postgresql, sqlserver)
dam.dialect=mysql

# Connection Pool
dam.pool.minSize=5
dam.pool.maxSize=20
dam.pool.timeout=30000

# Logging
dam.showSql=true
```

### Programmatic Configuration

```java
Configuration config = new Configuration()
    .setUrl("jdbc:mysql://localhost:3306/mydb")
    .setUsername("root")
    .setPassword("secret")
    .setDriver("com.mysql.cj.jdbc.Driver")
    .setDialect(DialectType.MYSQL)
    .setShowSql(true)
    .addAnnotatedClass(User.class)
    .addAnnotatedClass(Order.class);

SessionFactory factory = config.buildSessionFactory();
```

---

## Usage Examples

### Entity Definition

```java
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "price")
    private Double price;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    // Default constructor required
    public Product() {}
    
    // Getters and setters...
}
```

### CRUD Operations

```java
try (Session session = factory.openSession()) {
    
    // CREATE
    Product product = new Product();
    product.setName("Laptop");
    product.setPrice(999.99);
    product.setQuantity(10);
    session.save(product);
    
    // READ
    Product found = session.find(Product.class, product.getId());
    List<Product> all = session.findAll(Product.class);
    
    // UPDATE
    found.setPrice(899.99);
    session.update(found);
    
    // DELETE
    session.delete(found);
}
```

### Query Building

```java
// Simple WHERE
List<Product> expensive = session.createQuery(Product.class)
    .where("price > ?", 500)
    .getResultList();

// Multiple conditions
List<Product> filtered = session.createQuery(Product.class)
    .where("price > ?", 100)
    .and("quantity > ?", 0)
    .orderBy("name", Order.ASC)
    .getResultList();

// Pagination
List<Product> page = session.createQuery(Product.class)
    .orderBy("id", Order.ASC)
    .offset(10)
    .limit(10)
    .getResultList();

// GROUP BY with HAVING
List<Object[]> stats = session.createQuery(Product.class)
    .select("category", "COUNT(*)", "AVG(price)")
    .groupBy("category")
    .having("COUNT(*) > ?", 5)
    .getResultList();
```

### Transaction Management

```java
Transaction tx = session.beginTransaction();
try {
    session.save(product1);
    session.save(product2);
    session.update(product3);
    tx.commit();
} catch (Exception e) {
    tx.rollback();
    throw e;
}
```

---

## Design Patterns

This framework implements the following Gang-of-Four (GoF) design patterns:

| Pattern | Implementation | Purpose |
|---------|---------------|---------|
| **Factory** | `SessionFactory` | Creates Session instances |
| **Singleton** | `Configuration` | Single configuration instance |
| **Strategy** | `Dialect` interface | Database-specific SQL generation |
| **Builder** | `Query` interface | Fluent query construction |

### Additional Patterns (Extended)

| Pattern | Implementation | Purpose |
|---------|---------------|---------|
| **Proxy** | Lazy loading | Defer entity loading |
| **Template Method** | CRUD base class | Define operation skeleton |

---

## Supported Databases

| Database | Dialect | Driver | Tested Version |
|----------|---------|--------|----------------|
| MySQL | `MYSQL` | mysql-connector-java | 8.0+ |
| PostgreSQL | `POSTGRESQL` | postgresql | 13+ |
| SQL Server | `SQLSERVER` | mssql-jdbc | 2019+ |
| SQLite | `SQLITE` | sqlite-jdbc | 3.x |

---

## Documentation

| Document | Description |
|----------|-------------|
| [Project Breakdown](docs/project_breakdown.md) | Technical analysis, architecture, and patterns |
| [User Stories](docs/user_stories.md) | User stories with acceptance criteria |
| [Functional Requirements](docs/functional_requirements.md) | Detailed FRD document |
| [Project Plan](docs/project_plan.md) | Timeline, milestones, and work breakdown |
| [Core Requirements](docs/DP-Dac_ta_Do_an-Database_Access_Management_Framework-180420.md) | Original project requirements |

---

## Quick Reference

### Annotations

| Annotation | Target | Description |
|------------|--------|-------------|
| `@Entity` | Class | Marks class as database entity |
| `@Table(name="")` | Class | Specifies table name |
| `@Id` | Field | Marks primary key field |
| `@GeneratedValue` | Field | Auto-generate primary key |
| `@Column(name="")` | Field | Specifies column mapping |

### Session Methods

| Method | Description |
|--------|-------------|
| `find(Class, id)` | Find entity by primary key |
| `findAll(Class)` | Find all entities of type |
| `save(entity)` | Insert new entity |
| `update(entity)` | Update existing entity |
| `delete(entity)` | Delete entity |
| `createQuery(Class)` | Create query builder |
| `beginTransaction()` | Start transaction |

### Query Methods

| Method | Description |
|--------|-------------|
| `where(condition, params)` | Add WHERE clause |
| `and(condition, params)` | Add AND condition |
| `or(condition, params)` | Add OR condition |
| `groupBy(columns)` | Add GROUP BY |
| `having(condition, params)` | Add HAVING clause |
| `orderBy(column, order)` | Add ORDER BY |
| `limit(n)` | Limit results |
| `offset(n)` | Skip results |
| `getResultList()` | Execute and get list |
| `getSingleResult()` | Execute and get single |

---

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- Martin Fowler's [Patterns of Enterprise Application Architecture](http://www.martinfowler.com/eaaCatalog/)
- Hibernate ORM for inspiration
- Gang of Four Design Patterns

---

*Built with ❤️ for Database Access Management*
