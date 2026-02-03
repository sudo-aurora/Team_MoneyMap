# MoneyMap Portfolio Manager API

A comprehensive REST API for managing investment portfolios, built with Spring Boot. Designed for asset managers handling ~30-35 clients with support for Gold, Stocks, Mutual Funds, and Cryptocurrencies. Includes **Payment Processing** and **Transaction Monitoring & Alerts** systems.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Sample API Requests](#sample-api-requests)
  - [Client Management](#1-client-management)
  - [Portfolio Management](#2-portfolio-management)
  - [Asset Management](#3-asset-management)
  - [Transaction Management](#4-transaction-management)
  - [Payment Processing](#5-payment-processing)
  - [Monitoring Rules](#6-monitoring-rules)
  - [Alert Management](#7-alert-management)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)

---

## Prerequisites

1. **Java 21** - [Download from Adoptium](https://adoptium.net/temurin/releases/?version=21)
2. **MySQL 8.x** - Running on localhost:3306
3. **Maven** (included via wrapper)

## Quick Start

### 1. Start MySQL Server

Make sure MySQL is running:
```cmd
net start MySQL80
```

### 2. Configure Database (if needed)

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moneymap_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 3. Build the Application

```cmd
.\mvnw.cmd clean package -DskipTests
```

### 4. Run the Application

```cmd
java -jar target\MoneyMap-0.0.1-SNAPSHOT.jar
```

### 5. Access the Application

| Resource | URL |
|----------|-----|
| **Swagger UI** | http://localhost:8181/swagger-ui.html |
| **API Docs (JSON)** | http://localhost:8181/api-docs |
| **Base API URL** | http://localhost:8181/api/v1 |

---

## API Documentation

Once running, visit **http://localhost:8181/swagger-ui.html** for interactive API documentation.

---

## Sample API Requests

Below are curl commands to test all APIs. Execute them in order to build up data.

### 1. Client Management

#### Create Client 1
```cmd
curl -X POST http://localhost:8181/api/v1/clients -H "Content-Type: application/json" -d "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"phone\": \"+1-555-123-4567\", \"address\": \"123 Wall Street, New York, NY 10005\"}"
```

#### Create Client 2
```cmd
curl -X POST http://localhost:8181/api/v1/clients -H "Content-Type: application/json" -d "{\"firstName\": \"Jane\", \"lastName\": \"Smith\", \"email\": \"jane.smith@example.com\", \"phone\": \"+1-555-987-6543\", \"address\": \"456 Market Street, San Francisco, CA 94102\"}"
```

#### Create Client 3
```cmd
curl -X POST http://localhost:8181/api/v1/clients -H "Content-Type: application/json" -d "{\"firstName\": \"Robert\", \"lastName\": \"Johnson\", \"email\": \"robert.j@example.com\", \"phone\": \"+1-555-456-7890\", \"address\": \"789 Lake Shore Drive, Chicago, IL 60601\"}"
```

#### Get All Clients
```cmd
curl -X GET "http://localhost:8181/api/v1/clients?page=0&size=10"
```

#### Get Client by ID
```cmd
curl -X GET http://localhost:8181/api/v1/clients/1
```

#### Get Client with Portfolios
```cmd
curl -X GET http://localhost:8181/api/v1/clients/1/portfolios
```

#### Search Clients
```cmd
curl -X GET "http://localhost:8181/api/v1/clients/search?query=john"
```

#### Update Client
```cmd
curl -X PUT http://localhost:8181/api/v1/clients/1 -H "Content-Type: application/json" -d "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe.updated@example.com\", \"phone\": \"+1-555-111-2222\", \"address\": \"100 Park Avenue, New York, NY 10017\"}"
```

#### Deactivate Client
```cmd
curl -X PATCH http://localhost:8181/api/v1/clients/3/deactivate
```

#### Activate Client
```cmd
curl -X PATCH http://localhost:8181/api/v1/clients/3/activate
```

#### Get Active Client Count
```cmd
curl -X GET http://localhost:8181/api/v1/clients/count/active
```

#### Delete Client
```cmd
curl -X DELETE http://localhost:8181/api/v1/clients/3
```

---

### 2. Portfolio Management

#### Create Portfolio for Client 1 - Growth Portfolio
```cmd
curl -X POST http://localhost:8181/api/v1/portfolios -H "Content-Type: application/json" -d "{\"name\": \"Growth Portfolio\", \"description\": \"High-growth tech stocks and crypto investments\", \"clientId\": 1}"
```

#### Create Portfolio for Client 1 - Conservative Portfolio
```cmd
curl -X POST http://localhost:8181/api/v1/portfolios -H "Content-Type: application/json" -d "{\"name\": \"Conservative Portfolio\", \"description\": \"Gold and stable mutual funds for wealth preservation\", \"clientId\": 1}"
```

#### Create Portfolio for Client 2
```cmd
curl -X POST http://localhost:8181/api/v1/portfolios -H "Content-Type: application/json" -d "{\"name\": \"Balanced Portfolio\", \"description\": \"Mix of stocks, gold, and mutual funds\", \"clientId\": 2}"
```

#### Get All Portfolios
```cmd
curl -X GET "http://localhost:8181/api/v1/portfolios?page=0&size=10"
```

#### Get Portfolio by ID
```cmd
curl -X GET http://localhost:8181/api/v1/portfolios/1
```

#### Get Portfolio with Assets
```cmd
curl -X GET http://localhost:8181/api/v1/portfolios/1/assets
```

#### Get Portfolios by Client
```cmd
curl -X GET http://localhost:8181/api/v1/portfolios/client/1
```

#### Get Active Portfolios by Client
```cmd
curl -X GET http://localhost:8181/api/v1/portfolios/client/1/active
```

#### Search Portfolios
```cmd
curl -X GET "http://localhost:8181/api/v1/portfolios/search?query=growth"
```

#### Get Total Value for Client
```cmd
curl -X GET http://localhost:8181/api/v1/portfolios/client/1/total-value
```

#### Update Portfolio
```cmd
curl -X PUT http://localhost:8181/api/v1/portfolios/1 -H "Content-Type: application/json" -d "{\"name\": \"Aggressive Growth Portfolio\", \"description\": \"Updated: Focus on high-risk, high-reward investments\", \"clientId\": 1}"
```

#### Recalculate Portfolio Value
```cmd
curl -X POST http://localhost:8181/api/v1/portfolios/1/recalculate
```

#### Deactivate Portfolio
```cmd
curl -X PATCH http://localhost:8181/api/v1/portfolios/2/deactivate
```

#### Activate Portfolio
```cmd
curl -X PATCH http://localhost:8181/api/v1/portfolios/2/activate
```

---

### 3. Asset Management

#### Get All Asset Types
```cmd
curl -X GET http://localhost:8181/api/v1/assets/types
```

#### Add Stock Asset - Apple
```cmd
curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Apple Inc.\", \"symbol\": \"AAPL\", \"assetType\": \"STOCK\", \"quantity\": 100, \"purchasePrice\": 150.00, \"currentPrice\": 175.50, \"purchaseDate\": \"2024-01-15\", \"portfolioId\": 1, \"notes\": \"Long-term hold\"}"
```

#### Add Stock Asset - Tesla
```cmd
curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Tesla Inc.\", \"symbol\": \"TSLA\", \"assetType\": \"STOCK\", \"quantity\": 50, \"purchasePrice\": 200.00, \"currentPrice\": 250.00, \"purchaseDate\": \"2024-02-20\", \"portfolioId\": 1, \"notes\": \"EV sector bet\"}"
```

#### Add Stock Asset - Microsoft
```cmd
curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Microsoft Corp\", \"symbol\": \"MSFT\", \"assetType\": \"STOCK\", \"quantity\": 75, \"purchasePrice\": 320.00, \"currentPrice\": 380.00, \"purchaseDate\": \"2024-03-10\", \"portfolioId\": 1, \"notes\": \"Cloud computing leader\"}"
```

#### Add Crypto Asset - Bitcoin
```cmd
curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Bitcoin\", \"symbol\": \"BTC\", \"assetType\": \"CRYPTO\", \"quantity\": 2.5, \"purchasePrice\": 40000.00, \"currentPrice\": 45000.00, \"purchaseDate\": \"2024-03-01\", \"portfolioId\": 1, \"notes\": \"Crypto allocation\"}"
```

#### Add Crypto Asset - Ethereum
```cmd
curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Ethereum\", \"symbol\": \"ETH\", \"assetType\": \"CRYPTO\", \"quantity\": 10, \"purchasePrice\": 2500.00, \"currentPrice\": 3000.00, \"purchaseDate\": \"2024-03-15\", \"portfolioId\": 1, \"notes\": \"Smart contract platform\"}"
```

#### Add Gold Asset
```cmd
curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Gold Bullion\", \"symbol\": \"GLD\", \"assetType\": \"GOLD\", \"quantity\": 10, \"purchasePrice\": 1950.00, \"currentPrice\": 2050.00, \"purchaseDate\": \"2024-01-10\", \"portfolioId\": 2, \"notes\": \"Safe haven asset\"}"
```

#### Add Mutual Fund Asset
```cmd
curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Vanguard 500 Index Fund\", \"symbol\": \"VFIAX\", \"assetType\": \"MUTUAL_FUND\", \"quantity\": 200, \"purchasePrice\": 400.00, \"currentPrice\": 425.00, \"purchaseDate\": \"2024-02-01\", \"portfolioId\": 2, \"notes\": \"Index fund for diversification\"}"
```

#### Add Asset to Client 2's Portfolio
```cmd
curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Amazon\", \"symbol\": \"AMZN\", \"assetType\": \"STOCK\", \"quantity\": 30, \"purchasePrice\": 140.00, \"currentPrice\": 175.00, \"purchaseDate\": \"2024-04-01\", \"portfolioId\": 3, \"notes\": \"E-commerce giant\"}"
```

#### Get All Assets
```cmd
curl -X GET "http://localhost:8181/api/v1/assets?page=0&size=10"
```

#### Get Asset by ID
```cmd
curl -X GET http://localhost:8181/api/v1/assets/1
```

#### Get Assets by Portfolio
```cmd
curl -X GET http://localhost:8181/api/v1/assets/portfolio/1
```

#### Get Assets by Portfolio (Paginated)
```cmd
curl -X GET "http://localhost:8181/api/v1/assets/portfolio/1/paged?page=0&size=5"
```

#### Get Assets by Type - STOCK
```cmd
curl -X GET http://localhost:8181/api/v1/assets/type/STOCK
```

#### Get Assets by Type - CRYPTO
```cmd
curl -X GET http://localhost:8181/api/v1/assets/type/CRYPTO
```

#### Get Assets by Type - GOLD
```cmd
curl -X GET http://localhost:8181/api/v1/assets/type/GOLD
```

#### Get Assets by Type - MUTUAL_FUND
```cmd
curl -X GET http://localhost:8181/api/v1/assets/type/MUTUAL_FUND
```

#### Get Assets by Client
```cmd
curl -X GET http://localhost:8181/api/v1/assets/client/1
```

#### Search Assets
```cmd
curl -X GET "http://localhost:8181/api/v1/assets/search?query=apple"
```

#### Update Asset
```cmd
curl -X PUT http://localhost:8181/api/v1/assets/1 -H "Content-Type: application/json" -d "{\"name\": \"Apple Inc.\", \"symbol\": \"AAPL\", \"assetType\": \"STOCK\", \"quantity\": 150, \"purchasePrice\": 150.00, \"currentPrice\": 185.00, \"purchaseDate\": \"2024-01-15\", \"portfolioId\": 1, \"notes\": \"Increased position\"}"
```

#### Update Asset Price Only
```cmd
curl -X PATCH "http://localhost:8181/api/v1/assets/1/price?currentPrice=190.00"
```

#### Get Total Value by Asset Type
```cmd
curl -X GET http://localhost:8181/api/v1/assets/type/STOCK/total-value
```

#### Delete Asset
```cmd
curl -X DELETE http://localhost:8181/api/v1/assets/8
```

---

### 4. Transaction Management

#### Get All Transaction Types
```cmd
curl -X GET http://localhost:8181/api/v1/transactions/types
```

#### Record a BUY Transaction
```cmd
curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"BUY\", \"quantity\": 25, \"pricePerUnit\": 178.00, \"fees\": 9.99, \"assetId\": 1, \"transactionDate\": \"2024-06-15T10:30:00\", \"notes\": \"Adding to Apple position\"}"
```

#### Record Another BUY Transaction
```cmd
curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"BUY\", \"quantity\": 10, \"pricePerUnit\": 245.00, \"fees\": 9.99, \"assetId\": 2, \"transactionDate\": \"2024-07-01T11:00:00\", \"notes\": \"Adding to Tesla position\"}"
```

#### Record a SELL Transaction
```cmd
curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"SELL\", \"quantity\": 10, \"pricePerUnit\": 260.00, \"fees\": 9.99, \"assetId\": 2, \"transactionDate\": \"2024-07-20T14:15:00\", \"notes\": \"Taking some Tesla profits\"}"
```

#### Record a DIVIDEND Transaction
```cmd
curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"DIVIDEND\", \"quantity\": 1, \"pricePerUnit\": 96.00, \"fees\": 0, \"assetId\": 1, \"transactionDate\": \"2024-08-15T00:00:00\", \"notes\": \"Quarterly dividend from Apple\"}"
```

#### Record a Crypto BUY
```cmd
curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"BUY\", \"quantity\": 0.5, \"pricePerUnit\": 44000.00, \"fees\": 25.00, \"assetId\": 4, \"transactionDate\": \"2024-09-01T09:00:00\", \"notes\": \"DCA into Bitcoin\"}"
```

#### Record a TRANSFER_IN Transaction
```cmd
curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"TRANSFER_IN\", \"quantity\": 5, \"pricePerUnit\": 2800.00, \"fees\": 0, \"assetId\": 5, \"transactionDate\": \"2024-09-10T10:00:00\", \"notes\": \"Transfer from external wallet\"}"
```

#### Get All Transactions
```cmd
curl -X GET "http://localhost:8181/api/v1/transactions?page=0&size=10"
```

#### Get Transaction by ID
```cmd
curl -X GET http://localhost:8181/api/v1/transactions/1
```

#### Get Transactions by Asset
```cmd
curl -X GET http://localhost:8181/api/v1/transactions/asset/1
```

#### Get Transactions by Asset (Paginated)
```cmd
curl -X GET "http://localhost:8181/api/v1/transactions/asset/1/paged?page=0&size=5"
```

#### Get Transactions by Portfolio
```cmd
curl -X GET http://localhost:8181/api/v1/transactions/portfolio/1
```

#### Get Transactions by Client
```cmd
curl -X GET http://localhost:8181/api/v1/transactions/client/1
```

#### Get Transactions by Type - BUY
```cmd
curl -X GET "http://localhost:8181/api/v1/transactions/type/BUY?page=0&size=10"
```

#### Get Transactions by Type - SELL
```cmd
curl -X GET "http://localhost:8181/api/v1/transactions/type/SELL?page=0&size=10"
```

#### Get Transactions by Date Range
```cmd
curl -X GET "http://localhost:8181/api/v1/transactions/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59&page=0&size=10"
```

#### Update Transaction
```cmd
curl -X PUT http://localhost:8181/api/v1/transactions/1 -H "Content-Type: application/json" -d "{\"transactionType\": \"BUY\", \"quantity\": 30, \"pricePerUnit\": 178.00, \"fees\": 9.99, \"assetId\": 1, \"transactionDate\": \"2024-06-15T10:30:00\", \"notes\": \"Updated: Added more shares\"}"
```

#### Delete Transaction
```cmd
curl -X DELETE http://localhost:8181/api/v1/transactions/6
```

---

### 5. Payment Processing

The Payment Processing module handles financial payments through their complete lifecycle: CREATED → VALIDATED → SENT → COMPLETED (or FAILED).

#### Create a Payment
```cmd
curl -X POST http://localhost:8181/api/v1/payments -H "Content-Type: application/json" -d "{\"idempotencyKey\": \"pay-001\", \"sourceAccount\": \"ACC-001-123456\", \"destinationAccount\": \"ACC-002-789012\", \"amount\": 1500.00, \"currency\": \"USD\", \"reference\": \"Invoice-2026-001\", \"description\": \"Payment for consulting services\"}"
```

#### Create Another Payment
```cmd
curl -X POST http://localhost:8181/api/v1/payments -H "Content-Type: application/json" -d "{\"idempotencyKey\": \"pay-002\", \"sourceAccount\": \"ACC-001-123456\", \"destinationAccount\": \"ACC-003-456789\", \"amount\": 15000.00, \"currency\": \"USD\", \"reference\": \"Invoice-2026-002\", \"description\": \"Large equipment purchase\"}"
```

#### Create High-Value Payment (Triggers Alert)
```cmd
curl -X POST http://localhost:8181/api/v1/payments -H "Content-Type: application/json" -d "{\"sourceAccount\": \"ACC-001-123456\", \"destinationAccount\": \"ACC-004-111222\", \"amount\": 25000.00, \"currency\": \"USD\", \"reference\": \"Wire-001\", \"description\": \"Wire transfer\"}"
```

#### Get All Payments
```cmd
curl -X GET "http://localhost:8181/api/v1/payments?page=0&size=10"
```

#### Get Payment by ID
```cmd
curl -X GET http://localhost:8181/api/v1/payments/1
```

#### Get Payment by Reference
```cmd
curl -X GET http://localhost:8181/api/v1/payments/reference/PAY-1234567890
```

#### Get Payment with Full History
```cmd
curl -X GET http://localhost:8181/api/v1/payments/1/details
```

#### Get Payment Status History
```cmd
curl -X GET http://localhost:8181/api/v1/payments/1/history
```

#### Get Payments by Status
```cmd
curl -X GET "http://localhost:8181/api/v1/payments/status/CREATED?page=0&size=10"
```

#### Get Payments by Source Account
```cmd
curl -X GET "http://localhost:8181/api/v1/payments/account/ACC-001-123456?page=0&size=10"
```

#### Search Payments
```cmd
curl -X GET "http://localhost:8181/api/v1/payments/search?query=invoice"
```

#### Validate Payment (CREATED → VALIDATED)
```cmd
curl -X POST http://localhost:8181/api/v1/payments/1/validate
```

#### Send Payment (VALIDATED → SENT)
```cmd
curl -X POST http://localhost:8181/api/v1/payments/1/send
```

#### Complete Payment (SENT → COMPLETED)
```cmd
curl -X POST http://localhost:8181/api/v1/payments/1/complete
```

#### Fail Payment
```cmd
curl -X POST "http://localhost:8181/api/v1/payments/2/fail?errorCode=INSUFFICIENT_FUNDS&errorMessage=Account%20balance%20too%20low"
```

#### Update Payment Status (Manual)
```cmd
curl -X PUT http://localhost:8181/api/v1/payments/1/status -H "Content-Type: application/json" -d "{\"status\": \"VALIDATED\", \"notes\": \"Manual validation complete\"}"
```

#### Get Payment Count by Status
```cmd
curl -X GET http://localhost:8181/api/v1/payments/count/COMPLETED
```

---

### 6. Monitoring Rules

Monitoring rules define conditions for detecting suspicious transaction patterns and generating alerts.

#### Get All Rule Types
```cmd
curl -X GET http://localhost:8181/api/v1/rules/types
```

#### Create Amount Threshold Rule
```cmd
curl -X POST http://localhost:8181/api/v1/rules -H "Content-Type: application/json" -d "{\"ruleName\": \"High Value Transaction\", \"ruleType\": \"AMOUNT_THRESHOLD\", \"severity\": \"HIGH\", \"active\": true, \"description\": \"Alert when transaction exceeds $10,000\", \"thresholdAmount\": 10000.00, \"thresholdCurrency\": \"USD\"}"
```

#### Create Velocity Rule
```cmd
curl -X POST http://localhost:8181/api/v1/rules -H "Content-Type: application/json" -d "{\"ruleName\": \"Rapid Transactions\", \"ruleType\": \"VELOCITY\", \"severity\": \"MEDIUM\", \"active\": true, \"description\": \"Alert when more than 5 transactions in 10 minutes\", \"maxTransactions\": 5, \"timeWindowMinutes\": 10}"
```

#### Create New Payee Rule
```cmd
curl -X POST http://localhost:8181/api/v1/rules -H "Content-Type: application/json" -d "{\"ruleName\": \"New Payee Detection\", \"ruleType\": \"NEW_PAYEE\", \"severity\": \"LOW\", \"active\": true, \"description\": \"Alert on first transaction to a new payee\", \"lookbackDays\": 90}"
```

#### Create Daily Limit Rule
```cmd
curl -X POST http://localhost:8181/api/v1/rules -H "Content-Type: application/json" -d "{\"ruleName\": \"Daily Limit Exceeded\", \"ruleType\": \"DAILY_LIMIT\", \"severity\": \"HIGH\", \"active\": true, \"description\": \"Alert when daily total exceeds $50,000\", \"dailyLimitAmount\": 50000.00}"
```

#### Get All Rules
```cmd
curl -X GET "http://localhost:8181/api/v1/rules?page=0&size=10"
```

#### Get Rule by ID
```cmd
curl -X GET http://localhost:8181/api/v1/rules/1
```

#### Get Active Rules
```cmd
curl -X GET http://localhost:8181/api/v1/rules/active
```

#### Get Rules by Type
```cmd
curl -X GET "http://localhost:8181/api/v1/rules/type/AMOUNT_THRESHOLD?page=0&size=10"
```

#### Update Rule
```cmd
curl -X PUT http://localhost:8181/api/v1/rules/1 -H "Content-Type: application/json" -d "{\"ruleName\": \"Very High Value Transaction\", \"ruleType\": \"AMOUNT_THRESHOLD\", \"severity\": \"HIGH\", \"active\": true, \"description\": \"Alert when transaction exceeds $15,000\", \"thresholdAmount\": 15000.00, \"thresholdCurrency\": \"USD\"}"
```

#### Deactivate Rule
```cmd
curl -X PATCH http://localhost:8181/api/v1/rules/1/deactivate
```

#### Activate Rule
```cmd
curl -X PATCH http://localhost:8181/api/v1/rules/1/activate
```

#### Delete Rule
```cmd
curl -X DELETE http://localhost:8181/api/v1/rules/4
```

---

### 7. Alert Management

Alerts are generated when transactions trigger monitoring rules. Alert lifecycle: OPEN → ACKNOWLEDGED → INVESTIGATING → CLOSED (or DISMISSED).

#### Get All Alerts
```cmd
curl -X GET "http://localhost:8181/api/v1/alerts?page=0&size=10"
```

#### Get Alert by ID
```cmd
curl -X GET http://localhost:8181/api/v1/alerts/1
```

#### Get Alert with Full Details
```cmd
curl -X GET http://localhost:8181/api/v1/alerts/1/details
```

#### Get Alerts by Status
```cmd
curl -X GET "http://localhost:8181/api/v1/alerts/status/OPEN?page=0&size=10"
```

#### Get Alerts by Severity
```cmd
curl -X GET "http://localhost:8181/api/v1/alerts/severity/HIGH?page=0&size=10"
```

#### Get Alerts by Status and Severity
```cmd
curl -X GET "http://localhost:8181/api/v1/alerts/filter?status=OPEN&severity=HIGH&page=0&size=10"
```

#### Get Alerts by Rule ID
```cmd
curl -X GET "http://localhost:8181/api/v1/alerts/rule/1?page=0&size=10"
```

#### Get Alerts by Account
```cmd
curl -X GET "http://localhost:8181/api/v1/alerts/account/ACC-001-123456?page=0&size=10"
```

#### Get Open Alerts Prioritized (HIGH first)
```cmd
curl -X GET "http://localhost:8181/api/v1/alerts/open/prioritized?limit=20"
```

#### Acknowledge Alert (OPEN → ACKNOWLEDGED)
```cmd
curl -X PUT "http://localhost:8181/api/v1/alerts/1/acknowledge?operatorName=John%20Doe"
```

#### Update Alert Status (Mark as Investigating)
```cmd
curl -X PUT http://localhost:8181/api/v1/alerts/1/status -H "Content-Type: application/json" -d "{\"status\": \"INVESTIGATING\", \"operatorName\": \"John Doe\"}"
```

#### Close Alert
```cmd
curl -X PUT http://localhost:8181/api/v1/alerts/1/status -H "Content-Type: application/json" -d "{\"status\": \"CLOSED\", \"operatorName\": \"John Doe\", \"resolutionNotes\": \"Verified as legitimate business transaction\"}"
```

#### Dismiss Alert (False Positive)
```cmd
curl -X PUT http://localhost:8181/api/v1/alerts/2/status -H "Content-Type: application/json" -d "{\"status\": \"DISMISSED\", \"operatorName\": \"Jane Smith\", \"resolutionNotes\": \"False positive - regular customer transaction\"}"
```

#### Add Resolution Notes
```cmd
curl -X POST "http://localhost:8181/api/v1/alerts/1/notes?notes=Called%20customer%20to%20verify%20transaction"
```

#### Get Alert Statistics
```cmd
curl -X GET http://localhost:8181/api/v1/alerts/statistics
```

#### Get Alert Count by Status
```cmd
curl -X GET http://localhost:8181/api/v1/alerts/count/OPEN
```

---

## Quick Setup Script

Copy and paste these commands one by one to populate your database:

```cmd
curl -X POST http://localhost:8181/api/v1/clients -H "Content-Type: application/json" -d "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john@example.com\", \"phone\": \"+1-555-0001\"}"

curl -X POST http://localhost:8181/api/v1/clients -H "Content-Type: application/json" -d "{\"firstName\": \"Jane\", \"lastName\": \"Smith\", \"email\": \"jane@example.com\", \"phone\": \"+1-555-0002\"}"

curl -X POST http://localhost:8181/api/v1/portfolios -H "Content-Type: application/json" -d "{\"name\": \"Tech Growth\", \"description\": \"Technology stocks\", \"clientId\": 1}"

curl -X POST http://localhost:8181/api/v1/portfolios -H "Content-Type: application/json" -d "{\"name\": \"Safe Haven\", \"description\": \"Gold and bonds\", \"clientId\": 1}"

curl -X POST http://localhost:8181/api/v1/portfolios -H "Content-Type: application/json" -d "{\"name\": \"Balanced\", \"description\": \"Mixed portfolio\", \"clientId\": 2}"

curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Apple\", \"symbol\": \"AAPL\", \"assetType\": \"STOCK\", \"quantity\": 100, \"purchasePrice\": 150, \"currentPrice\": 175, \"purchaseDate\": \"2024-01-15\", \"portfolioId\": 1}"

curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Tesla\", \"symbol\": \"TSLA\", \"assetType\": \"STOCK\", \"quantity\": 50, \"purchasePrice\": 200, \"currentPrice\": 250, \"purchaseDate\": \"2024-02-01\", \"portfolioId\": 1}"

curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Bitcoin\", \"symbol\": \"BTC\", \"assetType\": \"CRYPTO\", \"quantity\": 2, \"purchasePrice\": 40000, \"currentPrice\": 45000, \"purchaseDate\": \"2024-02-01\", \"portfolioId\": 1}"

curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Gold\", \"symbol\": \"GLD\", \"assetType\": \"GOLD\", \"quantity\": 10, \"purchasePrice\": 1950, \"currentPrice\": 2050, \"purchaseDate\": \"2024-01-10\", \"portfolioId\": 2}"

curl -X POST http://localhost:8181/api/v1/assets -H "Content-Type: application/json" -d "{\"name\": \"Vanguard Fund\", \"symbol\": \"VFIAX\", \"assetType\": \"MUTUAL_FUND\", \"quantity\": 100, \"purchasePrice\": 400, \"currentPrice\": 420, \"purchaseDate\": \"2024-01-20\", \"portfolioId\": 2}"

curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"BUY\", \"quantity\": 50, \"pricePerUnit\": 180, \"fees\": 10, \"assetId\": 1, \"transactionDate\": \"2024-06-15T10:00:00\", \"notes\": \"Adding shares\"}"

curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"SELL\", \"quantity\": 10, \"pricePerUnit\": 260, \"fees\": 10, \"assetId\": 2, \"transactionDate\": \"2024-07-20T14:00:00\", \"notes\": \"Taking profits\"}"

curl -X POST http://localhost:8181/api/v1/transactions -H "Content-Type: application/json" -d "{\"transactionType\": \"DIVIDEND\", \"quantity\": 1, \"pricePerUnit\": 96, \"fees\": 0, \"assetId\": 1, \"transactionDate\": \"2024-08-15T00:00:00\", \"notes\": \"Quarterly dividend\"}"
```

### Verify Setup
```cmd
curl http://localhost:8181/api/v1/clients
curl http://localhost:8181/api/v1/clients/1/portfolios
curl http://localhost:8181/api/v1/portfolios/1/assets
curl http://localhost:8181/api/v1/transactions
```

### Quick Setup - Payment Processing & Monitoring

Set up monitoring rules first, then create payments to trigger alerts:

```cmd
curl -X POST http://localhost:8181/api/v1/rules -H "Content-Type: application/json" -d "{\"ruleName\": \"High Value Transaction\", \"ruleType\": \"AMOUNT_THRESHOLD\", \"severity\": \"HIGH\", \"active\": true, \"description\": \"Alert when transaction exceeds $10,000\", \"thresholdAmount\": 10000.00, \"thresholdCurrency\": \"USD\"}"

curl -X POST http://localhost:8181/api/v1/rules -H "Content-Type: application/json" -d "{\"ruleName\": \"Rapid Transactions\", \"ruleType\": \"VELOCITY\", \"severity\": \"MEDIUM\", \"active\": true, \"description\": \"Alert when more than 5 transactions in 10 minutes\", \"maxTransactions\": 5, \"timeWindowMinutes\": 10}"

curl -X POST http://localhost:8181/api/v1/rules -H "Content-Type: application/json" -d "{\"ruleName\": \"New Payee Detection\", \"ruleType\": \"NEW_PAYEE\", \"severity\": \"LOW\", \"active\": true, \"description\": \"Alert on first transaction to new payee\", \"lookbackDays\": 90}"

curl -X POST http://localhost:8181/api/v1/payments -H "Content-Type: application/json" -d "{\"sourceAccount\": \"ACC-001\", \"destinationAccount\": \"ACC-002\", \"amount\": 500.00, \"currency\": \"USD\", \"reference\": \"PAY-001\", \"description\": \"Small payment\"}"

curl -X POST http://localhost:8181/api/v1/payments -H "Content-Type: application/json" -d "{\"sourceAccount\": \"ACC-001\", \"destinationAccount\": \"ACC-003\", \"amount\": 15000.00, \"currency\": \"USD\", \"reference\": \"PAY-002\", \"description\": \"Large payment - should trigger alert\"}"

curl -X POST http://localhost:8181/api/v1/payments -H "Content-Type: application/json" -d "{\"sourceAccount\": \"ACC-001\", \"destinationAccount\": \"ACC-NEW\", \"amount\": 1000.00, \"currency\": \"USD\", \"reference\": \"PAY-003\", \"description\": \"New payee - should trigger alert\"}"
```

### Verify Payment & Alert Setup
```cmd
curl http://localhost:8181/api/v1/rules/active
curl http://localhost:8181/api/v1/payments
curl http://localhost:8181/api/v1/alerts
curl http://localhost:8181/api/v1/alerts/statistics
```

### Process Payment Through Lifecycle
```cmd
curl -X POST http://localhost:8181/api/v1/payments/1/validate
curl -X POST http://localhost:8181/api/v1/payments/1/send
curl -X POST http://localhost:8181/api/v1/payments/1/complete
curl -X GET http://localhost:8181/api/v1/payments/1/details
```

### Manage Alert Lifecycle
```cmd
curl -X PUT "http://localhost:8181/api/v1/alerts/1/acknowledge?operatorName=Admin"
curl -X PUT http://localhost:8181/api/v1/alerts/1/status -H "Content-Type: application/json" -d "{\"status\": \"INVESTIGATING\", \"operatorName\": \"Admin\"}"
curl -X PUT http://localhost:8181/api/v1/alerts/1/status -H "Content-Type: application/json" -d "{\"status\": \"CLOSED\", \"operatorName\": \"Admin\", \"resolutionNotes\": \"Verified legitimate\"}"
```

---

## Database Schema

### Portfolio Management Schema
```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐     ┌──────────────┐
│   CLIENTS   │────<│  PORTFOLIOS  │────<│   ASSETS    │────<│ TRANSACTIONS │
├─────────────┤     ├──────────────┤     ├─────────────┤     ├──────────────┤
│ id          │     │ id           │     │ id          │     │ id           │
│ first_name  │     │ name         │     │ name        │     │ type         │
│ last_name   │     │ description  │     │ symbol      │     │ quantity     │
│ email       │     │ total_value  │     │ asset_type  │     │ price_per_unit│
│ phone       │     │ active       │     │ quantity    │     │ total_amount │
│ address     │     │ client_id    │     │ purchase_price│   │ fees         │
│ active      │     │ created_at   │     │ current_price│    │ asset_id     │
│ created_at  │     │ updated_at   │     │ current_value│    │ transaction_date│
│ updated_at  │     └──────────────┘     │ portfolio_id│     │ created_at   │
└─────────────┘                          │ created_at  │     └──────────────┘
                                         │ updated_at  │
                                         └─────────────┘
```

### Payment Processing Schema
```
┌───────────────────┐     ┌─────────────────────────┐
│     PAYMENTS      │────<│  PAYMENT_STATUS_HISTORY │
├───────────────────┤     ├─────────────────────────┤
│ id                │     │ id                      │
│ payment_reference │     │ payment_id (FK)         │
│ idempotency_key   │     │ previous_status         │
│ source_account    │     │ status                  │
│ destination_account│    │ notes                   │
│ amount            │     │ timestamp               │
│ currency          │     └─────────────────────────┘
│ status            │
│ error_code        │
│ error_message     │
│ reference         │
│ description       │
│ created_at        │
│ updated_at        │
└───────────────────┘
```

### Transaction Monitoring Schema
```
┌───────────────────┐     ┌─────────────────┐     ┌────────────────────┐
│ MONITORING_RULES  │────<│     ALERTS      │────<│ ALERT_TRANSACTIONS │
├───────────────────┤     ├─────────────────┤     ├────────────────────┤
│ id                │     │ id              │     │ alert_id (FK)      │
│ rule_name         │     │ alert_reference │     │ payment_id (FK)    │
│ rule_type         │     │ rule_id (FK)    │     └────────────────────┘
│ severity          │     │ severity        │
│ active            │     │ status          │
│ description       │     │ message         │
│ threshold_amount  │     │ account_id      │
│ threshold_currency│     │ acknowledged_at │
│ max_transactions  │     │ acknowledged_by │
│ time_window_minutes│    │ closed_at       │
│ daily_limit_amount│     │ closed_by       │
│ lookback_days     │     │ resolution_notes│
│ created_at        │     │ created_at      │
│ updated_at        │     └─────────────────┘
└───────────────────┘
```

### Asset Types
| Type | Description |
|------|-------------|
| `GOLD` | Physical gold or gold-related investments |
| `STOCK` | Equity shares in publicly traded companies |
| `MUTUAL_FUND` | Professionally managed investment funds |
| `CRYPTO` | Cryptocurrencies (Bitcoin, Ethereum, etc.) |

### Transaction Types
| Type | Description | Effect on Quantity |
|------|-------------|-------------------|
| `BUY` | Purchase of an asset | Increases |
| `SELL` | Sale of an asset | Decreases |
| `DIVIDEND` | Dividend received | No change |
| `INTEREST` | Interest earned | No change |
| `TRANSFER_IN` | Asset transferred in | Increases |
| `TRANSFER_OUT` | Asset transferred out | Decreases |

### Payment Status Lifecycle
| Status | Description |
|--------|-------------|
| `CREATED` | Payment submitted but not yet validated |
| `VALIDATED` | Payment passed validation, ready to send |
| `SENT` | Payment transmitted to destination |
| `COMPLETED` | Payment successfully processed |
| `FAILED` | Payment failed (with error code) |

**Valid Transitions:** CREATED → VALIDATED → SENT → COMPLETED (FAILED can occur from any non-terminal state)

### Alert Status Lifecycle
| Status | Description |
|--------|-------------|
| `OPEN` | Alert generated, not yet reviewed |
| `ACKNOWLEDGED` | Alert seen by operator, not investigated |
| `INVESTIGATING` | Alert actively being investigated |
| `CLOSED` | Investigation complete, resolved |
| `DISMISSED` | Determined to be false positive |

**Valid Transitions:** OPEN → ACKNOWLEDGED → INVESTIGATING → CLOSED (DISMISSED can occur from OPEN, ACKNOWLEDGED, or INVESTIGATING)

### Alert Severity Levels
| Severity | Description | Priority |
|----------|-------------|----------|
| `HIGH` | Critical, requires immediate attention | 1 |
| `MEDIUM` | Important, review soon | 2 |
| `LOW` | Informational, routine review | 3 |

### Monitoring Rule Types
| Type | Description | Parameters |
|------|-------------|------------|
| `AMOUNT_THRESHOLD` | Alert when transaction exceeds amount | thresholdAmount, thresholdCurrency |
| `VELOCITY` | Alert when N transactions in T time | maxTransactions, timeWindowMinutes |
| `NEW_PAYEE` | Alert on first transaction to new payee | lookbackDays |
| `DAILY_LIMIT` | Alert when daily total exceeds limit | dailyLimitAmount |

---

## Project Structure

```
src/main/java/com/demo/MoneyMap/
├── config/                  # Configuration classes
│   ├── OpenApiConfig.java   # Swagger configuration
│   ├── AssetFactory.java    # Factory pattern for assets
│   └── TransactionFactory.java
├── controller/              # REST Controllers
│   ├── ClientController.java
│   ├── PortfolioController.java
│   ├── AssetController.java
│   ├── TransactionController.java
│   ├── PaymentController.java        # Payment Processing
│   ├── AlertController.java          # Alert Management
│   └── MonitoringRuleController.java # Rule Management
├── dto/                     # Data Transfer Objects
│   ├── request/             # Request DTOs
│   └── response/            # Response DTOs
├── entity/                  # JPA Entities
│   ├── Client.java
│   ├── Portfolio.java
│   ├── Asset.java
│   ├── Transaction.java
│   ├── Payment.java                  # Payment entity
│   ├── PaymentStatusHistory.java     # Payment audit trail
│   ├── MonitoringRule.java           # Rule configuration
│   ├── Alert.java                    # Alert entity
│   └── enums/                        # Enums for all types
├── exception/               # Exception handling
│   └── GlobalExceptionHandler.java
├── mapper/                  # Entity-DTO mappers
├── repository/              # JPA Repositories (DAO)
├── service/                 # Business logic
│   ├── ClientService.java
│   ├── PortfolioService.java
│   ├── AssetService.java
│   ├── TransactionService.java
│   ├── PaymentService.java           # Payment lifecycle
│   ├── AlertService.java             # Alert management
│   ├── MonitoringRuleService.java    # Rule CRUD
│   ├── RuleEngineService.java        # Rule evaluation
│   ├── rule/                         # Rule evaluators (Strategy Pattern)
│   │   ├── RuleEvaluator.java        # Strategy interface
│   │   ├── AmountThresholdRuleEvaluator.java
│   │   ├── VelocityRuleEvaluator.java
│   │   ├── NewPayeeRuleEvaluator.java
│   │   └── DailyLimitRuleEvaluator.java
│   └── impl/                # Service implementations
└── MoneyMapApplication.java # Main class
```

---

## Design Patterns Used

| Pattern | Implementation |
|---------|----------------|
| **SOLID Principles** | Single Responsibility in services, Interface Segregation |
| **Strategy Pattern** | `RuleEvaluator` interface with specific evaluators for each rule type |
| **Factory Pattern** | `AssetFactory`, `TransactionFactory` |
| **Repository Pattern** | JPA repositories as DAO layer |
| **DTO Pattern** | Separate request/response DTOs |
| **Builder Pattern** | Lombok @Builder for entity creation |
| **State Pattern** | Payment and Alert status lifecycle management |

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8181 in use | Change `server.port` in application.properties |
| DB connection failed | Start MySQL: `net start MySQL80` |
| Access denied | Check username/password in application.properties |
| JAVA_HOME not set | Set environment variable to Java 21 path |
| Swagger not loading | Rebuild: `.\mvnw.cmd clean package -DskipTests` |

---

## License

MIT License
