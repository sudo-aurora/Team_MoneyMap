-- MoneyMap Database Cleanup Script
-- Run this to clear all data and reset for re-seeding

USE moneymap_db;

SET FOREIGN_KEY_CHECKS = 0;

-- Delete all data in reverse dependency order
DELETE FROM payment_status_history;
DELETE FROM alert_transactions;
DELETE FROM alerts;
DELETE FROM monitoring_rules;
DELETE FROM payments;
DELETE FROM transactions;
DELETE FROM assets;
DELETE FROM portfolios;
DELETE FROM clients;

-- Reset auto-increment counters
ALTER TABLE clients AUTO_INCREMENT = 1;
ALTER TABLE portfolios AUTO_INCREMENT = 1;
ALTER TABLE assets AUTO_INCREMENT = 1;
ALTER TABLE transactions AUTO_INCREMENT = 1;
ALTER TABLE payments AUTO_INCREMENT = 1;
ALTER TABLE payment_status_history AUTO_INCREMENT = 1;
ALTER TABLE monitoring_rules AUTO_INCREMENT = 1;
ALTER TABLE alerts AUTO_INCREMENT = 1;
ALTER TABLE alert_transactions AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Database cleaned successfully!' AS Status;
