from flask import Flask, jsonify, request
from flask_cors import CORS
import yfinance as yf
import requests
import os
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

# =========================
# CONFIG
# =========================
FINNHUB_API_KEY = os.getenv("FINNHUB_API_KEY")
FINNHUB_QUOTE_URL = "https://finnhub.io/api/v1/quote"

ALLOWED_PERIODS = ["5d", "1mo", "3mo", "6mo", "1y"]

# =========================
# 1️⃣ LIVE PRICE (Finnhub)
# =========================
@app.route("/quote", methods=["GET"])
def get_live_quote():
    symbol = request.args.get("symbol")

    if not symbol:
        return jsonify({"error": "symbol query param is required"}), 400

    params = {
        "symbol": symbol.upper(),
        "token": FINNHUB_API_KEY
    }

    response = requests.get(FINNHUB_QUOTE_URL, params=params)
    data = response.json()

    if response.status_code != 200 or "c" not in data:
        return jsonify({"error": "Failed to fetch live quote"}), 500

    return jsonify({
        "symbol": symbol.upper(),
        "currentPrice": data["c"],        # current price
        "change": data["d"],               # price change
        "changePercent": data["dp"],       # % change
        "high": data["h"],
        "low": data["l"],
        "open": data["o"],
        "previousClose": data["pc"],
        "timestamp": data["t"]
    })


# =========================
# 2️⃣ HISTORICAL DATA (Yahoo)
# =========================
@app.route("/stock/<ticker>", methods=["GET"])
def get_stock_data(ticker):
    try:
        period = request.args.get("period", "3mo")

        if period not in ALLOWED_PERIODS:
            return jsonify({
                "error": f"Invalid period. Allowed values: {ALLOWED_PERIODS}"
            }), 400

        stock = yf.Ticker(ticker)
        info = stock.info

        history = stock.history(period=period)

        if history.empty:
            return jsonify({"error": "Invalid ticker or no data available"}), 404

        historical_data = []
        for date, row in history.iterrows():
            historical_data.append({
                "date": date.strftime("%Y-%m-%d"),
                "open": round(float(row["Open"]), 2),
                "high": round(float(row["High"]), 2),
                "low": round(float(row["Low"]), 2),
                "close": round(float(row["Close"]), 2),
                "volume": int(row["Volume"])
            })

        latest_price = historical_data[-1]["close"]
        first_price = historical_data[0]["close"]

        return_percent = round(
            ((latest_price - first_price) / first_price) * 100, 2
        )

        return jsonify({
            "metadata": {
                "ticker": ticker.upper(),
                "companyName": info.get("longName"),
                "sector": info.get("sector"),
                "industry": info.get("industry"),
                "currency": info.get("currency"),
                "exchange": info.get("exchange")
            },
            "latestPrice": latest_price,
            "period": period,
            "historicalData": historical_data,
            "returnPercentage": return_percent
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


# =========================
# START SERVER
# =========================
if __name__ == "__main__":
    app.run(port=5000, debug=True)
