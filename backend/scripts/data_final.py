"""
MoneyMap Database Final Seeder
Populates database with comprehensive dummy data matching exact schema.
Includes 9+ clients from different regions with proper nuances.
"""

import requests
import json
from datetime import datetime, timedelta
import random
from typing import Dict, List

# API Base URL
BASE_URL = "http://localhost:8181/api/v1"

# Enhanced regional data with proper nuances
REGIONAL_DATA = [
    {
        "code": "US",
        "name": "United States",
        "currency": "USD",
        "timezone": "America/New_York",
        "locale": "en_US",
        "cities": ["New York", "Los Angeles", "Chicago", "Houston", "San Francisco"],
        "first_names": ["Michael", "Sarah", "James", "Jennifer", "David", "Emily", "Robert", "Lisa"],
        "last_names": ["Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"],
        "phone_prefix": "+1",
        "postal_format": lambda: f"{random.randint(10000, 99999)}",
        "address_pattern": ["Main St", "Oak Ave", "Pine Rd", "Elm Dr", "Maple Ln"]
    },
    {
        "code": "GB", 
        "name": "United Kingdom",
        "currency": "GBP",
        "timezone": "Europe/London",
        "locale": "en_GB",
        "cities": ["London", "Manchester", "Birmingham", "Edinburgh", "Glasgow"],
        "first_names": ["Oliver", "Emma", "George", "Amelia", "Harry", "Olivia", "Jack", "Isla"],
        "last_names": ["Smith", "Jones", "Taylor", "Brown", "Wilson", "Davies", "Evans", "Thomas"],
        "phone_prefix": "+44",
        "postal_format": lambda: f"{random.choice(['SW1A', 'M1', 'B1', 'EH1', 'G1'])} {random.randint(1,9)}{random.choice(['A','B','C'])}",
        "address_pattern": ["High St", "Queen's Rd", "Church Ln", "Park Ave", "Victoria St"]
    },
    {
        "code": "IN",
        "name": "India", 
        "currency": "INR",
        "timezone": "Asia/Kolkata",
        "locale": "en_IN",
        "cities": ["Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata", "Hyderabad"],
        "first_names": ["Rajesh", "Priya", "Amit", "Anjali", "Vikram", "Deepika", "Arjun", "Kavya", "Rahul", "Pooja"],
        "last_names": ["Patel", "Kumar", "Singh", "Sharma", "Gupta", "Reddy", "Kapoor", "Mehta", "Joshi", "Nair"],
        "phone_prefix": "+91",
        "postal_format": lambda: f"{random.randint(100000, 999999)}",
        "address_pattern": ["MG Road", "Park Street", " Nehru Place", " Brigade Rd", " Connaught Place"]
    },
    {
        "code": "SG",
        "name": "Singapore",
        "currency": "SGD", 
        "timezone": "Asia/Singapore",
        "locale": "en_SG",
        "cities": ["Singapore"],
        "first_names": ["Wei", "Mei", "Jun", "Ling", "Kai", "Hui", "Zhen", "Ying"],
        "last_names": ["Tan", "Lim", "Lee", "Ng", "Ong", "Wong", "Goh", "Chua"],
        "phone_prefix": "+65",
        "postal_format": lambda: f"{random.randint(100000, 999999)}",
        "address_pattern": ["Orchard Rd", "Raffles Place", "Marina Bay", "Shenton Way", "Tanjong Pagar"]
    },
    {
        "code": "AE",
        "name": "United Arab Emirates",
        "currency": "AED",
        "timezone": "Asia/Dubai", 
        "locale": "en_AE",
        "cities": ["Dubai", "Abu Dhabi", "Sharjah", "Ajman"],
        "first_names": ["Mohammed", "Fatima", "Ahmed", "Aisha", "Omar", "Mariam", "Youssef", "Noora"],
        "last_names": ["Al-Rashid", "Al-Mansouri", "Al-Falasi", "Al-Mazrouei", "Al-Qassimi", "Al-Nuaimi", "Al-Shamsi", "Al-Muhairi"],
        "phone_prefix": "+971",
        "postal_format": lambda: f"{random.randint(1000, 9999)}",
        "address_pattern": ["Sheikh Zayed Rd", "Al Wasl Rd", "Jumeirah Rd", "Business Bay", "Dubai Marina"]
    },
    {
        "code": "AU",
        "name": "Australia",
        "currency": "AUD",
        "timezone": "Australia/Sydney",
        "locale": "en_AU", 
        "cities": ["Sydney", "Melbourne", "Brisbane", "Perth", "Adelaide"],
        "first_names": ["Jack", "Olivia", "William", "Charlotte", "Thomas", "Sophia", "James", "Amelia"],
        "last_names": ["Smith", "Jones", "Williams", "Brown", "Wilson", "Taylor", "Anderson", "Johnson"],
        "phone_prefix": "+61",
        "postal_format": lambda: f"{random.randint(2000, 2999)}",
        "address_pattern": ["George St", "Collins St", "Queen St", "Adelaide St", "Bourke St"]
    },
    {
        "code": "CA",
        "name": "Canada",
        "currency": "CAD",
        "timezone": "America/Toronto",
        "locale": "en_CA",
        "cities": ["Toronto", "Vancouver", "Montreal", "Calgary", "Ottawa"],
        "first_names": ["Liam", "Olivia", "Noah", "Emma", "Lucas", "Ava", "Ethan", "Sophia"],
        "last_names": ["Smith", "Brown", "Tremblay", "Martin", "Roy", "Gagnon", "Wilson", "MacDonald"],
        "phone_prefix": "+1",
        "postal_format": lambda: f"{random.choice(['K1A', 'M5V', 'V6B', 'T2P', 'H2X'])} {random.randint(1,9)}{random.choice(['A','B','C'])}",
        "address_pattern": ["Yonge St", "Robson St", "Ste-Catherine", "Stephen Ave", "Spadina Ave"]
    },
    {
        "code": "DE",
        "name": "Germany",
        "currency": "EUR",
        "timezone": "Europe/Berlin",
        "locale": "de_DE",
        "cities": ["Berlin", "Munich", "Frankfurt", "Hamburg", "Cologne"],
        "first_names": ["Hans", "Anna", "Klaus", "Maria", "Peter", "Julia", "Michael", "Sophie"],
        "last_names": ["Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker"],
        "phone_prefix": "+49",
        "postal_format": lambda: f"{random.randint(10000, 99999)}",
        "address_pattern": ["Unter den Linden", "Königstrasse", "Hauptstrasse", "Schillerstrasse", "Goethestrasse"]
    },
    {
        "code": "JP",
        "name": "Japan",
        "currency": "JPY",
        "timezone": "Asia/Tokyo",
        "locale": "ja_JP",
        "cities": ["Tokyo", "Osaka", "Kyoto", "Yokohama", "Nagoya"],
        "first_names": ["Takeshi", "Yuki", "Hiroshi", "Sakura", "Kenji", "Emi", "Ryo", "Mika"],
        "last_names": ["Sato", "Suzuki", "Takahashi", "Tanaka", "Watanabe", "Ito", "Yamamoto", "Nakamura"],
        "phone_prefix": "+81",
        "postal_format": lambda: f"{random.randint(100, 999)}-{random.randint(1000, 9999)}",
        "address_pattern": ["Shibuya", "Ginza", "Shinjuku", "Marunouchi", "Roppongi"]
    }
]

