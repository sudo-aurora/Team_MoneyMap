# Fix Database Constraint Violation

## Problem
Your database has multiple portfolios per client, but the backend enforces **ONE portfolio per client**. This causes the error:
```
More than one row with the given identifier was found: 1, for class: Portfolio
```

## Solution

### Option 1: Clean Database (RECOMMENDED)

1. **Stop the backend** (if running)

2. **Open MySQL Command Line** or MySQL Workbench

3. **Run the cleanup script:**
   ```cmd
   mysql -u root -p moneymap_db < cleanup_database.sql
   ```
   
   OR copy-paste this in MySQL Workbench:
   ```sql
   USE moneymap_db;
   SET FOREIGN_KEY_CHECKS = 0;
   
   DELETE FROM payment_status_history;
   DELETE FROM alert_transactions;
   DELETE FROM alerts;
   DELETE FROM monitoring_rules;
   DELETE FROM payments;
   DELETE FROM transactions;
   DELETE FROM assets;
   DELETE FROM portfolios;
   DELETE FROM clients;
   
   ALTER TABLE clients AUTO_INCREMENT = 1;
   ALTER TABLE portfolios AUTO_INCREMENT = 1;
   ALTER TABLE assets AUTO_INCREMENT = 1;
   
   SET FOREIGN_KEY_CHECKS = 1;
   ```

4. **Restart the backend:**
   ```cmd
   cd C:\Users\sarga\Downloads\MoneyMap\portfoliomanager
   .\mvnw.cmd spring-boot:run
   ```

5. **Re-run the seed script:**
   ```cmd
   cd C:\Users\sarga\Downloads\MoneyMap\portfoliomanager\scripts
   python seed_data.py
   ```

6. **Test frontend:**
   - Open http://localhost:3000
   - Dashboard should now load properly!

---

### Option 2: Manual Fix (If you want to keep some data)

1. **Find duplicate portfolios:**
   ```sql
   SELECT client_id, COUNT(*) as count 
   FROM portfolios 
   GROUP BY client_id 
   HAVING count > 1;
   ```

2. **Delete duplicates (keep only the first one):**
   ```sql
   DELETE p1 FROM portfolios p1
   INNER JOIN portfolios p2 
   WHERE p1.id > p2.id 
   AND p1.client_id = p2.client_id;
   ```

3. **Restart backend**

---

## Why This Happened

The backend enforces a **1:1 relationship** between Client and Portfolio:
- Database: `UNIQUE` constraint on `portfolios.client_id`
- Entity: `@OneToOne` in Client.java
- Service: Validation in PortfolioServiceImpl

When the seed script ran initially (or if run multiple times), it may have created multiple portfolios before the constraint was added.

---

## After Fix

Your frontend should work perfectly:
- ✅ Dashboard loads
- ✅ Clients list shows
- ✅ Portfolios display correctly
- ✅ Assets, Payments, Alerts work

---

**Run Option 1 for a clean slate!** 🚀
