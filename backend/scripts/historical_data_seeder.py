"""
Quick Historical Data Seeder for MoneyMap
Populates historical price data for existing tickers
"""

import requests
import json
from datetime import datetime, timedelta
import random

BASE_URL = "http://localhost:8181/api/v1"

# All tickers from your existing data
TICKERS = {
    "STOCKS": ["AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "JPM", "V", "JNJ", "RELIANCE", "TCS", "HDFC", "DBS", "ENBD"],
    "CRYPTO": ["BTC", "ETH", "BNB", "ADA", "SOL"],
    "GOLD": ["GOLD"],
    "MUTUAL_FUNDS": ["VFIAX", "FXAIX", "VTSAX", "VIGAX"]
}

def generate_historical_prices(base_price, days=365):
    """Generate realistic historical price data"""
    prices = []
    current_price = base_price
    
    for i in range(days, 0, -1):
        date = (datetime.now() - timedelta(days=i)).strftime("%Y-%m-%d")
        
        # Add some realistic volatility
        change_percent = random.uniform(-0.05, 0.05)  # +/- 5% daily change
        current_price = current_price * (1 + change_percent)
        
        # Ensure price doesn't go negative
        current_price = max(current_price, base_price * 0.3)
        
        prices.append({
            "date": date,
            "price": round(current_price, 2),
            "volume": random.randint(100000, 10000000)
        })
    
    return prices

def create_historical_data():
    """Create historical data for all tickers using existing API"""
    print("📈 Creating Historical Price Data...")
    
    # Use existing fetch endpoint for each ticker
    tickers = TICKERS["STOCKS"] + TICKERS["CRYPTO"] + TICKERS["GOLD"] + TICKERS["MUTUAL_FUNDS"]
    
    for ticker in tickers:
        try:
            # Use the existing fetch endpoint
            response = requests.post(f"{BASE_URL}/api/price-history/fetch/{ticker}?period=1mo")
            if response.status_code == 200:
                print(f"  ✓ Added historical data for {ticker}")
            else:
                print(f"  ✗ Failed {ticker}: {response.status_code} - {response.text}")
        except Exception as e:
            print(f"  ✗ Error {ticker}: {e}")

def main():
    print("="*50)
    print("MoneyMap Historical Data Seeder")
    print("="*50)
    
    try:
        create_historical_data()
        print("\n✅ Historical data creation complete!")
    except requests.exceptions.ConnectionError:
        print("\n❌ Error: Could not connect to API at", BASE_URL)
        print("Make sure your Spring Boot application is running on port 8181")
    except Exception as e:
        print(f"\n❌ Error: {str(e)}")

if __name__ == "__main__":
    main()
