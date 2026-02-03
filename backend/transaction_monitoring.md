# Transaction Monitoring & Alerts Dashboard Training Project

[TOC]

## Overview

Your team is challenged with designing a transaction monitoring and alerting system that detects suspicious or unusual transaction patterns in real-time.

The system will continuously evaluate transactions against configurable rules and generate alerts when thresholds or patterns are detected, maintaining a full audit trail of all alerts and their resolutions.

Your task is to build the application.

## Technical Goals

You should aim to create a Transaction Monitoring REST API and dashboard. 

This API should allow storing transactions, defining monitoring rules, generating alerts, and managing the alert lifecycle.

If/When you have made progress on the core requirements then requirements for further enhancements will be provided. This will include open-ended enhancements whereby you can make use of your particular skills and experience.

We will continue working on this project into the week where you start looking at Web front ends.

For the Front end, you can use the technologies you have learned about in your training. If you wish to use a specific framework or some other technology, please check with your instructor.

The Front end should facilitate your users to (in order of priority):

* View all transactions (with filtering and search)
* View active alerts
* View alert details and triggering transactions
* Acknowledge alerts
* Close or dismiss alerts
* View alert history
* View and edit monitoring rules

In terms of detailed requirements, your instructor will act as customer, and will tell you what they want. You can arrange meetings with them as required.

## Alert Lifecycle

Your system should implement the following alert workflow:

```
OPEN â†’ ACKNOWLEDGED â†’ INVESTIGATING â†’ CLOSED
         â†“                 â†“
    DISMISSED         DISMISSED
```

**Status Definitions:**

* **OPEN** - Alert has been generated but not yet reviewed
* **ACKNOWLEDGED** - Alert has been seen by an operator but not yet investigated
* **INVESTIGATING** - Alert is actively being investigated
* **CLOSED** - Investigation complete, issue resolved or confirmed legitimate
* **DISMISSED** - Alert determined to be false positive or not requiring action

## Core Requirements

### Database Schema

Your database should include at minimum:

1. **Transactions Table**
   - Transaction ID (unique identifier)
   - Account ID
   - Counterparty/Payee ID
   - Transaction type (DEBIT/CREDIT)
   - Amount
   - Currency
   - Timestamp
   - Description/Reference
   - Status

2. **Alerts Table**
   - Alert ID (unique identifier)
   - Rule ID (which rule triggered this alert)
   - Severity (HIGH/MEDIUM/LOW)
   - Status (OPEN/ACKNOWLEDGED/INVESTIGATING/CLOSED/DISMISSED)
   - Created timestamp
   - Acknowledged timestamp
   - Acknowledged by (user/operator)
   - Closed timestamp
   - Closed by (user/operator)
   - Resolution notes

3. **Alert Transactions Table** (many-to-many relationship)
   - Alert ID
   - Transaction ID
   - (Links alerts to the transactions that triggered them)

4. **Rules Table**
   - Rule ID (unique identifier)
   - Rule name
   - Rule type (AMOUNT_THRESHOLD, VELOCITY, NEW_PAYEE, etc.)
   - Rule parameters (JSON or separate columns)
   - Active/Inactive status
   - Severity level
   - Created timestamp
   - Last modified timestamp

### API Endpoints

Your API should support operations such as:

* `POST /transactions` - Record a new transaction
* `GET /transactions` - List transactions (with filtering by date, account, amount)
* `GET /transactions/{id}` - Retrieve transaction details
* `GET /alerts` - List alerts (with filtering by status, severity, date)
* `GET /alerts/{id}` - Retrieve alert details including related transactions
* `PUT /alerts/{id}/acknowledge` - Acknowledge an alert
* `PUT /alerts/{id}/status` - Update alert status (investigating, closed, dismissed)
* `POST /alerts/{id}/notes` - Add resolution notes to an alert
* `GET /rules` - List all monitoring rules
* `POST /rules` - Create a new monitoring rule
* `PUT /rules/{id}` - Update a monitoring rule
* `DELETE /rules/{id}` - Delete or deactivate a rule

