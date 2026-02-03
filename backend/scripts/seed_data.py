"""
MoneyMap Database Seeder
Populates the database with comprehensive dummy data for demo purposes.
"""

import requests
import json
from datetime import datetime, timedelta
import random
from typing import Dict, List

# API Base URL
BASE_URL = "http://localhost:8181/api/v1"

# Sample data
FIRST_NAMES_US = ["John", "Sarah", "Michael", "Emily", "David", "Jessica", "James", "Jennifer"]
LAST_NAMES_US = ["Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"]

FIRST_NAMES_UK = ["Oliver", "Emma", "George", "Amelia", "Harry", "Olivia", "Jack", "Isla"]
LAST_NAMES_UK = ["Smith", "Jones", "Taylor", "Brown", "Wilson", "Davies", "Evans", "Thomas"]

FIRST_NAMES_IN = ["Raj", "Priya", "Amit", "Anjali", "Vikram", "Deepika", "Arjun", "Kavya"]
LAST_NAMES_IN = ["Patel", "Kumar", "Singh", "Sharma", "Gupta", "Reddy", "Kapoor", "Mehta"]

FIRST_NAMES_DE = ["Hans", "Anna", "Klaus", "Maria", "Peter", "Julia", "Michael", "Sophie"]
LAST_NAMES_DE = ["Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker"]

COUNTRIES = [
    {"code": "US", "name": "United States", "currency": "USD", "timezone": "America/New_York", "locale": "en_US",
     "firstNames": FIRST_NAMES_US, "lastNames": LAST_NAMES_US, "cities": ["New York", "Los Angeles", "Chicago", "Houston"]},
    {"code": "GB", "name": "United Kingdom", "currency": "GBP", "timezone": "Europe/London", "locale": "en_GB",
     "firstNames": FIRST_NAMES_UK, "lastNames": LAST_NAMES_UK, "cities": ["London", "Manchester", "Birmingham", "Edinburgh"]},
    {"code": "IN", "name": "India", "currency": "INR", "timezone": "Asia/Kolkata", "locale": "en_IN",
     "firstNames": FIRST_NAMES_IN, "lastNames": LAST_NAMES_IN, "cities": ["Mumbai", "Delhi", "Bangalore", "Chennai"]},
    {"code": "DE", "name": "Germany", "currency": "EUR", "timezone": "Europe/Berlin", "locale": "de_DE",
     "firstNames": FIRST_NAMES_DE, "lastNames": LAST_NAMES_DE, "cities": ["Berlin", "Munich", "Frankfurt", "Hamburg"]},
]

STOCK_SYMBOLS = [
    {"symbol": "AAPL", "name": "Apple Inc.", "exchange": "NASDAQ", "sector": "Technology", "price": 175.50, "dividend": 0.55},
    {"symbol": "MSFT", "name": "Microsoft Corp", "exchange": "NASDAQ", "sector": "Technology", "price": 380.00, "dividend": 0.82},
    {"symbol": "GOOGL", "name": "Alphabet Inc.", "exchange": "NASDAQ", "sector": "Technology", "price": 140.00, "dividend": 0.00},
    {"symbol": "AMZN", "name": "Amazon.com Inc.", "exchange": "NASDAQ", "sector": "E-Commerce", "price": 175.00, "dividend": 0.00},
    {"symbol": "TSLA", "name": "Tesla Inc.", "exchange": "NASDAQ", "sector": "Automotive", "price": 250.00, "dividend": 0.00},
    {"symbol": "JPM", "name": "JPMorgan Chase", "exchange": "NYSE", "sector": "Finance", "price": 150.00, "dividend": 3.50},
    {"symbol": "V", "name": "Visa Inc.", "exchange": "NYSE", "sector": "Finance", "price": 240.00, "dividend": 1.60},
    {"symbol": "JNJ", "name": "Johnson & Johnson", "exchange": "NYSE", "sector": "Healthcare", "price": 160.00, "dividend": 4.20},
]

CRYPTO_SYMBOLS = [
    {"symbol": "BTC", "name": "Bitcoin", "network": "Bitcoin", "price": 45000.00, "staking": False},
    {"symbol": "ETH", "name": "Ethereum", "network": "Ethereum", "price": 3000.00, "staking": True, "apy": 4.5},
    {"symbol": "BNB", "name": "Binance Coin", "network": "Binance Smart Chain", "price": 320.00, "staking": True, "apy": 5.2},
    {"symbol": "ADA", "name": "Cardano", "network": "Cardano", "price": 0.50, "staking": True, "apy": 4.0},
    {"symbol": "SOL", "name": "Solana", "network": "Solana", "price": 110.00, "staking": True, "apy": 6.5},
]

