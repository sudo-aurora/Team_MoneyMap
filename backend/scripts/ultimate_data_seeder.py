"""
MoneyMap Ultimate Database Seeder
Populates database with comprehensive data for all asset types:
- Stocks (US, International)
- Cryptocurrency 
- Mutual Funds
- Gold & Precious Metals
- Real Estate (REITs)
- Bonds

Includes comprehensive transactions:
- Buy/Sell transactions
- Dividend payments
- Interest income
- Transfer in/out
- Proper entity relationships and all required fields
"""

import requests
import json
from datetime import datetime, timedelta
import random
from typing import Dict, List
from decimal import Decimal

# API Base URL
BASE_URL = "http://localhost:8181/api/v1"

# Enhanced regional data
REGIONAL_DATA = [
    {
        "code": "US", "name": "United States", "currency": "USD", "timezone": "America/New_York", "locale": "en_US",
        "cities": ["New York", "Los Angeles", "Chicago", "Houston", "San Francisco"],
        "first_names": ["Michael", "Sarah", "James", "Jennifer", "David", "Emily"],
        "last_names": ["Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia"],
        "phone_prefix": "+1", "postal_format": lambda: f"{random.randint(10000, 99999)}"
    },
    {
        "code": "GB", "name": "United Kingdom", "currency": "GBP", "timezone": "Europe/London", "locale": "en_GB",
        "cities": ["London", "Manchester", "Birmingham", "Edinburgh", "Glasgow"],
        "first_names": ["Oliver", "Emma", "George", "Amelia", "Harry", "Olivia"],
        "last_names": ["Smith", "Jones", "Taylor", "Brown", "Wilson", "Davies"],
        "phone_prefix": "+44", "postal_format": lambda: f"{random.choice(['SW1A', 'M1', 'B1'])} {random.randint(1,9)}{random.choice(['A','B','C'])}"
    },
    {
        "code": "IN", "name": "India", "currency": "INR", "timezone": "Asia/Kolkata", "locale": "en_IN",
        "cities": ["Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata"],
        "first_names": ["Rahul", "Priya", "Amit", "Anita", "Vikram", "Deepika"],
        "last_names": ["Sharma", "Patel", "Kumar", "Singh", "Reddy", "Gupta"],
        "phone_prefix": "+91", "postal_format": lambda: f"{random.randint(110001, 600000)}"
    }
]

