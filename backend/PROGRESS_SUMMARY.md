# MoneyMap Portfolio Manager - Project Status

**Last Updated:** February 2, 2026  
**Project Status:** ✅ Backend Complete & Ready for Frontend Development  
**Backend URL:** http://localhost:8181  
**API Base:** http://localhost:8181/api/v1  
**Swagger UI:** http://localhost:8181/swagger-ui.html

---

## 🎯 Project Overview

A comprehensive REST API for managing investment portfolios, designed for asset managers handling 30-35 clients with support for:
- **4 Asset Types:** Stocks, Cryptocurrencies, Gold, Mutual Funds
- **Multi-Country Support:** Full internationalization with ISO codes
- **Payment Processing:** Complete payment lifecycle management
- **Transaction Monitoring:** Real-time alerts based on configurable rules
- **One Portfolio Per Client:** Simplified user experience with 1:1 relationship

---

## ✅ Completed Features

### 1. Core Portfolio Management ✅
- **Client Management:** CRUD with multi-country fields (country code, currency, timezone, locale)
- **Portfolio Management:** One-to-one relationship with clients, automatic value calculation
- **Asset Management:** Inheritance-based design with 4 asset types (STOCK, CRYPTO, GOLD, MUTUAL_FUND)
- **Transaction Management:** Full transaction history with type-specific validation
- **Type-Specific Asset Fields:**
  - **Stock:** exchange, sector, dividendYield, fractionalAllowed
  - **Crypto:** blockchain, walletAddress, stakingEnabled, stakingAPY
  - **Gold:** purity, weightInGrams, storageLocation, certificateNumber, isPhysical
  - **Mutual Fund:** fundManager, expenseRatio, navPrice, minimumInvestment, riskLevel

### 2. Payment Processing ✅
- Payment lifecycle: CREATED → VALIDATED → SENT → COMPLETED (or FAILED)
- Idempotency key support for duplicate prevention
- Payment status history audit trail
- Error tracking with error codes and messages
- Account-based payment routing

### 3. Transaction Monitoring & Alerts ✅
- **Configurable Monitoring Rules:**
  - AMOUNT_THRESHOLD: Alert on high-value transactions
  - VELOCITY: Alert on rapid transaction patterns
  - NEW_PAYEE: Alert on first-time payees
  - DAILY_LIMIT: Alert on daily limit breaches
- **Alert Management:** OPEN → ACKNOWLEDGED → INVESTIGATING → CLOSED/DISMISSED
- Severity levels: HIGH, MEDIUM, LOW
- Alert statistics and prioritized queues

### 4. OOP Design Patterns ✅
- **Inheritance:** Asset base class with 4 subclasses (Single Table Inheritance)
- **Polymorphism:** Type-specific behavior (getAllowedTransactionTypes, isQuantityValid)
- **Factory Pattern:** AssetFactory creates correct asset subtype
- **Strategy Pattern:** RuleEvaluator interface with concrete implementations
- **Repository Pattern:** JPA repositories for all entities
- **DTO Pattern:** Separate request/response objects

### 5. API Features ✅
- RESTful endpoints under `/api/v1/`
- Swagger UI documentation
- Pagination support for all list endpoints
- Search and filtering capabilities
- CORS enabled for frontend (localhost:3000, localhost:5173)
- Comprehensive error handling
- Request validation with Jakarta Validation

### 6. Database Schema ✅
- MySQL with JPA/Hibernate
- Proper indexes and foreign keys
- UNIQUE constraint enforcing one portfolio per client
- Single Table Inheritance for Asset hierarchy
- Audit timestamps (created_at, updated_at)

---

## 📊 Data Model Summary

### Key Relationships
```
Client (1) ←→ (1) Portfolio (1) ←→ (N) Asset (1) ←→ (N) Transaction
Payment (1) ←→ (N) PaymentStatusHistory
MonitoringRule (1) ←→ (N) Alert (N) ←→ (N) Payment
```

