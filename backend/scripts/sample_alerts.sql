-- Sample Alert Data for MoneyMap
-- This script inserts sample monitoring rules and alerts for testing

-- Insert sample monitoring rules
INSERT INTO monitoring_rules (rule_name, rule_type, severity, active, description, threshold_amount, threshold_currency, max_transactions, time_window_minutes, daily_limit_amount, lookback_days, created_at, updated_at) VALUES
('High Value Transaction Alert', 'AMOUNT_THRESHOLD', 'HIGH', true, 'Alert when transaction amount exceeds $10,000', 10000.00, 'USD', NULL, NULL, NULL, NULL, NOW(), NOW()),
('Frequent Transactions Alert', 'VELOCITY', 'MEDIUM', true, 'Alert when more than 5 transactions occur within 30 minutes', NULL, NULL, 5, 30, NULL, NULL, NOW(), NOW()),
('Daily Spending Limit Alert', 'DAILY_LIMIT', 'HIGH', true, 'Alert when daily spending exceeds $5,000', NULL, NULL, NULL, NULL, 5000.00, NULL, NOW(), NOW()),
('New Payee Alert', 'NEW_PAYEE', 'LOW', true, 'Alert on first transaction to a new payee within 90 days', NULL, NULL, NULL, NULL, NULL, 90, NOW(), NOW()),
('Medium Value Transaction Alert', 'AMOUNT_THRESHOLD', 'MEDIUM', true, 'Alert when transaction amount exceeds $1,000', 1000.00, 'USD', NULL, NULL, NULL, NULL, NOW(), NOW());

-- Get the rule IDs for use in alerts (assuming they were inserted with IDs 1-5)
-- Insert sample alerts with different statuses and severities
INSERT INTO alerts (alert_reference, rule_id, severity, status, message, account_id, acknowledged_at, acknowledged_by, closed_at, closed_by, resolution_notes, created_at) VALUES
('ALT-20250201-001', 1, 'HIGH', 'OPEN', 'Transaction amount $12,500.00 exceeds threshold of $10,000.00', 'ACC-001-123456', NULL, NULL, NULL, NULL, NULL, '2025-02-01 10:30:00'),
('ALT-20250201-002', 2, 'MEDIUM', 'ACKNOWLEDGED', '6 transactions detected in last 25 minutes', 'ACC-002-789012', '2025-02-01 11:15:00', 'Admin', NULL, NULL, NULL, '2025-02-01 10:45:00'),
('ALT-20250201-003', 3, 'HIGH', 'INVESTIGATING', 'Daily spending total $5,250.00 exceeds limit of $5,000.00', 'ACC-003-345678', '2025-02-01 12:00:00', 'Admin', NULL, NULL, NULL, '2025-02-01 11:30:00'),
('ALT-20250201-004', 4, 'LOW', 'CLOSED', 'First transaction to new payee: Netflix Subscription', 'ACC-001-123456', '2025-02-01 09:00:00', 'System', '2025-02-01 09:30:00', 'Admin', 'Legitimate subscription service', '2025-02-01 08:45:00'),
('ALT-20250201-005', 5, 'MEDIUM', 'OPEN', 'Transaction amount $1,200.00 exceeds threshold of $1,000.00', 'ACC-004-901234', NULL, NULL, NULL, NULL, NULL, '2025-02-01 13:15:00'),
('ALT-20250201-006', 1, 'HIGH', 'DISMISSED', 'Transaction amount $15,000.00 exceeds threshold of $10,000.00', 'ACC-005-567890', NULL, NULL, '2025-02-01 14:00:00', 'Admin', 'Large business expense - approved', '2025-02-01 12:30:00'),
('ALT-20250201-007', 2, 'MEDIUM', 'OPEN', '7 transactions detected in last 20 minutes', 'ACC-002-789012', NULL, NULL, NULL, NULL, NULL, '2025-02-01 14:45:00'),
('ALT-20250201-008', 3, 'HIGH', 'CLOSED', 'Daily spending total $6,100.00 exceeds limit of $5,000.00', 'ACC-006-234567', '2025-02-01 10:30:00', 'Admin', '2025-02-01 11:00:00', 'Admin', 'Emergency medical expense - verified', '2025-02-01 09:15:00'),
('ALT-20250201-009', 5, 'MEDIUM', 'ACKNOWLEDGED', 'Transaction amount $2,500.00 exceeds threshold of $1,000.00', 'ACC-007-890123', '2025-02-01 15:30:00', 'System', NULL, NULL, NULL, '2025-02-01 15:00:00'),
('ALT-20250201-010', 1, 'HIGH', 'OPEN', 'Transaction amount $11,750.00 exceeds threshold of $10,000.00', 'ACC-008-456789', NULL, NULL, NULL, NULL, NULL, '2025-02-01 16:00:00');

-- Insert some sample payments to link with alerts (optional)
INSERT INTO payments (payment_reference, source_account, destination_account, amount, currency, status, reference, description, created_at, updated_at) VALUES
('PAY-20250201-001', 'ACC-001-123456', 'MERCHANT-001', 12500.00, 'USD', 'COMPLETED', 'REF-001', 'High value purchase', '2025-02-01 10:30:00', '2025-02-01 10:35:00'),
('PAY-20250201-002', 'ACC-002-789012', 'MERCHANT-002', 250.00, 'USD', 'COMPLETED', 'REF-002', 'Online shopping', '2025-02-01 10:45:00', '2025-02-01 10:50:00'),
('PAY-20250201-003', 'ACC-002-789012', 'MERCHANT-003', 180.00, 'USD', 'COMPLETED', 'REF-003', 'Restaurant payment', '2025-02-01 11:00:00', '2025-02-01 11:05:00'),
('PAY-20250201-004', 'ACC-002-789012', 'MERCHANT-004', 320.00, 'USD', 'COMPLETED', 'REF-004', 'Gas station', '2025-02-01 11:10:00', '2025-02-01 11:15:00'),
('PAY-20250201-005', 'ACC-002-789012', 'MERCHANT-005', 150.00, 'USD', 'COMPLETED', 'REF-005', 'Grocery store', '2025-02-01 11:20:00', '2025-02-01 11:25:00'),
('PAY-20250201-006', 'ACC-002-789012', 'MERCHANT-006', 95.00, 'USD', 'COMPLETED', 'REF-006', 'Coffee shop', '2025-02-01 11:30:00', '2025-02-01 11:35:00');

-- Link alerts to payments (alert_transactions junction table)
INSERT INTO alert_transactions (alert_id, payment_id) VALUES
(1, 1),
(2, 2),
(2, 3),
(2, 4),
(2, 5),
(2, 6);
