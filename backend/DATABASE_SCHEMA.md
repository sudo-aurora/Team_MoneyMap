# MoneyMap Portfolio Manager - Database Schema

## Overview
This document describes the complete database schema for the MoneyMap Portfolio Manager application, including all entities, relationships, and constraints.

---

## Entity Relationship Diagrams

### Core Portfolio Management Schema

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐         ┌──────────────────┐
│    CLIENTS      │   1:1   │   PORTFOLIOS     │   1:N   │     ASSETS      │   1:N   │  TRANSACTIONS    │
├─────────────────┤─────────├──────────────────┤─────────├─────────────────┤─────────├──────────────────┤
│ id (PK)         │         │ id (PK)          │         │ id (PK)         │         │ id (PK)          │
│ first_name      │         │ name             │         │ name            │         │ transaction_type │
│ last_name       │         │ description      │         │ symbol          │         │ quantity         │
│ email (UQ)      │         │ client_id (FK,UQ)│         │ asset_type (D)  │         │ price_per_unit   │
│ phone           │         │ total_value      │         │ quantity        │         │ total_amount     │
│ address         │         │ active           │         │ purchase_price  │         │ fees             │
│ city            │         │ created_at       │         │ current_price   │         │ asset_id (FK)    │
│ state_or_province│        │ updated_at       │         │ current_value   │         │ transaction_date │
│ postal_code     │         └──────────────────┘         │ purchase_date   │         │ notes            │
│ country_code    │                                      │ notes           │         │ created_at       │
│ country         │                                      │ portfolio_id(FK)│         └──────────────────┘
│ preferred_currency│                                    │ created_at      │
│ timezone        │                                      │ updated_at      │
│ locale          │                                      │                 │
│ active          │                                      │ [Subclasses]    │
│ created_at      │                                      │ - StockAsset    │
│ updated_at      │                                      │ - CryptoAsset   │
└─────────────────┘                                      │ - GoldAsset     │
                                                         │ - MutualFundAsset│
                                                         └─────────────────┘

Key Constraints:
- Each Client has EXACTLY ONE Portfolio (1:1)
- Each Portfolio has MANY Assets (1:N)
- Each Asset has MANY Transactions (1:N)
- portfolios.client_id has UNIQUE constraint (enforces 1:1)
```

### Payment Processing & Monitoring Schema

```
┌───────────────────────┐     ┌─────────────────────────────┐
│      PAYMENTS         │────<│  PAYMENT_STATUS_HISTORY     │
├───────────────────────┤     ├─────────────────────────────┤
│ id (PK)               │     │ id (PK)                     │
│ payment_reference (UQ)│     │ payment_id (FK)             │
│ idempotency_key (UQ)  │     │ previous_status             │
│ source_account        │     │ status                      │
│ destination_account   │     │ notes                       │
│ amount                │     │ timestamp                   │
│ currency              │     └─────────────────────────────┘
│ status                │
│ error_code            │
│ error_message         │
│ reference             │
│ description           │
│ created_at            │
│ updated_at            │
└───────────────────────┘

