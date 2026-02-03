// services/transactionService.js
import apiClient from './api';

export const transactionService = {
  // Get all transactions with pagination
  getAll: (page = 0, size = 10) => {
    return apiClient.get(`/transactions?page=${page}&size=${size}`);
  },

  // Get transaction by ID
  getById: (id) => {
    return apiClient.get(`/transactions/${id}`);
  },

  // Get all transaction types (enum values)
  getTypes: () => {
    return apiClient.get('/transactions/types');
  },

  // Get transactions by asset ID
  getByAsset: (assetId) => {
    return apiClient.get(`/transactions/asset/${assetId}`);
  },

  // Get transactions by asset ID with pagination
  getByAssetPaged: (assetId, page = 0, size = 10) => {
    return apiClient.get(`/transactions/asset/${assetId}/paged?page=${page}&size=${size}`);
  },

  // Get transactions by portfolio ID
  getByPortfolio: (portfolioId) => {
    return apiClient.get(`/transactions/portfolio/${portfolioId}`);
  },

  // Get transactions by client ID
  getByClient: (clientId) => {
    return apiClient.get(`/transactions/client/${clientId}`);
  },

  // Get transactions by type with pagination
  getByType: (transactionType, page = 0, size = 10) => {
    return apiClient.get(`/transactions/type/${transactionType}?page=${page}&size=${size}`);
  },

  // Get transactions by date range with pagination
  getByDateRange: (startDate, endDate, page = 0, size = 10) => {
    return apiClient.get(`/transactions/date-range?startDate=${startDate}&endDate=${endDate}&page=${page}&size=${size}`);
  },

  // Create a new transaction
  create: (transactionData) => {
    return apiClient.post('/transactions', transactionData);
  },

  // Update an existing transaction
  update: (id, transactionData) => {
    return apiClient.put(`/transactions/${id}`, transactionData);
  },

  // Delete a transaction
  delete: (id) => {
    return apiClient.delete(`/transactions/${id}`);
  },

  // Helper function to format transaction data for API
  formatTransactionData: (data) => {
    return {
      transactionType: data.transactionType, // BUY, SELL, DIVIDEND, INTEREST, TRANSFER_IN, TRANSFER_OUT
      quantity: parseFloat(data.quantity),
      pricePerUnit: parseFloat(data.pricePerUnit),
      fees: parseFloat(data.fees) || 0,
      assetId: parseInt(data.assetId),
      transactionDate: data.transactionDate, // ISO format: "2024-06-15T10:30:00"
      notes: data.notes || ''
    };
  },

  // Helper function to calculate total amount
  calculateTotalAmount: (quantity, pricePerUnit, fees = 0, transactionType) => {
    const subtotal = quantity * pricePerUnit;
    if (transactionType === 'SELL') {
      return subtotal - fees; // For sells, deduct fees from proceeds
    }
    return subtotal + fees; // For buys, add fees to cost
  },

  // Get transaction statistics (if you want to add custom aggregations)
  getStatistics: async (clientId = null, portfolioId = null) => {
    try {
      let transactions = [];
      
      if (clientId) {
        transactions = await transactionService.getByClient(clientId);
      } else if (portfolioId) {
        transactions = await transactionService.getByPortfolio(portfolioId);
      } else {
        const response = await transactionService.getAll(0, 1000);
        transactions = response.content || [];
      }

      // Calculate statistics
      const stats = {
        totalTransactions: transactions.length,
        totalBuys: transactions.filter(t => t.transactionType === 'BUY').length,
        totalSells: transactions.filter(t => t.transactionType === 'SELL').length,
        totalDividends: transactions.filter(t => t.transactionType === 'DIVIDEND').length,
        totalBuyAmount: transactions
          .filter(t => t.transactionType === 'BUY')
          .reduce((sum, t) => sum + (t.totalAmount || 0), 0),
        totalSellAmount: transactions
          .filter(t => t.transactionType === 'SELL')
          .reduce((sum, t) => sum + (t.totalAmount || 0), 0),
        totalDividendAmount: transactions
          .filter(t => t.transactionType === 'DIVIDEND')
          .reduce((sum, t) => sum + (t.totalAmount || 0), 0),
        totalFees: transactions.reduce((sum, t) => sum + (t.fees || 0), 0),
      };

      return stats;
    } catch (error) {
      console.error('Error calculating transaction statistics:', error);
      throw error;
    }
  },

  // Get recent transactions (helper for dashboard)
  getRecent: (limit = 10) => {
    return apiClient.get(`/transactions?page=0&size=${limit}`);
  },

  // Get transactions for a specific month
  getByMonth: (year, month) => {
    const startDate = new Date(year, month - 1, 1).toISOString();
    const endDate = new Date(year, month, 0, 23, 59, 59).toISOString();
    return transactionService.getByDateRange(startDate, endDate, 0, 100);
  },

  // Validate transaction data before submission
  validateTransaction: (data) => {
    const errors = {};

    if (!data.transactionType) {
      errors.transactionType = 'Transaction type is required';
    }

    if (!data.quantity || data.quantity <= 0) {
      errors.quantity = 'Quantity must be greater than 0';
    }

    if (!data.pricePerUnit || data.pricePerUnit < 0) {
      errors.pricePerUnit = 'Price per unit must be 0 or greater';
    }

    if (data.fees && data.fees < 0) {
      errors.fees = 'Fees cannot be negative';
    }

    if (!data.assetId) {
      errors.assetId = 'Asset is required';
    }

    if (!data.transactionDate) {
      errors.transactionDate = 'Transaction date is required';
    }

    return {
      isValid: Object.keys(errors).length === 0,
      errors
    };
  },

  // Transaction type configurations
  transactionTypes: {
    BUY: { label: 'Buy', color: 'success', icon: '📈' },
    SELL: { label: 'Sell', color: 'error', icon: '📉' },
    DIVIDEND: { label: 'Dividend', color: 'info', icon: '💰' },
    INTEREST: { label: 'Interest', color: 'info', icon: '💵' },
    TRANSFER_IN: { label: 'Transfer In', color: 'primary', icon: '⬅️' },
    TRANSFER_OUT: { label: 'Transfer Out', color: 'warning', icon: '➡️' },
  },
};