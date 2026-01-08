# Run MySQL

docker-compose up mysql -d

# Rebuild framework and demo

cd ..\dam-framework
mvn clean install -DskipTests

cd ..\dam-demo
mvn clean package -DskipTests

# Run the demo program

java -jar target\dam-demo-app.jar

# Connect to MySQL to show db

docker exec -it dam-mysql mysql -u root -prootpassword dam_demo

## Then run SQL commands:

SHOW TABLES;
SELECT _ FROM users;
SELECT _ FROM products;
SELECT _ FROM orders;
SELECT _ FROM sessions;

## Exit when done

exit