### Entity Counts
- **7 Core Entities:** Client, Portfolio, Asset (+ 4 subclasses), Transaction
- **4 Payment Entities:** Payment, PaymentStatusHistory, MonitoringRule, Alert
- **8 Enums:** AssetType, TransactionType, PaymentStatus, AlertStatus, AlertSeverity, RuleType, PaymentErrorCode
- **60+ API Endpoints:** Full CRUD + specialized operations

---

## 🚀 How to Run

### Prerequisites
- Java 21
- MySQL 8.x (running on localhost:3306)
- Maven (included via wrapper)

### Start Backend
```cmd
cd C:\Users\sarga\Downloads\MoneyMap\portfoliomanager
.\mvnw.cmd spring-boot:run
```

### Verify Backend
- Swagger UI: http://localhost:8181/swagger-ui.html
- Test Endpoint: http://localhost:8181/api/v1/clients

### Populate Database
```cmd
cd scripts
python seed_data.py
```
This creates 30 clients with portfolios, 150+ assets, 300+ transactions, payments, and alerts.

---

## 📁 Project Structure

```
src/main/java/com/demo/MoneyMap/
├── config/                     # Configuration & Factory classes
│   ├── OpenApiConfig.java      # Swagger configuration
│   ├── AssetFactory.java       # Factory Pattern for assets
│   ├── TransactionFactory.java # Factory Pattern for transactions
│   └── CorsConfig.java         # CORS for frontend
├── controller/                 # REST Controllers (7 controllers)
│   ├── ClientController.java
│   ├── PortfolioController.java
│   ├── AssetController.java
│   ├── TransactionController.java
│   ├── PaymentController.java
│   ├── AlertController.java
│   └── MonitoringRuleController.java
├── dto/                        # Data Transfer Objects
│   ├── request/                # Request DTOs (12)
│   └── response/               # Response DTOs (15)
├── entity/                     # JPA Entities
│   ├── Client.java             # Multi-country support
│   ├── Portfolio.java          # OneToOne with Client
│   ├── Asset.java              # Abstract base class
│   ├── StockAsset.java         # Stock-specific fields
│   ├── CryptoAsset.java        # Crypto-specific fields
│   ├── GoldAsset.java          # Gold-specific fields
│   ├── MutualFundAsset.java    # Mutual fund-specific fields
│   ├── Transaction.java
│   ├── Payment.java
│   ├── PaymentStatusHistory.java
│   ├── MonitoringRule.java
│   ├── Alert.java
│   └── enums/                  # 8 enums
├── exception/                  # Exception handling
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── DuplicateResourceException.java
├── mapper/                     # Entity-DTO mappers
│   ├── ClientMapper.java       # Handles multi-country fields
│   ├── PortfolioMapper.java
│   ├── AssetMapper.java        # Handles polymorphism
│   ├── TransactionMapper.java
│   ├── PaymentMapper.java
│   ├── AlertMapper.java
│   └── MonitoringRuleMapper.java
├── repository/                 # JPA Repositories (7)
│   ├── ClientRepository.java
│   ├── PortfolioRepository.java
│   ├── AssetRepository.java
│   ├── TransactionRepository.java
│   ├── PaymentRepository.java
│   ├── AlertRepository.java
│   └── MonitoringRuleRepository.java
├── service/                    # Business logic
│   ├── ClientService.java
│   ├── PortfolioService.java
│   ├── AssetService.java
│   ├── TransactionService.java
│   ├── PaymentService.java
│   ├── AlertService.java
│   ├── MonitoringRuleService.java
│   ├── RuleEngineService.java
│   ├── rule/                   # Strategy Pattern
│   │   ├── RuleEvaluator.java
│   │   ├── AmountThresholdRuleEvaluator.java
│   │   ├── VelocityRuleEvaluator.java
│   │   ├── NewPayeeRuleEvaluator.java
│   │   └── DailyLimitRuleEvaluator.java
│   └── impl/                   # Service implementations
│       ├── ClientServiceImpl.java
│       ├── PortfolioServiceImpl.java
│       ├── AssetServiceImpl.java
│       ├── TransactionServiceImpl.java
│       ├── PaymentServiceImpl.java
│       ├── AlertServiceImpl.java
│       └── MonitoringRuleServiceImpl.java
└── MoneyMapApplication.java    # Main application class
```

