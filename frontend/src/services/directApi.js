import axios from 'axios';

// Direct API client that bypasses proxy and calls backend directly
const directApiClient = axios.create({
  baseURL: 'http://localhost:8181/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const directApiService = {
  // Buy an asset directly to backend
  buyAsset: (clientId, symbol, quantity, price) => {
    const params = new URLSearchParams({
      clientId,
      symbol,
      quantity: quantity.toString(),
      price: price.toString()
    });
    const url = `/trading/buy?${params}`;
    console.log('Direct API Buy URL:', url);
    console.log('Direct API Method: POST');
    return directApiClient.post(url);
  },

  // Get wallet balance directly
  getWalletBalance: (clientId) => {
    console.log('Direct API Wallet URL:', `/trading/wallet/${clientId}`);
    return directApiClient.get(`/trading/wallet/${clientId}`);
  },

  // Get available assets directly
  getAvailableAssets: () => {
    console.log('Direct API Available Assets URL:', '/trading/assets/available');
    return directApiClient.get('/trading/assets/available');
  }
};
