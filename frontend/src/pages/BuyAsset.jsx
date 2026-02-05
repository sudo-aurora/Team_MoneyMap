import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  TextField,
  Button,
  Grid,
  CircularProgress,
  Alert,
  MenuItem,
  IconButton,
  FormControlLabel,
  Switch,
  Divider,
  Chip,
  Paper,
  InputAdornment,
} from '@mui/material';
import { 
  ArrowBack as BackIcon, 
  AccountBalanceWallet as WalletIcon,
  TrendingUp as TrendingUpIcon,
  Search as SearchIcon
} from '@mui/icons-material';
import { tradingService } from '../services/tradingService';
import { clientService } from '../services/clientService';
import { portfolioService } from '../services/portfolioService';

const ASSET_TYPES = ['STOCK', 'CRYPTO', 'GOLD', 'MUTUAL_FUND'];

export default function BuyAsset() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const clientIdParam = searchParams.get('clientId');
  const portfolioIdParam = searchParams.get('portfolioId');
  
  console.log('URL Parameters - clientIdParam:', clientIdParam, 'portfolioIdParam:', portfolioIdParam);
  
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  
  // Available assets
  const [availableAssets, setAvailableAssets] = useState([]);
  const [filteredAssets, setFilteredAssets] = useState([]);
  const [selectedAsset, setSelectedAsset] = useState(null);
  
  // Client wallet and portfolio
  const [walletBalance, setWalletBalance] = useState(0);
  const [clientId, setClientId] = useState(null);
  const [portfolioId, setPortfolioId] = useState(portfolioIdParam || '');
  const [portfolio, setPortfolio] = useState(null);
  
  // Add state to track if we have a valid client
  const [hasValidClient, setHasValidClient] = useState(false);
  
  // Initialize clientId from URL params
  useEffect(() => {
    if (clientIdParam) {
      const clientIdNum = parseInt(clientIdParam);
      console.log('Setting clientId from URL param:', clientIdNum);
      setClientId(clientIdNum);
      setHasValidClient(true);
      loadWalletBalance(clientIdNum);
    }
  }, [clientIdParam]);
  
  // Form data
  const [formData, setFormData] = useState({
    symbol: '',
    quantity: '',
    price: '',
    assetType: 'STOCK',
    searchQuery: ''
  });

  useEffect(() => {
    loadAvailableAssets();
    // Only load wallet balance if we have a clientId
    if (clientId) {
      loadWalletBalance(clientId);
    }
    // Only load portfolio if we have portfolioId but no clientId
    if (portfolioId && !clientId) {
      loadPortfolioAndClient();
    }
  }, [clientId, portfolioId]);

  const loadPortfolioAndClient = async () => {
    try {
      const portfolioData = await portfolioService.getById(portfolioId);
      setPortfolio(portfolioData);
      if (portfolioData.clientId) {
        const clientIdNum = parseInt(portfolioData.clientId);
        setClientId(clientIdNum);
        setHasValidClient(true);
        loadWalletBalance(clientIdNum);
      }
    } catch (err) {
      console.error('Error loading portfolio:', err);
      setError('Failed to load portfolio information');
    }
  };

  useEffect(() => {
    filterAssets();
  }, [availableAssets, formData.assetType, formData.searchQuery]);

  const loadAvailableAssets = async () => {
    try {
      setLoading(true);
      const assets = await tradingService.getAvailableAssets();
      setAvailableAssets(Array.isArray(assets) ? assets : []);
    } catch (err) {
      console.error('Error loading available assets:', err);
      setError('Failed to load available assets');
    } finally {
      setLoading(false);
    }
  };

  const loadWalletBalance = async (clientIdToLoad = null) => {
    try {
      const clientIdToUse = clientIdToLoad || clientId;
      console.log('Loading wallet balance for client:', clientIdToUse);
      
      // Validate clientId before making API call
      if (!clientIdToUse || clientIdToUse === '' || clientIdToUse === ':') {
        console.log('Invalid clientId, skipping wallet balance load');
        return;
      }
      
      const balance = await tradingService.getWalletBalance(clientIdToUse);
      console.log('Raw balance response:', balance);
      console.log('Balance type:', typeof balance);
      console.log('Balance value:', balance);
      
      // Handle BigDecimal object or string response
      let numericBalance = 0;
      if (typeof balance === 'object' && balance !== null) {
        // If it's an object, try to get the value property or convert to string then number
        numericBalance = parseFloat(balance.value || balance.toString() || '0');
      } else if (typeof balance === 'string') {
        numericBalance = parseFloat(balance);
      } else if (typeof balance === 'number') {
        numericBalance = balance;
      }
      
      const finalBalance = numericBalance || 0;
      
      console.log('Final numeric balance:', finalBalance);
      setWalletBalance(finalBalance);
    } catch (err) {
      console.error('Error loading wallet balance:', err);
      console.error('Error details:', err.response?.data);
      setWalletBalance(0);
    }
  };

  const filterAssets = () => {
    let filtered = availableAssets;
    
    // Filter by type
    if (formData.assetType !== 'ALL') {
      filtered = filtered.filter(asset => asset.assetType === formData.assetType);
    }
    
    // Filter by search query
    if (formData.searchQuery) {
      const query = formData.searchQuery.toLowerCase();
      filtered = filtered.filter(asset => 
        asset.symbol.toLowerCase().includes(query) ||
        asset.name.toLowerCase().includes(query)
      );
    }
    
    setFilteredAssets(filtered);
  };

  const handleAssetSelect = (asset) => {
    setSelectedAsset(asset);
    setFormData(prev => ({
      ...prev,
      symbol: asset.symbol,
      price: asset.currentMarketPrice.toString(),
      assetType: asset.assetType
    }));
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const calculateTotalCost = () => {
    const quantity = parseFloat(formData.quantity) || 0;
    const price = parseFloat(formData.price) || 0;
    return quantity * price;
  };

  const canAfford = () => {
    return walletBalance >= calculateTotalCost();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      setError(null);
      setSuccess(null);
      
      if (!clientId) {
        setError('Please select a client');
        return;
      }
      
      if (!formData.symbol || !formData.quantity || !formData.price) {
        setError('Please fill all required fields');
        return;
      }
      
      const quantity = parseFloat(formData.quantity);
      const price = parseFloat(formData.price);
      
      console.log('Buy Asset Request:', {
        clientId,
        symbol: formData.symbol,
        quantity,
        price
      });
      
      if (quantity <= 0 || price <= 0) {
        setError('Quantity and price must be positive');
        return;
      }
      
      if (!canAfford()) {
        setError('Insufficient wallet balance');
        return;
      }
      
      await tradingService.buyAsset(clientId, formData.symbol, quantity, price);
      
      setSuccess('Asset purchased successfully!');
      setFormData(prev => ({
        ...prev,
        quantity: '',
        symbol: '',
        price: ''
      }));
      setSelectedAsset(null);
      
      // Reload wallet balance
      loadWalletBalance(clientId);
      
    } catch (err) {
      setError(err.message || 'Failed to purchase asset');
    } finally {
      setSaving(false);
    }
  };

  const getTypeColor = (type) => {
    const colors = {
      STOCK: 'primary',
      CRYPTO: 'secondary',
      GOLD: 'warning',
      MUTUAL_FUND: 'info',
    };
    return colors[type] || 'default';
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" alignItems="center" gap={2} mb={3}>
        <IconButton onClick={() => navigate(-1)}>
          <BackIcon />
        </IconButton>
        <Typography variant="h4" fontWeight="bold">
          Buy Assets
        </Typography>
      </Box>

      {!hasValidClient && (
        <Alert severity="warning" sx={{ mb: 2 }}>
          No client selected. Please go to a client's profile or portfolio to buy assets.
          <Button 
            variant="outlined" 
            size="small" 
            sx={{ ml: 2 }}
            onClick={() => navigate('/clients')}
          >
            Go to Clients
          </Button>
        </Alert>
      )}

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
          {success}
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Left Column - Asset Selection */}
        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom color="primary">
                Available Assets
              </Typography>
              
              {/* Filters */}
              <Box display="flex" gap={2} mb={2}>
                <TextField
                  fullWidth
                  placeholder="Search assets..."
                  name="searchQuery"
                  value={formData.searchQuery}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <SearchIcon />
                      </InputAdornment>
                    ),
                  }}
                />
                <TextField
                  select
                  label="Type"
                  name="assetType"
                  value={formData.assetType}
                  onChange={handleChange}
                  sx={{ minWidth: 150 }}
                >
                  <MenuItem value="ALL">All Types</MenuItem>
                  {ASSET_TYPES.map((type) => (
                    <MenuItem key={type} value={type}>
                      {type}
                    </MenuItem>
                  ))}
                </TextField>
              </Box>

              {/* Asset List */}
              <Paper sx={{ maxHeight: 400, overflow: 'auto' }}>
                {filteredAssets.length === 0 ? (
                  <Box p={3} textAlign="center">
                    <Typography color="text.secondary">
                      No assets found
                    </Typography>
                  </Box>
                ) : (
                  <Box>
                    {filteredAssets.map((asset) => (
                      <Box
                        key={asset.symbol}
                        p={2}
                        sx={{
                          cursor: 'pointer',
                          '&:hover': { backgroundColor: 'action.hover' },
                          backgroundColor: selectedAsset?.symbol === asset.symbol ? 'action.selected' : 'transparent'
                        }}
                        onClick={() => handleAssetSelect(asset)}
                      >
                        <Box display="flex" justifyContent="space-between" alignItems="center">
                          <Box>
                            <Typography variant="subtitle1" fontWeight="medium">
                              {asset.symbol}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              {asset.name}
                            </Typography>
                          </Box>
                          <Box textAlign="right">
                            <Chip
                              label={asset.assetType}
                              size="small"
                              color={getTypeColor(asset.assetType)}
                              variant="outlined"
                            />
                            <Typography variant="h6" color="primary">
                              ${asset.currentMarketPrice?.toLocaleString()}
                            </Typography>
                          </Box>
                        </Box>
                      </Box>
                    ))}
                  </Box>
                )}
              </Paper>
            </CardContent>
          </Card>
        </Grid>

        {/* Right Column - Buy Form */}
        <Grid item xs={12} md={5}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom color="primary">
                Purchase Details
              </Typography>

              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <WalletIcon color="primary" />
                <Typography variant="body2">
                  Wallet Balance: <strong>${hasValidClient ? walletBalance.toLocaleString() : '0.00'}</strong>
                </Typography>
              </Box>

              <form onSubmit={handleSubmit}>
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Client ID"
                      name="clientId"
                      value={clientId || ''}
                      onChange={(e) => {
                        const newClientId = e.target.value ? parseInt(e.target.value) : null;
                        setClientId(newClientId);
                        setHasValidClient(!!newClientId);
                        if (newClientId) {
                          loadWalletBalance(newClientId);
                        }
                      }}
                      placeholder="Enter client ID"
                      helperText={!hasValidClient ? "Enter a client ID to enable trading" : ""}
                      required
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Asset Symbol"
                      name="symbol"
                      value={formData.symbol}
                      onChange={handleChange}
                      placeholder="Select asset from list"
                      required
                      InputProps={{
                        readOnly: true,
                        style: { backgroundColor: 'action.disabled' }
                      }}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Quantity"
                      name="quantity"
                      type="number"
                      value={formData.quantity}
                      onChange={handleChange}
                      placeholder="0.00"
                      inputProps={{ step: "0.01", min: "0" }}
                      required
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Price per Unit"
                      name="price"
                      type="number"
                      value={formData.price}
                      onChange={handleChange}
                      placeholder="0.00"
                      inputProps={{ step: "0.01", min: "0" }}
                      required
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <Divider sx={{ my: 1 }} />
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Typography variant="h6">
                        Total Cost:
                      </Typography>
                      <Typography variant="h6" color={canAfford() ? 'success.main' : 'error.main'}>
                        ${calculateTotalCost().toLocaleString()}
                      </Typography>
                    </Box>
                    
                    {!canAfford() && calculateTotalCost() > 0 && (
                      <Typography variant="body2" color="error.main">
                        Insufficient wallet balance
                      </Typography>
                    )}
                  </Grid>

                  <Grid item xs={12}>
                    <Button
                      type="submit"
                      fullWidth
                      variant="contained"
                      startIcon={saving ? <CircularProgress size={20} /> : <TrendingUpIcon />}
                      disabled={saving || !hasValidClient || !canAfford() || !formData.symbol || !formData.quantity || !formData.price}
                      sx={{ mt: 2 }}
                    >
                      {saving ? 'Processing...' : 'Buy Asset'}
                    </Button>
                  </Grid>
                </Grid>
              </form>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
