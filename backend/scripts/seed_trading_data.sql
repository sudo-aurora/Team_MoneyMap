-- Seed Data for Trading System
-- This script creates sample data for testing the new trading functionality

-- ===========================================
-- 1. INSERT CLIENTS WITH WALLETS
-- ===========================================

INSERT INTO clients (first_name, last_name, email, phone, address, city, state_or_province, postal_code, country, preferred_currency, timezone, locale, wallet_balance, wallet_locked_balance, wallet_created_at, active, created_at, updated_at)
VALUES 
('John', 'Doe', 'john.doe@example.com', '+1-555-0101', '123 Main St', 'New York', 'NY', '10001', 'United States', 'USD', 'America/New_York', 'en_US', 10000.00, 0.00, NOW(), true, NOW(), NOW()),
('Jane', 'Smith', 'jane.smith@example.com', '+1-555-0102', '456 Oak Ave', 'San Francisco', 'CA', '94102', 'United States', 'USD', 'America/Los_Angeles', 'en_US', 15000.00, 0.00, NOW(), true, NOW(), NOW()),
('Robert', 'Johnson', 'robert.johnson@example.com', '+1-555-0103', '789 Pine Rd', 'Chicago', 'IL', '60601', 'United States', 'USD', 'America/Chicago', 'en_US', 7500.00, 0.00, NOW(), true, NOW(), NOW()),
('Emily', 'Davis', 'emily.davis@example.com', '+1-555-0104', '321 Elm St', 'Boston', 'MA', '02101', 'United States', 'USD', 'America/New_York', 'en_US', 20000.00, 0.00, NOW(), true, NOW(), NOW()),
('Michael', 'Wilson', 'michael.wilson@example.com', '+1-555-0105', '654 Maple Dr', 'Seattle', 'WA', '98101', 'United States', 'USD', 'America/Los_Angeles', 'en_US', 12000.00, 0.00, NOW(), true, NOW(), NOW());

-- ===========================================
-- 2. INSERT PORTFOLIOS (one per client)
-- ===========================================

INSERT INTO portfolios (name, description, total_value, client_id, active, created_at, updated_at)
VALUES 
('Primary Portfolio', 'Main investment portfolio', 0.00, 1, true, NOW(), NOW()),
('Growth Portfolio', 'Aggressive growth strategy', 0.00, 2, true, NOW(), NOW()),
('Balanced Portfolio', 'Balanced risk approach', 0.00, 3, true, NOW(), NOW()),
('Conservative Portfolio', 'Low-risk investments', 0.00, 4, true, NOW(), NOW()),
('Tech Portfolio', 'Technology focused investments', 0.00, 5, true, NOW(), NOW());

-- ===========================================
-- 3. INSERT SAMPLE ASSETS (for existing holdings)
-- ===========================================

-- Stocks
INSERT INTO assets (name, symbol, asset_type, quantity, purchase_price, current_price, current_value, purchase_date, notes, portfolio_id, created_at, updated_at)
VALUES 
('Apple Inc.', 'AAPL', 'STOCK', 50.0000, 150.00, 175.50, 8775.00, '2024-01-15', 'Long-term investment', 1, NOW(), NOW()),
('Microsoft Corporation', 'MSFT', 'STOCK', 25.0000, 350.00, 380.75, 9518.75, '2024-01-20', 'Tech sector holding', 1, NOW(), NOW()),
('Tesla Inc.', 'TSLA', 'STOCK', 15.0000, 200.00, 245.80, 3687.00, '2024-02-01', 'Growth stock', 2, NOW(), NOW()),
('Alphabet Inc.', 'GOOGL', 'STOCK', 30.0000, 120.00, 140.25, 4207.50, '2024-01-25', 'Search engine giant', 2, NOW(), NOW()),
('Amazon.com Inc.', 'AMZN', 'STOCK', 20.0000, 140.00, 155.30, 3106.00, '2024-02-05', 'E-commerce leader', 3, NOW(), NOW());

