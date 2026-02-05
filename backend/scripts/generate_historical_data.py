#!/usr/bin/env python3
"""
Dummy Historical Data Generator for MoneyMap Assets
Populates AssetPriceHistory table with realistic historical price data
"""

import random
import math
from datetime import datetime, timedelta
from decimal import Decimal
import mysql.connector
from mysql.connector import Error

# Database connection settings
DB_CONFIG = {
    'host': 'localhost',
    'database': 'moneymap',
    'user': 'root',
    'password': 'root',
    'port': 3306
}

# Asset configurations with base prices from AvailableAsset enum
ASSETS = {
    'AAPL': {'base_price': 175.50, 'volatility': 0.03, 'trend': 0.001},
    'GOOGL': {'base_price': 140.25, 'volatility': 0.035, 'trend': 0.0015},
    'MSFT': {'base_price': 380.75, 'volatility': 0.025, 'trend': 0.002},
    'TSLA': {'base_price': 245.80, 'volatility': 0.05, 'trend': 0.003},
    'AMZN': {'base_price': 155.30, 'volatility': 0.04, 'trend': 0.0018},
    'META': {'base_price': 485.20, 'volatility': 0.045, 'trend': 0.0025},
    'NVDA': {'base_price': 875.40, 'volatility': 0.06, 'trend': 0.004},
    'JPM': {'base_price': 195.60, 'volatility': 0.02, 'trend': 0.0008},
    'V': {'base_price': 275.80, 'volatility': 0.022, 'trend': 0.0012},
    'JNJ': {'base_price': 165.40, 'volatility': 0.018, 'trend': 0.0005},
    'BTC': {'base_price': 43250.00, 'volatility': 0.08, 'trend': 0.005},
    'ETH': {'base_price': 2280.50, 'volatility': 0.07, 'trend': 0.004},
    'ADA': {'base_price': 0.58, 'volatility': 0.06, 'trend': 0.003},
    'SOL': {'base_price': 98.75, 'volatility': 0.09, 'trend': 0.006},
    'DOT': {'base_price': 7.85, 'volatility': 0.07, 'trend': 0.004},
    'MATIC': {'base_price': 0.92, 'volatility': 0.08, 'trend': 0.005},
    'LINK': {'base_price': 14.65, 'volatility': 0.065, 'trend': 0.0035},
    'UNI': {'base_price': 6.25, 'volatility': 0.075, 'trend': 0.0045},
    'ATOM': {'base_price': 12.45, 'volatility': 0.08, 'trend': 0.0055},
    'AVAX': {'base_price': 38.90, 'volatility': 0.085, 'trend': 0.006},
    'GOLD24K': {'base_price': 68.50, 'volatility': 0.015, 'trend': 0.0003},
    'GOLD22K': {'base_price': 62.75, 'volatility': 0.015, 'trend': 0.0003},
    'GOLD18K': {'base_price': 51.25, 'volatility': 0.015, 'trend': 0.0003},
    'SILVER': {'base_price': 0.95, 'volatility': 0.02, 'trend': 0.0004},
    'VFIAX': {'base_price': 425.30, 'volatility': 0.012, 'trend': 0.0006},
    'FXAIX': {'base_price': 118.75, 'volatility': 0.011, 'trend': 0.0005},
    'SWPPX': {'base_price': 95.40, 'volatility': 0.011, 'trend': 0.0005},
    'VTSAX': {'base_price': 245.80, 'volatility': 0.013, 'trend': 0.0007},
    'FSKAX': {'base_price': 135.60, 'volatility': 0.012, 'trend': 0.0006}
}

def generate_price_data(base_price, volatility, trend, days=365):
    """Generate realistic price data using geometric Brownian motion"""
    prices = []
    current_price = base_price
    
    for day in range(days):
        # Random walk with trend
        daily_return = random.gauss(trend, volatility)
        current_price = current_price * (1 + daily_return)
        
        # Generate OHLC for the day
        high = current_price * (1 + random.uniform(0, volatility))
        low = current_price * (1 - random.uniform(0, volatility))
        open_price = low + (high - low) * random.random()
        close_price = low + (high - low) * random.random()
        
        # Ensure close is near current_price with some variation
        close_price = current_price * random.uniform(0.98, 1.02)
        
        prices.append({
            'open': round(open_price, 4),
            'high': round(high, 4),
            'low': round(low, 4),
            'close': round(close_price, 4),
            'volume': random.randint(100000, 10000000)
        })
    
    return prices

def connect_to_database():
    """Connect to MySQL database"""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        if connection.is_connected():
            print("Connected to MySQL database")
            return connection
    except Error as e:
        print(f"Error connecting to MySQL: {e}")
        return None

def clear_existing_data(connection, symbol):
    """Clear existing price history for a symbol"""
    cursor = connection.cursor()
    delete_query = "DELETE FROM asset_price_history WHERE symbol = %s"
    cursor.execute(delete_query, (symbol,))
    print(f"Cleared existing data for {symbol}")
    cursor.close()

def insert_price_data(connection, symbol, price_data):
    """Insert price data into database"""
    cursor = connection.cursor()
    insert_query = """
    INSERT INTO asset_price_history 
    (symbol, price_date, open_price, high_price, low_price, close_price, volume)
    VALUES (%s, %s, %s, %s, %s, %s, %s)
    """
    
    # Generate dates for the past year
    end_date = datetime.now().date()
    dates = [end_date - timedelta(days=i) for i in range(len(price_data)-1, -1, -1)]
    
    records_to_insert = []
    for i, (date, data) in enumerate(zip(dates, price_data)):
        records_to_insert.append((
            symbol,
            date,
            data['open'],
            data['high'],
            data['low'],
            data['close'],
            data['volume']
        ))
    
    cursor.executemany(insert_query, records_to_insert)
    connection.commit()
    print(f"Inserted {len(records_to_insert)} records for {symbol}")
    cursor.close()

def main():
    """Main function to populate historical data"""
    connection = connect_to_database()
    if not connection:
        return
    
    try:
        total_records = 0
        for symbol, config in ASSETS.items():
            print(f"\nGenerating data for {symbol}...")
            
            # Clear existing data
            clear_existing_data(connection, symbol)
            
            # Generate new price data
            price_data = generate_price_data(
                config['base_price'],
                config['volatility'],
                config['trend'],
                days=365
            )
            
            # Insert into database
            insert_price_data(connection, symbol, price_data)
            total_records += len(price_data)
        
        print(f"\n✅ Successfully populated {total_records} total price records!")
        print("📊 Historical data is now available for all assets")
        
    except Exception as e:
        print(f"Error: {e}")
    finally:
        if connection.is_connected():
            connection.close()
            print("Database connection closed")

if __name__ == "__main__":
    main()
