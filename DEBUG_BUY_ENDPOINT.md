# Debug Buy Endpoint Issue

## 🐛 Problem: Getting METHOD_NOT_ALLOWED Error

The error shows:
```json
{"success":false,"message":"HTTP method 'GET' is not supported for this endpoint"}
```

This means the frontend is making a GET request instead of POST to `/api/v1/trading/buy`.

## 🔍 Debug Steps

### **1. Check Console Logs**
When you click "Buy Asset", look for:
```
Buy Asset Request: {clientId: 1, symbol: "AAPL", quantity: 10, price: 175.5}
TradingService Buy URL: /trading/buy?clientId=1&symbol=AAPL&quantity=10&price=175.5
TradingService Buy Method: POST
```

### **2. Check Network Tab**
1. Open Dev Tools → Network tab
2. Click "Buy Asset" button
3. Look for the `/trading/buy` request
4. Check:
   - **Method**: Should be POST (not GET)
   - **URL**: Should have query parameters
   - **Status**: Should be 200/201 (not 405)

### **3. Test Backend Directly**
Run this in terminal:
```bash
curl -X POST "http://localhost:8080/api/v1/trading/buy?clientId=1&symbol=AAPL&quantity=10&price=175.50"
```

Should return success, not METHOD_NOT_ALLOWED.

## 🔧 Possible Causes

### **Cause 1: Form Submission Issue**
The form might be submitting as GET instead of POST.

### **Cause 2: Axios Configuration**
The apiClient might be configured incorrectly.

### **Cause 3: URL Encoding Issue**
The URL parameters might be getting mangled.

## 🧪 Quick Test

### **Test 1: Manual API Call**
In browser console:
```javascript
fetch('/api/v1/trading/buy?clientId=1&symbol=AAPL&quantity=10&price=175.50', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'}
}).then(r => r.json()).then(console.log);
```

### **Test 2: Direct TradingService Call**
In browser console:
```javascript
import { tradingService } from './services/tradingService';
tradingService.buyAsset(1, 'AAPL', 10, 175.50).then(console.log);
```

## ✅ Expected Results

### **Working:**
- Console shows POST method
- Network tab shows POST request
- API returns success (201 Created)
- Asset is purchased successfully

### **Broken:**
- Console shows GET method
- Network tab shows GET request
- API returns METHOD_NOT_ALLOWED
- Purchase fails

## 🚀 What to Do

1. **Check console logs** when clicking Buy
2. **Check Network tab** for request method
3. **Run backend test** to confirm endpoint works
4. **Share screenshots** of console/network tabs

**This will tell us exactly where the GET vs POST issue is happening!** 🔍
