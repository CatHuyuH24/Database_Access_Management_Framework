# 🚀 DAM Framework Interactive Demo - Quick Start Guide

## ✅ What You Get

1. **Interactive Command-Line Demo** - Navigate with number keys
2. **Docker MySQL Database** - Pre-configured and ready
3. **Complete CRUD Operations** - Create, Read, Update, Delete
4. **Transaction Management** - Commit & Rollback examples
5. **Query Builder** - Search and filter data
6. **All 4 Entity Types** - Users, Products, Orders, Sessions

---

## 📋 Step-by-Step Instructions

### Step 1: Start Docker Containers

```bash
cd "d:\University\Design_pattern_application_HK2_25\Database_Access_Management_Framework"

docker-compose up --build
```

**What happens:**

- Pulls MySQL 8.0 image
- Builds your DAM framework
- Builds the interactive demo
- Initializes database with tables
- Starts the demo application

**You'll see:**

```
╔═══════════════════════════════════════════════════════════════╗
║        DAM Framework - Interactive Demo Application          ║
║        Database Access Management Framework v1.0              ║
╚═══════════════════════════════════════════════════════════════╝

🔧 Initializing DAM Framework...
✅ Framework initialized successfully!

╔════════════════════════════════════════════════════════════╗
║              DAM FRAMEWORK - MAIN MENU                     ║
╚════════════════════════════════════════════════════════════╝

  [1] 👥 User Management
  [2] 📦 Product Management
  [3] 🧪 Demo All Features
  [4] 🔍 Query & Search
  [5] 💾 Transaction Examples
  [6] 📊 View All Data
  [0] 🚪 Exit

👉 Choose an option:
```

### Step 2: Try the Demo

**Option 1: Create a User**

```
1 → 1 → Enter username, email, age
```

**Option 3: See Everything**

```
3 → Watch automated demo of all features
```

**Option 6: View Database State**

```
6 → See total users and products
```

### Step 3: View Database (Without External Tools)

**Open a NEW terminal** while demo is running:

```bash
# Method 1: From demo container
docker exec -it dam-demo-app mysql -h mysql -u root -prootpassword dam_demo

# Method 2: Directly from MySQL container
docker exec -it dam-mysql mysql -u root -prootpassword dam_demo
```

**Once in MySQL:**

```sql
-- See all tables
SHOW TABLES;

-- View data
SELECT * FROM users;
SELECT * FROM products;
SELECT * FROM orders;
SELECT * FROM sessions;

-- Count records
SELECT COUNT(*) FROM users;

-- Exit MySQL
exit
```

### Step 4: Monitor Changes in Real-Time

**Terminal 1:** Run the demo, create users

**Terminal 2:** Watch database changes

```bash
docker exec -it dam-mysql mysql -u root -prootpassword -e "SELECT * FROM dam_demo.users ORDER BY id DESC LIMIT 5;"
```

---

## 🎮 Interactive Menu Guide

### 👥 User Management (Option 1)

```
[1] Create User    - Add new users
[2] List All       - View all users
[3] Update User    - Modify user data
[4] Delete User    - Remove users
```

### 📦 Product Management (Option 2)

```
[1] Create Product     - Add products
[2] List All           - View inventory
[3] Update Price       - Change pricing
[4] Delete Product     - Remove items
```

### 🧪 Demo All Features (Option 3)

- Automated demonstration
- Creates sample data
- Shows CRUD operations
- Demonstrates transactions

### 🔍 Query & Search (Option 4)

- Find users by age range
- Find products by price
- Custom filtering

### 💾 Transaction Examples (Option 5)

- Successful commit demo
- Rollback demonstration

### 📊 View All Data (Option 6)

- Quick database overview
- Entity counts

---

## 🔍 SQL Cheat Sheet for Database Viewing

```sql
-- View table structures
DESCRIBE users;
DESCRIBE products;
DESCRIBE orders;
DESCRIBE sessions;

-- View all data
SELECT * FROM users;
SELECT * FROM products;

-- Filtered queries
SELECT username, email, age FROM users WHERE age > 25;
SELECT name, price FROM products WHERE price < 100;

-- Counts
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_products FROM products;

-- Recent entries
SELECT * FROM users ORDER BY id DESC LIMIT 5;

-- Specific user
SELECT * FROM users WHERE username = 'john_doe';
```

---

## 🛠️ Management Commands

### Stop Everything