MUTUAL_FUNDS = [
    {"symbol": "VFIAX", "name": "Vanguard 500 Index Fund", "amc": "Vanguard", "category": "Index Fund", "price": 425.00, "expense": 0.04, "risk": "Moderate"},
    {"symbol": "FXAIX", "name": "Fidelity 500 Index Fund", "amc": "Fidelity", "category": "Index Fund", "price": 165.00, "expense": 0.015, "risk": "Moderate"},
    {"symbol": "VTSAX", "name": "Vanguard Total Stock Market", "amc": "Vanguard", "category": "Large Cap", "price": 115.00, "expense": 0.04, "risk": "Moderate"},
    {"symbol": "VIGAX", "name": "Vanguard Growth Index", "amc": "Vanguard", "category": "Growth", "price": 130.00, "expense": 0.05, "risk": "High"},
]

def create_client(country: Dict) -> Dict:
    """Create a client from a specific country"""
    first_name = random.choice(country["firstNames"])
    last_name = random.choice(country["lastNames"])
    email = f"{first_name.lower()}.{last_name.lower()}@example.com"
    city = random.choice(country["cities"])
    
    client_data = {
        "firstName": first_name,
        "lastName": last_name,
        "email": email,
        "phone": f"+{random.randint(1, 99)}-{random.randint(100, 999)}-{random.randint(1000, 9999)}",
        "address": f"{random.randint(1, 999)} Main Street",
        "city": city,
        "stateOrProvince": city,
        "postalCode": f"{random.randint(10000, 99999)}",
        "countryCode": country["code"],
        "country": country["name"],
        "preferredCurrency": country["currency"],
        "timezone": country["timezone"],
        "locale": country["locale"]
    }
    
    response = requests.post(f"{BASE_URL}/clients", json=client_data)
    if response.status_code == 201:
        print(f"✓ Created client: {first_name} {last_name} ({country['code']})")
        return response.json()["data"]
    else:
        print(f"✗ Failed to create client: {response.status_code}")
        return None

def create_portfolio(client_id: int, client_name: str) -> Dict:
    """Create a portfolio for a client"""
    portfolio_data = {
        "name": f"{client_name}'s Investment Portfolio",
        "description": f"Diversified portfolio for {client_name}",
        "clientId": client_id
    }
    
    response = requests.post(f"{BASE_URL}/portfolios", json=portfolio_data)
    if response.status_code == 201:
        print(f"  ✓ Created portfolio for client {client_id}")
        return response.json()["data"]
    else:
        print(f"  ✗ Failed to create portfolio: {response.status_code}")
        return None

def create_stock_asset(portfolio_id: int, stock: Dict) -> Dict:
    """Create a stock asset"""
    quantity = random.randint(10, 500)
    purchase_price = stock["price"] * random.uniform(0.7, 1.3)
    days_ago = random.randint(30, 365)
    purchase_date = (datetime.now() - timedelta(days=days_ago)).strftime("%Y-%m-%d")
    
    asset_data = {
        "name": stock["name"],
        "symbol": stock["symbol"],
        "assetType": "STOCK",
        "quantity": quantity,
        "purchasePrice": round(purchase_price, 2),
        "currentPrice": stock["price"],
        "purchaseDate": purchase_date,
        "portfolioId": portfolio_id,
        "notes": f"Purchased {quantity} shares",
        "exchange": stock["exchange"],
        "sector": stock["sector"],
        "dividendYield": stock["dividend"],
        "fractionalAllowed": True
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Added stock: {stock['symbol']}")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to add stock: {response.status_code} - {response.text}")
        return None

def create_crypto_asset(portfolio_id: int, crypto: Dict) -> Dict:
    """Create a crypto asset"""
    quantity = round(random.uniform(0.1, 10), 8)
    purchase_price = crypto["price"] * random.uniform(0.5, 1.5)
    days_ago = random.randint(30, 365)
    purchase_date = (datetime.now() - timedelta(days=days_ago)).strftime("%Y-%m-%d")
    
    asset_data = {
        "name": crypto["name"],
        "symbol": crypto["symbol"],
        "assetType": "CRYPTO",
        "quantity": quantity,
        "purchasePrice": round(purchase_price, 2),
        "currentPrice": crypto["price"],
        "purchaseDate": purchase_date,
        "portfolioId": portfolio_id,
        "notes": f"Digital asset investment",
        "blockchainNetwork": crypto["network"],
        "stakingEnabled": crypto["staking"],
        "stakingApy": crypto.get("apy", 0) if crypto["staking"] else None
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Added crypto: {crypto['symbol']}")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to add crypto: {response.status_code}")
        return None