# Asset data matching schema exactly
STOCK_DATA = [
    {"symbol": "AAPL", "name": "Apple Inc.", "exchange": "NASDAQ", "sector": "Technology", "dividend_yield": 0.55, "fractional_allowed": True, "price": 175.50},
    {"symbol": "MSFT", "name": "Microsoft Corp", "exchange": "NASDAQ", "sector": "Technology", "dividend_yield": 0.82, "fractional_allowed": True, "price": 380.00},
    {"symbol": "GOOGL", "name": "Alphabet Inc.", "exchange": "NASDAQ", "sector": "Technology", "dividend_yield": 0.00, "fractional_allowed": True, "price": 140.00},
    {"symbol": "AMZN", "name": "Amazon.com Inc.", "exchange": "NASDAQ", "sector": "E-Commerce", "dividend_yield": 0.00, "fractional_allowed": True, "price": 175.00},
    {"symbol": "TSLA", "name": "Tesla Inc.", "exchange": "NASDAQ", "sector": "Automotive", "dividend_yield": 0.00, "fractional_allowed": True, "price": 250.00},
    {"symbol": "JPM", "name": "JPMorgan Chase", "exchange": "NYSE", "sector": "Finance", "dividend_yield": 3.50, "fractional_allowed": True, "price": 150.00},
    {"symbol": "V", "name": "Visa Inc.", "exchange": "NYSE", "sector": "Finance", "dividend_yield": 1.60, "fractional_allowed": True, "price": 240.00},
    {"symbol": "JNJ", "name": "Johnson & Johnson", "exchange": "NYSE", "sector": "Healthcare", "dividend_yield": 4.20, "fractional_allowed": True, "price": 160.00},
    {"symbol": "RELIANCE", "name": "Reliance Industries", "exchange": "NSE", "sector": "Conglomerate", "dividend_yield": 1.20, "fractional_allowed": False, "price": 2450.80},
    {"symbol": "TCS", "name": "Tata Consultancy Services", "exchange": "NSE", "sector": "Technology", "dividend_yield": 1.80, "fractional_allowed": False, "price": 3456.70},
    {"symbol": "HDFC", "name": "HDFC Bank", "exchange": "NSE", "sector": "Banking", "dividend_yield": 2.10, "fractional_allowed": False, "price": 1456.30},
    {"symbol": "DBS", "name": "DBS Bank Ltd.", "exchange": "SGX", "sector": "Banking", "dividend_yield": 3.20, "fractional_allowed": True, "price": 28.90},
    {"symbol": "ENBD", "name": "Emirates NBD", "exchange": "DFM", "sector": "Banking", "dividend_yield": 4.50, "fractional_allowed": False, "price": 8.45}
]