```bash
docker-compose down
```

### Stop and Delete All Data

```bash
docker-compose down -v
```

### Restart

```bash
docker-compose restart
```

### View Logs

```bash
# Demo application logs
docker logs -f dam-demo-app

# MySQL logs
docker logs -f dam-mysql
```

### List Containers

```bash
docker ps
```

---

## 💡 Example Workflow

1. **Start Docker:**

   ```bash
   docker-compose up --build
   ```

2. **In Demo Menu (Terminal 1):**

   - Press `1` (User Management)
   - Press `1` (Create User)
   - Enter: `john`, `john@test.com`, `28`

3. **Verify in Database (Terminal 2):**

   ```bash
   docker exec -it dam-mysql mysql -u root -prootpassword -e \
     "SELECT * FROM dam_demo.users WHERE username='john';"
   ```

4. **Update in Demo:**

   - Press `3` (Update User)
   - Enter ID of john
   - Change email

5. **Verify Update:**
   ```bash
   docker exec -it dam-mysql mysql -u root -prootpassword -e \
     "SELECT username, email FROM dam_demo.users WHERE username='john';"
   ```

---

## 🐛 Troubleshooting

### Connection Refused

```bash
# Wait for MySQL to start (check logs)
docker logs dam-mysql | grep "ready for connections"
```

### Reset Everything

```bash
docker-compose down -v
docker-compose up --build
```

### Access Container Shell

```bash
# Demo app container
docker exec -it dam-demo-app sh

# MySQL container
docker exec -it dam-mysql bash
```

### Check Init Script

```bash
docker exec -it dam-mysql cat /docker-entrypoint-initdb.d/init.sql
```

---

## 📊 Database Schema

### Users Table

| Column   | Type         |
| -------- | ------------ |
| id       | BIGINT (PK)  |
| username | VARCHAR(255) |
| email    | VARCHAR(255) |
| age      | INT          |
| status   | VARCHAR(50)  |

### Products Table

| Column | Type         |
| ------ | ------------ |
| id     | BIGINT (PK)  |
| name   | VARCHAR(255) |
| price  | DOUBLE       |

### Orders Table

| Column        | Type         |
| ------------- | ------------ |
| id            | BIGINT (PK)  |
| order_number  | VARCHAR(255) |
| customer_name | VARCHAR(255) |
| total_amount  | DOUBLE       |

### Sessions Table

| Column     | Type         |
| ---------- | ------------ |
| id         | VARCHAR(36)  |
| user_id    | BIGINT       |
| token      | VARCHAR(255) |
| created_at | DATETIME     |

---

## 🎯 Learning Exercises

### Exercise 1: Full CRUD Cycle

1. Create 3 users via demo
2. List them in MySQL: `SELECT * FROM users;`
3. Update one user
4. Delete one user
5. Verify in MySQL

### Exercise 2: Transaction Testing

1. Run transaction commit demo
2. Verify user created: `SELECT * FROM users ORDER BY id DESC LIMIT 1;`
3. Run rollback demo
4. Verify user NOT created

### Exercise 3: Query Comparison

1. Create 10 products with various prices
2. Use demo query: "Find products under $50"
3. Compare with SQL: `SELECT * FROM products WHERE price < 50;`

---

## 📁 Project Files

```
Database_Access_Management_Framework/
├── docker-compose.yml           # Docker orchestration
├── Dockerfile                   # Application container
├── init.sql                     # Database initialization
├── DOCKER_GUIDE.md             # Detailed guide
├── dam-framework/              # Core framework
└── dam-demo/
    └── src/main/java/com/dam/demo/
        ├── InteractiveDemoApplication.java  # Main demo
        ├── SimpleDemoApplication.java       # Simple demo
        └── entity/
            ├── User.java
            ├── Product.java
            ├── Order.java
            └── Session.java
```

---

## ✅ Success Checklist

- [ ] Docker containers running (`docker ps`)
- [ ] MySQL initialized (`docker logs dam-mysql`)
- [ ] Demo menu visible
- [ ] Can create users/products
- [ ] Can query database via MySQL client
- [ ] Transactions work (commit/rollback)
- [ ] All CRUD operations functional

---

## 🎉 You're All Set!

**Enjoy exploring the DAM Framework!**

For detailed information, see [DOCKER_GUIDE.md](DOCKER_GUIDE.md)

Questions? Check the troubleshooting section or container logs.