Use the REST API technology taught in your class (e.g., Spring Boot, Flask, Express.js, etc.).

## Rule Types

Your system should support these basic rule types:

### 1. Amount Threshold Rule
Trigger alert when a single transaction exceeds a threshold amount.

**Example:** Alert if any transaction > $10,000

### 2. Velocity Rule
Trigger alert when N transactions occur within T time period.

**Example:** Alert if more than 5 transactions occur within 10 minutes from the same account

### 3. New Payee Rule
Trigger alert when a transaction is made to a previously unseen payee/counterparty.

**Example:** Alert on first transaction to any new payee from an account

### 4. Daily Limit Rule (Advanced)
Trigger alert when cumulative transaction amount exceeds daily limit.

**Example:** Alert if total transactions from an account exceed $50,000 in one day

## Notes

1. There will be no authentication and a single operator is assumed, i.e. there is no requirement to manage multiple users or operators.

2. You should use the database technology you have been using in the training for any persistent storage.

3. Make good use of git. Use branching and pull requests if you can.

4. Any documentation about how to use your REST API would be useful. Maybe Swagger/OpenAPI if you have covered it in your training.

5. For this training project, transactions can be generated via API calls or through a simple data generator/simulator.

6. Rules can initially be hardcoded, but implementing configurable rules is a great enhancement.

## Technical Getting Started Checklist

1. Create your project structure.

2. Create a Git repository. Your instructors will guide you as to which Git platform to use.

3. Add, commit, push your skeleton project to your Git repository.

4. Ensure your team has access to the Git repository.

5. Decide on the absolute MINIMUM fields for a first working system e.g. the first version may just have transactions with id, amount, timestamp, and a single hardcoded rule for amount threshold.

If you get stuck getting any of the above completed then contact your instructor for help.

## Project Management Getting Started Checklist

1. As a team decide how you will approach the work. E.g. 2 people on the backend, 1 person on UI Design Vs. Everyone on the backend until a basic system is working.

2. Make a task list. Ideally use a tool such as Trello to keep track of tasks.

3. Some of your team may work on the DESIGN of a more fully-featured application, while some of your team work on BUILDING some small pieces as demonstration.

4. Choose the tasks required for a MINIMAL implementation first.

5. Your instructor will drop in regularly to see how you're progressing. Make a note of any questions so that you're ready to ask them then.

6. Your team should get together and decide on an initial set of data that you will store. A good team decision on this is a good path to success, however remember to STAY AGILE.
The single biggest problem teams face is starting out with a data model that is too complex.

## Suggestions for Success

1. START SMALL. Get a system working that stores transactions and implements ONE simple rule (e.g., amount threshold). You can then add more sophisticated rules.

2. Try pair programming, it can be very effective.

3. Take conscious steps to keep a good energy in the team. E.g. give your team a name, systematically plan check-ins with each other.

4. Emphasise quality over quantity.

5. Think about performance early - what if you need to check 1000 transactions per second against multiple rules?

6. Consider separation of concerns - transaction recording should be fast and independent from rule evaluation.

### Event-Driven Architecture
* Should rule evaluation happen synchronously when a transaction is recorded?
* Consider using an event/message queue for asynchronous processing. This will require some research by you.
* Decoupling transaction recording from alert generation
* What are the tradeoffs between real-time and near-real-time processing?

### Rule Engine Patterns
* How do you design a flexible rule engine?
* Should rules be hardcoded or configurable?
* Consider the Strategy pattern or Chain of Responsibility pattern
* How do you make it easy to add new rule types without changing core code?

### Real-time vs Batch Processing
* Should rules evaluate every transaction immediately?
* Or should they run periodically (e.g., every minute) in batch?
* What are the performance implications?
* Consider hybrid approaches for different rule types

### Alert Fatigue / Priority Levels
* Too many alerts = operators ignore them
* How do you prioritize alerts? (HIGH/MEDIUM/LOW severity)
* Should repeated similar alerts be grouped?
* Consider alert throttling or deduplication