CRYPTO_DATA = [
    {"symbol": "BTC", "name": "Bitcoin", "blockchain": "Bitcoin", "staking_enabled": False, "staking_apy": None, "price": 45000.00},
    {"symbol": "ETH", "name": "Ethereum", "blockchain": "Ethereum", "staking_enabled": True, "staking_apy": 4.5, "price": 3000.00},
    {"symbol": "BNB", "name": "Binance Coin", "blockchain": "Binance Smart Chain", "staking_enabled": True, "staking_apy": 5.2, "price": 320.00},
    {"symbol": "ADA", "name": "Cardano", "blockchain": "Cardano", "staking_enabled": True, "staking_apy": 4.0, "price": 0.50},
    {"symbol": "SOL", "name": "Solana", "blockchain": "Solana", "staking_enabled": True, "staking_apy": 6.5, "price": 110.00}
]

MUTUAL_FUND_DATA = [
    {"symbol": "VFIAX", "name": "Vanguard 500 Index Fund", "fund_manager": "Vanguard", "expense_ratio": 0.04, "nav_price": 425.00, "minimum_investment": 1000.00, "risk_level": "Moderate", "price": 425.00},
    {"symbol": "FXAIX", "name": "Fidelity 500 Index Fund", "fund_manager": "Fidelity", "expense_ratio": 0.015, "nav_price": 165.00, "minimum_investment": 1000.00, "risk_level": "Moderate", "price": 165.00},
    {"symbol": "VTSAX", "name": "Vanguard Total Stock Market", "fund_manager": "Vanguard", "expense_ratio": 0.04, "nav_price": 115.00, "minimum_investment": 3000.00, "risk_level": "Moderate", "price": 115.00},
    {"symbol": "VIGAX", "name": "Vanguard Growth Index", "fund_manager": "Vanguard", "expense_ratio": 0.05, "nav_price": 130.00, "minimum_investment": 3000.00, "risk_level": "High", "price": 130.00}
]

