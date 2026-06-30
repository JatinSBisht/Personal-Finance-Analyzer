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

1. Create the database:

```sql
create database finance_analyzer;
```

2. Install Python ML dependencies:

```powershell
cd finance-analyzer/ml
python -m pip install -r requirements.txt
```

3. Start the Spring Boot app with the bundled local Maven:

```powershell
cd finance-analyzer/backend
..\..\tools\apache-maven-3.9.11\bin\mvn.cmd spring-boot:run
```

4. Open `http://localhost:8080/register`.

Default database credentials are in `backend/src/main/resources/application.properties`.

## Resume Line

Built a full-stack financial management application using Java Spring Boot, PostgreSQL, and Chart.js with secure authentication, transaction management, interactive dashboards, and machine learning-based expense prediction.