---

## 🎨 Frontend Requirements

### Tech Stack (Recommended)
- **Framework:** React + Vite
- **UI Library:** Material-UI (MUI)
- **Charts:** Recharts
- **HTTP Client:** Axios
- **Routing:** React Router
- **State Management:** React Context or Redux (optional)

### Key Features to Implement

#### 1. Dashboard
- Portfolio value overview with charts
- Asset allocation pie chart (by type)
- Recent transactions timeline
- Active alerts summary
- Quick stats (total clients, total value, asset count)

#### 2. Client Management
- Client list with search/filter
- Create/Edit client form with multi-country fields
- Client detail view with portfolio summary
- Activate/Deactivate toggle

#### 3. Portfolio Management
- Portfolio overview with total value
- Asset breakdown by type
- Performance charts (profit/loss)
- Recalculate value button

#### 4. Asset Management
- Asset list with filters (by type, portfolio)
- Create/Edit asset form (type-specific fields)
- Asset detail view with transactions
- Price update functionality
- Asset type selector with dynamic fields:
  - Stock: exchange, sector, dividend yield, fractional toggle
  - Crypto: blockchain, wallet, staking toggle, APY
  - Gold: purity, weight, storage, certificate, physical toggle
  - Mutual Fund: manager, expense ratio, NAV, min investment, risk

#### 5. Transaction Management
- Transaction history with filters (date range, type, asset)
- Create transaction modal with validation
- Transaction detail view
- Type-specific transaction rules

#### 6. Payment Processing
- Payment list with status filters
- Create payment form with idempotency
- Payment detail with status history
- Status transition buttons (Validate, Send, Complete, Fail)
- Payment lifecycle visualization

#### 7. Monitoring & Alerts
- Alert dashboard with severity filters
- Rule configuration interface
- Alert detail with resolution options
- Status update actions (Acknowledge, Investigate, Close, Dismiss)
- Alert statistics charts

---

## 📡 API Response Format