-- Cryptocurrencies
INSERT INTO assets (name, symbol, asset_type, quantity, purchase_price, current_price, current_value, purchase_date, notes, portfolio_id, created_at, updated_at)
VALUES 
('Bitcoin', 'BTC', 'CRYPTO', 0.5000, 40000.00, 43250.00, 21625.00, '2024-01-10', 'Digital gold', 1, NOW(), NOW()),
('Ethereum', 'ETH', 'CRYPTO', 5.0000, 2000.00, 2280.50, 11402.50, '2024-01-12', 'Smart contracts platform', 1, NOW(), NOW()),
('Cardano', 'ADA', 'CRYPTO', 1000.0000, 0.50, 0.58, 580.00, '2024-01-18', 'Proof of stake blockchain', 2, NOW(), NOW()),
('Solana', 'SOL', 'CRYPTO', 50.0000, 80.00, 98.75, 4937.50, '2024-02-03', 'High-performance blockchain', 3, NOW(), NOW());

-- Gold
INSERT INTO assets (name, symbol, asset_type, quantity, purchase_price, current_price, current_value, purchase_date, notes, portfolio_id, created_at, updated_at)
VALUES 
('24 Karat Gold', 'GOLD24K', 'GOLD', 10.0000, 65.00, 68.50, 685.00, '2024-01-05', 'Physical gold bars', 4, NOW(), NOW()),
('22 Karat Gold', 'GOLD22K', 'GOLD', 15.0000, 60.00, 62.75, 941.25, '2024-01-08', 'Gold jewelry', 4, NOW(), NOW()),
('Silver', 'SILVER', 'GOLD', 100.0000, 0.90, 0.95, 95.00, '2024-01-22', 'Silver coins', 5, NOW(), NOW());

-- Mutual Funds
INSERT INTO assets (name, symbol, asset_type, quantity, purchase_price, current_price, current_value, purchase_date, notes, portfolio_id, created_at, updated_at)
VALUES 
('Vanguard 500 Index Admiral', 'VFIAX', 'MUTUAL_FUND', 20.0000, 400.00, 425.30, 8506.00, '2024-01-03', 'S&P 500 index fund', 5, NOW(), NOW()),
('Fidelity 500 Index', 'FXAIX', 'MUTUAL_FUND', 50.0000, 110.00, 118.75, 5937.50, '2024-01-15', 'Low-cost index fund', 5, NOW(), NOW());

-- ===========================================
-- 4. INSERT SAMPLE TRANSACTIONS
-- ===========================================

-- Buy transactions for existing assets
INSERT INTO transactions (asset_id, transaction_type, quantity, price_per_unit, total_amount, fees, transaction_date, notes, created_at)
VALUES 
-- Stock purchases
(1, 'BUY', 50.0000, 150.00, 7500.00, 7.50, '2024-01-15 10:30:00', 'Initial Apple purchase', NOW()),
(2, 'BUY', 25.0000, 350.00, 8750.00, 8.75, '2024-01-20 14:15:00', 'Microsoft investment', NOW()),
(3, 'BUY', 15.0000, 200.00, 3000.00, 6.00, '2024-02-01 09:45:00', 'Tesla shares', NOW()),
(4, 'BUY', 30.0000, 120.00, 3600.00, 7.20, '2024-01-25 11:20:00', 'Google stock', NOW()),
(5, 'BUY', 20.0000, 140.00, 2800.00, 5.60, '2024-02-05 16:30:00', 'Amazon purchase', NOW()),

-- Crypto purchases
(6, 'BUY', 0.5000, 40000.00, 20000.00, 50.00, '2024-01-10 13:00:00', 'Bitcoin investment', NOW()),
(7, 'BUY', 5.0000, 2000.00, 10000.00, 25.00, '2024-01-12 15:30:00', 'Ethereum purchase', NOW()),
(8, 'BUY', 1000.0000, 0.50, 500.00, 5.00, '2024-01-18 10:15:00', 'Cardano accumulation', NOW()),
(9, 'BUY', 50.0000, 80.00, 4000.00, 20.00, '2024-02-03 14:45:00', 'Solana investment', NOW()),

-- Gold purchases
(10, 'BUY', 10.0000, 65.00, 650.00, 10.00, '2024-01-05 12:00:00', 'Gold bars purchase', NOW()),
(11, 'BUY', 15.0000, 60.00, 900.00, 15.00, '2024-01-08 11:30:00', 'Gold jewelry', NOW()),
(12, 'BUY', 100.0000, 0.90, 90.00, 2.00, '2024-01-22 16:00:00', 'Silver coins', NOW()),