┌───────────────────────┐     ┌─────────────────────┐     ┌────────────────────────┐
│  MONITORING_RULES     │────<│      ALERTS         │────<│  ALERT_TRANSACTIONS    │
├───────────────────────┤     ├─────────────────────┤     ├────────────────────────┤
│ id (PK)               │     │ id (PK)             │     │ alert_id (FK)          │
│ rule_name             │     │ alert_reference (UQ)│     │ payment_id (FK)        │
│ rule_type             │     │ rule_id (FK)        │     └────────────────────────┘
│ severity              │     │ severity            │
│ active                │     │ status              │
│ description           │     │ message             │
│ threshold_amount      │     │ account_id          │
│ threshold_currency    │     │ acknowledged_at     │
│ max_transactions      │     │ acknowledged_by     │
│ time_window_minutes   │     │ closed_at           │
│ daily_limit_amount    │     │ closed_by           │
│ lookback_days         │     │ resolution_notes    │
│ created_at            │     │ created_at          │
│ updated_at            │     └─────────────────────┘
└───────────────────────┘
```

---

## Table Definitions

### 1. clients

**Purpose:** Stores client information with multi-country support.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| first_name | VARCHAR(100) | NOT NULL | Client's first name |
| last_name | VARCHAR(100) | NOT NULL | Client's last name |
| email | VARCHAR(150) | NOT NULL, UNIQUE | Email address |
| phone | VARCHAR(30) | | Phone number with country code |
| address | VARCHAR(500) | | Street address |
| city | VARCHAR(100) | | City |
| state_or_province | VARCHAR(100) | | State or Province |
| postal_code | VARCHAR(20) | | ZIP/Postal code |
| country_code | VARCHAR(2) | | ISO 3166-1 alpha-2 (e.g., US, IN, GB) |
| country | VARCHAR(100) | | Full country name |
| preferred_currency | VARCHAR(3) | | ISO 4217 currency code (e.g., USD, EUR) |
| timezone | VARCHAR(50) | | IANA timezone (e.g., America/New_York) |
| locale | VARCHAR(10) | | Locale code (e.g., en_US, hi_IN) |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Whether client is active |
| created_at | TIMESTAMP | AUTO | Creation timestamp |
| updated_at | TIMESTAMP | AUTO | Last update timestamp |

**Relationships:**
- One-to-One with `portfolios` (client_id)

---

### 2. portfolios

**Purpose:** Stores portfolio information. Each client has exactly ONE portfolio.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| name | VARCHAR(150) | NOT NULL | Portfolio name |
| description | VARCHAR(500) | | Portfolio description |
| client_id | BIGINT | NOT NULL, UNIQUE, FK → clients(id) | Client owner (1:1 relationship) |
| total_value | DECIMAL(15,2) | DEFAULT 0.00 | Total portfolio value |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Whether portfolio is active |
| created_at | TIMESTAMP | AUTO | Creation timestamp |
| updated_at | TIMESTAMP | AUTO | Last update timestamp |

**Relationships:**
- One-to-One with `clients` (client_id) - **UNIQUE constraint enforces this**
- One-to-Many with `assets` (portfolio_id)

**Key Constraint:** `client_id` has UNIQUE constraint to enforce one portfolio per client.

---

### 3. assets

**Purpose:** Stores asset information using Single Table Inheritance. Abstract base class with 4 subclasses.

**Inheritance Strategy:** SINGLE_TABLE with discriminator column `asset_type`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| asset_type | VARCHAR(50) | NOT NULL, DISCRIMINATOR | Asset type: STOCK, CRYPTO, GOLD, MUTUAL_FUND |
| name | VARCHAR(150) | NOT NULL | Asset name |
| symbol | VARCHAR(20) | NOT NULL | Trading symbol/ticker |
| quantity | DECIMAL(18,8) | NOT NULL | Quantity owned |
| purchase_price | DECIMAL(15,2) | NOT NULL | Price at purchase |
| current_price | DECIMAL(15,2) | NOT NULL | Current market price |
| current_value | DECIMAL(15,2) | CALCULATED | quantity × current_price |
| purchase_date | DATE | NOT NULL | Date of purchase |
| notes | VARCHAR(500) | | Additional notes |
| portfolio_id | BIGINT | NOT NULL, FK → portfolios(id) | Portfolio this asset belongs to |
| created_at | TIMESTAMP | AUTO | Creation timestamp |
| updated_at | TIMESTAMP | AUTO | Last update timestamp |

**Type-Specific Fields (Stock):**
| Column | Type | Description |
|--------|------|-------------|
| exchange | VARCHAR(50) | Stock exchange (NASDAQ, NYSE) |
| sector | VARCHAR(100) | Industry sector |
| dividend_yield | DECIMAL(5,2) | Dividend yield percentage |
| fractional_allowed | BOOLEAN | Can trade fractional shares |

**Type-Specific Fields (Crypto):**
| Column | Type | Description |
|--------|------|-------------|
| blockchain | VARCHAR(50) | Blockchain network |
| wallet_address | VARCHAR(200) | Crypto wallet address |
| staking_enabled | BOOLEAN | Can stake this crypto |
| staking_apy | DECIMAL(5,2) | Staking APY percentage |

**Type-Specific Fields (Gold):**
| Column | Type | Description |
|--------|------|-------------|
| purity | VARCHAR(10) | Gold purity (24K, 22K) |
| weight_in_grams | DECIMAL(10,2) | Weight in grams |
| storage_location | VARCHAR(200) | Where gold is stored |
| certificate_number | VARCHAR(100) | Certificate number |
| is_physical | BOOLEAN | Physical vs ETF/Paper |

**Type-Specific Fields (Mutual Fund):**
| Column | Type | Description |
|--------|------|-------------|
| fund_manager | VARCHAR(200) | Fund manager/company |
| expense_ratio | DECIMAL(5,2) | Fund expense ratio |
| nav_price | DECIMAL(15,2) | Net Asset Value per unit |
| minimum_investment | DECIMAL(15,2) | Minimum investment amount |
| risk_level | VARCHAR(50) | Risk level (Low/Moderate/High) |

**Relationships:**
- Many-to-One with `portfolios` (portfolio_id)
- One-to-Many with `transactions` (asset_id)

**Polymorphism:** Each asset type has:
- `getType()` - Returns AssetType enum
- `getAllowedTransactionTypes()` - Returns Set<TransactionType>
- `isQuantityValid(quantity)` - Validates quantity for asset type
- `getMinimumQuantityIncrement()` - Returns minimum quantity increment

---

### 4. transactions

**Purpose:** Records all transactions for assets.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| transaction_type | VARCHAR(50) | NOT NULL | BUY, SELL, DIVIDEND, INTEREST, TRANSFER_IN, TRANSFER_OUT |
| quantity | DECIMAL(18,8) | NOT NULL | Transaction quantity |
| price_per_unit | DECIMAL(15,2) | NOT NULL | Price per unit |
| total_amount | DECIMAL(15,2) | CALCULATED | quantity × price_per_unit |
| fees | DECIMAL(10,2) | DEFAULT 0.00 | Transaction fees |
| asset_id | BIGINT | NOT NULL, FK → assets(id) | Associated asset |
| transaction_date | TIMESTAMP | NOT NULL | When transaction occurred |
| notes | VARCHAR(500) | | Additional notes |
| created_at | TIMESTAMP | AUTO | Creation timestamp |

**Relationships:**
- Many-to-One with `assets` (asset_id)

**Transaction Types & Effects:**
| Type | Effect on Quantity | Allowed For |
|------|-------------------|-------------|
| BUY | Increases | All asset types |
| SELL | Decreases | All asset types |
| DIVIDEND | No change | STOCK, MUTUAL_FUND |
| INTEREST | No change | CRYPTO (staking rewards), MUTUAL_FUND |
| TRANSFER_IN | Increases | All asset types |
| TRANSFER_OUT | Decreases | All asset types |

---

### 5. payments

**Purpose:** Tracks financial payments through their lifecycle.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| payment_reference | VARCHAR(50) | UNIQUE | Payment reference number |
| idempotency_key | VARCHAR(100) | UNIQUE | For duplicate detection |
| source_account | VARCHAR(100) | NOT NULL | Source account |
| destination_account | VARCHAR(100) | NOT NULL | Destination account |
| amount | DECIMAL(15,2) | NOT NULL | Payment amount |
| currency | VARCHAR(3) | NOT NULL | Currency code (ISO 4217) |
| status | VARCHAR(50) | NOT NULL | CREATED, VALIDATED, SENT, COMPLETED, FAILED |
| error_code | VARCHAR(50) | | Error code if failed |
| error_message | VARCHAR(500) | | Error message if failed |
| reference | VARCHAR(100) | | External reference |
| description | VARCHAR(500) | | Payment description |
| created_at | TIMESTAMP | AUTO | Creation timestamp |
| updated_at | TIMESTAMP | AUTO | Last update timestamp |

**Status Lifecycle:**
```
CREATED → VALIDATED → SENT → COMPLETED
             ↓          ↓        ↓
           FAILED ← ← ← ← ← ← ← ←