# Comprehensive asset data for all types
ASSETS_DATA = {
    "STOCKS": {
        "US": [
            {"name": "Apple Inc.", "symbol": "AAPL", "price": 175.50, "exchange": "NASDAQ"},
            {"name": "Microsoft Corporation", "symbol": "MSFT", "price": 380.25, "exchange": "NASDAQ"},
            {"name": "Amazon.com Inc.", "symbol": "AMZN", "price": 145.80, "exchange": "NASDAQ"},
            {"name": "Tesla Inc.", "symbol": "TSLA", "price": 250.75, "exchange": "NASDAQ"},
            {"name": "NVIDIA Corporation", "symbol": "NVDA", "price": 480.50, "exchange": "NASDAQ"},
            {"name": "Alphabet Inc.", "symbol": "GOOGL", "price": 140.30, "exchange": "NASDAQ"},
            {"name": "Meta Platforms Inc.", "symbol": "META", "price": 320.60, "exchange": "NASDAQ"},
            {"name": "Berkshire Hathaway", "symbol": "BRK.B", "price": 350.25, "exchange": "NYSE"}
        ],
        "International": [
            {"name": "Toyota Motor Corporation", "symbol": "TM", "price": 180.45, "exchange": "NYSE"},
            {"name": "Shell plc", "symbol": "SHEL", "price": 58.30, "exchange": "NYSE"},
            {"name": "ASML Holding NV", "symbol": "ASML", "price": 750.80, "exchange": "NASDAQ"},
            {"name": "TSMC", "symbol": "TSM", "price": 95.20, "exchange": "NYSE"},
            {"name": "Novartis AG", "symbol": "NVS", "price": 85.60, "exchange": "NYSE"}
        ]
    },
    "CRYPTOCURRENCY": [
        {"name": "Bitcoin", "symbol": "BTC", "price": 45000.00, "exchange": "Crypto"},
        {"name": "Ethereum", "symbol": "ETH", "price": 2500.00, "exchange": "Crypto"},
        {"name": "Binance Coin", "symbol": "BNB", "price": 320.50, "exchange": "Crypto"},
        {"name": "Cardano", "symbol": "ADA", "price": 0.65, "exchange": "Crypto"},
        {"name": "Solana", "symbol": "SOL", "price": 105.30, "exchange": "Crypto"},
        {"name": "Ripple", "symbol": "XRP", "price": 0.55, "exchange": "Crypto"}
    ],
    "MUTUAL_FUNDS": [
        {"name": "Vanguard 500 Index Fund", "symbol": "VOO", "price": 420.15, "exchange": "Mutual Fund"},
        {"name": "Fidelity Magellan Fund", "symbol": "FMAGX", "price": 95.80, "exchange": "Mutual Fund"},
        {"name": "American Funds Growth Fund", "symbol": "AGTHX", "price": 55.25, "exchange": "Mutual Fund"},
        {"name": "T. Rowe Price Equity Income", "symbol": "PRFDX", "price": 38.90, "exchange": "Mutual Fund"},
        {"name": "Vanguard Total Bond Market", "symbol": "BND", "price": 72.45, "exchange": "Mutual Fund"}
    ],
    "GOLD_METALS": [
        {"name": "SPDR Gold Shares", "symbol": "GLD", "price": 185.50, "exchange": "ETF"},
        {"name": "iShares Silver Trust", "symbol": "SLV", "price": 21.30, "exchange": "ETF"},
        {"name": "Aberdeen Standard Physical", "symbol": "SGOL", "price": 18.75, "exchange": "ETF"},
        {"name": "Perth Mint Physical Gold", "symbol": "AAAU", "price": 19.20, "exchange": "ETF"}
    ],
    "REAL_ESTATE": [
        {"name": "Vanguard Real Estate ETF", "symbol": "VNQ", "price": 85.60, "exchange": "ETF"},
        {"name": "iShares U.S. Real Estate", "symbol": "IYR", "price": 78.90, "exchange": "ETF"},
        {"name": "Prologis Inc.", "symbol": "PLD", "price": 125.45, "exchange": "NYSE"},
        {"name": "American Tower Corp", "symbol": "AMT", "price": 195.30, "exchange": "NYSE"},
        {"name": "Equity Residential", "symbol": "EQR", "price": 68.75, "exchange": "NYSE"}
    ],
    "BONDS": [
        {"name": "iShares 20+ Year Treasury", "symbol": "TLT", "price": 95.30, "exchange": "ETF"},
        {"name": "Vanguard Intermediate-Term", "symbol": "BIV", "price": 72.85, "exchange": "ETF"},
        {"name": "iShares Core U.S. Aggregate", "symbol": "AGG", "price": 105.60, "exchange": "ETF"},
        {"name": "SPDR Bloomberg High Yield", "symbol": "JNK", "price": 88.45, "exchange": "ETF"}
    ]
}

def create_client(region: Dict) -> Dict:
    """Create a client with all required fields"""
    first_name = random.choice(region["first_names"])
    last_name = random.choice(region["last_names"])
    city = random.choice(region["cities"])
    
    client_data = {
        "firstName": first_name,
        "lastName": last_name,
        "email": f"{first_name.lower()}.{last_name.lower()}@example.com",
        "phone": f"{region['phone_prefix']} {random.randint(1000000000, 9999999999)}",
        "address": f"{random.randint(100, 999)} {random.choice(['Main St', 'Oak Ave', 'Park Rd'])}",
        "city": city,
        "stateOrProvince": random.choice(["CA", "NY", "TX", "FL"]) if region["code"] == "US" else "State",
        "postalCode": region["postal_format"](),
        "country": region["name"],
        "preferredCurrency": region["currency"],
        "timezone": region["timezone"],
        "locale": region["locale"],
        "active": True,
        "walletBalance": random.uniform(5000, 50000)
    }
    
    response = requests.post(f"{BASE_URL}/clients", json=client_data)
    if response.status_code == 201:
        print(f"✓ Created client: {first_name} {last_name} ({region['code']}) - {city}")
        return response.json()["data"]
    else:
        print(f"✗ Failed to create client: {response.status_code} - {response.text}")
        return None

