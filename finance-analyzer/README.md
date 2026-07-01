# Smart Personal Finance Analyzer

A full-stack personal finance dashboard built with Java 21, Spring Boot 3, PostgreSQL, Thymeleaf, Bootstrap 5, Chart.js, and a Python linear regression script for next-month expense prediction.

## Features

- Register, login, and logout with Spring Security
- Dashboard with total income, total expenses, current balance, recent transactions, charts, and ML prediction
- Add, edit, delete, search, and filter transactions
- Monthly and category-wise reports
- PostgreSQL schema and seed data
- Postman collection for API testing

## Run Locally

0. check
finance-analyzer/backend/src/main/resources/application.properties
whether its password or root user is their or not


1. Log in to MySQL

Type your root password when prompted. If it works, you'll see a mysql> prompt  that means MySQL is running correctly.

2.  Basic commands once you're inside
```
sql
```
SHOW DATABASES;                          -- list all databases
CREATE DATABASE finance_analyzer;        -- create a new database
USE finance_analyzer;                    -- switch into it
SHOW TABLES;                             -- list tables in current database
SELECT * FROM transactions;              -- view data in a table
```

3. Run the backend
Open a terminal, navigate to the backend folder, and run:

bash.cd 
```finance-analyzer/backend
   mvn clean spring-boot:run
```
This is the actual "connecting" step when the app starts, Spring Boot reads application.properties, opens a connection to your MySQL server using those credentials, creates the database/tables if they don't exist, and starts the web server.

4. Once that's running cleanly, go to `http://localhost:8080` in your browser that's the actual proof the project is talking to MySQL.`.

Default database credentials are in `backend/src/main/resources/application.properties`.