```

**Relationships:**
- One-to-Many with `payment_status_history` (payment_id)
- Many-to-Many with `alerts` through `alert_transactions`

---

### 6. payment_status_history

**Purpose:** Audit trail for payment status changes.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| payment_id | BIGINT | NOT NULL, FK → payments(id) | Associated payment |
| previous_status | VARCHAR(50) | | Previous status |
| status | VARCHAR(50) | NOT NULL | New status |
| notes | VARCHAR(500) | | Status change notes |
| timestamp | TIMESTAMP | AUTO | When status changed |

---

### 7. monitoring_rules

**Purpose:** Defines rules for transaction monitoring and alert generation.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| rule_name | VARCHAR(200) | NOT NULL | Rule name |
| rule_type | VARCHAR(50) | NOT NULL | AMOUNT_THRESHOLD, VELOCITY, NEW_PAYEE, DAILY_LIMIT |
| severity | VARCHAR(20) | NOT NULL | HIGH, MEDIUM, LOW |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Whether rule is active |
| description | VARCHAR(500) | | Rule description |
| threshold_amount | DECIMAL(15,2) | | For AMOUNT_THRESHOLD rules |
| threshold_currency | VARCHAR(3) | | Currency for threshold |
| max_transactions | INTEGER | | For VELOCITY rules |
| time_window_minutes | INTEGER | | For VELOCITY rules |
| daily_limit_amount | DECIMAL(15,2) | | For DAILY_LIMIT rules |
| lookback_days | INTEGER | | For NEW_PAYEE rules |
| created_at | TIMESTAMP | AUTO | Creation timestamp |
| updated_at | TIMESTAMP | AUTO | Last update timestamp |

**Rule Types:**
| Type | Description | Parameters |
|------|-------------|------------|
| AMOUNT_THRESHOLD | Alert when transaction exceeds amount | threshold_amount, threshold_currency |
| VELOCITY | Alert when N transactions in T time | max_transactions, time_window_minutes |
| NEW_PAYEE | Alert on first transaction to new payee | lookback_days |
| DAILY_LIMIT | Alert when daily total exceeds limit | daily_limit_amount |

---

### 8. alerts

**Purpose:** Stores generated alerts when monitoring rules are triggered.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique identifier |
| alert_reference | VARCHAR(50) | UNIQUE | Alert reference number |
| rule_id | BIGINT | NOT NULL, FK → monitoring_rules(id) | Rule that triggered alert |
| severity | VARCHAR(20) | NOT NULL | HIGH, MEDIUM, LOW |
| status | VARCHAR(50) | NOT NULL | OPEN, ACKNOWLEDGED, INVESTIGATING, CLOSED, DISMISSED |
| message | TEXT | NOT NULL | Alert message |
| account_id | VARCHAR(100) | | Account involved |
| acknowledged_at | TIMESTAMP | | When acknowledged |
| acknowledged_by | VARCHAR(100) | | Who acknowledged |
| closed_at | TIMESTAMP | | When closed |
| closed_by | VARCHAR(100) | | Who closed |
| resolution_notes | TEXT | | Resolution notes |
| created_at | TIMESTAMP | AUTO | Creation timestamp |

**Status Lifecycle:**
```
OPEN → ACKNOWLEDGED → INVESTIGATING → CLOSED
  ↓           ↓              ↓
  → DISMISSED ← ← ← ← ← ← ← ←