def create_portfolio(client_id: int, client_name: str, region: Dict) -> Dict:
    """Create a portfolio with all required fields"""
    portfolio_names = {
        "US": ["Growth Portfolio", "Tech Innovation Fund", "Balanced Strategy"],
        "GB": ["Conservative Wealth Builder", "UK Growth Fund", "British Portfolio"],
        "IN": ["Indian Equity Focus", "Emerging Markets Fund", "Asian Growth Portfolio"]
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

def create_asset(portfolio_id: int, asset: Dict, asset_type: str) -> Dict:
    """Create an asset with all required fields"""
    quantity = round(random.uniform(10, 500), 2)
    purchase_price = asset["price"] * random.uniform(0.7, 1.3)
    days_ago = random.randint(30, 365)
    purchase_date = (datetime.now() - timedelta(days=days_ago)).strftime("%Y-%m-%d")
    
    asset_data = {
        "name": asset["name"],
        "symbol": asset["symbol"],
        "assetType": asset_type,
        "quantity": quantity,
        "purchasePrice": round(purchase_price, 2),
        "currentPrice": asset["price"],
        "purchaseDate": purchase_date,
        "portfolioId": portfolio_id,
        "notes": f"Purchased {quantity} shares of {asset['symbol']}",
        "exchange": asset["exchange"],
        "sector": random.choice(["Technology", "Healthcare", "Finance", "Energy", "Consumer"]),
        "active": True
    }
    
    response = requests.post(f"{BASE_URL}/assets", json=asset_data)
    if response.status_code == 201:
        print(f"    ✓ Created {asset_type}: {asset['symbol']} ({quantity} shares)")
        return response.json()["data"]
    else:
        print(f"    ✗ Failed to create asset: {response.status_code} - {response.text}")
        return None

def create_buy_transaction(asset_id: int, asset: Dict, quantity: float, price: float) -> Dict:
    """Create a BUY transaction"""
    transaction_data = {
        "assetId": asset_id,
        "transactionType": "BUY",
        "quantity": quantity,
        "pricePerUnit": price,
        "transactionDate": (datetime.now() - timedelta(days=random.randint(1, 30))).isoformat(),
        "notes": f"Purchased {quantity} shares of {asset['symbol']} at ${price}",
        "fees": round(random.uniform(5, 25), 2)
    }
    
    response = requests.post(f"{BASE_URL}/transactions", json=transaction_data)
    if response.status_code == 201:
        print(f"      ✓ Created BUY transaction: {quantity} {asset['symbol']} at ${price}")
        return response.json()["data"]
    else:
        print(f"      ✗ Failed to create BUY transaction: {response.status_code} - {response.text}")
        return None

def create_sell_transaction(asset_id: int, asset: Dict, quantity: float, price: float) -> Dict:
    """Create a SELL transaction"""
    transaction_data = {
        "assetId": asset_id,
        "transactionType": "SELL",
        "quantity": quantity,
        "pricePerUnit": price,
        "transactionDate": (datetime.now() - timedelta(days=random.randint(1, 30))).isoformat(),
        "notes": f"Sold {quantity} shares of {asset['symbol']} at ${price}",
        "fees": round(random.uniform(5, 25), 2)
    }
    
    response = requests.post(f"{BASE_URL}/transactions", json=transaction_data)
    if response.status_code == 201:
        print(f"      ✓ Created SELL transaction: {quantity} {asset['symbol']} at ${price}")
        return response.json()["data"]
    else:
        print(f"      ✗ Failed to create SELL transaction: {response.status_code} - {response.text}")
        return None

def create_dividend_transaction(asset_id: int, asset: Dict, amount: float) -> Dict:
    """Create a DIVIDEND transaction"""
    transaction_data = {
        "assetId": asset_id,
        "transactionType": "DIVIDEND",
        "quantity": 0,
        "pricePerUnit": 0,
        "totalAmount": amount,
        "transactionDate": (datetime.now() - timedelta(days=random.randint(1, 90))).isoformat(),
        "notes": f"Quarterly dividend from {asset['symbol']}",
        "fees": 0
    }
    
    response = requests.post(f"{BASE_URL}/transactions", json=transaction_data)
    if response.status_code == 201:
        print(f"      ✓ Created DIVIDEND transaction: ${amount} from {asset['symbol']}")
        return response.json()["data"]
    else:
        print(f"      ✗ Failed to create DIVIDEND transaction: {response.status_code} - {response.text}")
        return None

def create_interest_transaction(asset_id: int, asset: Dict, amount: float) -> Dict:
    """Create an INTEREST transaction"""
    transaction_data = {
        "assetId": asset_id,
        "transactionType": "INTEREST",
        "quantity": 0,
        "pricePerUnit": 0,
        "totalAmount": amount,
        "transactionDate": (datetime.now() - timedelta(days=random.randint(1, 30))).isoformat(),
        "notes": f"Monthly interest from {asset['symbol']}",
        "fees": 0
    }
    
    response = requests.post(f"{BASE_URL}/transactions", json=transaction_data)
    if response.status_code == 201:
        print(f"      ✓ Created INTEREST transaction: ${amount} from {asset['symbol']}")
        return response.json()["data"]
    else:
        print(f"      ✗ Failed to create INTEREST transaction: {response.status_code} - {response.text}")
        return None

def create_transfer_in_transaction(asset_id: int, asset: Dict, quantity: float) -> Dict:
    """Create a TRANSFER_IN transaction"""
    transaction_data = {
        "assetId": asset_id,
        "transactionType": "TRANSFER_IN",
        "quantity": quantity,
        "pricePerUnit": asset["price"],
        "transactionDate": (datetime.now() - timedelta(days=random.randint(1, 60))).isoformat(),
        "notes": f"Transferred in {quantity} shares of {asset['symbol']} from external account",
        "fees": 0
    }
    
    response = requests.post(f"{BASE_URL}/transactions", json=transaction_data)
    if response.status_code == 201:
        print(f"      ✓ Created TRANSFER_IN transaction: {quantity} {asset['symbol']}")
        return response.json()["data"]
    else:
        print(f"      ✗ Failed to create TRANSFER_IN transaction: {response.status_code} - {response.text}")
        return None

def create_transfer_out_transaction(asset_id: int, asset: Dict, quantity: float) -> Dict:
    """Create a TRANSFER_OUT transaction"""
    transaction_data = {
        "assetId": asset_id,
        "transactionType": "TRANSFER_OUT",
        "quantity": quantity,
        "pricePerUnit": asset["price"],
        "transactionDate": (datetime.now() - timedelta(days=random.randint(1, 60))).isoformat(),
        "notes": f"Transferred out {quantity} shares of {asset['symbol']} to external account",
        "fees": 0
    }
    
    response = requests.post(f"{BASE_URL}/transactions", json=transaction_data)
    if response.status_code == 201:
        print(f"      ✓ Created TRANSFER_OUT transaction: {quantity} {asset['symbol']}")
        return response.json()["data"]
    else:
        print(f"      ✗ Failed to create TRANSFER_OUT transaction: {response.status_code} - {response.text}")
        return None

def populate_client_data(client: Dict, portfolio: Dict, region: Dict):
    """Populate comprehensive data for a client"""
    print(f"\n📊 Populating data for {client['firstName']} {client['lastName']}")
    
    # Create diverse assets across all categories
    all_assets = []
    
    # Add US Stocks
    for stock in random.sample(ASSETS_DATA["STOCKS"]["US"], random.randint(2, 4)):
        asset = create_asset(portfolio["id"], stock, "STOCK")
        if asset:
            all_assets.append(asset)
    
    # Add International Stocks
    for stock in random.sample(ASSETS_DATA["STOCKS"]["International"], random.randint(1, 2)):
        asset = create_asset(portfolio["id"], stock, "STOCK")
        if asset:
            all_assets.append(asset)
    
    # Add Cryptocurrency
    for crypto in random.sample(ASSETS_DATA["CRYPTOCURRENCY"], random.randint(1, 3)):
        asset = create_asset(portfolio["id"], crypto, "CRYPTOCURRENCY")
        if asset:
            all_assets.append(asset)
    
    # Add Mutual Funds
    for fund in random.sample(ASSETS_DATA["MUTUAL_FUNDS"], random.randint(1, 2)):
        asset = create_asset(portfolio["id"], fund, "MUTUAL_FUND")
        if asset:
            all_assets.append(asset)
    
    # Add Gold/Metals
    for gold in random.sample(ASSETS_DATA["GOLD_METALS"], random.randint(1, 2)):
        asset = create_asset(portfolio["id"], gold, "GOLD")
        if asset:
            all_assets.append(asset)
    
    # Add Real Estate
    for reit in random.sample(ASSETS_DATA["REAL_ESTATE"], random.randint(1, 2)):
        asset = create_asset(portfolio["id"], reit, "REAL_ESTATE")
        if asset:
            all_assets.append(asset)
    
    # Add Bonds
    for bond in random.sample(ASSETS_DATA["BONDS"], random.randint(1, 2)):
        asset = create_asset(portfolio["id"], bond, "BOND")
        if asset:
            all_assets.append(asset)
    
    # Create comprehensive transactions for each asset
    for asset in all_assets:
        # Initial BUY transaction
        create_buy_transaction(asset["id"], asset, asset["quantity"], asset["purchasePrice"])
        
        # Random additional transactions
        num_transactions = random.randint(1, 4)
        
        for _ in range(num_transactions):
            transaction_type = random.choice(["BUY", "SELL", "DIVIDEND", "INTEREST", "TRANSFER_IN", "TRANSFER_OUT"])
            
            if transaction_type == "BUY":
                quantity = round(random.uniform(5, 50), 2)
                price = asset["currentPrice"] * random.uniform(0.9, 1.1)
                create_buy_transaction(asset["id"], asset, quantity, price)
                
            elif transaction_type == "SELL":
                quantity = round(random.uniform(5, min(50, asset["quantity"] * 0.3)), 2)
                price = asset["currentPrice"] * random.uniform(0.9, 1.1)
                create_sell_transaction(asset["id"], asset, quantity, price)
                
            elif transaction_type == "DIVIDEND":
                if asset["assetType"] in ["STOCK", "MUTUAL_FUND"]:
                    amount = round(random.uniform(50, 500), 2)
                    create_dividend_transaction(asset["id"], asset, amount)
                    
            elif transaction_type == "INTEREST":
                if asset["assetType"] in ["BOND", "GOLD"]:
                    amount = round(random.uniform(25, 200), 2)
                    create_interest_transaction(asset["id"], asset, amount)
                    
            elif transaction_type == "TRANSFER_IN":
                quantity = round(random.uniform(10, 100), 2)
                create_transfer_in_transaction(asset["id"], asset, quantity)
                
            elif transaction_type == "TRANSFER_OUT":
                quantity = round(random.uniform(5, min(50, asset["quantity"] * 0.2)), 2)
                create_transfer_out_transaction(asset["id"], asset, quantity)

def main():
    """Main seeder function"""
    print("🚀 Starting MoneyMap Ultimate Database Seeder")
    print("=" * 60)
    
    # Create clients from different regions
    clients = []
    for region in REGIONAL_DATA:
        for _ in range(random.randint(2, 3)):  # 2-3 clients per region
            client = create_client(region)
            if client:
                clients.append((client, region))
    
    print(f"\n✅ Created {len(clients)} clients")
    
    # Create portfolios and populate data
    for client, region in clients:
        portfolio = create_portfolio(client["id"], f"{client['firstName']} {client['lastName']}", region)
        if portfolio:
            populate_client_data(client, portfolio, region)
    
    print("\n🎉 Ultimate Database Seeder Complete!")
    print("📈 Assets created: Stocks, Crypto, Mutual Funds, Gold, Real Estate, Bonds")
    print("💰 Transactions created: Buy, Sell, Dividends, Interest, Transfers")
    print("🌍 Regions covered: US, UK, India")
    print("📊 All entity relationships and fields properly populated!")

if __name__ == "__main__":
    main()
