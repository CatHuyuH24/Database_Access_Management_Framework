# DAM Framework - Project Plan (Simplified)

**Project:** Database Access Management (DAM) Framework  
**Duration:** December 7, 2024 – January 15, 2025 (40 days / 6 weeks)  
**Team Size:** 3 Developers  
**Target:** MVP-driven, concurrent development

---

## Table of Contents

1. [Overview](#overview)
2. [Development Strategy](#development-strategy)
3. [Team Structure](#team-structure)
4. [MVP Stages](#mvp-stages)
5. [Milestone Roadmap](#milestone-roadmap)
6. [Sprint Details](#sprint-details)
7. [Dependencies & Parallelization](#dependencies--parallelization)
8. [Risk Mitigation](#risk-mitigation)

---

## Overview

### Project Goal

Build a lightweight ORM framework in Java supporting CRUD operations, query building (WHERE, GROUP BY, HAVING), multi-database compatibility (MySQL, PostgreSQL, SQL Server), and implementing 4+ GoF design patterns.

### Success Metrics

- ✅ All CRUD operations functional
- ✅ Query builder with WHERE, GROUP BY, HAVING
- ✅ 3 database dialects working
- ✅ 4+ GoF patterns documented
- ✅ Demo application + video
- ✅ Comprehensive documentation

---

## Development Strategy

### MVP-Driven Approach

We follow a **3-stage MVP** model:

| Stage     | Focus                       | Timeline  | Outcome                           |
| --------- | --------------------------- | --------- | --------------------------------- |
| **MVP 1** | Basic ORM + MySQL CRUD      | Weeks 1-2 | Can save/load entities from MySQL |
| **MVP 2** | Query Building + Testing    | Weeks 3-4 | Can execute complex queries       |
| **MVP 3** | Multi-DB + Production Ready | Weeks 5-6 | Framework ready for submission    |

### Concurrent Development Model

- **3 parallel workstreams** with minimal dependencies
- **Daily synchronization** through standup meetings
- **Weekly integration** at end of each sprint
- **Shared codebase** with feature branches

---

## Team Structure

### Role Distribution

| Developer | Primary Focus                       | Secondary Focus             |
| --------- | ----------------------------------- | --------------------------- |
| **Dev A** | Core Engine & Session Management    | Transaction & Integration   |
| **Dev B** | Query Building & SQL Generation     | Error Handling & Validation |
| **Dev C** | Database Dialects & Connection Pool | Testing & Demo App          |

### Workload Balance

- **Dev A:** 33% architecture + 67% implementation
- **Dev B:** 100% implementation (query-heavy)
- **Dev C:** 70% implementation + 30% DevOps/testing

---

## MVP Stages

### MVP 1: Basic ORM (Weeks 1-2)

**Goal:** Simple entity persistence with MySQL

**Features:**

- ✅ Entity annotations (@Entity, @Table, @Column, @Id)
- ✅ Configuration from properties file
- ✅ Session & SessionFactory
- ✅ Basic CRUD (save, find, update, delete)
- ✅ MySQL dialect only

**Deliverable:** Can persist a User entity to MySQL

**Acceptance Test:**

```java
User user = new User("John", "john@example.com");
session.save(user);  // Works
User found = session.find(User.class, 1);  // Works
```

---

### MVP 2: Query Building (Weeks 3-4)

**Goal:** Advanced query capabilities

**Features:**

- ✅ Query builder API
- ✅ WHERE clause (AND, OR, comparison operators)
- ✅ GROUP BY with aggregate functions
- ✅ HAVING clause
- ✅ ORDER BY + pagination
- ✅ Parameter binding & SQL injection prevention

**Deliverable:** Can execute complex filtered queries

**Acceptance Test:**

```java
List<User> users = session.createQuery(User.class)
    .where("age", ">", 18)
    .andWhere("city", "=", "Hanoi")
    .orderBy("name", Order.ASC)
    .limit(10)
    .execute();
```

---

### MVP 3: Production Ready (Weeks 5-6)

**Goal:** Multi-database support + polish

**Features:**

- ✅ PostgreSQL dialect
- ✅ SQL Server dialect
- ✅ Transaction management (commit/rollback)
- ✅ Connection pooling optimization
- ✅ Comprehensive error handling
- ✅ Full documentation + demo

**Deliverable:** Framework works with all 3 databases

**Acceptance Test:**

- Switch between MySQL, PostgreSQL, SQL Server via config
- All CRUD + query operations work identically

---

## Milestone Roadmap

```
Timeline: [======================================] 6 weeks

Week 1     Week 2     Week 3     Week 4     Week 5     Week 6
[Setup]    [Core]     [Query]    [Advance]  [Polish]   [Submit]
  ↓          ↓          ↓          ↓          ↓          ↓
  M1         M2         M3         M4         M5         M6
```

### Milestone Details

| Milestone              | Date   | Deliverables                            | Success Criteria                   |
| ---------------------- | ------ | --------------------------------------- | ---------------------------------- |
| **M1: Foundation**     | Dec 13 | Architecture, annotations, basic config | Project builds, interfaces defined |
| **M2: MVP 1**          | Dec 20 | Session management, CRUD, MySQL         | Can persist entities to MySQL      |
| **M3: MVP 2 (Part 1)** | Dec 27 | Query builder, WHERE clause             | Can filter data with WHERE         |
| **M4: MVP 2 (Part 2)** | Jan 3  | GROUP BY, HAVING, ORDER BY              | Complex queries work               |
| **M5: MVP 3**          | Jan 10 | Multi-DB, transactions, polish          | All 3 databases functional         |
| **M6: Submission**     | Jan 15 | Documentation, demo, packaging          | Ready to submit                    |

---

## Sprint Details

### Sprint 1: Foundation (Dec 7-13)

**Goal:** Project setup + core architecture

| Workstream               | Developer | Tasks                                                                                                        | Dependencies |
| ------------------------ | --------- | ------------------------------------------------------------------------------------------------------------ | ------------ |
| **Annotations & Config** | Dev B     | Create @Entity, @Table, @Column, @Id, @GeneratedValue<br>Build Configuration class<br>Properties file parser | None         |
| **Core Interfaces**      | Dev A     | Define Session, SessionFactory, Transaction<br>Create EntityMetadata structure<br>Build reflection utilities | None         |
| **Connection Setup**     | Dev C     | ConnectionManager skeleton<br>MySQL JDBC setup<br>Connection pool basic impl                                 | None         |

**Integration Point:** End of week - all interfaces compile together

**Parallel Work:** ✅ All 3 developers work independently

---

### Sprint 2: MVP 1 - CRUD (Dec 14-20)

**Goal:** Basic CRUD operations with MySQL

| Workstream                 | Developer | Tasks                                                                                                     | Dependencies        |
| -------------------------- | --------- | --------------------------------------------------------------------------------------------------------- | ------------------- |
| **Session Implementation** | Dev A     | SessionFactory implementation<br>Session lifecycle (open/close)<br>EntityMetadata parser using reflection | Sprint 1 (Dev A, B) |
| **SQL Generation**         | Dev B     | INSERT SQL generator<br>SELECT SQL generator<br>UPDATE/DELETE SQL generators                              | Sprint 1 (Dev B)    |
| **MySQL Dialect**          | Dev C     | MySQL-specific SQL syntax<br>Connection pool finalization<br>Type mapping (Java ↔ MySQL)                  | Sprint 1 (Dev C)    |

**Integration Point:** Mid-week sync on SQL generator interface

**Dependencies:**

- Dev A needs Dev B's annotation structure
- Dev B needs Dev A's metadata model
- Dev C works mostly independent

**Solution:** Dev A defines metadata model first (Day 1), Dev B uses it (Day 2+)

---

### Sprint 3: MVP 2 Part 1 - Query Building (Dec 21-27)

**Goal:** WHERE clause + filtering

| Workstream            | Developer | Tasks                                                                                                                                     | Dependencies     |
| --------------------- | --------- | ----------------------------------------------------------------------------------------------------------------------------------------- | ---------------- |
| **Query API**         | Dev A     | Query interface design<br>QueryBuilder class<br>Execute logic integration with Session                                                    | Sprint 2 (Dev A) |
| **WHERE Builder**     | Dev B     | WHERE clause builder<br>Condition handling (AND, OR)<br>Comparison operators (=, !=, <, >, LIKE)<br>Parameter binding & PreparedStatement | Sprint 2 (Dev B) |
| **Testing Framework** | Dev C     | Unit test setup for query builder<br>Integration tests with MySQL<br>Test data generation utilities                                       | Sprint 2 (Dev C) |

**Integration Point:** Day 3 - Query interface shared

**Parallel Work:** ✅ Dev B and C work independently while Dev A builds foundation

---

### Sprint 4: MVP 2 Part 2 - Advanced Queries (Dec 28-Jan 3)

**Goal:** GROUP BY, HAVING, ORDER BY

| Workstream            | Developer | Tasks                                                                                                      | Dependencies     |
| --------------------- | --------- | ---------------------------------------------------------------------------------------------------------- | ---------------- |
| **Aggregation**       | Dev A     | ORDER BY implementation<br>Pagination (LIMIT/OFFSET)<br>Result mapping for aggregates                      | Sprint 3 (Dev A) |
| **GROUP BY & HAVING** | Dev B     | GROUP BY clause builder<br>HAVING clause builder<br>Aggregate function support (COUNT, SUM, AVG, MAX, MIN) | Sprint 3 (Dev B) |
| **Integration Tests** | Dev C     | Complex query test scenarios<br>Edge case testing<br>Performance benchmarks (basic)                        | Sprint 3 (Dev C) |

**Integration Point:** Day 2 - aggregate function interface

**Parallel Work:** ✅ High parallelization, minimal overlap

---

### Sprint 5: MVP 3 - Multi-DB + Polish (Jan 4-10)

**Goal:** PostgreSQL, SQL Server, transactions

| Workstream                 | Developer | Tasks                                                                                                                       | Dependencies     |
| -------------------------- | --------- | --------------------------------------------------------------------------------------------------------------------------- | ---------------- |
| **Transaction Management** | Dev A     | Transaction interface<br>Commit/rollback logic<br>Transaction state management<br>Auto-commit handling                      | Sprint 4 (Dev A) |
| **PostgreSQL Dialect**     | Dev B     | PostgreSQL SQL syntax differences<br>Type mapping for PostgreSQL<br>Testing with PostgreSQL                                 | Sprint 4 (Dev B) |
| **SQL Server Dialect**     | Dev C     | SQL Server SQL syntax differences<br>Type mapping for SQL Server<br>Testing with SQL Server<br>Connection pool optimization | Sprint 4 (Dev C) |

**Integration Point:** Day 4 - cross-database testing

**Parallel Work:** ✅ Each developer owns one database dialect

**Overlap Solution:**

- Dev A defines Dialect interface (Day 1)
- Dev B & C implement for their DBs (Day 2-4)

---

### Sprint 6: Documentation & Submission (Jan 11-15)

**Goal:** Complete documentation + demo

| Workstream             | Developer | Tasks                                                                                                                    | Dependencies |
| ---------------------- | --------- | ------------------------------------------------------------------------------------------------------------------------ | ------------ |
| **Core Documentation** | Dev A     | Class diagrams (full framework)<br>Design pattern documentation (4+ patterns)<br>Architecture overview<br>JavaDoc review | All sprints  |
| **User Documentation** | Dev B     | User guide<br>API reference<br>Code examples<br>Installation guide                                                       | All sprints  |
| **Demo & Packaging**   | Dev C     | Demo application (complete example)<br>Demo video recording<br>Final testing (all DBs)<br>Package for submission         | All sprints  |

**Integration Point:** Daily reviews (Mon-Thu)

**Final Day (Jan 15):** All-hands final review + submission

---

## Dependencies & Parallelization

### Dependency Map

```
Sprint 1 (Foundation)
   ├── Dev A: Interfaces ──────┐
   ├── Dev B: Annotations ─────┼──> Sprint 2 (CRUD)
   └── Dev C: Connection ──────┘
                                   ├── Dev A: Session ──────┐
                                   ├── Dev B: SQL Gen ──────┼──> Sprint 3 (Query)
                                   └── Dev C: MySQL Dialect ┘
                                                                ├── Dev A: Query API ──────┐
                                                                ├── Dev B: WHERE Builder ───┼──> Sprint 4 (Advanced)
                                                                └── Dev C: Tests ──────────┘
                                                                                              ├── Dev A: ORDER BY ───┐
                                                                                              ├── Dev B: GROUP BY ───┼──> Sprint 5 (Multi-DB)
                                                                                              └── Dev C: Integration ┘
                                                                                                                         ├── Dev A: Transaction ┐
                                                                                                                         ├── Dev B: PostgreSQL ─┼──> Sprint 6 (Docs)
                                                                                                                         └── Dev C: SQL Server ─┘
                                                                                                                                                   └──> Submission
```

### Critical Path

1. **Week 1:** Interfaces & annotations (all developers blocked until done)
2. **Week 2:** Session + SQL generation (Dev A → Dev B dependency)
3. **Week 3-4:** Query building (parallel, low dependency)
4. **Week 5:** Multi-DB (parallel, shared Dialect interface)
5. **Week 6:** Documentation (parallel)

### Parallelization Strategy

#### High Parallelization (✅ Minimal blocking)

- **Sprint 3-4:** Query building components are independent
- **Sprint 5:** Each DB dialect is independent
- **Sprint 6:** Documentation can be done in parallel

#### Medium Parallelization (⚠️ Some coordination needed)

- **Sprint 2:** SQL generation needs metadata model from Dev A
- **Sprint 5:** Transaction needs to coordinate with dialects

#### Low Parallelization (🔴 Sequential work)

- **Sprint 1:** Interfaces must be defined before implementation
- **Sprint 2 Day 1-2:** Metadata model must be stable

### Daily Sync Points

**Daily Standup (15 min):**

1. What I completed yesterday
2. What I'm working on today
3. Any blockers or dependencies

**Key Questions:**

- "Is your interface stable for others to use?"
- "Do you need anything from another developer?"

---

## Risk Mitigation

### High-Risk Areas

| Risk                             | Impact | Mitigation Strategy                           | Owner |
| -------------------------------- | ------ | --------------------------------------------- | ----- |
| **Interface changes mid-sprint** | High   | Freeze interfaces by Day 2 of each sprint     | Dev A |
| **Database connectivity issues** | High   | Test all 3 DBs in Sprint 1 (basic connection) | Dev C |
| **Metadata parsing complexity**  | Medium | Use simple reflection first, optimize later   | Dev A |
| **SQL syntax differences**       | Medium | Abstract through Dialect pattern early        | All   |
| **Integration failures**         | High   | Weekly integration builds + tests             | Dev C |

### Contingency Plans

**If behind schedule:**

1. **Defer SQL Server dialect** (keep MySQL + PostgreSQL)
2. **Simplify transaction management** (basic commit/rollback only)
3. **Reduce demo complexity** (simple CRUD demo)

**If ahead of schedule:**

1. **Add relationship mapping** (One-to-Many, Many-to-One)
2. **Implement caching** (simple entity cache)
3. **Performance optimization** (query optimization)

---

## Design Patterns Implementation

### Required Patterns (Minimum 4)

| Pattern       | Location                                | Implementation Sprint | Owner        |
| ------------- | --------------------------------------- | --------------------- | ------------ |
| **Factory**   | SessionFactory                          | Sprint 2              | Dev A        |
| **Singleton** | Configuration                           | Sprint 1              | Dev B        |
| **Strategy**  | Dialect (MySQL, PostgreSQL, SQL Server) | Sprint 2, 5           | Dev C, Dev B |
| **Builder**   | QueryBuilder                            | Sprint 3              | Dev B        |

### Optional Patterns (If Time Permits)

| Pattern             | Location                | Benefit     |
| ------------------- | ----------------------- | ----------- |
| **Template Method** | Base CRUD operations    | Code reuse  |
| **Proxy**           | Lazy loading            | Performance |
| **Adapter**         | JDBC driver abstraction | Flexibility |

---

## Weekly Integration Checkpoints

### Week 1 Review (Dec 13)

- ✅ All interfaces compile
- ✅ Annotations functional
- ✅ MySQL connection successful

### Week 2 Review (Dec 20)

- ✅ Can save/load entity from MySQL
- ✅ All CRUD operations work
- ✅ Unit tests passing

### Week 3 Review (Dec 27)

- ✅ WHERE clause functional
- ✅ Parameter binding works
- ✅ SQL injection prevented

### Week 4 Review (Jan 3)

- ✅ GROUP BY + HAVING work
- ✅ ORDER BY + pagination work
- ✅ Complex queries execute

### Week 5 Review (Jan 10)

- ✅ All 3 databases work
- ✅ Transactions functional
- ✅ Feature freeze

### Week 6 Review (Jan 15)

- ✅ Documentation complete
- ✅ Demo ready
- ✅ **SUBMISSION**

---

## Success Criteria

### MVP 1 Success (Week 2)

```java
// This code must work:
Configuration config = new Configuration()
    .setUrl("jdbc:mysql://localhost:3306/test")
    .setUsername("root")
    .setPassword("password")
    .addAnnotatedClass(User.class);

SessionFactory factory = config.buildSessionFactory();
Session session = factory.openSession();

User user = new User("John", "john@email.com");
session.save(user);

User found = session.find(User.class, 1);
System.out.println(found.getName()); // "John"

session.close();
```

### MVP 2 Success (Week 4)

```java
// This code must work:
List<User> adults = session.createQuery(User.class)
    .where("age", ">=", 18)
    .andWhere("city", "=", "Hanoi")
    .orderBy("name", Order.ASC)
    .limit(10)
    .execute();

List<Object[]> stats = session.createQuery(User.class)
    .select("city", "COUNT(*)")
    .groupBy("city")
    .having("COUNT(*)", ">", 5)
    .execute();
```

### MVP 3 Success (Week 6)

```java
// This code must work with MySQL, PostgreSQL, SQL Server:
Configuration config = new Configuration()
    .setDialect(DialectType.POSTGRESQL)  // or MYSQL, SQLSERVER
    .setUrl("jdbc:postgresql://localhost:5432/test")
    // ... same code works for all DBs

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

## Final Deliverables

### Code

- ✅ `dam-framework` module (main framework)
- ✅ `dam-demo` module (demonstration app)
- ✅ Unit tests (>80% coverage)
- ✅ Integration tests

### Documentation

- ✅ Class diagrams (UML)
- ✅ Design pattern documentation
- ✅ User guide
- ✅ Installation guide
- ✅ JavaDoc (100% public APIs)

### Demo

- ✅ Working demo application
- ✅ Demo video (5-10 minutes)
- ✅ Features checklist

### Submission Package

```
MSSV1-MSSV2-MSSV3.zip/
├── 1.Documents/
│   ├── Class_Diagrams.pdf
│   ├── Design_Patterns.pdf
│   ├── User_Guide.pdf
│   └── Installation_Guide.pdf
├── 2.Source_code/
│   ├── dam-framework/
│   └── dam-demo/
├── 3.Functions_List/
│   └── Features_Checklist.xlsx
└── 4.Others/
    └── demo_video.mp4
```

---

**Project Timeline:** Dec 7, 2024 - Jan 15, 2025  
**Last Updated:** December 6, 2024  
**Status:** Ready to execute