def create_gold_asset(portfolio_id: int) -> Dict:
    """Create a gold asset"""
    quantity = random.randint(5, 100)
    price_per_gram = random.uniform(60, 70)
    purchase_price = price_per_gram * random.uniform(0.9, 1.1)
    days_ago = random.randint(30, 365)
    purchase_date = (datetime.now() - timedelta(days=days_ago)).strftime("%Y-%m-%d")
    
    forms = ["Physical", "ETF", "Digital Gold"]
    form = random.choice(forms)
    
    asset_data = {
        "name": "Gold Investment",
        "symbol": "GOLD",
        "assetType": "GOLD",
        "quantity": quantity,
        "purchasePrice": round(purchase_price, 2),
        "currentPrice": round(price_per_gram, 2),
        "purchaseDate": purchase_date,
        "portfolioId": portfolio_id,
        "notes": f"{form} gold investment",
        "goldForm": form,
        "purity": "24K",
        "weightUnit": "grams",
        "storageLocation": "Bank Locker" if form == "Physical" else "Digital",
        "isPhysical": form == "Physical"
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Added gold asset")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to add gold: {response.status_code}")
        return None

def create_mutual_fund_asset(portfolio_id: int, fund: Dict) -> Dict:
    """Create a mutual fund asset"""
    quantity = random.randint(10, 500)
    purchase_price = fund["price"] * random.uniform(0.8, 1.2)
    days_ago = random.randint(30, 365)
    purchase_date = (datetime.now() - timedelta(days=days_ago)).strftime("%Y-%m-%d")
    
    asset_data = {
        "name": fund["name"],
        "symbol": fund["symbol"],
        "assetType": "MUTUAL_FUND",
        "quantity": quantity,
        "purchasePrice": round(purchase_price, 2),
        "currentPrice": fund["price"],
        "purchaseDate": purchase_date,
        "portfolioId": portfolio_id,
        "notes": f"Long-term investment",
        "fundCategory": fund["category"],
        "amcName": fund["amc"],
        "planType": "GROWTH",
        "expenseRatio": fund["expense"],
        "navDate": purchase_date,
        "riskLevel": fund["risk"],
        "minInvestment": 1000.00
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Added mutual fund: {fund['symbol']}")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to add fund: {response.status_code}")
        return None

def create_transaction(asset_id: int, transaction_type: str, asset_details: Dict):
    """Create a transaction for an asset"""
    quantity = round(asset_details["quantity"] * random.uniform(0.1, 0.3), 2)
    price = asset_details["currentPrice"] * random.uniform(0.9, 1.1)
    days_ago = random.randint(1, 30)
    
    transaction_data = {
        "assetId": asset_id,
        "transactionType": transaction_type,
        "quantity": quantity,
        "pricePerUnit": round(price, 2),
        "totalAmount": round(quantity * price, 2),
        "fees": round(quantity * price * 0.01, 2),
        "transactionDate": (datetime.now() - timedelta(days=days_ago)).isoformat(),
        "notes": f"{transaction_type} transaction"
    }
    
    response = requests.post(f"{BASE_URL}/transactions", json=transaction_data)
    if response.status_code == 201:
        print(f"      ✓ Created {transaction_type} transaction")
    else:
        print(f"      ✗ Failed transaction: {response.status_code}")

def create_payment(amount: float, currency: str) -> Dict:
    """Create a payment"""
    payment_data = {
        "sourceAccount": f"ACC{random.randint(1000, 9999)}",
        "destinationAccount": f"ACC{random.randint(1000, 9999)}",
        "amount": amount,
        "currency": currency,
        "reference": f"REF{random.randint(10000, 99999)}",
        "description": "Portfolio investment transfer"
    }
    
    response = requests.post(f"{BASE_URL}/payments", json=payment_data)
    if response.status_code == 201:
        print(f"  ✓ Created payment: {amount} {currency}")
        return response.json()["data"]
    else:
        print(f"  ✗ Failed to create payment: {response.status_code}")
        return None

def create_monitoring_rule(rule_type: str, severity: str):
    """Create a monitoring rule"""
    rules = {
        "AMOUNT_THRESHOLD": {
            "ruleName": "High Value Transaction Alert",
            "description": "Alert on transactions over 10,000",
            "thresholdAmount": 10000.00,
            "thresholdCurrency": "USD"
        },
        "VELOCITY": {
            "ruleName": "Rapid Transaction Detection",
            "description": "Alert on 5+ transactions in 10 minutes",
            "maxTransactions": 5,
            "timeWindowMinutes": 10
        },
        "NEW_PAYEE": {
            "ruleName": "New Recipient Alert",
            "description": "Alert on first-time payees",
            "lookbackDays": 90
        },
        "DAILY_LIMIT": {
            "ruleName": "Daily Spending Limit",
            "description": "Alert when daily total exceeds limit",
            "dailyLimitAmount": 50000.00,
            "thresholdCurrency": "USD"
        }
    }
    
    rule_data = {
        "ruleType": rule_type,
        "severity": severity,
        "active": True,
        **rules[rule_type]
    }
    
    response = requests.post(f"{BASE_URL}/monitoring-rules", json=rule_data)
    if response.status_code == 201:
        print(f"  ✓ Created {rule_type} rule")
        return response.json()["data"]
    else:
        print(f"  ✗ Failed to create rule: {response.status_code}")
        return None