-- Mutual fund purchases
(13, 'BUY', 20.0000, 400.00, 8000.00, 0.00, '2024-01-03 09:00:00', 'Vanguard index fund', NOW()),
(14, 'BUY', 50.0000, 110.00, 5500.00, 0.00, '2024-01-15 10:30:00', 'Fidelity index fund', NOW());

-- Sample sell transactions
INSERT INTO transactions (asset_id, transaction_type, quantity, price_per_unit, total_amount, fees, transaction_date, notes, created_at)
VALUES 
(1, 'SELL', 10.0000, 170.00, 1700.00, 5.00, '2024-01-30 15:20:00', 'Partial Apple profit taking', NOW()),
(6, 'SELL', 0.1000, 42000.00, 4200.00, 10.00, '2024-01-25 12:30:00', 'Bitcoin profit taking', NOW());

-- ===========================================
-- 5. UPDATE PORTFOLIO TOTAL VALUES
-- ===========================================

UPDATE portfolios 
SET total_value = (
    SELECT COALESCE(SUM(current_value), 0) 
    FROM assets 
    WHERE assets.portfolio_id = portfolios.id
);

-- ===========================================
-- 6. UPDATE CLIENT WALLETS (simulate some trading activity)
-- ===========================================

-- Client 1: Started with $15000, spent $7500 on stocks + $30000 on crypto = $37500 spent, $10000 remaining
UPDATE clients SET wallet_balance = 10000.00 WHERE id = 1;

-- Client 2: Started with $15000, spent $6600 on stocks + $5000 on crypto = $11600 spent, $3400 remaining  
UPDATE clients SET wallet_balance = 3400.00 WHERE id = 2;

-- Client 3: Started with $7500, spent $2800 on stocks + $4000 on crypto = $6800 spent, $700 remaining
UPDATE clients SET wallet_balance = 700.00 WHERE id = 3;

-- Client 4: Started with $20000, spent $1635 on gold = $1635 spent, $18365 remaining
UPDATE clients SET wallet_balance = 18365.00 WHERE id = 4;

-- Client 5: Started with $12000, spent $95 on gold + $14443.50 on mutual funds = $14538.50 spent, negative balance (margin)
UPDATE clients SET wallet_balance = -2538.50 WHERE id = 5;

-- ===========================================
-- 7. VERIFICATION QUERIES (for testing)
-- ===========================================

-- Check client wallet balances
SELECT id, first_name, last_name, wallet_balance FROM clients;

-- Check portfolio values
SELECT p.id, p.name, p.total_value, c.first_name, c.last_name 
FROM portfolios p 
JOIN clients c ON p.client_id = c.id;

-- Check asset holdings
SELECT a.symbol, a.name, a.asset_type, a.quantity, a.current_value, 
       c.first_name, c.last_name 
FROM assets a 
JOIN portfolios p ON a.portfolio_id = p.id 
JOIN clients c ON p.client_id = c.id 
ORDER BY c.last_name, a.asset_type;

-- Check transaction history
SELECT t.transaction_type, t.quantity, t.price_per_unit, t.total_amount, 
       a.symbol, a.name, c.first_name, c.last_name, t.transaction_date
FROM transactions t
JOIN assets a ON t.asset_id = a.id
JOIN portfolios p ON a.portfolio_id = p.id
JOIN clients c ON p.client_id = c.id
ORDER BY t.transaction_date DESC;

-- ===========================================
-- 8. SAMPLE TRADING TEST QUERIES
-- ===========================================

-- Test buying more AAPL for client 1 (should succeed if they have funds)
-- INSERT INTO transactions (asset_id, transaction_type, quantity, price_per_unit, total_amount, transaction_date, notes)
-- VALUES (1, 'BUY', 10.0000, 175.50, 1755.00, NOW(), 'Additional Apple purchase');

-- Test selling some BTC for client 1 (should succeed if they have enough)
-- INSERT INTO transactions (asset_id, transaction_type, quantity, price_per_unit, total_amount, transaction_date, notes)  
-- VALUES (6, 'SELL', 0.1000, 43250.00, 4325.00, NOW(), 'Bitcoin partial sale');

COMMIT;
