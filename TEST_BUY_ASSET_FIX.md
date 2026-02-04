# Test BuyAsset Fix

## 🧪 Quick Test Steps

### **1. Start both apps:**
```bash
cd backend && mvn spring-boot:run
cd frontend && npm start
```

### **2. Test Different Scenarios:**

#### **Scenario A: Direct Access (Should Show Warning)**
- URL: `http://localhost:3000/buy-asset`
- Expected: Warning message "No client selected"
- Console: Should show `URL Parameters - clientIdParam: null`

#### **Scenario B: With Client ID (Should Work)**
- URL: `http://localhost:3000/buy-asset?clientId=1`
- Expected: Working form with wallet balance
- Console: Should show `URL Parameters - clientIdParam: 1`

#### **Scenario C: Manual Entry (Should Work)**
- URL: `http://localhost:3000/buy-asset`
- Enter "1" in Client ID field
- Expected: Wallet balance loads, form enables

### **3. Check Console Logs:**
Look for these specific logs:
```
URL Parameters - clientIdParam: 1 portfolioIdParam: null
Setting clientId from URL param: 1
Loading wallet balance for client: 1
Raw balance response: 10000
Final numeric balance: 10000
```

### **4. Check Network Tab:**
Should see: `GET /api/v1/trading/wallet/1` → **200 OK**

## 🔍 What to Look For

### **✅ Working:**
- No "Loading wallet balance for client: null" errors
- Proper URL parameter parsing
- Wallet balance loads correctly
- Submit button enables/disables properly

### **❌ Still Broken:**
- Still seeing "Loading wallet balance for client: null"
- URL parameters not being parsed
- API calls with invalid endpoints

## 🚀 If Still Broken

### **Check 1: URL Parameters**
```javascript
// In browser console, run:
console.log(new URLSearchParams(window.location.search).get('clientId'));
```

### **Check 2: React Router**
```javascript
// In browser console, run:
import { useSearchParams } from 'react-router-dom';
// Check if useSearchParams is working
```

### **Check 3: Manual Test**
```javascript
// In browser console, run:
fetch('/api/v1/trading/wallet/1')
  .then(r => r.json())
  .then(console.log);
```

## 📋 Debug Checklist

- [ ] Console shows URL parameters correctly
- [ ] No "null clientId" errors
- [ ] Wallet balance loads when clientId=1
- [ ] Submit button works correctly
- [ ] Manual client entry works

**Run these tests and tell me what you see in the console!** 🔍