```

---

## Enums

### AssetType
- `STOCK` - Equity shares
- `CRYPTO` - Cryptocurrencies
- `GOLD` - Gold investments
- `MUTUAL_FUND` - Mutual funds

### TransactionType
- `BUY` - Purchase transaction
- `SELL` - Sale transaction
- `DIVIDEND` - Dividend received
- `INTEREST` - Interest earned
- `TRANSFER_IN` - Asset transferred in
- `TRANSFER_OUT` - Asset transferred out

### PaymentStatus
- `CREATED` - Payment created
- `VALIDATED` - Payment validated
- `SENT` - Payment transmitted
- `COMPLETED` - Payment successful
- `FAILED` - Payment failed

### AlertStatus
- `OPEN` - Alert generated
- `ACKNOWLEDGED` - Alert seen
- `INVESTIGATING` - Under investigation
- `CLOSED` - Resolved
- `DISMISSED` - False positive

### AlertSeverity / RuleSeverity
- `HIGH` - Critical, immediate attention
- `MEDIUM` - Important, review soon
- `LOW` - Informational

---

## Indexes (Recommended)

```sql
-- Clients
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_active ON clients(active);

-- Portfolios
CREATE UNIQUE INDEX idx_portfolios_client_id ON portfolios(client_id);
CREATE INDEX idx_portfolios_active ON portfolios(active);

