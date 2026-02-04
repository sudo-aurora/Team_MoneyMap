# Debug Wallet Balance API

## 🐛 Issue: Wallet Balance Showing Zero

### **🔍 Debug Steps:**

### 1. Test the API directly
```bash
# Start backend
cd backend && mvn spring-boot:run

# Test wallet balance API
curl -v http://localhost:8080/api/v1/trading/wallet/1

# Expected response:
{
  "success": true,
  "data": 10000.00
}
```

### 2. Check database
```sql
-- Run this in your database
SELECT id, first_name, last_name, wallet_balance, wallet_locked_balance, wallet_created_at 
FROM clients 
ORDER BY id;
```

### 3. Run fix script if needed
```bash
# Run the SQL script to fix wallet balances
mysql -u username -p database_name < backend/scripts/fix_wallet_balances.sql
```

### 4. Test frontend
- Open browser dev tools
- Go to Network tab
- Visit client detail page
- Check the `/trading/wallet/1` call
- Verify the response format

## 🔧 Frontend Fix Applied

Updated both BuyAsset.jsx and ClientDetail.jsx to handle BigDecimal properly:

```javascript
const loadWalletBalance = async () => {
  try {
    const balance = await tradingService.getWalletBalance(clientId);
    // Convert BigDecimal to number and handle potential string values
    const numericBalance = typeof balance === 'string' ? parseFloat(balance) : balance;
    setWalletBalance(numericBalance || 0);
  } catch (err) {
    console.error('Error loading wallet balance:', err);
    setWalletBalance(0);
  }
};
```

## ✅ Success Criteria

- [ ] API returns wallet balance (not zero)
- [ ] Frontend shows correct balance
- [ ] ClientDetail page shows wallet balance
- [ ] BuyAsset page shows wallet balance
- [ ] Balance updates after purchases

## 🚀 Test Flow

1. **Test API:** `curl http://localhost:8080/api/v1/trading/wallet/1`
2. **Check Database:** Run SQL to verify wallet balances
3. **Run Fix Script:** If balances are zero
4. **Test Frontend:** Visit client detail and buy asset pages
5. **Verify Display:** Should show $10,000.00 for client 1
