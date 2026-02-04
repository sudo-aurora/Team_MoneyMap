from flask import Flask, jsonify, request
import requests
import os
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)

FINNHUB_API_KEY = os.getenv("FINNHUB_API_KEY")
BASE_URL = "https://finnhub.io/api/v1"


# -------------------------------
# Health check
# -------------------------------
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "UP"}), 200


# -------------------------------
# Get stock quote
# Example: /quote?symbol=AAPL
# -------------------------------
@app.route("/quote", methods=["GET"])
def get_quote():
    symbol = request.args.get("symbol")
    if not symbol:
        return jsonify({"error": "symbol query param required"}), 400

    url = f"{BASE_URL}/quote"
    params = {
        "symbol": symbol,
        "token": FINNHUB_API_KEY
    }

    response = requests.get(url, params=params)
    return jsonify(response.json()), response.status_code


# -------------------------------
# Get company profile
# Example: /company-profile?symbol=AAPL
# -------------------------------
@app.route("/company-profile", methods=["GET"])
def company_profile():
    symbol = request.args.get("symbol")
    if not symbol:
        return jsonify({"error": "symbol query param required"}), 400

    url = f"{BASE_URL}/stock/profile2"
    params = {
        "symbol": symbol,
        "token": FINNHUB_API_KEY
    }

    response = requests.get(url, params=params)
    return jsonify(response.json()), response.status_code



@app.route("/candles", methods=["GET"])
def get_candles():
    symbol = request.args.get("symbol")
    resolution = request.args.get("resolution", "D")
    from_ts = request.args.get("from")
    to_ts = request.args.get("to")

    if not all([symbol, from_ts, to_ts]):
        return jsonify({"error": "symbol, from, to are required"}), 400

    url = f"{BASE_URL}/stock/candle"
    params = {
        "symbol": symbol,
        "resolution": resolution,
        "from": from_ts,
        "to": to_ts,
        "token": FINNHUB_API_KEY
    }

    response = requests.get(url, params=params)
    return jsonify(response.json()), response.status_code


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