### Database Query Optimization
* Velocity rules require counting recent transactions - how do you make this fast?
* Indexes on timestamp and account fields are crucial
* Consider using window functions or time-series databases
* SQL GROUP BY and HAVING clauses for aggregation

### Time-Based Logic
* All velocity rules depend on accurate time handling
* Time zones matter!
* Consider using UTC internally
* Be careful with date/time arithmetic

## Project Presentations

At the end of the program you will get the opportunity to present your project to your instructors and also potentially your manager and other interested stakeholders from within the firm.

The duration of your presentation will be decided by your instructor, but they are typically 15-20 mins for groups of 3 and sometimes up to 25 or even 30 minutes for larger groups.

### Presentation Guidelines

Here is a suggested flow. You don't have to follow this exactly, but it gives you a suggested outline:

- Tell a story!
    - Your presentation should have a beginning, a middle and an end
- Start by introducing your team
- Then introduce the project
    - What have you been learning?
    - What were you asked to do?
    - How much time have you had to work on it?
- Then explain how you approached the project
    - Did you divide roles, e.g. backend or frontend?
    - Or did you code together, e.g. pair-programming?
    - What technologies and tools did you use?
- Then show what you built
    - Start with an overview of your data model â€“ explain your decisions
    - Then show a high-level architecture of your application
      - This could be a simple diagram in PowerPoint
    - Demonstrate the rule engine in action with a live demo
    - Show how alerts are generated and managed
    - Demonstrate alert lifecycle (acknowledge, investigate, close)
- Then tell us what challenges you faced
    - Did you work well together as a team?
    - Were there any technical challenges?
    - What mistakes did you make?
    - What would you do differently?
    - How did you optimize rule evaluation performance?
- Then tell us what you would do next if you had more time
- And finally â€“ thank you for listening, any questions

- Everyone is expected to speak
- Keep your cameras on throughout the presentation
- NOTE: YOU WILL BE EXPECTED TO ASK OTHER TEAMS QUESTIONS

### Presentation Mechanics

* Depending upon the size of your class, the presentations will be delivered with your groups nominated lead instructor
* The lead instructor will typically have created a schedule for the presentations and will have circulated that in advance with 15-30 mins per group
* The presentation will NOT be allowed to overrun to ensure we keep to time
* The presentation schedule is sent out to wider firm staff so they know when to come if someone wants to see your presentation
* When a group says "any questions?", to avoid any unnecessary silences, the group that went before you MUST ask a question. If you are going first, then the group scheduled last must ask a question
* If your class is using virtual machines then they will continue to be available for the presentation

## Appendix A: Notes on Teamwork

It is expected that you work closely as a team during this project.

Your team should be self-organising, but should raise issues with instructors if they are potential blockers to progress.

Your team can use a task management system such as Trello to keep track of tasks and progress. Divide the work appropriately.

Your team should keep track of all source code with git.

You may choose to create a separate repository for each component that you tackle e.g. front-end code can be in its own repository. If you create more than one back end application, then each can have its own repository. To keep track of your repositories, you can use a single 'Project' that each of your repositories is part of.

Your instructor and team members need to access all repositories, so they should be either:

a) Made public
b) Shared with your instructor and all team members.

Throughout your work, you should ensure good communication and organise regular check-ins with each other.

## Appendix B: Sample Data Structures

Below are example data structures to give you ideas. You are NOT expected to implement these exactly - adapt them to your chosen technology stack and requirements.

### Transaction Object (JSON)

```json
{
  "transactionId": "TXN-20260122-001234",
  "accountId": "ACC-123456",
  "payeeId": "PAYEE-789012",
  "payeeName": "ACME Corp",
  "type": "DEBIT",
  "amount": 15000.00,
  "currency": "USD",
  "timestamp": "2026-01-22T14:30:45Z",
  "description": "Wire transfer to supplier",
  "status": "COMPLETED"
}
```

### Alert Object (JSON)

