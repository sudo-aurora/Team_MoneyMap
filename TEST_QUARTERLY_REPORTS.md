# 🧪 Test Quarterly Email Reports

## 🚀 Quick Setup Steps

### **1. Configure Email Settings**
Update `application.properties` with your Gmail credentials:

```properties
spring.mail.username=your-actual-email@gmail.com
spring.mail.password=your-actual-app-password
```

**Note:** Use an App Password, not your regular Gmail password.

### **2. Enable Scheduling**
Add `@EnableScheduling` to your main application class:

```java
@SpringBootApplication
@EnableScheduling
public class MoneyMapApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoneyMapApplication.class, args);
    }
}
```

### **3. Restart Backend**
```bash
cd backend
mvn spring-boot:run
```

## 🧪 Test the System

### **Test 1: Manual Report Generation**
**Endpoint:** `GET http://localhost:8181/api/v1/reports/quarterly/1`

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "clientInfo": { ... },
    "reportPeriod": "Q1 2026",
    "portfolioMetrics": { ... },
    "assetBreakdown": [ ... ],
    "topTransactions": [ ... ]
  },
  "message": "Quarterly report generated successfully"
}
```

### **Test 2: Test Email Configuration**
**Endpoint:** `GET http://localhost:8181/api/v1/reports/test-email`

**Expected Response:**
```json
{
  "success": true,
  "data": "Test email sent successfully",
  "message": "Check your inbox for the test email"
}
```

### **Test 3: Trigger All Reports**
**Endpoint:** `POST http://localhost:8181/api/v1/reports/quarterly/generate-all`

**Expected Response:**
```json
{
  "success": true,
  "data": "Quarterly report generation started for all active clients",
  "message": "Reports will be sent via email"
}
```

## 📊 What You'll See in Emails

### **Professional Report Design:**
- 🎨 Modern gradient header
- 📱 Responsive layout
- 📊 Metrics cards with hover effects
- 📈 Color-coded performance indicators
- 🏆 Professional branding

### **Comprehensive Data:**
- 👤 Client information (name, email, risk tolerance, goals)
- 💰 Portfolio metrics (total value, wallet balance, net worth)
- 📈 Performance data (returns, percentages, transaction count)
- 🏦 Asset breakdown (symbols, quantities, prices, percentages)
- 💼 Transaction history (top quarterly transactions)
- 🎯 AI-ready insights

### **Professional Features:**
- 📧 Automatic quarterly scheduling
- 📧 Manual report generation
- 📧 Email testing capabilities
- 📧 Error handling and logging
- 📧 Swagger documentation

## 🎯 Success Criteria

- [ ] Backend starts without errors
- [ ] Test email sends successfully
- [ ] Manual report generation works
- [ ] All reports trigger works
- [ ] Email contains all client data
- [ ] Report looks professional

## 🚀 Ready for Production

Once tests pass, you have:

✅ **Enterprise-grade quarterly reporting**
✅ **Professional client communications**
✅ **Automated portfolio analysis**
✅ **Email template system**
✅ **API endpoints for integration**
✅ **Maximum data utilization**

**This matches what top investment firms provide their clients!** 🎉