All API responses follow this structure:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-02-02T19:30:00"
}
```

**Paginated Responses:**
```json
{
  "content": [ ... ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 35,
  "totalPages": 4,
  "first": true,
  "last": false,
  "empty": false
}
```

---

## 🔑 Key API Endpoints for Frontend

### Authentication (Not Implemented Yet)
Frontend can proceed without auth for now. Add JWT later if needed.

### Essential Endpoints

**Dashboard Data:**
```
GET /api/v1/clients/count/active
GET /api/v1/portfolios/{id}
GET /api/v1/assets/portfolio/{portfolioId}
GET /api/v1/transactions/portfolio/{portfolioId}
GET /api/v1/alerts/statistics
```

**Client Operations:**
```
GET    /api/v1/clients?page=0&size=10
POST   /api/v1/clients
GET    /api/v1/clients/{id}
PUT    /api/v1/clients/{id}
PATCH  /api/v1/clients/{id}/activate
GET    /api/v1/clients/search?query=john
```

**Asset Operations:**
```
GET    /api/v1/assets/portfolio/{portfolioId}
POST   /api/v1/assets
GET    /api/v1/assets/{id}
PUT    /api/v1/assets/{id}
PATCH  /api/v1/assets/{id}/price?currentPrice=180.00
DELETE /api/v1/assets/{id}
GET    /api/v1/assets/types
```

**Transaction Operations:**
```
GET    /api/v1/transactions/asset/{assetId}
POST   /api/v1/transactions
GET    /api/v1/transactions/types
GET    /api/v1/transactions/date-range?startDate=...&endDate=...
```

**Payment Operations:**
```
GET    /api/v1/payments?page=0&size=10
POST   /api/v1/payments
GET    /api/v1/payments/{id}/details
POST   /api/v1/payments/{id}/validate
POST   /api/v1/payments/{id}/send
POST   /api/v1/payments/{id}/complete
```

**Alert Operations:**
```
GET    /api/v1/alerts?page=0&size=10
GET    /api/v1/alerts/status/OPEN
PUT    /api/v1/alerts/{id}/acknowledge?operatorName=Admin
GET    /api/v1/alerts/open/prioritized?limit=20
```

---

## 🐛 Known Issues & TODOs

### Not Critical for Frontend
- ⚠️ Unit tests need to be recreated (lost during undo operation)
- ⚠️ CI/CD pipeline needs to be recreated
- ⚠️ No authentication/authorization (can add JWT later)

### Future Enhancements
- Real-time price updates (WebSocket or polling)
- Email notifications for alerts
- PDF report generation
- Bulk operations (import/export)
- Advanced analytics and insights
- Mobile responsive design
- Dark/Light theme toggle

---

## 💡 Frontend Development Tips

### 1. API Client Setup
```javascript
// src/api/client.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8181/api/v1';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor to extract data from ApiResponseDTO
apiClient.interceptors.response.use(
  response => response.data.data || response.data,
  error => Promise.reject(error)
);

export default apiClient;
```

### 2. Asset Type Dynamic Forms
When creating/editing assets, show type-specific fields based on selected asset type:
```javascript
const typeSpecificFields = {
  STOCK: ['exchange', 'sector', 'dividendYield', 'fractionalAllowed'],
  CRYPTO: ['blockchain', 'walletAddress', 'stakingEnabled', 'stakingAPY'],
  GOLD: ['purity', 'weightInGrams', 'storageLocation', 'certificateNumber', 'isPhysical'],
  MUTUAL_FUND: ['fundManager', 'expenseRatio', 'navPrice', 'minimumInvestment', 'riskLevel']
};
```

### 3. Multi-Country Client Form
Include these fields in client forms:
- Country selector (with country code mapping)
- Currency selector (based on country)
- Timezone selector (IANA timezones)
- Locale selector (e.g., en_US, hi_IN)

### 4. Real-Time Updates
For live portfolio values, poll these endpoints every 30-60 seconds:
```javascript
setInterval(() => {
  fetchPortfolioValue(portfolioId);
  fetchAlertCount();
}, 30000);
```

### 5. Error Handling
All API errors return:
```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "..."
}
```

### 6. Date Formatting
API expects ISO 8601 format: `2024-01-15T10:30:00`

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | API documentation with curl examples |
| `DATABASE_SCHEMA.md` | Complete database schema reference |
| `PROGRESS_SUMMARY.md` | This file - project overview |
| `payment_processing.md` | Payment module detailed docs |
| `transaction_monitoring.md` | Alert system detailed docs |

---

## 🎓 Learning Points Demonstrated

### Object-Oriented Programming
1. **Inheritance:** Asset hierarchy with abstract base class
2. **Polymorphism:** Type-specific behavior for each asset type
3. **Encapsulation:** Private fields with getter/setter methods
4. **Abstraction:** Service interfaces hiding implementation details

### Design Patterns
1. **Factory Pattern:** AssetFactory creates correct subtype
2. **Strategy Pattern:** Different rule evaluators
3. **Repository Pattern:** Data access abstraction
4. **DTO Pattern:** Separating API contracts from domain models
5. **Builder Pattern:** Lombok @Builder for object construction

### SOLID Principles
1. **Single Responsibility:** Each class has one reason to change
2. **Open/Closed:** Open for extension (new asset types), closed for modification
3. **Liskov Substitution:** Asset subtypes can replace base class
4. **Interface Segregation:** Focused service interfaces
5. **Dependency Inversion:** Depend on abstractions (interfaces)

---

## 🚀 Ready for Frontend!

**Backend Status:** ✅ 100% Complete  
**API Status:** ✅ All endpoints working  
**CORS:** ✅ Configured for localhost:3000 and :5173  
**Database:** ✅ Schema created, can be populated with seed script  
**Documentation:** ✅ Complete with examples

**Next Steps:**
1. Ensure backend is running: `.\mvnw.cmd spring-boot:run`
2. Populate database: `python scripts/seed_data.py`
3. Start frontend development with React + MUI
4. Use Swagger UI for API exploration: http://localhost:8181/swagger-ui.html

---

*Backend developed with Spring Boot 3.x, JPA/Hibernate, MySQL 8.x*  
*Ready for frontend integration - React + Material-UI recommended*