```json
{
  "alertId": "ALERT-20260122-5678",
  "ruleId": "RULE-001",
  "ruleName": "High Value Transaction",
  "severity": "HIGH",
  "status": "OPEN",
  "message": "Transaction amount exceeds threshold of $10,000",
  "createdAt": "2026-01-22T14:30:46Z",
  "acknowledgedAt": null,
  "acknowledgedBy": null,
  "closedAt": null,
  "closedBy": null,
  "resolutionNotes": null,
  "transactionIds": ["TXN-20260122-001234"]
}
```

### Rule Definition Object (JSON)

```json
{
  "ruleId": "RULE-001",
  "ruleName": "High Value Transaction",
  "ruleType": "AMOUNT_THRESHOLD",
  "active": true,
  "severity": "HIGH",
  "parameters": {
    "threshold": 10000.00,
    "currency": "USD",
    "transactionType": "DEBIT"
  },
  "createdAt": "2026-01-15T09:00:00Z",
  "lastModified": "2026-01-15T09:00:00Z"
}
```

### Velocity Rule Example (JSON)

```json
{
  "ruleId": "RULE-002",
  "ruleName": "Rapid Transaction Velocity",
  "ruleType": "VELOCITY",
  "active": true,
  "severity": "MEDIUM",
  "parameters": {
    "maxTransactions": 5,
    "timeWindowMinutes": 10,
    "scope": "PER_ACCOUNT"
  },
  "createdAt": "2026-01-15T09:00:00Z",
  "lastModified": "2026-01-15T09:00:00Z"
}
```

### New Payee Rule Example (JSON)

```json
{
  "ruleId": "RULE-003",
  "ruleName": "New Payee Detection",
  "ruleType": "NEW_PAYEE",
  "active": true,
  "severity": "LOW",
  "parameters": {
    "scope": "PER_ACCOUNT",
    "lookbackDays": 90
  },
  "createdAt": "2026-01-15T09:00:00Z",
  "lastModified": "2026-01-15T09:00:00Z"
}
```

## Appendix C: SQL Query Examples

These queries demonstrate the types of database operations useful for rule evaluation:

### Query: Transactions Exceeding Threshold

```sql
SELECT transaction_id, account_id, amount, timestamp
FROM transactions
WHERE amount > 10000
  AND timestamp > NOW() - INTERVAL '1 hour'
  AND status = 'COMPLETED';
```

### Query: Count Recent Transactions (Velocity Check)

```sql
SELECT account_id, COUNT(*) as transaction_count
FROM transactions
WHERE timestamp > NOW() - INTERVAL '10 minutes'
GROUP BY account_id
HAVING COUNT(*) > 5;
```

### Query: Detect New Payee

```sql
-- Check if payee has been used before by this account
SELECT COUNT(*) as previous_transactions
FROM transactions
WHERE account_id = 'ACC-123456'
  AND payee_id = 'PAYEE-789012'
  AND timestamp < '2026-01-22T14:30:45Z';
-- If count = 0, this is a new payee
```

### Query: Daily Transaction Total

```sql
SELECT account_id, 
       DATE(timestamp) as transaction_date,
       SUM(amount) as daily_total
FROM transactions
WHERE DATE(timestamp) = CURRENT_DATE
  AND type = 'DEBIT'
GROUP BY account_id, DATE(timestamp)
HAVING SUM(amount) > 50000;
```

### Query: Alert Statistics

```sql
-- Count of alerts by status
SELECT status, COUNT(*) as alert_count
FROM alerts
GROUP BY status;

-- Average time to acknowledge alerts
SELECT AVG(EXTRACT(EPOCH FROM (acknowledged_at - created_at))/60) as avg_minutes
FROM alerts
WHERE acknowledged_at IS NOT NULL;
```

## Appendix D: UI Ideas

Below are some UI concepts that might give you ideas. You are DEFINITELY NOT expected to implement these exactly as shown. This is JUST FOR DEMONSTRATION of the type of thing that COULD be shown.

### Alerts Dashboard Screen
* Summary cards at top:
  - Open alerts count (red badge)
  - Acknowledged alerts count (yellow badge)
  - Alerts today
  - Average resolution time
* Alert list/table:
  - Columns: Alert ID, Severity, Rule Name, Status, Created Time
  - Color coding by severity (red=HIGH, yellow=MEDIUM, blue=LOW)
  - Filter by severity, status, date range
  - Click row to view details

