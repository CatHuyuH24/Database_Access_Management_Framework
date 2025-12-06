# Functional Requirements Document (FRD)

## Database Access Management (DAM) Framework

**Document Version:** 1.0  
**Last Updated:** December 2024  
**Status:** Draft

---

## Table of Contents

1. [Document Information](#document-information)
2. [Introduction](#introduction)
3. [System Overview](#system-overview)
4. [Functional Requirements](#functional-requirements)
   - [FR-1: Configuration Management](#fr-1-configuration-management)
   - [FR-2: Connection Management](#fr-2-connection-management)
   - [FR-3: Entity Mapping](#fr-3-entity-mapping)
   - [FR-4: Session Management](#fr-4-session-management)
   - [FR-5: CRUD Operations](#fr-5-crud-operations)
   - [FR-6: Query Building](#fr-6-query-building)
   - [FR-7: SQL Generation](#fr-7-sql-generation)
   - [FR-8: Transaction Management](#fr-8-transaction-management)
   - [FR-9: Relationship Mapping](#fr-9-relationship-mapping)
   - [FR-10: Database Dialect Support](#fr-10-database-dialect-support)
5. [Non-Functional Requirements](#non-functional-requirements)
6. [Interface Requirements](#interface-requirements)
7. [Data Requirements](#data-requirements)
8. [Design Constraints](#design-constraints)
9. [Traceability Matrix](#traceability-matrix)
10. [Appendices](#appendices)

---

## Document Information

| Attribute | Value |
|-----------|-------|
| Project Name | Database Access Management (DAM) Framework |
| Document Type | Functional Requirements Document |
| Author | Project Team |
| Reviewers | Project Stakeholders |
| Approval Date | TBD |

### Document History

| Version | Date | Author | Description |
|---------|------|--------|-------------|
| 1.0 | Dec 2024 | Project Team | Initial draft |

---

## Introduction

### Purpose

This Functional Requirements Document (FRD) defines the detailed functional requirements for the Database Access Management (DAM) Framework. It serves as the primary reference for developers implementing the framework and testers validating its functionality.

### Scope

The DAM Framework is a Java-based Object-Relational Mapping (ORM) framework that provides:
- Database connection management
- Entity-to-table mapping using annotations
- CRUD (Create, Read, Update, Delete) operations
- Query building with WHERE, GROUP BY, and HAVING clauses
- Support for multiple database systems
- Implementation of at least 4 Gang-of-Four (GoF) design patterns

### Definitions and Acronyms

| Term | Definition |
|------|------------|
| CRUD | Create, Read, Update, Delete |
| DAO | Data Access Object |
| DAM | Database Access Management |
| FRD | Functional Requirements Document |
| GoF | Gang of Four (Design Patterns) |
| JDBC | Java Database Connectivity |
| ORM | Object-Relational Mapping |
| SQL | Structured Query Language |

### References

- Core Requirements Document: `DP-Dac_ta_Do_an-Database_Access_Management_Framework-180420.md`
- User Stories Document: `user_stories.md`
- Martin Fowler's Patterns of Enterprise Application Architecture

---

## System Overview

### System Context

```
┌──────────────────────────────────────────────────────────────────┐
│                     Client Application                            │
│                                                                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐               │
│  │ Business    │  │ Service     │  │ Data Access │               │
│  │ Logic       │──│ Layer       │──│ Layer       │               │
│  └─────────────┘  └─────────────┘  └──────┬──────┘               │
│                                           │                       │
└───────────────────────────────────────────┼───────────────────────┘
                                            │
                                            ▼
┌──────────────────────────────────────────────────────────────────┐
│                    DAM Framework                                  │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │ Public API                                                │    │
│  │  - SessionFactory    - Session    - Query API             │    │
│  └──────────────────────────────────────────────────────────┘    │
│                              │                                    │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │ Core Engine                                               │    │
│  │  - Metadata Parser   - Entity Manager  - SQL Generator    │    │
│  └──────────────────────────────────────────────────────────┘    │
│                              │                                    │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │ Database Layer                                            │    │
│  │  - Connection Pool   - Dialects      - JDBC Wrapper       │    │
│  └──────────────────────────────────────────────────────────┘    │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
                                            │
                                            ▼
┌──────────────────────────────────────────────────────────────────┐
│                    Database Systems                               │
│                                                                   │
│     MySQL      PostgreSQL      SQL Server      SQLite             │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

### System Boundaries

**In Scope:**
- JDBC-based database connectivity
- Annotation-based ORM mapping
- CRUD operations
- Query building (SELECT, INSERT, UPDATE, DELETE)
- Multiple database dialect support
- Connection pooling
- Transaction management

**Out of Scope:**
- Caching (second-level cache)
- Schema migration/DDL generation
- Stored procedure support
- Database-specific advanced features
- Distributed transactions

---

## Functional Requirements

### FR-1: Configuration Management

#### FR-1.1: Properties File Configuration

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-1.1 |
| **Name** | Properties File Configuration |
| **Priority** | High |
| **Source** | US-1.1 |

**Description:**  
The system shall support loading configuration from a properties file.

**Input:**
- Properties file path (default: `dam.properties` in classpath)

**Processing:**
1. Locate properties file in classpath or specified path
2. Parse key-value pairs
3. Validate required properties
4. Create Configuration object

**Output:**
- Configured `Configuration` object
- `ConfigurationException` if validation fails

**Properties Supported:**

| Property Key | Required | Description | Default |
|--------------|----------|-------------|---------|
| `dam.connection.url` | Yes | JDBC connection URL | - |
| `dam.connection.username` | Yes | Database username | - |
| `dam.connection.password` | Yes | Database password | - |
| `dam.connection.driver` | Yes | JDBC driver class | - |
| `dam.dialect` | Yes | Database dialect (mysql, postgresql, sqlserver) | - |
| `dam.pool.minSize` | No | Minimum pool size | 5 |
| `dam.pool.maxSize` | No | Maximum pool size | 20 |
| `dam.pool.timeout` | No | Connection timeout (ms) | 30000 |
| `dam.showSql` | No | Log generated SQL | false |

---

#### FR-1.2: Programmatic Configuration

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-1.2 |
| **Name** | Programmatic Configuration |
| **Priority** | High |
| **Source** | US-1.1 |

**Description:**  
The system shall support programmatic configuration using a fluent API.

**API Specification:**

```java
public class Configuration {
    public Configuration setUrl(String url);
    public Configuration setUsername(String username);
    public Configuration setPassword(String password);
    public Configuration setDriver(String driverClass);
    public Configuration setDialect(DialectType dialect);
    public Configuration setPoolMinSize(int size);
    public Configuration setPoolMaxSize(int size);
    public Configuration setPoolTimeout(int timeoutMs);
    public Configuration setShowSql(boolean show);
    public Configuration addAnnotatedClass(Class<?> entityClass);
    public Configuration scanPackage(String packageName);
    public SessionFactory buildSessionFactory();
}
```

---

### FR-2: Connection Management

#### FR-2.1: Database Connection Acquisition

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-2.1 |
| **Name** | Database Connection Acquisition |
| **Priority** | High |
| **Source** | US-1.2 |

**Description:**  
The system shall acquire database connections from a connection pool.

**Processing:**
1. Request connection from pool
2. If available connection exists, return it
3. If pool not at max size, create new connection
4. If pool at max size, wait until timeout
5. Validate connection before returning

**Exceptions:**
- `ConnectionException`: If connection cannot be acquired within timeout
- `ConnectionException`: If database is unreachable

---

#### FR-2.2: Connection Release

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-2.2 |
| **Name** | Connection Release |
| **Priority** | High |
| **Source** | US-1.3 |

**Description:**  
The system shall release connections back to the pool when sessions are closed.

**Processing:**
1. Roll back any uncommitted transaction
2. Reset connection state (auto-commit, etc.)
3. Return connection to pool
4. Mark connection as available

---

#### FR-2.3: Connection Pooling

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-2.3 |
| **Name** | Connection Pooling |
| **Priority** | Medium |
| **Source** | US-1.4 |

**Description:**  
The system shall maintain a pool of reusable database connections.

**Pool Behavior:**
| Scenario | Behavior |
|----------|----------|
| Pool initialization | Create `minSize` connections |
| Connection request (pool has idle) | Return idle connection |
| Connection request (pool below max) | Create new connection |
| Connection request (pool at max) | Wait until timeout |
| Connection return | Mark as idle, validate |
| Idle connection timeout | Close and remove from pool |

---

### FR-3: Entity Mapping

#### FR-3.1: Entity Annotation

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-3.1 |
| **Name** | Entity Annotation Processing |
| **Priority** | High |
| **Source** | US-2.1 |

**Description:**  
The system shall recognize classes annotated with `@Entity` as persistable entities.

**Annotation Definition:**

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
}
```

**Rules:**
- Entity class MUST have a no-argument constructor
- Entity class MUST have at least one field annotated with `@Id`
- Entity class SHOULD be a top-level class (not inner class)

---

#### FR-3.2: Table Mapping

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-3.2 |
| **Name** | Table Name Mapping |
| **Priority** | High |
| **Source** | US-2.1 |

**Description:**  
The system shall map entity classes to database tables.

**Annotation Definition:**

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    String name() default "";
    String schema() default "";
}
```

**Mapping Rules:**
| Scenario | Table Name |
|----------|------------|
| `@Table(name="users")` present | "users" |
| `@Table` present without name | Class simple name |
| `@Table` not present | Class simple name |

---

#### FR-3.3: Primary Key Mapping

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-3.3 |
| **Name** | Primary Key Mapping |
| **Priority** | High |
| **Source** | US-2.2 |

**Description:**  
The system shall identify and map primary key fields.

**Annotation Definitions:**

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GeneratedValue {
    GenerationType strategy() default GenerationType.IDENTITY;
}

public enum GenerationType {
    IDENTITY,    // Auto-increment
    SEQUENCE,    // Database sequence
    NONE         // Manually assigned
}
```

**Supported ID Types:**
- `Long` / `long`
- `Integer` / `int`
- `String`
- `UUID`

---

#### FR-3.4: Column Mapping

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-3.4 |
| **Name** | Column Mapping |
| **Priority** | High |
| **Source** | US-2.3 |

**Description:**  
The system shall map entity fields to database columns.

**Annotation Definition:**

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String name() default "";
    boolean nullable() default true;
    int length() default 255;
    boolean unique() default false;
}
```

**Mapping Rules:**
| Scenario | Column Name |
|----------|-------------|
| `@Column(name="user_name")` | "user_name" |
| `@Column` without name | Field name |
| No annotation | Field name |

**Type Mappings:**

| Java Type | SQL Type |
|-----------|----------|
| `String` | VARCHAR |
| `Integer`, `int` | INTEGER |
| `Long`, `long` | BIGINT |
| `Double`, `double` | DOUBLE |
| `Float`, `float` | FLOAT |
| `Boolean`, `boolean` | BOOLEAN |
| `java.util.Date` | TIMESTAMP |
| `java.time.LocalDate` | DATE |
| `java.time.LocalDateTime` | TIMESTAMP |

---

#### FR-3.5: Metadata Caching

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-3.5 |
| **Name** | Entity Metadata Caching |
| **Priority** | Medium |
| **Source** | US-2.4 |

**Description:**  
The system shall cache parsed entity metadata to avoid repeated reflection.

**Metadata Structure:**

```java
public class EntityMetadata {
    private Class<?> entityClass;
    private String tableName;
    private String schemaName;
    private FieldMetadata primaryKey;
    private List<FieldMetadata> columns;
    private List<RelationshipMetadata> relationships;
}

public class FieldMetadata {
    private Field field;
    private String columnName;
    private Class<?> javaType;
    private boolean nullable;
    private int length;
    private boolean primaryKey;
    private GenerationType generationType;
}
```

---

### FR-4: Session Management

#### FR-4.1: Session Creation

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-4.1 |
| **Name** | Session Creation |
| **Priority** | High |
| **Source** | US-1.2 |

**Description:**  
The system shall create session instances for database operations.

**API Specification:**

```java
public interface SessionFactory extends AutoCloseable {
    Session openSession();
    Session getCurrentSession();
    void close();
}

public interface Session extends AutoCloseable {
    <T> T find(Class<T> entityClass, Object id);
    <T> List<T> findAll(Class<T> entityClass);
    void save(Object entity);
    void update(Object entity);
    void delete(Object entity);
    <T> Query<T> createQuery(Class<T> entityClass);
    Transaction beginTransaction();
    void close();
    boolean isOpen();
}
```

---

#### FR-4.2: Session Lifecycle

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-4.2 |
| **Name** | Session Lifecycle Management |
| **Priority** | High |
| **Source** | US-1.3 |

**Description:**  
The system shall manage session lifecycle properly.

**Lifecycle States:**

```
┌─────────┐     openSession()      ┌─────────┐
│ Created │ ─────────────────────► │  Open   │
└─────────┘                        └────┬────┘
                                        │
                                        │ close()
                                        ▼
                                   ┌─────────┐
                                   │ Closed  │
                                   └─────────┘
```

**Rules:**
- Operations on closed session throw `SessionException`
- Session is not thread-safe
- One connection per session

---

### FR-5: CRUD Operations

#### FR-5.1: Create (Insert)

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-5.1 |
| **Name** | Insert Entity |
| **Priority** | High |
| **Source** | US-3.1 |

**Description:**  
The system shall insert new entity records into the database.

**Method Signature:**
```java
void save(Object entity);
```

**Processing:**
1. Validate entity is annotated with `@Entity`
2. Extract entity metadata
3. Generate INSERT SQL statement
4. Set parameter values from entity fields
5. Execute statement
6. If `@GeneratedValue`, set generated ID back to entity

**Generated SQL Example:**
```sql
INSERT INTO users (username, email, age) VALUES (?, ?, ?)
```

---

#### FR-5.2: Read (Select)

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-5.2 |
| **Name** | Select Entity |
| **Priority** | High |
| **Source** | US-3.4, US-3.5 |

**Description:**  
The system shall retrieve entities from the database.

**Method Signatures:**
```java
<T> T find(Class<T> entityClass, Object id);
<T> List<T> findAll(Class<T> entityClass);
```

**Processing (find by ID):**
1. Get entity metadata
2. Generate SELECT SQL with WHERE on primary key
3. Execute query
4. Map ResultSet to entity instance
5. Return entity or null if not found

**Generated SQL Example:**
```sql
SELECT id, username, email, age FROM users WHERE id = ?
```

---

#### FR-5.3: Update

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-5.3 |
| **Name** | Update Entity |
| **Priority** | High |
| **Source** | US-3.2 |

**Description:**  
The system shall update existing entity records.

**Method Signature:**
```java
void update(Object entity);
```

**Processing:**
1. Validate entity has primary key value
2. Get entity metadata
3. Generate UPDATE SQL statement
4. Set parameter values
5. Execute statement
6. Throw exception if no rows affected (optional)

**Generated SQL Example:**
```sql
UPDATE users SET username = ?, email = ?, age = ? WHERE id = ?
```

---

#### FR-5.4: Delete

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-5.4 |
| **Name** | Delete Entity |
| **Priority** | High |
| **Source** | US-3.3 |

**Description:**  
The system shall delete entity records from the database.

**Method Signature:**
```java
void delete(Object entity);
```

**Processing:**
1. Get entity primary key value
2. Get entity metadata
3. Generate DELETE SQL statement
4. Execute statement

**Generated SQL Example:**
```sql
DELETE FROM users WHERE id = ?
```

---

### FR-6: Query Building

#### FR-6.1: Query Interface

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-6.1 |
| **Name** | Query Interface Definition |
| **Priority** | High |
| **Source** | US-4.1 |

**Description:**  
The system shall provide a fluent query building interface.

**API Specification:**

```java
public interface Query<T> {
    Query<T> select(String... columns);
    Query<T> where(String condition, Object... params);
    Query<T> and(String condition, Object... params);
    Query<T> or(String condition, Object... params);
    Query<T> groupBy(String... columns);
    Query<T> having(String condition, Object... params);
    Query<T> orderBy(String column, Order order);
    Query<T> limit(int limit);
    Query<T> offset(int offset);
    T getSingleResult();
    List<T> getResultList();
    int executeUpdate();
}

public enum Order {
    ASC, DESC
}
```

---

#### FR-6.2: WHERE Clause

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-6.2 |
| **Name** | WHERE Clause Support |
| **Priority** | High |
| **Source** | US-4.1 |

**Description:**  
The system shall support WHERE clauses with various operators.

**Supported Operators:**
| Operator | Example |
|----------|---------|
| `=` | `where("status = ?", "active")` |
| `<>` or `!=` | `where("status <> ?", "deleted")` |
| `<`, `>`, `<=`, `>=` | `where("age >= ?", 18)` |
| `LIKE` | `where("name LIKE ?", "%john%")` |
| `IN` | `where("status IN (?, ?)", "active", "pending")` |
| `IS NULL` | `where("email IS NULL")` |
| `IS NOT NULL` | `where("email IS NOT NULL")` |
| `BETWEEN` | `where("age BETWEEN ? AND ?", 18, 65)` |

**Combining Conditions:**
```java
query.where("age >= ?", 18)
     .and("status = ?", "active")
     .or("role = ?", "admin");
```

---

#### FR-6.3: GROUP BY Clause

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-6.3 |
| **Name** | GROUP BY Support |
| **Priority** | High |
| **Source** | US-4.2 |

**Description:**  
The system shall support GROUP BY clause with aggregate functions.

**Supported Aggregates:**
- `COUNT(*)`
- `COUNT(column)`
- `SUM(column)`
- `AVG(column)`
- `MIN(column)`
- `MAX(column)`

**Example:**
```java
List<Object[]> results = session.createQuery(Order.class)
    .select("status", "COUNT(*)", "SUM(total)")
    .groupBy("status")
    .getResultList();
```

---

#### FR-6.4: HAVING Clause

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-6.4 |
| **Name** | HAVING Clause Support |
| **Priority** | High |
| **Source** | US-4.3 |

**Description:**  
The system shall support HAVING clause for filtering grouped results.

**Example:**
```java
List<Object[]> results = session.createQuery(Order.class)
    .select("customer_id", "COUNT(*)")
    .groupBy("customer_id")
    .having("COUNT(*) > ?", 5)
    .getResultList();
```

---

#### FR-6.5: ORDER BY Clause

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-6.5 |
| **Name** | ORDER BY Support |
| **Priority** | Medium |
| **Source** | US-4.4 |

**Description:**  
The system shall support ordering query results.

**Example:**
```java
List<User> users = session.createQuery(User.class)
    .orderBy("lastName", Order.ASC)
    .orderBy("firstName", Order.ASC)
    .getResultList();
```

---

#### FR-6.6: Pagination

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-6.6 |
| **Name** | Pagination Support |
| **Priority** | Medium |
| **Source** | US-4.5 |

**Description:**  
The system shall support result pagination using LIMIT and OFFSET.

**Example:**
```java
List<User> page2 = session.createQuery(User.class)
    .orderBy("id", Order.ASC)
    .offset(10)
    .limit(10)
    .getResultList();
```

**Dialect-Specific Implementation:**
| Database | SQL Syntax |
|----------|------------|
| MySQL | `LIMIT {limit} OFFSET {offset}` |
| PostgreSQL | `LIMIT {limit} OFFSET {offset}` |
| SQL Server | `OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY` |

---

### FR-7: SQL Generation

#### FR-7.1: SQL Generator Interface

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-7.1 |
| **Name** | SQL Generator Interface |
| **Priority** | High |
| **Source** | Core Requirements |

**Description:**  
The system shall generate SQL statements from entity operations.

**Interface Definition:**

```java
public interface SQLGenerator {
    String generateInsert(EntityMetadata metadata);
    String generateUpdate(EntityMetadata metadata);
    String generateDelete(EntityMetadata metadata);
    String generateSelect(EntityMetadata metadata);
    String generateSelectById(EntityMetadata metadata);
    String generateSelectWithCriteria(EntityMetadata metadata, QueryCriteria criteria);
}
```

---

#### FR-7.2: Prepared Statement Parameter Binding

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-7.2 |
| **Name** | Parameter Binding |
| **Priority** | High |
| **Source** | Security Requirement |

**Description:**  
The system shall use prepared statements with parameter binding to prevent SQL injection.

**Requirements:**
- All user-provided values MUST use parameter placeholders (?)
- Never concatenate user input into SQL strings
- Type-safe parameter setting

---

### FR-8: Transaction Management

#### FR-8.1: Transaction Interface

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-8.1 |
| **Name** | Transaction Interface |
| **Priority** | Medium |
| **Source** | US-6.1, US-6.2, US-6.3 |

**Description:**  
The system shall provide transaction management capabilities.

**API Specification:**

```java
public interface Transaction {
    void begin();
    void commit();
    void rollback();
    boolean isActive();
}
```

---

#### FR-8.2: Transaction Behavior

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-8.2 |
| **Name** | Transaction Behavior |
| **Priority** | Medium |
| **Source** | US-6.1, US-6.2, US-6.3 |

**Description:**  
The system shall handle transactions according to ACID properties.

**Behavior Table:**

| Action | Effect |
|--------|--------|
| `beginTransaction()` | Set auto-commit = false |
| `commit()` | Commit changes, set auto-commit = true |
| `rollback()` | Rollback changes, set auto-commit = true |
| Session close with active transaction | Auto-rollback |

---

### FR-9: Relationship Mapping

#### FR-9.1: One-to-Many Relationship

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-9.1 |
| **Name** | One-to-Many Mapping |
| **Priority** | Medium |
| **Source** | US-5.1 |

**Description:**  
The system shall support one-to-many entity relationships.

**Annotation:**

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {
    String mappedBy() default "";
    FetchType fetch() default FetchType.LAZY;
}

public enum FetchType {
    EAGER, LAZY
}
```

---

#### FR-9.2: Many-to-One Relationship

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-9.2 |
| **Name** | Many-to-One Mapping |
| **Priority** | Medium |
| **Source** | US-5.2 |

**Description:**  
The system shall support many-to-one entity relationships.

**Annotations:**

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {
    FetchType fetch() default FetchType.EAGER;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumn {
    String name();
    boolean nullable() default true;
}
```

---

### FR-10: Database Dialect Support

#### FR-10.1: Dialect Interface

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-10.1 |
| **Name** | Database Dialect Interface |
| **Priority** | High |
| **Source** | US-7.1, US-7.2, US-7.3 |

**Description:**  
The system shall support multiple database systems through dialect abstraction.

**Interface Definition:**

```java
public interface Dialect {
    String getName();
    String getDriverClassName();
    String getValidationQuery();
    String getLimitClause(int limit, int offset);
    String getIdentityColumnString();
    String getTypeName(int sqlType, int length);
    boolean supportsSequences();
    String getSequenceNextValString(String sequenceName);
}
```

---

#### FR-10.2: MySQL Dialect

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-10.2 |
| **Name** | MySQL Dialect Implementation |
| **Priority** | High |
| **Source** | US-7.1 |

**Description:**  
The system shall provide MySQL-specific SQL generation.

**MySQL-Specific Features:**
| Feature | Implementation |
|---------|---------------|
| Limit | `LIMIT {limit} OFFSET {offset}` |
| Auto-increment | `AUTO_INCREMENT` |
| String quotes | Backticks (`) |
| Boolean | TINYINT(1) |

---

#### FR-10.3: PostgreSQL Dialect

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-10.3 |
| **Name** | PostgreSQL Dialect Implementation |
| **Priority** | High |
| **Source** | US-7.2 |

**Description:**  
The system shall provide PostgreSQL-specific SQL generation.

**PostgreSQL-Specific Features:**
| Feature | Implementation |
|---------|---------------|
| Limit | `LIMIT {limit} OFFSET {offset}` |
| Auto-increment | `SERIAL` or `GENERATED ALWAYS AS IDENTITY` |
| String quotes | Double quotes (") |
| Boolean | BOOLEAN |

---

#### FR-10.4: SQL Server Dialect

| Attribute | Description |
|-----------|-------------|
| **ID** | FR-10.4 |
| **Name** | SQL Server Dialect Implementation |
| **Priority** | Medium |
| **Source** | US-7.3 |

**Description:**  
The system shall provide SQL Server-specific SQL generation.

**SQL Server-Specific Features:**
| Feature | Implementation |
|---------|---------------|
| Limit | `OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY` |
| Auto-increment | `IDENTITY(1,1)` |
| String quotes | Square brackets ([]) |
| Boolean | BIT |

---

## Non-Functional Requirements

### NFR-1: Performance

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-1.1 | Connection acquisition time | < 100ms from pool |
| NFR-1.2 | Simple query execution | < 50ms overhead |
| NFR-1.3 | Metadata parsing | Once per entity class |
| NFR-1.4 | Memory usage | < 100MB for 1000 entities |

### NFR-2: Reliability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-2.1 | Connection recovery | Automatic reconnection |
| NFR-2.2 | Transaction integrity | Full ACID compliance |
| NFR-2.3 | Error handling | No silent failures |

### NFR-3: Maintainability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-3.1 | Code coverage | > 80% unit test coverage |
| NFR-3.2 | Documentation | JavaDoc for all public APIs |
| NFR-3.3 | Design patterns | Minimum 4 GoF patterns |

### NFR-4: Portability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-4.1 | Java version | Java 11+ |
| NFR-4.2 | Database support | MySQL, PostgreSQL, SQL Server |
| NFR-4.3 | OS support | Windows, Linux, macOS |

---

## Interface Requirements

### External Interfaces

#### EI-1: JDBC Interface
- The framework MUST use JDBC for all database communication
- The framework SHALL NOT use any ORM libraries (Hibernate, JPA, etc.)
- The framework MAY use JDBC connection pool libraries

#### EI-2: Logging Interface
- The framework SHOULD support SLF4J for logging
- The framework SHALL provide configurable log levels

### Internal Interfaces

#### II-1: Plugin Interface
- Dialects MUST implement the `Dialect` interface
- Custom type converters MAY implement a `TypeConverter` interface

---

## Data Requirements

### Entity Requirements

| Requirement | Description |
|-------------|-------------|
| Entity classes | Must be public, non-final |
| No-arg constructor | Required for instantiation |
| Primary key | Exactly one `@Id` field required |
| Field access | Via reflection (fields) or getters/setters |

### Type Support

**Primitive Types:**
- `int`, `long`, `double`, `float`, `boolean`

**Wrapper Types:**
- `Integer`, `Long`, `Double`, `Float`, `Boolean`

**Other Types:**
- `String`
- `java.util.Date`
- `java.time.LocalDate`
- `java.time.LocalDateTime`
- `java.math.BigDecimal`
- `byte[]` (BLOB)

---

## Design Constraints

### DC-1: Technology Constraints

| Constraint | Requirement |
|------------|-------------|
| Language | Java 11 or higher |
| Database Access | JDBC only (no JPA, Hibernate) |
| Build Tool | Maven |
| Testing | JUnit 5 |

### DC-2: Design Pattern Requirements

The framework MUST implement at least 4 of these GoF patterns:

| Pattern | Application |
|---------|-------------|
| Factory | SessionFactory creating Sessions |
| Singleton | Configuration/SessionFactory instance |
| Strategy | Database Dialects |
| Builder | Query Builder |
| Proxy | Lazy Loading (optional) |
| Template Method | CRUD operations base class |

### DC-3: Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Packages | lowercase | `com.dam.framework` |
| Classes | PascalCase | `SessionFactory` |
| Methods | camelCase | `openSession()` |
| Constants | UPPER_SNAKE | `DEFAULT_POOL_SIZE` |

---

## Traceability Matrix

| FR ID | User Story | Priority | Status |
|-------|------------|----------|--------|
| FR-1.1 | US-1.1 | High | Planned |
| FR-1.2 | US-1.1 | High | Planned |
| FR-2.1 | US-1.2 | High | Planned |
| FR-2.2 | US-1.3 | High | Planned |
| FR-2.3 | US-1.4 | Medium | Planned |
| FR-3.1 | US-2.1 | High | Planned |
| FR-3.2 | US-2.1 | High | Planned |
| FR-3.3 | US-2.2 | High | Planned |
| FR-3.4 | US-2.3 | High | Planned |
| FR-3.5 | US-2.4 | Medium | Planned |
| FR-4.1 | US-1.2 | High | Planned |
| FR-4.2 | US-1.3 | High | Planned |
| FR-5.1 | US-3.1 | High | Planned |
| FR-5.2 | US-3.4, US-3.5 | High | Planned |
| FR-5.3 | US-3.2 | High | Planned |
| FR-5.4 | US-3.3 | High | Planned |
| FR-6.1 | US-4.1 | High | Planned |
| FR-6.2 | US-4.1 | High | Planned |
| FR-6.3 | US-4.2 | High | Planned |
| FR-6.4 | US-4.3 | High | Planned |
| FR-6.5 | US-4.4 | Medium | Planned |
| FR-6.6 | US-4.5 | Medium | Planned |
| FR-7.1 | Core Req | High | Planned |
| FR-7.2 | Security | High | Planned |
| FR-8.1 | US-6.1-6.3 | Medium | Planned |
| FR-8.2 | US-6.1-6.3 | Medium | Planned |
| FR-9.1 | US-5.1 | Medium | Planned |
| FR-9.2 | US-5.2 | Medium | Planned |
| FR-10.1 | US-7.* | High | Planned |
| FR-10.2 | US-7.1 | High | Planned |
| FR-10.3 | US-7.2 | High | Planned |
| FR-10.4 | US-7.3 | Medium | Planned |

---

## Appendices

### Appendix A: SQL Generation Examples

#### Insert Statement
```sql
-- Entity: User
INSERT INTO users (username, email, age, status, created_at)
VALUES (?, ?, ?, ?, ?)
```

#### Select Statement
```sql
-- Find by ID
SELECT id, username, email, age, status, created_at
FROM users
WHERE id = ?

-- Find with criteria
SELECT id, username, email, age, status, created_at
FROM users
WHERE age >= ? AND status = ?
ORDER BY username ASC
LIMIT ? OFFSET ?
```

#### Update Statement
```sql
UPDATE users
SET username = ?, email = ?, age = ?, status = ?
WHERE id = ?
```

#### Delete Statement
```sql
DELETE FROM users WHERE id = ?
```

### Appendix B: Example Entity

```java
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    
    @Column(name = "email", unique = true)
    private String email;
    
    private Integer age;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Default constructor (required)
    public User() {}
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

---

*Document prepared for Database Access Management Framework Project*