def create_client(region: Dict) -> Dict:
    """Create a client from a specific region with proper nuances"""
    first_name = random.choice(region["first_names"])
    last_name = random.choice(region["last_names"])
    email = f"{first_name.lower()}.{last_name.lower()}@{random.choice(['gmail.com', 'yahoo.com', 'outlook.com', 'example.com'])}"
    city = random.choice(region["cities"])
    
    client_data = {
        "firstName": first_name,
        "lastName": last_name,
        "email": email,
        "phone": f"{region['phone_prefix']}-{random.randint(100, 999)}-{random.randint(1000, 9999)}" if region["code"] != "SG" else f"{region['phone_prefix']}-{random.randint(1000, 9999)}-{random.randint(1000, 9999)}",
        "address": f"{random.randint(1, 999)} {random.choice(region['address_pattern'])}",
        "city": city,
        "stateOrProvince": city if region["code"] in ["SG", "AE"] else random.choice(["California", "New York", "Texas", "Ontario", "Bavaria"]),
        "postalCode": region["postal_format"](),
        "countryCode": region["code"],
        "country": region["name"],
        "preferredCurrency": region["currency"],
        "timezone": region["timezone"],
        "locale": region["locale"],
        "active": True
    }
    
    response = requests.post(f"{BASE_URL}/clients", json=client_data)
    if response.status_code == 201:
        print(f"✓ Created client: {first_name} {last_name} ({region['code']}) - {city}")
        return response.json()["data"]
    else:
        print(f"✗ Failed to create client: {response.status_code} - {response.text}")
        return None

def create_portfolio(client_id: int, client_name: str, region: Dict) -> Dict:
    """Create a portfolio for a client with region-specific naming"""
    portfolio_names = {
        "US": ["Growth Portfolio", "Tech Innovation Fund", "Balanced Strategy"],
        "GB": ["Conservative Wealth Builder", "UK Growth Fund", "British Portfolio"],
        "IN": ["Indian Equity Focus", "Emerging Markets Fund", "Asian Growth Portfolio"],
        "SG": ["Asian Growth Fund", "Singapore Portfolio", "Regional Strategy"],
        "AE": ["Middle East Expansion", "Gulf Portfolio", "Regional Growth"],
        "AU": ["Australian Portfolio", "Pacific Growth Fund", "Down Under Strategy"],
        "CA": ["Canadian Portfolio", "North American Fund", "Maple Strategy"],
        "DE": ["European Portfolio", "German Growth Fund", "Continental Strategy"],
        "JP": ["Japanese Portfolio", "Asia Pacific Fund", "Nikkei Strategy"]
    }
    
    portfolio_name = random.choice(portfolio_names.get(region["code"], ["Investment Portfolio"]))
    
    portfolio_data = {
        "name": f"{portfolio_name} - {client_name}",
        "description": f"Diversified portfolio for {client_name} based in {region['name']}",
        "clientId": client_id,
        "active": True
    }
    
    response = requests.post(f"{BASE_URL}/portfolios", json=portfolio_data)
    if response.status_code == 201:
        print(f"  ✓ Created portfolio: {portfolio_name}")
        return response.json()["data"]
    else:
        print(f"  ✗ Failed to create portfolio: {response.status_code} - {response.text}")
        return None