def main():
    print("\n" + "="*60)
    print("MoneyMap Database Seeder - Starting")
    print("="*60 + "\n")
    
    clients_created = []
    portfolios_created = []
    assets_created = []
    
    # Create clients (30-35 clients)
    print("\n📊 Creating Clients...")
    num_clients = random.randint(30, 35)
    for i in range(num_clients):
        country = random.choice(COUNTRIES)
        client = create_client(country)
        if client:
            clients_created.append(client)
    
    print(f"\n✅ Created {len(clients_created)} clients\n")
    
    # Create portfolios and assets for each client
    print("\n💼 Creating Portfolios and Assets...")
    for client in clients_created:
        portfolio = create_portfolio(client["id"], client["fullName"])
        if portfolio:
            portfolios_created.append(portfolio)
            
            # Add 3-6 assets per portfolio
            num_assets = random.randint(3, 6)
            asset_types = ["STOCK", "CRYPTO", "GOLD", "MUTUAL_FUND"]
            random.shuffle(asset_types)
            
            for j in range(num_assets):
                asset_type = asset_types[j % len(asset_types)]
                
                if asset_type == "STOCK":
                    stock = random.choice(STOCK_SYMBOLS)
                    asset = create_stock_asset(portfolio["id"], stock)
                elif asset_type == "CRYPTO":
                    crypto = random.choice(CRYPTO_SYMBOLS)
                    asset = create_crypto_asset(portfolio["id"], crypto)
                elif asset_type == "GOLD":
                    asset = create_gold_asset(portfolio["id"])
                elif asset_type == "MUTUAL_FUND":
                    fund = random.choice(MUTUAL_FUNDS)
                    asset = create_mutual_fund_asset(portfolio["id"], fund)
                
                if asset:
                    assets_created.append(asset)
                    
                    # Create 1-3 transactions for this asset
                    num_transactions = random.randint(1, 3)
                    for _ in range(num_transactions):
                        txn_type = random.choice(["BUY", "SELL"])
                        create_transaction(asset["id"], txn_type, asset)
    
    print(f"\n✅ Created {len(portfolios_created)} portfolios")
    print(f"✅ Created {len(assets_created)} assets\n")
    
    # Create payments
    print("\n💳 Creating Payments...")
    num_payments = random.randint(15, 25)
    for i in range(num_payments):
        amount = round(random.uniform(500, 25000), 2)
        currency = random.choice(["USD", "GBP", "EUR", "INR"])
        create_payment(amount, currency)
    
    print(f"\n✅ Created payments\n")
    
    # Create monitoring rules
    print("\n🔍 Creating Monitoring Rules...")
    rule_types = ["AMOUNT_THRESHOLD", "VELOCITY", "NEW_PAYEE", "DAILY_LIMIT"]
    severities = ["HIGH", "MEDIUM", "LOW"]
    
    for rule_type in rule_types:
        severity = random.choice(severities)
        create_monitoring_rule(rule_type, severity)
    
    print(f"\n✅ Created monitoring rules\n")
    
    # Summary
    print("\n" + "="*60)
    print("Database Seeding Complete!")
    print("="*60)
    print(f"\n📊 Summary:")
    print(f"  • Clients: {len(clients_created)}")
    print(f"  • Portfolios: {len(portfolios_created)}")
    print(f"  • Assets: {len(assets_created)}")
    print(f"  • Transactions: Created for each asset")
    print(f"  • Payments: {num_payments}")
    print(f"  • Monitoring Rules: 4")
    print(f"\n🌐 Multi-country clients from: US, UK, India, Germany")
    print(f"📈 Asset types: Stocks, Crypto, Gold, Mutual Funds")
    print(f"\n✅ Your database is now populated with realistic demo data!")
    print("\n" + "="*60 + "\n")

if __name__ == "__main__":
    try:
        main()
    except requests.exceptions.ConnectionError:
        print("\n❌ Error: Could not connect to the API at", BASE_URL)
        print("Make sure your Spring Boot application is running on port 8181")
    except Exception as e:
        print(f"\n❌ Error: {str(e)}")