-- Assets
CREATE INDEX idx_assets_portfolio_id ON assets(portfolio_id);
CREATE INDEX idx_assets_symbol ON assets(symbol);
CREATE INDEX idx_assets_type ON assets(asset_type);

-- Transactions
CREATE INDEX idx_transactions_asset_id ON transactions(asset_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_type ON transactions(transaction_type);

-- Payments
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_source_account ON payments(source_account);
CREATE INDEX idx_payments_reference ON payments(payment_reference);
CREATE INDEX idx_payments_idempotency ON payments(idempotency_key);

-- Alerts
CREATE INDEX idx_alerts_status ON alerts(status);
CREATE INDEX idx_alerts_severity ON alerts(severity);
CREATE INDEX idx_alerts_rule_id ON alerts(rule_id);
CREATE INDEX idx_alerts_account ON alerts(account_id);
```

---

## Design Patterns Used

| Pattern | Implementation | Location |
|---------|----------------|----------|
| **Inheritance** | Asset hierarchy with Single Table Inheritance | Asset, StockAsset, CryptoAsset, GoldAsset, MutualFundAsset |
| **Polymorphism** | Asset subtypes define type-specific behavior | getAllowedTransactionTypes(), isQuantityValid() |
| **Factory** | AssetFactory creates correct asset subtype | AssetFactory.createFromDTO() |
| **Strategy** | RuleEvaluator for different rule types | RuleEvaluator interface + concrete implementations |
| **Repository** | Data access abstraction | All *Repository interfaces |
| **DTO** | Separate request/response objects | All *RequestDTO, *ResponseDTO classes |

---

## Key Business Rules

1. **One Portfolio Per Client:** Enforced by UNIQUE constraint on `portfolios.client_id`
2. **Asset Type Validation:** Each asset type has specific allowed transaction types
3. **Quantity Constraints:** Different asset types have different minimum quantity increments
4. **Payment Status Transitions:** Must follow defined lifecycle (cannot skip states)
5. **Alert Severity:** Inherited from the rule that triggered the alert

---

## API Endpoints

All endpoints are prefixed with `/api/v1/`

### Clients: `/clients`
- GET, POST, PUT `/clients`
- GET `/clients/{id}`
- GET `/clients/{id}/portfolios`
- PATCH `/clients/{id}/activate`, `/clients/{id}/deactivate`

### Portfolios: `/portfolios`
- GET, POST, PUT `/portfolios`
- GET `/portfolios/{id}`
- GET `/portfolios/{id}/assets`
- POST `/portfolios/{id}/recalculate`

### Assets: `/assets`
- GET, POST, PUT `/assets`
- GET `/assets/{id}`
- GET `/assets/portfolio/{portfolioId}`
- GET `/assets/type/{assetType}`
- PATCH `/assets/{id}/price`

### Transactions: `/transactions`
- GET, POST, PUT, DELETE `/transactions`
- GET `/transactions/{id}`
- GET `/transactions/asset/{assetId}`
- GET `/transactions/type/{type}`

### Payments: `/payments`
- GET, POST `/payments`
- GET `/payments/{id}/details`
- POST `/payments/{id}/validate`, `/send`, `/complete`, `/fail`

### Monitoring Rules: `/rules`
- GET, POST, PUT, DELETE `/rules`
- PATCH `/rules/{id}/activate`, `/deactivate`

### Alerts: `/alerts`
- GET `/alerts`
- GET `/alerts/{id}/details`
- PUT `/alerts/{id}/acknowledge`, `/status`
- GET `/alerts/statistics`

---

## Technology Stack

- **Framework:** Spring Boot 3.x
- **Database:** MySQL 8.x
- **ORM:** JPA/Hibernate
- **API Documentation:** SpringDoc OpenAPI (Swagger)
- **Validation:** Jakarta Validation
- **Mapping:** MapStruct, Custom Mappers
- **Build Tool:** Maven

---

*Last Updated: February 2, 2026*