def create_stock_asset(portfolio_id: int, stock: Dict, region: Dict) -> Dict:
    """Create a stock asset matching exact schema"""
    quantity = round(random.uniform(10, 500), 2)
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
        "notes": f"Purchased {quantity} shares of {stock['symbol']}",
        "exchange": stock["exchange"],
        "sector": stock["sector"],
        "dividendYield": stock["dividend_yield"],
        "fractionalAllowed": stock["fractional_allowed"]
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Added stock: {stock['symbol']} ({stock['exchange']})")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to add stock: {response.status_code} - {response.text}")
        return None

def create_crypto_asset(portfolio_id: int, crypto: Dict) -> Dict:
    """Create a crypto asset matching exact schema"""
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
        "notes": f"Digital asset investment - {crypto['blockchain']} network",
        "blockchain": crypto["blockchain"],
        "walletAddress": f"0x{''.join(random.choices('0123456789abcdef', k=40))}",
        "stakingEnabled": crypto["staking_enabled"],
        "stakingApy": crypto["staking_apy"]
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Added crypto: {crypto['symbol']} ({crypto['blockchain']})")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to add crypto: {response.status_code} - {response.text}")
        return None

def create_gold_asset(portfolio_id: int, region: Dict) -> Dict:
    """Create a gold asset matching exact schema"""
    quantity = round(random.uniform(5, 100), 2)
    price_per_gram = random.uniform(60, 70)
    purchase_price = price_per_gram * random.uniform(0.9, 1.1)
    days_ago = random.randint(30, 365)
    purchase_date = (datetime.now() - timedelta(days=days_ago)).strftime("%Y-%m-%d")
    
    is_physical = random.choice([True, False])
    purity = random.choice(["24K", "22K", "18K"])
    
    asset_data = {
        "name": "Gold Investment",
        "symbol": "GOLD",
        "assetType": "GOLD",
        "quantity": quantity,
        "purchasePrice": round(purchase_price, 2),
        "currentPrice": round(price_per_gram, 2),
        "purchaseDate": purchase_date,
        "portfolioId": portfolio_id,
        "notes": f"{'Physical' if is_physical else 'Digital'} gold investment - {purity} purity",
        "purity": purity,
        "weightInGrams": quantity,
        "storageLocation": "Bank Locker" if is_physical else "Digital Vault",
        "certificateNumber": f"GOLD-{random.randint(100000, 999999)}" if is_physical else None,
        "isPhysical": is_physical
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Added gold: {quantity}g ({'Physical' if is_physical else 'Digital'})")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to add gold: {response.status_code} - {response.text}")
        return None

def create_mutual_fund_asset(portfolio_id: int, fund: Dict) -> Dict:
    """Create a mutual fund asset matching exact schema"""
    quantity = round(random.uniform(10, 500), 2)
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
        "notes": f"Long-term investment in {fund['name']}",
        "fundManager": fund["fund_manager"],
        "expenseRatio": fund["expense_ratio"],
        "navPrice": fund["nav_price"],
        "minimumInvestment": fund["minimum_investment"],
        "riskLevel": fund["risk_level"]
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Added mutual fund: {fund['symbol']} ({fund['fund_manager']})")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to add fund: {response.status_code} - {response.text}")
        return None

def create_transaction(asset_id: int, transaction_type: str, asset_details: Dict):
    """Create a transaction matching exact schema"""
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
        "notes": f"{transaction_type} transaction for {asset_details['symbol']}"
    }
    
    response = requests.post(f"{BASE_URL}/transactions", json=transaction_data)
    if response.status_code == 201:
        print(f"      ✓ Created {transaction_type} transaction")
    else:
        print(f"      ✗ Failed transaction: {response.status_code} - {response.text}")

def create_payment(region: Dict) -> Dict:
    """Create a payment matching exact schema"""
    amount = round(random.uniform(500, 25000), 2)
    
    payment_data = {
        "sourceAccount": f"ACC{random.randint(1000, 9999)}",
        "destinationAccount": f"ACC{random.randint(1000, 9999)}",
        "amount": amount,
        "currency": region["currency"],
        "reference": f"REF{random.randint(10000, 99999)}",
        "description": f"Portfolio investment transfer from {region['name']}"
    }
    
    response = requests.post(f"{BASE_URL}/payments", json=payment_data)
    if response.status_code == 201:
        print(f"  ✓ Created payment: {amount} {region['currency']}")
        return response.json()["data"]
    else:
        print(f"  ✗ Failed to create payment: {response.status_code} - {response.text}")
        return None

