-- Simple Test Data for Buy/Sell Functionality
-- Minimal data to test the trading system

-- 1. Insert a test client with wallet
INSERT INTO clients (first_name, last_name, email, phone, address, city, state_or_province, postal_code, country, preferred_currency, timezone, locale, wallet_balance, wallet_locked_balance, wallet_created_at, active, created_at, updated_at)
VALUES 
('Test', 'User', 'test@example.com', '+1-555-0123', '123 Test St', 'Test City', 'TS', '12345', 'United States', 'USD', 'America/New_York', 'en_US', 10000.00, 0.00, NOW(), true, NOW(), NOW());

-- 2. Create a portfolio for the test client
INSERT INTO portfolios (name, description, total_value, client_id, active, created_at, updated_at)
VALUES 
('Test Portfolio', 'Portfolio for testing buy/sell', 0.00, 1, true, NOW(), NOW());

-- 3. Insert one test asset (Apple stock)
INSERT INTO assets (name, symbol, asset_type, quantity, purchase_price, current_price, current_value, purchase_date, notes, portfolio_id, exchange, sector, dividend_yield, fractional_allowed, created_at, updated_at)
VALUES 
('Apple Inc.', 'AAPL', 'STOCK', 0.0000, 0.00, 175.50, 0.00, '2024-01-01', 'Test Apple stock', 1, 'NASDAQ', 'Technology', 0.55, true, NOW(), NOW());

-- 4. Check the data
SELECT 'Client Wallet Balance:' as info, wallet_balance FROM clients WHERE id = 1;
SELECT 'Portfolio Total Value:' as info, total_value FROM portfolios WHERE id = 1;
SELECT 'Asset Details:' as info, symbol, quantity, current_price FROM assets WHERE portfolio_id = 1;
