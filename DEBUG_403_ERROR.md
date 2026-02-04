# Debug 403 Forbidden Error

## 🐛 Issue: 403 Forbidden on Buy Asset

The logs show:
- Frontend: ✅ Working correctly (POST method, correct URL)
- Backend: ❌ Returning 403 Forbidden

## 🔍 Debug Steps

### **1. Test Backend Directly**
Run this in terminal:
```bash
curl -v -X POST "http://localhost:8080/api/v1/trading/buy?clientId=1&symbol=TSLA&quantity=2&price=245.8"
```

**Expected:** 201 Created or proper error message  
**If 403:** Backend issue

### **2. Check Backend Logs**
Look in backend console for:
- Any security-related errors
- Validation errors
- Database connection issues
- Transaction errors

### **3. Test Simpler Request**
```bash
curl -v -X POST "http://localhost:8080/api/v1/trading/buy?clientId=1&symbol=AAPL&quantity=1&price=100"
```

### **4. Test Available Assets Endpoint**
```bash
curl -v "http://localhost:8080/api/v1/trading/assets/available"
```

Should return assets list (200 OK).

## 🔧 Possible Causes

### **Cause 1: Database Connection**
- Client ID 1 doesn't exist
- Database connection issues
- Transaction deadlocks

### **Cause 2: Validation Issues**
- Symbol validation failing
- Price/quantity validation
- Wallet balance check

### **Cause 3: Spring Security**
- Default security blocking POST
- CSRF protection
- Method security

### **Cause 4: Exception Handling**
- Exception being converted to 403
- Global exception handler issue

## 🧪 Quick Tests

### **Test 1: Check Client Exists**
```bash
curl -v "http://localhost:8080/api/v1/trading/wallet/1"
```

### **Test 2: Check Available Assets**
```bash
curl -v "http://localhost:8080/api/v1/trading/assets/available"
```

### **Test 3: Test with Different Client**
```bash
curl -v -X POST "http://localhost:8080/api/v1/trading/buy?clientId=2&symbol=AAPL&quantity=1&price=100"
```

## ✅ Expected Working Response

```json
{
  "success": true,
  "data": {
    "id": 1,
    "transactionType": "BUY",
    "quantity": 2,
    "pricePerUnit": 245.80,
    // ... other fields
  },
  "message": "Asset purchased successfully"
}
```

## ❌ If Still Getting 403

1. **Check backend logs** for detailed error
2. **Test with curl** to isolate frontend
3. **Check database** for client existence
4. **Verify seed data** was loaded

**Run the curl test and share the exact error message!** 🔍