def create_monitoring_rule(rule_type: str, severity: str):
    """Create a monitoring rule matching exact schema"""
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
        print(f"  ✗ Failed to create rule: {response.status_code} - {response.text}")
        return None

def main():
    print("\n" + "="*60)
    print("MoneyMap Database Final Seeder - Starting")
    print("="*60 + "\n")
    
    clients_created = []
    portfolios_created = []
    assets_created = []
    
    # Create clients from different regions (at least 9)
    print("\n🌍 Creating Clients from Different Regions...")
    num_clients_per_region = 1  # Start with 1 per region for 9 total
    
    for region in REGIONAL_DATA[:9]:  # Take first 9 regions
        for _ in range(num_clients_per_region):
            client = create_client(region)
            if client:
                clients_created.append((client, region))
    
    print(f"\n✅ Created {len(clients_created)} clients from 9 different regions\n")
    
    # Create portfolios and assets for each client
    print("\n💼 Creating Portfolios and Assets...")
    for client, region in clients_created:
        portfolio = create_portfolio(client["id"], f"{client['firstName']} {client['lastName']}", region)
        if portfolio:
            portfolios_created.append(portfolio)
            
            # Add 3-6 assets per portfolio
            num_assets = random.randint(3, 6)
            asset_types = ["STOCK", "CRYPTO", "GOLD", "MUTUAL_FUND"]
            random.shuffle(asset_types)
            
            for j in range(num_assets):
                asset_type = asset_types[j % len(asset_types)]
                
                if asset_type == "STOCK":
                    stock = random.choice(STOCK_DATA)
                    asset = create_stock_asset(portfolio["id"], stock, region)
                elif asset_type == "CRYPTO":
                    crypto = random.choice(CRYPTO_DATA)
                    asset = create_crypto_asset(portfolio["id"], crypto)
                elif asset_type == "GOLD":
                    asset = create_gold_asset(portfolio["id"], region)
                elif asset_type == "MUTUAL_FUND":
                    fund = random.choice(MUTUAL_FUND_DATA)
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
    for region in REGIONAL_DATA[:9]:
        for _ in range(random.randint(1, 3)):
            create_payment(region)
    
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
    print(f"  • Clients: {len(clients_created)} from 9 different regions")
    print(f"  • Portfolios: {len(portfolios_created)}")
    print(f"  • Assets: {len(assets_created)}")
    print(f"  • Transactions: Created for each asset")
    print(f"  • Payments: Multiple payments across regions")
    print(f"  • Monitoring Rules: 4")
    
    print(f"\n🌍 Regional Coverage:")
    for region in REGIONAL_DATA[:9]:
        print(f"  • {region['name']} ({region['code']}) - {region['currency']} - {region['timezone']}")
    
    print(f"\n📈 Asset Types:")
    print(f"  • Stocks: US, UK, India, Singapore, UAE markets")
    print(f"  • Crypto: Bitcoin, Ethereum, BNB, Cardano, Solana")
    print(f"  • Gold: Physical and Digital forms")
    print(f"  • Mutual Funds: Vanguard, Fidelity funds")
    
    print(f"\n✅ Your database is now populated with realistic multi-regional demo data!")
    print("\n" + "="*60 + "\n")

if __name__ == "__main__":
    try:
        main()
    except requests.exceptions.ConnectionError:
        print("\n❌ Error: Could not connect to API at", BASE_URL)
        print("Make sure your Spring Boot application is running on port 8181")
    except Exception as e:
        print(f"\n❌ Error: {str(e)}")
