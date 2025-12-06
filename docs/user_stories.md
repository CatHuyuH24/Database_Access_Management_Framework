# User Stories

## Database Access Management (DAM) Framework

**Version:** 1.0  
**Last Updated:** December 2024

---

## Table of Contents

1. [Introduction](#introduction)
2. [User Personas](#user-personas)
3. [Epic Overview](#epic-overview)
4. [User Stories by Epic](#user-stories-by-epic)
   - [Epic 1: Database Connection Management](#epic-1-database-connection-management)
   - [Epic 2: Entity Mapping & Configuration](#epic-2-entity-mapping--configuration)
   - [Epic 3: CRUD Operations](#epic-3-crud-operations)
   - [Epic 4: Query Building](#epic-4-query-building)
   - [Epic 5: Relationship Mapping](#epic-5-relationship-mapping)
   - [Epic 6: Transaction Management](#epic-6-transaction-management)
   - [Epic 7: Multi-Database Support](#epic-7-multi-database-support)
5. [Non-Functional Requirements](#non-functional-requirements)
6. [Story Map](#story-map)

---

## Introduction

This document contains user stories for the Database Access Management (DAM) Framework project. Each story follows the format:

> **As a** [type of user]  
> **I want** [goal]  
> **So that** [benefit]

Stories include acceptance criteria and are prioritized using MoSCoW method:
- **Must Have** (M) - Essential for MVP
- **Should Have** (S) - Important but not critical
- **Could Have** (C) - Nice to have
- **Won't Have** (W) - Out of scope for current release

---

## User Personas

### Developer Dave
- **Role**: Application Developer
- **Goal**: Use the DAM framework to build applications faster
- **Pain Points**: Writing repetitive SQL, managing connections manually
- **Technical Level**: Intermediate Java developer

### Framework Frank
- **Role**: Framework Maintainer/Extender
- **Goal**: Extend the framework for new databases
- **Pain Points**: Tightly coupled code, poor documentation
- **Technical Level**: Senior Java developer

### Student Sarah
- **Role**: Student Learning ORM Concepts
- **Goal**: Understand how ORM frameworks work internally
- **Pain Points**: Complex existing frameworks, lack of learning resources
- **Technical Level**: Beginner to intermediate

---

## Epic Overview

| Epic ID | Epic Name | Priority | Description |
|---------|-----------|----------|-------------|
| E1 | Database Connection Management | Must Have | Core connection and pooling functionality |
| E2 | Entity Mapping & Configuration | Must Have | Annotation-based ORM mapping |
| E3 | CRUD Operations | Must Have | Basic Create, Read, Update, Delete |
| E4 | Query Building | Must Have | Fluent query API with WHERE, GROUP BY, HAVING |
| E5 | Relationship Mapping | Should Have | Entity relationships (1:1, 1:N, N:N) |
| E6 | Transaction Management | Should Have | Transaction support with rollback |
| E7 | Multi-Database Support | Must Have | Support for MySQL, PostgreSQL, SQL Server |

---

## User Stories by Epic

### Epic 1: Database Connection Management

#### US-1.1: Configure Database Connection
**Priority:** Must Have

**As a** Developer Dave  
**I want** to configure database connection using a properties file or programmatically  
**So that** I can easily connect to different databases without changing code

**Acceptance Criteria:**
- [ ] Support configuration via `dam.properties` file
- [ ] Support programmatic configuration via `Configuration` class
- [ ] Configuration includes: URL, username, password, driver class
- [ ] Validate configuration and throw meaningful errors for missing properties
- [ ] Sensitive credentials are not logged

**Example:**
```properties
# dam.properties
dam.connection.url=jdbc:mysql://localhost:3306/mydb
dam.connection.username=root
dam.connection.password=secret
dam.connection.driver=com.mysql.cj.jdbc.Driver
dam.dialect=mysql
```

```java
// Programmatic configuration
Configuration config = new Configuration()
    .setUrl("jdbc:mysql://localhost:3306/mydb")
    .setUsername("root")
    .setPassword("secret")
    .setDriver("com.mysql.cj.jdbc.Driver")
    .setDialect(Dialect.MYSQL);

SessionFactory factory = config.buildSessionFactory();
```

---

#### US-1.2: Open Database Session
**Priority:** Must Have

**As a** Developer Dave  
**I want** to open a database session from the SessionFactory  
**So that** I can perform database operations

**Acceptance Criteria:**
- [ ] `SessionFactory.openSession()` returns a new `Session` object
- [ ] Session provides access to database operations
- [ ] Session is not thread-safe (one session per thread)
- [ ] Session holds a database connection

**Example:**
```java
SessionFactory factory = config.buildSessionFactory();
Session session = factory.openSession();
// Use session for database operations
```

---

#### US-1.3: Close Database Connection
**Priority:** Must Have

**As a** Developer Dave  
**I want** to properly close database sessions and connections  
**So that** I don't leak database resources

**Acceptance Criteria:**
- [ ] `Session.close()` releases the underlying connection
- [ ] `SessionFactory.close()` closes all connections in the pool
- [ ] Closed sessions throw exception when used
- [ ] Support try-with-resources pattern (implement `AutoCloseable`)

**Example:**
```java
try (Session session = factory.openSession()) {
    // Database operations
} // Auto-closed here
```

---

#### US-1.4: Connection Pooling
**Priority:** Should Have

**As a** Developer Dave  
**I want** connection pooling for better performance  
**So that** my application doesn't create new connections for every request

**Acceptance Criteria:**
- [ ] Maintain a pool of reusable connections
- [ ] Configurable pool size (min, max connections)
- [ ] Connection validation before reuse
- [ ] Timeout for acquiring connections

**Example:**
```properties
dam.pool.minSize=5
dam.pool.maxSize=20
dam.pool.timeout=30000
```

---

### Epic 2: Entity Mapping & Configuration

#### US-2.1: Define Entity Class
**Priority:** Must Have

**As a** Developer Dave  
**I want** to mark a Java class as a database entity using annotations  
**So that** the framework knows which classes to persist

**Acceptance Criteria:**
- [ ] `@Entity` annotation marks a class as persistable
- [ ] `@Table(name="table_name")` specifies custom table name
- [ ] If `@Table` is not present, use class name as table name
- [ ] Entity class must have a no-arg constructor

**Example:**
```java
@Entity
@Table(name = "users")
public class User {
    // Fields and methods
}
```

---

#### US-2.2: Map Primary Key
**Priority:** Must Have

**As a** Developer Dave  
**I want** to specify the primary key field of an entity  
**So that** the framework can uniquely identify records

**Acceptance Criteria:**
- [ ] `@Id` annotation marks the primary key field
- [ ] `@GeneratedValue` supports auto-increment
- [ ] Support `GenerationType.IDENTITY` for auto-generated IDs
- [ ] Primary key can be Integer, Long, or String

**Example:**
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

---

#### US-2.3: Map Column Fields
**Priority:** Must Have

**As a** Developer Dave  
**I want** to map Java fields to database columns  
**So that** I can control how data is stored

**Acceptance Criteria:**
- [ ] `@Column(name="column_name")` specifies custom column name
- [ ] If `@Column` is not present, use field name
- [ ] Support `nullable`, `length` attributes
- [ ] Support basic types: String, Integer, Long, Double, Boolean, Date
- [ ] Fields without annotations are also mapped (convention over configuration)

**Example:**
```java
@Entity
public class User {
    @Id
    private Long id;
    
    @Column(name = "user_name", nullable = false, length = 100)
    private String username;
    
    @Column(name = "email_address")
    private String email;
    
    private Integer age; // Maps to "age" column
}
```

---

#### US-2.4: Entity Scanning
**Priority:** Should Have

**As a** Developer Dave  
**I want** the framework to automatically discover entity classes  
**So that** I don't have to register each entity manually

**Acceptance Criteria:**
- [ ] Scan specified packages for `@Entity` classes
- [ ] Cache entity metadata for performance
- [ ] Support manual entity registration as fallback

**Example:**
```java
Configuration config = new Configuration()
    .scanPackage("com.myapp.entities");
```

---

### Epic 3: CRUD Operations

#### US-3.1: Insert Entity
**Priority:** Must Have

**As a** Developer Dave  
**I want** to save a new entity to the database  
**So that** I can persist new data

**Acceptance Criteria:**
- [ ] `Session.save(entity)` inserts new record
- [ ] Returns generated ID for auto-increment columns
- [ ] Null fields are inserted as NULL
- [ ] Throw exception for constraint violations

**Example:**
```java
User user = new User();
user.setUsername("john");
user.setEmail("john@example.com");

session.save(user);
System.out.println("Generated ID: " + user.getId());
```

---

#### US-3.2: Update Entity
**Priority:** Must Have

**As a** Developer Dave  
**I want** to update an existing entity in the database  
**So that** I can modify persisted data

**Acceptance Criteria:**
- [ ] `Session.update(entity)` updates existing record
- [ ] Update based on primary key
- [ ] Only update changed fields (optional optimization)
- [ ] Throw exception if entity doesn't exist

**Example:**
```java
User user = session.find(User.class, 1L);
user.setEmail("newemail@example.com");
session.update(user);
```

---

#### US-3.3: Delete Entity
**Priority:** Must Have

**As a** Developer Dave  
**I want** to delete an entity from the database  
**So that** I can remove unwanted data

**Acceptance Criteria:**
- [ ] `Session.delete(entity)` removes record
- [ ] Delete based on primary key
- [ ] Handle cascade delete for relationships (extended feature)
- [ ] No error if entity doesn't exist (idempotent)

**Example:**
```java
User user = session.find(User.class, 1L);
session.delete(user);
```

---

#### US-3.4: Find Entity by ID
**Priority:** Must Have

**As a** Developer Dave  
**I want** to retrieve an entity by its primary key  
**So that** I can access specific records

**Acceptance Criteria:**
- [ ] `Session.find(Class, id)` returns entity or null
- [ ] Populate all mapped fields
- [ ] Support different ID types (Long, Integer, String)

**Example:**
```java
User user = session.find(User.class, 1L);
if (user != null) {
    System.out.println(user.getUsername());
}
```

---

#### US-3.5: Find All Entities
**Priority:** Must Have

**As a** Developer Dave  
**I want** to retrieve all entities of a type  
**So that** I can work with complete datasets

**Acceptance Criteria:**
- [ ] `Session.findAll(Class)` returns list of all entities
- [ ] Returns empty list if no records found
- [ ] Support pagination (offset, limit)

**Example:**
```java
List<User> users = session.findAll(User.class);
for (User user : users) {
    System.out.println(user.getUsername());
}
```

---

### Epic 4: Query Building

#### US-4.1: Select with WHERE Clause
**Priority:** Must Have

**As a** Developer Dave  
**I want** to query entities with conditions  
**So that** I can filter data based on criteria

**Acceptance Criteria:**
- [ ] Support WHERE with comparison operators (=, <>, <, >, <=, >=)
- [ ] Support AND, OR logical operators
- [ ] Support LIKE for pattern matching
- [ ] Support IN for multiple values
- [ ] Parameter binding to prevent SQL injection

**Example:**
```java
List<User> users = session.createQuery(User.class)
    .where("age > ?", 18)
    .and("status = ?", "active")
    .getResultList();
```

---

#### US-4.2: Select with GROUP BY
**Priority:** Must Have

**As a** Developer Dave  
**I want** to group query results  
**So that** I can aggregate data

**Acceptance Criteria:**
- [ ] Support GROUP BY single or multiple columns
- [ ] Support aggregate functions: COUNT, SUM, AVG, MIN, MAX
- [ ] Return results as maps or custom DTOs

**Example:**
```java
List<Object[]> results = session.createQuery(User.class)
    .select("status", "COUNT(*)")
    .groupBy("status")
    .getResultList();
```

---

#### US-4.3: Select with HAVING Clause
**Priority:** Must Have

**As a** Developer Dave  
**I want** to filter grouped results  
**So that** I can apply conditions on aggregates

**Acceptance Criteria:**
- [ ] Support HAVING with aggregate conditions
- [ ] HAVING works with GROUP BY
- [ ] Support same operators as WHERE

**Example:**
```java
List<Object[]> results = session.createQuery(User.class)
    .select("department", "COUNT(*)")
    .groupBy("department")
    .having("COUNT(*) > ?", 5)
    .getResultList();
```

---

#### US-4.4: Order By
**Priority:** Should Have

**As a** Developer Dave  
**I want** to sort query results  
**So that** I can control the order of returned data

**Acceptance Criteria:**
- [ ] Support ORDER BY single or multiple columns
- [ ] Support ASC and DESC ordering
- [ ] Default ordering is ASC

**Example:**
```java
List<User> users = session.createQuery(User.class)
    .orderBy("lastName", Order.ASC)
    .orderBy("firstName", Order.ASC)
    .getResultList();
```

---

#### US-4.5: Pagination
**Priority:** Should Have

**As a** Developer Dave  
**I want** to paginate query results  
**So that** I can handle large datasets efficiently

**Acceptance Criteria:**
- [ ] Support LIMIT and OFFSET
- [ ] Fluent API with `limit()` and `offset()`
- [ ] Works with all query types

**Example:**
```java
List<User> users = session.createQuery(User.class)
    .orderBy("id", Order.ASC)
    .offset(20)
    .limit(10)
    .getResultList();
```

---

### Epic 5: Relationship Mapping

#### US-5.1: One-to-Many Relationship
**Priority:** Should Have

**As a** Developer Dave  
**I want** to map one-to-many relationships between entities  
**So that** I can navigate object graphs

**Acceptance Criteria:**
- [ ] `@OneToMany` annotation on collection field
- [ ] Specify foreign key column with `mappedBy`
- [ ] Support lazy loading (fetch on access)
- [ ] Support eager loading (fetch immediately)

**Example:**
```java
@Entity
public class Department {
    @Id
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}
```

---

#### US-5.2: Many-to-One Relationship
**Priority:** Should Have

**As a** Developer Dave  
**I want** to map many-to-one relationships  
**So that** I can reference parent entities

**Acceptance Criteria:**
- [ ] `@ManyToOne` annotation on reference field
- [ ] `@JoinColumn` specifies foreign key column
- [ ] Load related entity when parent is loaded

**Example:**
```java
@Entity
public class Employee {
    @Id
    private Long id;
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
```

---

#### US-5.3: Many-to-Many Relationship
**Priority:** Could Have

**As a** Developer Dave  
**I want** to map many-to-many relationships  
**So that** I can model complex associations

**Acceptance Criteria:**
- [ ] `@ManyToMany` annotation
- [ ] `@JoinTable` for junction table specification
- [ ] Support bidirectional relationships

**Example:**
```java
@Entity
public class Student {
    @Id
    private Long id;
    
    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;
}
```

---

### Epic 6: Transaction Management

#### US-6.1: Begin Transaction
**Priority:** Should Have

**As a** Developer Dave  
**I want** to start a database transaction  
**So that** I can group multiple operations atomically

**Acceptance Criteria:**
- [ ] `Session.beginTransaction()` starts transaction
- [ ] Auto-commit is disabled when transaction begins
- [ ] Nested transactions throw exception

**Example:**
```java
Transaction tx = session.beginTransaction();
try {
    session.save(user1);
    session.save(user2);
    tx.commit();
} catch (Exception e) {
    tx.rollback();
}
```

---

#### US-6.2: Commit Transaction
**Priority:** Should Have

**As a** Developer Dave  
**I want** to commit a transaction  
**So that** my changes are persisted to the database

**Acceptance Criteria:**
- [ ] `Transaction.commit()` saves all changes
- [ ] Commit releases any locks
- [ ] Throw exception if commit fails

---

#### US-6.3: Rollback Transaction
**Priority:** Should Have

**As a** Developer Dave  
**I want** to rollback a transaction on error  
**So that** I can undo failed operations

**Acceptance Criteria:**
- [ ] `Transaction.rollback()` undoes all changes
- [ ] Rollback restores data to transaction start state
- [ ] Automatic rollback on uncaught exceptions (optional)

---

### Epic 7: Multi-Database Support

#### US-7.1: MySQL Support
**Priority:** Must Have

**As a** Developer Dave  
**I want** to use the framework with MySQL database  
**So that** I can build MySQL-backed applications

**Acceptance Criteria:**
- [ ] MySQL dialect generates valid MySQL SQL
- [ ] Support MySQL-specific data types
- [ ] Support MySQL LIMIT syntax
- [ ] Test with MySQL 8.0+

---

#### US-7.2: PostgreSQL Support
**Priority:** Must Have

**As a** Developer Dave  
**I want** to use the framework with PostgreSQL  
**So that** I can use PostgreSQL's advanced features

**Acceptance Criteria:**
- [ ] PostgreSQL dialect generates valid PostgreSQL SQL
- [ ] Support PostgreSQL-specific types
- [ ] Support PostgreSQL LIMIT/OFFSET syntax
- [ ] Test with PostgreSQL 13+

---

#### US-7.3: SQL Server Support
**Priority:** Should Have

**As a** Developer Dave  
**I want** to use the framework with SQL Server  
**So that** I can integrate with enterprise systems

**Acceptance Criteria:**
- [ ] SQL Server dialect generates valid T-SQL
- [ ] Support SQL Server TOP syntax for limits
- [ ] Support SQL Server identity columns
- [ ] Test with SQL Server 2019+

---

#### US-7.4: SQLite Support
**Priority:** Could Have

**As a** Developer Dave  
**I want** to use the framework with SQLite  
**So that** I can build lightweight applications

**Acceptance Criteria:**
- [ ] SQLite dialect generates valid SQLite SQL
- [ ] Support file-based database connections
- [ ] Useful for testing and development

---

## Non-Functional Requirements

### NFR-1: Performance

**As a** Developer Dave  
**I want** the framework to perform efficiently  
**So that** my application remains responsive

**Acceptance Criteria:**
- [ ] Connection pooling enabled by default
- [ ] Prepared statements for all queries
- [ ] Metadata caching to avoid repeated reflection
- [ ] Batch operations for bulk inserts/updates

---

### NFR-2: Error Handling

**As a** Developer Dave  
**I want** meaningful error messages  
**So that** I can debug issues quickly

**Acceptance Criteria:**
- [ ] Custom exception hierarchy (DAMException, ConnectionException, QueryException)
- [ ] Include SQL in exception messages (optional/debug mode)
- [ ] Stack traces point to user code, not internal framework code

---

### NFR-3: Logging

**As a** Developer Dave  
**I want** to see what SQL is being executed  
**So that** I can debug and optimize queries

**Acceptance Criteria:**
- [ ] Configurable logging levels
- [ ] Log generated SQL statements
- [ ] Log execution times
- [ ] Integration with SLF4J

---

### NFR-4: Documentation

**As a** Student Sarah  
**I want** comprehensive documentation  
**So that** I can learn how to use and extend the framework

**Acceptance Criteria:**
- [ ] JavaDoc for all public classes and methods
- [ ] Getting Started guide
- [ ] API reference
- [ ] Code examples for common use cases

---

### NFR-5: Extensibility

**As a** Framework Frank  
**I want** to easily add support for new databases  
**So that** I can extend the framework for custom needs

**Acceptance Criteria:**
- [ ] Clear Dialect interface for new database support
- [ ] Pluggable type converters
- [ ] Hook points for custom behaviors

---

## Story Map

```
                             User Journey
                                  │
    ┌─────────────────────────────┼─────────────────────────────┐
    │                             │                             │
    ▼                             ▼                             ▼
┌─────────┐               ┌─────────────┐              ┌──────────────┐
│ SETUP   │               │ DEVELOPMENT │              │  ADVANCED    │
└─────────┘               └─────────────┘              └──────────────┘
    │                             │                             │
    ├── US-1.1 Configure DB       ├── US-3.1 Insert            ├── US-5.1 One-to-Many
    ├── US-1.2 Open Session       ├── US-3.2 Update            ├── US-5.2 Many-to-One
    ├── US-1.3 Close Session      ├── US-3.3 Delete            ├── US-5.3 Many-to-Many
    ├── US-2.1 Define Entity      ├── US-3.4 Find by ID        ├── US-6.1 Begin Tx
    ├── US-2.2 Map Primary Key    ├── US-3.5 Find All          ├── US-6.2 Commit Tx
    ├── US-2.3 Map Columns        ├── US-4.1 WHERE             ├── US-6.3 Rollback Tx
    └── US-2.4 Entity Scan        ├── US-4.2 GROUP BY          └── US-7.* Databases
                                  ├── US-4.3 HAVING
                                  ├── US-4.4 ORDER BY
                                  └── US-4.5 Pagination

─────────────────────────────────────────────────────────────────────
MVP (Must Have)              Priority 2 (Should Have)    Priority 3 (Could Have)
```

---

## Story Dependencies

```
US-1.1 ──► US-1.2 ──► US-1.3
              │
              ▼
US-2.1 ──► US-2.2 ──► US-2.3 ──► US-2.4
              │
              ▼
US-3.1 ──┬─► US-3.2
         ├─► US-3.3
         └─► US-3.4 ──► US-3.5
                          │
                          ▼
              US-4.1 ──► US-4.2 ──► US-4.3
                 │
                 ├─► US-4.4
                 └─► US-4.5
```

---

*Document prepared for Database Access Management Framework Project*