### Alert Details Screen
* Alert information:
  - Alert ID and status with color badge
  - Rule that triggered it
  - Severity level
  - Created timestamp
  - Status history timeline
* Related transactions section:
  - Table of all transactions that triggered this alert
  - Transaction details (ID, amount, payee, timestamp)
* Actions section:
  - "Acknowledge" button (if status = OPEN)
  - "Mark as Investigating" button
  - "Close Alert" button with resolution notes text area
  - "Dismiss as False Positive" button

### Transactions List Screen
* Table of recent transactions:
  - Columns: Transaction ID, Account, Payee, Amount, Timestamp, Status
  - Search by transaction ID or description
  - Filter by date range, account, amount range
  - Sort by any column
  - Visual indicator if transaction triggered an alert
* Summary statistics:
  - Total transaction count
  - Total transaction volume
  - Alerts triggered

### Rules Management Screen
* List of all rules:
  - Table showing: Rule Name, Type, Status (Active/Inactive), Severity
  - Toggle to activate/deactivate rules
  - Edit and Delete buttons
* "Add New Rule" button opens a form:
  - Rule name
  - Rule type (dropdown)
  - Severity level
  - Parameters based on rule type (dynamic form)
  - Active checkbox

### Dashboard Charts (Advanced)
* Line chart: Transactions over time
* Bar chart: Alerts by severity
* Pie chart: Alert status distribution
* Line chart: Alert response times trend

## Appendix E: Advanced Features (If You Have Time)

Once you have the core system working, consider these enhancements:

1. **Alert Grouping/Deduplication**
   - Group similar alerts together
   - "5 high-value transactions detected in the last hour"
   - Reduce alert fatigue

2. **Machine Learning Integration**
   - Use historical data to identify anomalies
   - Learn "normal" patterns per account
   - Flag unusual behavior

3. **Real-time Dashboard Updates**
   - WebSocket or Server-Sent Events
   - Dashboard updates automatically when new alerts arrive
   - No need to refresh page

4. **Alert Routing/Assignment**
   - Assign alerts to specific operators
   - Queue management
   - SLA tracking (time to acknowledge, time to resolve)

5. **Rule Templates**
   - Pre-built rule templates for common scenarios
   - Clone existing rules
   - Import/export rule definitions

6. **Reporting**
   - Daily/weekly/monthly alert summaries
   - False positive rate tracking
   - Operator performance metrics
   - Trend analysis

7. **Notification System**
   - Email alerts for HIGH severity
   - SMS for critical alerts
   - Webhook integration for external systems

8. **Audit Trail**
   - Track all changes to rules
   - Track who acknowledged/closed each alert
   - Compliance reporting

9. **Multi-Currency Support**
   - Handle different currencies
   - Convert to base currency for thresholds
   - Currency-specific rules

10. **Complex Rule Logic**
    - Combine multiple conditions with AND/OR logic
    - "Amount > 10000 AND new payee"
    - Time-of-day rules (alerts for transactions outside business hours)

## Appendix F: Testing Considerations

Consider these testing scenarios:

1. **Rule Evaluation**
   - Submit transaction that exceeds amount threshold
   - Verify alert is generated with correct severity
   - Submit transaction below threshold
   - Verify no alert is generated

2. **Velocity Rules**
   - Submit 6 transactions in 5 minutes from same account
   - Verify velocity alert is triggered
   - Wait 15 minutes and submit another transaction
   - Verify no alert (outside time window)

3. **New Payee Detection**
   - Submit transaction to a payee never seen before
   - Verify new payee alert is generated
   - Submit second transaction to same payee
   - Verify no alert (payee already known)

4. **Alert Lifecycle**
   - Create an alert
   - Acknowledge it and verify status change
   - Close it with notes and verify status change
   - Verify all timestamps are recorded correctly

5. **Alert Status Validation**
   - Try to close an alert without acknowledging first
   - Decide if this should be allowed or not
   - Try to reopen a closed alert
   - Verify appropriate behavior

