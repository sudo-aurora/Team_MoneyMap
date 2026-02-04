# API Testing Checklist for Trading System

## 🔍 Step 1: Check if Application Starts

```bash
cd backend
mvn spring-boot:run
```

**Look for these logs:**
- `Started MoneyMapApplication`
- `Tomcat started on port(s): 8080`
- No ERROR messages during startup

## 🔍 Step 2: Test Basic Health

```bash
curl http://localhost:8080/actuator/health
```
**Expected:** `{"status":"UP"}`

## 🔍 Step 3: Test All Trading Endpoints

### 3.1 Available Assets
```bash
curl -v http://localhost:8080/api/v1/trading/assets/available
```
**Expected:** HTTP 200 + JSON array with assets like:
```json
[
  {"symbol":"AAPL","name":"Apple Inc.","assetType":"STOCK","currentMarketPrice":175.50},
  {"symbol":"MSFT","name":"Microsoft Corporation","assetType":"STOCK","currentMarketPrice":380.75}
]
```

### 3.2 Check Client Wallet
```bash
curl -v http://localhost:8080/api/v1/trading/wallet/1
```
**Expected:** HTTP 200 + wallet balance:
```json
{"data": 10000.00}
```

### 3.3 Buy Asset
```bash
curl -v -X POST "http://localhost:8080/api/v1/trading/buy?clientId=1&symbol=AAPL&quantity=10&price=175.50"
```
**Expected:** HTTP 201 + transaction details:
```json
{
  "data": {
    "id": 1,
    "transactionType": "BUY",
    "quantity": 10.0,
    "pricePerUnit": 175.50,
    "totalAmount": 1755.00
  }
}
```

### 3.4 Check Updated Wallet
```bash
curl -v http://localhost:8080/api/v1/trading/wallet/1
```
**Expected:** HTTP 200 + reduced balance:
```json
{"data": 8245.00}
```

### 3.5 Check Asset Holdings
```bash
curl -v http://localhost:8080/api/v1/assets/client/1
```
**Expected:** HTTP 200 + assets with AAPL quantity 10

### 3.6 Sell Asset
```bash
curl -v -X POST "http://localhost:8080/api/v1/trading/sell?clientId=1&assetId=1&quantity=5&price=180.00"
```
**Expected:** HTTP 201 + SELL transaction

### 3.7 Check Final State
```bash
# Wallet should increase
curl http://localhost:8080/api/v1/trading/wallet/1

# Asset quantity should decrease
curl http://localhost:8080/api/v1/assets/1
```

## 🔍 Step 4: Test Error Cases

### 4.1 Insufficient Funds
```bash
curl -v -X POST "http://localhost:8080/api/v1/trading/buy?clientId=1&symbol=TSLA&quantity=1000&price=250.00"
```
**Expected:** HTTP 400 + "Insufficient funds" error

### 4.2 Invalid Asset Symbol
```bash
curl -v -X POST "http://localhost:8080/api/v1/trading/buy?clientId=1&symbol=INVALID&quantity=10&price=100.00"
```
**Expected:** HTTP 404 + "Asset not found" error

### 4.3 Client Not Found
```bash
curl -v http://localhost:8080/api/v1/trading/wallet/999
```
**Expected:** HTTP 404 + "Client not found" error

## 🔍 Step 5: Verify Database Changes

```sql
-- Check transactions
SELECT * FROM transactions ORDER BY created_at DESC;

-- Check asset quantities  
SELECT symbol, quantity FROM assets WHERE portfolio_id = 1;

-- Check wallet balance
SELECT wallet_balance FROM clients WHERE id = 1;
```

## 🚨 Common Issues & Solutions

### Issue 1: 404 Not Found
**Cause:** Controller not registered or wrong URL
**Fix:** Check `@RequestMapping` in AssetTradingController

### Issue 2: 500 Internal Server Error  
**Cause:** Missing dependencies or null pointer
**Fix:** Check application logs for stack trace

### Issue 3: Wallet Balance Not Updating
**Cause:** Transaction not committed or entity not saved
**Fix:** Check `@Transactional` annotation and repository calls

### Issue 4: Asset Not Found
**Cause:** AvailableAsset enum missing symbol
**Fix:** Check AvailableAsset.java for the symbol

## ✅ Success Criteria

- [ ] Application starts without errors
- [ ] Available assets endpoint returns data
- [ ] Can buy AAPL stock successfully  
- [ ] Wallet balance decreases after buy
- [ ] Asset quantity increases after buy
- [ ] Transaction is created in database
- [ ] Can sell stock successfully
- [ ] Error cases return proper HTTP codes

## 📝 Test Results Log

Record your results here:
- App Startup: ✅/❌ 
- Available Assets: ✅/❌
- Buy AAPL: ✅/❌
- Wallet Update: ✅/❌
- Sell AAPL: ✅/❌
- Error Handling: ✅/❌
