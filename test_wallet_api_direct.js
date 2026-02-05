// Test wallet API directly
// Run this in browser console when frontend is running

async function testWalletAPI() {
  try {
    console.log('Testing wallet API...');
    
    // Test direct axios call
    const axios = require('axios');
    const response = await axios.get('/api/v1/trading/wallet/1');
    
    console.log('Direct API response:', response);
    console.log('Response data:', response.data);
    console.log('Response status:', response.status);
    
    // Test with tradingService
    const { tradingService } = require('./services/tradingService');
    const balance = await tradingService.getWalletBalance(1);
    
    console.log('TradingService balance:', balance);
    console.log('Type of balance:', typeof balance);
    
  } catch (error) {
    console.error('API Test Error:', error);
    console.error('Error response:', error.response?.data);
  }
}

// Run the test
testWalletAPI();
