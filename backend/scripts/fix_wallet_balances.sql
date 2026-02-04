-- Fix Wallet Balances for Trading System
-- This script ensures all clients have wallet balances

-- Check current wallet balances
SELECT id, first_name, last_name, wallet_balance, wallet_locked_balance, wallet_created_at 
FROM clients 
ORDER BY id;

-- Update wallet balances if they are null or zero
UPDATE clients 
SET wallet_balance = 10000.00, 
    wallet_locked_balance = 0.00, 
    wallet_created_at = COALESCE(wallet_created_at, NOW())
WHERE wallet_balance IS NULL OR wallet_balance = 0;

-- Show updated balances
SELECT id, first_name, last_name, wallet_balance, wallet_locked_balance, wallet_created_at 
FROM clients 
ORDER BY id;
