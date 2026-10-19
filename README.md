# Banking-backend-system

## Overview

A secure banking backend application built with Java and Spring Boot that allows customers to manage their bank accounts, perform financial transactions, and monitor their transaction history through RESTful APIs. The system implements JWT-based authentication, role-based access control, and business rules to ensure secure account access and maintain financial data integrity.

The application supports multiple account types, including current/business accounts and savings accounts, while providing separate customer and administrator functionalities.

## Features

### Authentication and Authorization
- **User Registration:** Customers can register and create a current/business account as part of the registration process.
- **Secure Login:** Users authenticate using their username and password.
- **JWT Authentication:** Generates a JSON Web Token (JWT) after successful authentication, which is used to access protected API endpoints.
- **Access Control:** Customers can access and manage only their own bank accounts and associated transaction records.
- **Role-Based Authorization:** Restricts administrative operations to authorized administrators.
- **Account Deactivation:** Administrators can deactivate customers, preventing them from logging in or accessing protected resources.

### Bank Account Management
- **Current/Business Accounts:** Automatically creates a primary account during customer registration.
- **Savings Accounts:** Customers can open up to five additional savings accounts.
- **Custom Account Names:** Allows customers to assign optional names to savings accounts for easier identification.
- **Account Closure:** Customers can close their savings accounts when they meet the closure requirements.
- **Protected Primary Account:** Customers cannot close their primary account; this operation is restricted to administrators.
- **Account Status Management:** Uses soft deletion to preserve customer and account records instead of permanently deleting them from the database.

### Financial Transactions
- **Deposits:** Allows customers to deposit money into their accounts.
- **Withdrawals:** Allows customers to withdraw funds, provided sufficient funds are available.
- **Internal Transfers:** Supports transferring funds between eligible accounts owned by the customer.
- **External Payments:** Allows customers to make payments to external bank accounts.
- **Transaction History:** Provides access to transaction records for a selected account.
- **Balance Validation:** Prevents withdrawals, transfers, and payments that exceed the available account balance.

### Business Rules and Data Integrity
- Customers cannot withdraw, transfer, or pay more money than their available balance.
- Accounts cannot be closed while they contain a non-zero balance.
- Customers cannot be deactivated while any of their accounts have a non-zero balance.
- Deactivated customers cannot log in.
- Customers are restricted to a maximum of five savings accounts.
- Customers cannot access accounts or transaction records belonging to other customers.
- Primary account closure is restricted to administrators.

These rules are enforced in the backend to maintain consistent account states and prevent invalid operations.

## Technology Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Security:** Spring Security, JWT
- **Data Access:** Spring Data JPA, Hibernate
- **API:** RESTful APIs
- **Database:** MySQL
- **Build Tool:** Maven

## API Documentation

This project uses Swagger UI to document and test the REST API endpoints interactively.

### How to Explore the API

1. Clone the repository and configure the MySQL database.
2. Run the Spring Boot application.
3. Open Swagger UI in your browser:

   `http://localhost:8080/swagger-ui/index.html`

4. Register a user, authenticate, and use the available endpoints to explore the banking features.

**Note:** Some endpoints require JWT authentication. Log in to obtain a token and authorize your requests in Swagger UI before accessing protected endpoints.

## Key Implementation Highlights

- Designed RESTful endpoints for authentication, account management, and financial transactions.
- Implemented JWT-based authentication and authorization for protected resources.
- Applied ownership-based access control to protect customer financial information.
- Used Spring Data JPA and Hibernate for database persistence and entity relationship management.
- Implemented soft deletion to preserve customer and account records for historical tracking.
- Enforced financial business rules, including balance validation and account closure restrictions.
- Separated customer operations from administrative operations through access control.

## Project Goals

The goal of this project was to build a secure and maintainable banking backend that demonstrates practical knowledge of Java backend development, Spring Boot, API security, relational database design, and the enforcement of financial business rules.

