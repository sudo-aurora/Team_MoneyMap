import apiClient from './api';

export const tradingService = {
  // Buy an asset
  buyAsset: (clientId, symbol, quantity, price) => {
    const params = new URLSearchParams({
      clientId,
      symbol,
      quantity: quantity.toString(),
      price: price.toString()
    });
    const url = `/trading/buy?${params}`;
    console.log('TradingService Buy URL:', url);
    console.log('TradingService Buy Method: POST');
    return apiClient.post(url);
  },

  // Sell an asset
  sellAsset: (clientId, assetId, quantity, price) => {
    const params = new URLSearchParams({
      clientId,
      assetId: assetId.toString(),
      quantity: quantity.toString(),
      price: price.toString()
    });
    return apiClient.post(`/trading/sell?${params}`);
  },

  // Get all available assets
  getAvailableAssets: () => {
    return apiClient.get('/trading/assets/available');
  },

  // Get available assets by type
  getAvailableAssetsByType: (assetType) => {
    return apiClient.get(`/trading/assets/available/type/${assetType}`);
  },

  // Search available assets
  searchAvailableAssets: (query) => {
    return apiClient.get(`/trading/assets/available/search?query=${encodeURIComponent(query)}`);
  },

  // Get wallet balance
  getWalletBalance: (clientId) => {
    return apiClient.get(`/trading/wallet/${clientId}`);
  },

  // Add funds to wallet
  addToWallet: (clientId, amount) => {
    return apiClient.post(`/trading/wallet/${clientId}/deposit?amount=${amount}`);
  },

  // Check sufficient funds
  checkSufficientFunds: (clientId, amount) => {
    return apiClient.get(`/trading/wallet/${clientId}/check?amount=${amount}`);
  }
};
