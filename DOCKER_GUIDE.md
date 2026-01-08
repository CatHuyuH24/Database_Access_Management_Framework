# DAM Framework - Docker Demo Guide

## 🚀 Quick Start

### Prerequisites

- Docker installed
- Docker Compose installed

### Step 1: Start the Application

```bash
# Navigate to project root
cd Database_Access_Management_Framework

# Start MySQL and Demo App
docker-compose up --build
```

The application will:

1. Pull MySQL 8.0 image
2. Build the DAM framework
3. Build the demo application
4. Initialize the database with tables and sample data
5. Start the interactive demo

### Step 2: Interact with the Demo

Once you see the main menu, navigate using numbers:

```
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

## 🔍 Viewing Database Without External Tools

### Method 1: Using MySQL Client in Demo Container

```bash
# Open a new terminal while demo is running
docker exec -it dam-demo-app sh

# Connect to MySQL
mysql -h mysql -u root -prootpassword dam_demo

# Run SQL queries
mysql> SHOW TABLES;
mysql> SELECT * FROM users;
mysql> SELECT * FROM products;
mysql> DESCRIBE users;
mysql> exit
```

### Method 2: Using MySQL Container Directly

```bash
# Open a new terminal
docker exec -it dam-mysql mysql -u root -prootpassword dam_demo

# Run queries
mysql> SELECT * FROM users;
mysql> SELECT * FROM products;
mysql> SELECT COUNT(*) FROM users;
```

### Method 3: View Logs and Database State

```bash
# View MySQL logs
docker logs dam-mysql

# View demo app logs
docker logs dam-demo-app
```

## 📊 Useful SQL Commands

### View All Tables

```sql
SHOW TABLES;
```

### View Table Structure

```sql
DESCRIBE users;
DESCRIBE products;
DESCRIBE orders;
DESCRIBE sessions;
```

### View All Data

```sql
SELECT * FROM users;
SELECT * FROM products;
SELECT * FROM orders;
SELECT * FROM sessions;
```

### View Specific Data

```sql
-- Users by age
SELECT username, email, age FROM users WHERE age > 25;

-- Products by price
SELECT name, price FROM products WHERE price < 100;

-- Count records
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_products FROM products;
```

### Monitor Changes in Real-Time

```sql
-- In one terminal, run the demo
-- In another terminal, watch database changes:
SELECT * FROM users ORDER BY id DESC LIMIT 10;

-- Refresh by re-running the query after operations
```

## 🛠️ Management Commands

### Stop the Application

```bash
# Stop containers (keeps data)
docker-compose down

# Stop and remove all data
docker-compose down -v
```

### Restart the Application

```bash
# Restart without rebuilding
docker-compose restart

# Rebuild and restart
docker-compose up --build
```

### View Running Containers

```bash
docker ps
```

### View Container Logs

```bash
# Follow demo app logs
docker logs -f dam-demo-app

# Follow MySQL logs
docker logs -f dam-mysql
```

### Access Container Shell

```bash
# Access demo app container
docker exec -it dam-demo-app sh

# Access MySQL container
docker exec -it dam-mysql bash
```

## 🎯 Demo Features

### 1. User Management

- Create users interactively
- List all users
- Update user information
- Delete users

### 2. Product Management

- Create products with names and prices
- List all products
- Update product prices
- Delete products

### 3. Demo All Features

- Automated demonstration of:
  - CRUD operations
  - Transactions
  - Query operations

### 4. Query & Search

- Find users by age range
- Find products by price range
- Custom filtering

### 5. Transaction Examples

- Successful commit demo
- Rollback demonstration
- See how transactions work

### 6. View All Data

- Quick overview of database state
- Total counts of entities

## 🔄 Database Operations Flow

```
Demo App Container (Java)
    ↓ JDBC Connection
MySQL Container
    ↓ MySQL Client
Your Terminal (SQL Queries)
```

## 📝 Example Workflow

1. **Start the demo:**

   ```bash
   docker-compose up --build
   ```

2. **In the demo, choose option 1 (User Management)**

   - Choose 1 to create a user
   - Enter: username="john", email="john@test.com", age=28

3. **In another terminal, verify in database:**

   ```bash
   docker exec -it dam-mysql mysql -u root -prootpassword -e \
     "SELECT * FROM dam_demo.users WHERE username='john';"
   ```

4. **Back in demo, update the user (option 3)**

   - Change email to "john.doe@test.com"

5. **Verify the update:**
   ```bash
   docker exec -it dam-mysql mysql -u root -prootpassword -e \
     "SELECT username, email FROM dam_demo.users WHERE username='john';"
   ```

## 🐛 Troubleshooting

### MySQL Connection Refused

```bash
# Wait for MySQL to be ready
docker logs dam-mysql

# Look for: "ready for connections"
```

### Reset Everything

```bash
# Stop and remove all containers, networks, volumes
docker-compose down -v

# Rebuild from scratch
docker-compose up --build
```

### View Detailed Logs

```bash
# All services
docker-compose logs

# Specific service
docker-compose logs demo-app
docker-compose logs mysql
```

### Database Not Initialized

```bash
# Check init script execution
docker exec -it dam-mysql cat /docker-entrypoint-initdb.d/init.sql

# Manually run init script
docker exec -it dam-mysql mysql -u root -prootpassword dam_demo < init.sql
```

## 🎓 Learning Exercises

### Exercise 1: CRUD Operations

1. Create 5 users via the demo
2. Query them using MySQL client
3. Update 2 users via demo
4. Verify changes in MySQL
5. Delete 1 user via demo
6. Confirm deletion in MySQL

### Exercise 2: Transaction Testing

1. Start a transaction demo (option 5)
2. Choose rollback example
3. In MySQL, verify the user was NOT created
4. Try commit example
5. Verify the user WAS created

### Exercise 3: Query Exploration

1. Create 10 products with various prices
2. Use demo query feature to find products under $50
3. In MySQL, verify with: `SELECT * FROM products WHERE price < 50;`
4. Compare results

## 📚 Additional Resources

### View Framework Tests

```bash
# Run framework tests in container
docker exec -it dam-demo-app sh
cd dam-framework
mvn test
```

### Inspect Configuration

```bash
# View database configuration
docker exec -it dam-demo-app cat /app/dam-demo/src/main/resources/dam.properties.example
```

### Export Database

```bash
# Backup database
docker exec dam-mysql mysqldump -u root -prootpassword dam_demo > backup.sql

# Restore database
docker exec -i dam-mysql mysql -u root -prootpassword dam_demo < backup.sql
```

---

## 🎉 Success Criteria

✅ MySQL container running  
✅ Demo app container running  
✅ Database tables created  
✅ Sample data loaded  
✅ Interactive demo accessible  
✅ Can query database via MySQL client  
✅ All CRUD operations working  
✅ Transactions working (commit/rollback)

**Enjoy exploring the DAM Framework! 🚀**
