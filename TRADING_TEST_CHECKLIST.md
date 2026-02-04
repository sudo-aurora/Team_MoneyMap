# Trading System Test Checklist

## 🚀 Quick Test for Buy/Sell Functionality

### 1. Start Application
```bash
cd backend
mvn spring-boot:run
```

### 2. Run Simple Test Data
```bash
mysql -u your_username -p your_database < test_buy_sell.sql
```

### 3. Test Available Assets Endpoint
```bash
curl http://localhost:8080/api/v1/trading/assets/available
```
**Expected:** JSON array with AAPL, MSFT, BTC, etc.

### 4. Test Client Wallet
```bash
curl http://localhost:8080/api/v1/trading/wallet/1
```
**Expected:** `{"data": 10000.00}`

### 5. Test Buy Stock
```bash
curl -X POST "http://localhost:8080/api/v1/trading/buy?clientId=1&symbol=AAPL&quantity=10&price=175.50"
```
**Expected:** Creates transaction, updates asset quantity, deducts from wallet

### 6. Test Sell Stock  
```bash
curl -X POST "http://localhost:8080/api/v1/trading/sell?clientId=1&assetId=1&quantity=5&price=180.00"
```
**Expected:** Creates SELL transaction, reduces asset quantity, adds to wallet

### 7. Verify Results
```bash
# Check updated wallet
curl http://localhost:8080/api/v1/trading/wallet/1

# Check asset quantity
curl http://localhost:8080/api/v1/assets/1

# Check transactions
curl http://localhost:8080/api/v1/assets/1/transactions
```

## 🔧 If Something Fails

### Common Issues:
1. **404 Errors** → Check if endpoints exist in AssetTradingController
2. **Wallet Issues** → Check if Client entity has wallet fields
3. **Asset Creation Issues** → Check if AssetFactory works with AvailableAsset enum
4. **Transaction Issues** → Check if TransactionService creates transactions properly

### Files to Check:
- `AvailableAsset.java` - Should have AAPL, MSFT, etc.
- `AssetTradingController.java` - Should have /trading endpoints  
- `AssetTradingServiceImpl.java` - Should handle buy/sell logic
- `Client.java` - Should have wallet_balance field
- `AssetFactory.java` - Should create assets from AvailableAsset

## 🎯 Success Criteria
✅ Available assets endpoint returns data  
✅ Can buy AAPL stock  
✅ Wallet balance decreases after buy  
✅ Asset quantity increases after buy  
✅ Transaction is created  
✅ Can sell stock  
✅ Wallet balance increases after sell  
✅ Asset quantity decreases after sell