6. **Performance Testing**
   - Insert 1000 transactions rapidly
   - Measure rule evaluation time
   - Ensure system remains responsive
   - Check database query performance

7. **Concurrent Operations**
   - Two operators trying to acknowledge same alert simultaneously
   - Verify data consistency

## Appendix G: Architecture Suggestions

Consider a layered architecture with asynchronous processing:

```
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚         Web UI (Frontend)            â”‚
â”‚   (React, Vue, Angular, or similar)  â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚ HTTP/REST
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â–¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚         REST API Layer               â”‚
â”‚    (Spring Boot / Flask / Express)   â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚      Transaction Service             â”‚
â”‚   - Record transactions              â”‚
â”‚   - Query transactions               â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚              â”‚ Events/Messages       â”‚
â”‚              â–¼                        â”‚
â”‚      Rule Engine Service             â”‚
â”‚   - Evaluate rules                   â”‚
â”‚   - Generate alerts                  â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚         Alert Service                â”‚
â”‚   - Manage alert lifecycle           â”‚
â”‚   - Alert queries and updates        â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚         Data Access Layer            â”‚
â”‚   - Repository pattern               â”‚
â”‚   - Transaction management           â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â–¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚         Database                     â”‚
â”‚   (PostgreSQL / MySQL / MongoDB)     â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜

Optional: Message Queue (RabbitMQ, Kafka)
for async transaction processing
```

**Key Considerations:**
* Separate transaction recording from rule evaluation for performance
* Consider asynchronous processing using message queues
* Make rule engine pluggable to easily add new rule types
* Use caching for frequently accessed data (e.g., historical transaction counts)
* Implement proper indexing on timestamp and account fields
* Consider read replicas for reporting queries
* Use transaction isolation appropriately

## Appendix H: Rule Engine Implementation Patterns

### Strategy Pattern Approach

Each rule type is a separate class implementing a common interface:

```
interface Rule {
  evaluate(transaction, context): Alert[]
}

class AmountThresholdRule implements Rule {
  evaluate(transaction, context) {
    if (transaction.amount > this.threshold) {
      return createAlert(transaction, this);
    }
    return [];
  }
}

class VelocityRule implements Rule {
  evaluate(transaction, context) {
    recentCount = context.countRecentTransactions(
      transaction.accountId, 
      this.timeWindow
    );
    if (recentCount > this.maxTransactions) {
      return createAlert(transaction, this);
    }
    return [];
  }
}

// Rule engine iterates through all active rules
for each rule in activeRules:
  alerts = rule.evaluate(transaction, context)
  if alerts:
    save(alerts)
```

### Configuration-Driven Approach

Rules are data, not code:

```
rules = loadFromDatabase()
for each rule in rules:
  result = evaluateRule(rule, transaction)
  if result.triggered:
    createAlert(rule, transaction, result)
```

This approach makes rules configurable without code changes but may be less flexible for complex logic.

## Appendix I: Sample Test Data Generator

To test your system, you may want to create a test data generator:

### Random Transaction Generator (Pseudocode)

```
function generateTestTransactions(count):
  accounts = ["ACC-001", "ACC-002", "ACC-003"]
  payees = ["PAYEE-A", "PAYEE-B", "PAYEE-C", "PAYEE-NEW"]
  
  for i in 1 to count:
    transaction = {
      accountId: random(accounts),
      payeeId: random(payees),
      amount: randomAmount(10, 20000),
      currency: "USD",
      type: "DEBIT",
      timestamp: now(),
      description: "Test transaction " + i
    }
    postTransaction(transaction)
    sleep(randomMillis(100, 1000))
```

### Scenario-Based Test Generator

Create specific scenarios to trigger alerts:

```
// Scenario 1: High-value transaction
postTransaction(account="ACC-001", amount=15000)

// Scenario 2: Velocity - rapid transactions
for i in 1 to 10:
  postTransaction(account="ACC-002", amount=100)
  sleep(30 seconds)

// Scenario 3: New payee
postTransaction(account="ACC-003", payee="PAYEE-BRAND-NEW", amount=5000)
```