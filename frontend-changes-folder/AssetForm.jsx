import { useState, useEffect } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
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
  FormControl,
  InputLabel,
  Select,
} from '@mui/material';
import { ArrowBack as BackIcon, Save as SaveIcon } from '@mui/icons-material';
import { assetService } from '../services/assetService';
import { portfolioService } from '../services/portfolioService';

const ASSET_TYPES = ['STOCK', 'CRYPTO', 'GOLD', 'MUTUAL_FUND'];

export default function AssetForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const portfolioIdParam = searchParams.get('portfolioId');
  const isEdit = Boolean(id);
  
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  
  const [formData, setFormData] = useState({
    // Common fields
    name: '',
    symbol: '',
    assetType: 'STOCK',
    quantity: '',
    purchasePrice: '',
    currentPrice: '',
    purchaseDate: new Date().toISOString().split('T')[0],
    portfolioId: portfolioIdParam || '',
    notes: '',
    
    // STOCK specific
    exchange: '',
    sector: '',
    dividendYield: '',
    fractionalAllowed: false,
    
    // CRYPTO specific
    blockchainNetwork: '',
    walletAddress: '',
    stakingEnabled: false,
    stakingApy: '',
    
    // GOLD specific
    purity: '',
    weightInGrams: '',
    storageLocation: '',
    certificateNumber: '',
    isPhysical: true,
    
    // MUTUAL_FUND specific
    fundManager: '',
    expenseRatio: '',
    navPrice: '',
    riskLevel: '',
    minimumInvestment: '',
  });

  useEffect(() => {
    loadPortfolios();
    if (isEdit) {
      loadAsset();
    }
  }, [id]);

  useEffect(() => {
    // When portfolios are loaded and we have a portfolioIdParam, find and set the selected portfolio
    if (portfolioIdParam && portfolios.length > 0) {
      const portfolio = portfolios.find(p => p.id.toString() === portfolioIdParam);
      if (portfolio) {
        setSelectedPortfolio(portfolio);
      }
    }
  }, [portfolioIdParam, portfolios]);

  const loadPortfolios = async () => {
    try {
      const response = await portfolioService.getAll(0, 100);
      const portfolioList = response.content || response || [];
      setPortfolios(portfolioList);
    } catch (err) {
      console.error('Error loading portfolios:', err);
    }
  };

  const loadAsset = async () => {
    try {
      setLoading(true);
      const data = await assetService.getById(id);
      
      setFormData({
        name: data.name || '',
        symbol: data.symbol || '',
        assetType: data.assetType || 'STOCK',
        quantity: data.quantity || '',
        purchasePrice: data.purchasePrice || '',
        currentPrice: data.currentPrice || '',
        purchaseDate: data.purchaseDate ? data.purchaseDate.split('T')[0] : '',
        portfolioId: data.portfolioId || '',
        notes: data.notes || '',
        
        // Type-specific fields
        exchange: data.exchange || '',
        sector: data.sector || '',
        dividendYield: data.dividendYield || '',
        fractionalAllowed: data.fractionalAllowed || false,
        
        blockchainNetwork: data.blockchainNetwork || '',
        walletAddress: data.walletAddress || '',
        stakingEnabled: data.stakingEnabled || false,
        stakingApy: data.stakingApy || '',
        
        purity: data.purity || '',
        weightInGrams: data.weightInGrams || '',
        storageLocation: data.storageLocation || '',
        certificateNumber: data.certificateNumber || '',
        isPhysical: data.isPhysical !== undefined ? data.isPhysical : true,
        
        fundManager: data.fundManager || '',
        expenseRatio: data.expenseRatio || '',
        navPrice: data.navPrice || '',
        riskLevel: data.riskLevel || '',
        minimumInvestment: data.minimumInvestment || '',
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      setError(null);
      
      // Validation
      if (!formData.portfolioId) {
        setError('Please select a portfolio');
        return;
      }
      
      // Build payload with only relevant fields based on asset type
      const payload = {
        name: formData.name,
        symbol: formData.symbol,
        assetType: formData.assetType,
        quantity: parseFloat(formData.quantity),
        purchasePrice: parseFloat(formData.purchasePrice),
        currentPrice: parseFloat(formData.currentPrice),
        purchaseDate: formData.purchaseDate,
        portfolioId: parseInt(formData.portfolioId),
        notes: formData.notes,
      };

      // Add type-specific fields
      if (formData.assetType === 'STOCK') {
        if (formData.exchange) payload.exchange = formData.exchange;
        if (formData.sector) payload.sector = formData.sector;
        if (formData.dividendYield) payload.dividendYield = parseFloat(formData.dividendYield);
        payload.fractionalAllowed = formData.fractionalAllowed;
      } else if (formData.assetType === 'CRYPTO') {
        if (formData.blockchainNetwork) payload.blockchainNetwork = formData.blockchainNetwork;
        if (formData.walletAddress) payload.walletAddress = formData.walletAddress;
        payload.stakingEnabled = formData.stakingEnabled;
        if (formData.stakingApy) payload.stakingApy = parseFloat(formData.stakingApy);
      } else if (formData.assetType === 'GOLD') {
        if (formData.purity) payload.purity = formData.purity;
        if (formData.weightInGrams) payload.weightInGrams = parseFloat(formData.weightInGrams);
        if (formData.storageLocation) payload.storageLocation = formData.storageLocation;
        if (formData.certificateNumber) payload.certificateNumber = formData.certificateNumber;
        payload.isPhysical = formData.isPhysical;
      } else if (formData.assetType === 'MUTUAL_FUND') {
        if (formData.fundManager) payload.fundManager = formData.fundManager;
        if (formData.expenseRatio) payload.expenseRatio = parseFloat(formData.expenseRatio);
        if (formData.navPrice) payload.navPrice = parseFloat(formData.navPrice);
        if (formData.riskLevel) payload.riskLevel = formData.riskLevel;
        if (formData.minimumInvestment) payload.minimumInvestment = parseFloat(formData.minimumInvestment);
      }
      
      if (isEdit) {
        await assetService.update(id, payload);
      } else {
        await assetService.create(payload);
      }
      
      // Navigate back to appropriate page
      if (portfolioIdParam) {
        navigate(`/portfolios/${portfolioIdParam}`);
      } else if (formData.portfolioId) {
        navigate(`/portfolios/${formData.portfolioId}`);
      } else {
        navigate('/assets');
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (portfolioIdParam) {
      navigate(`/portfolios/${portfolioIdParam}`);
    } else {
      navigate(-1);
    }
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
        <IconButton onClick={handleCancel}>
          <BackIcon />
        </IconButton>
        <Typography variant="h4" fontWeight="bold">
          {isEdit ? 'Edit Asset' : 'Create New Asset'}
        </Typography>
        {portfolioIdParam && selectedPortfolio && (
          <Typography variant="body2" color="text.secondary" sx={{ ml: 'auto' }}>
            For Portfolio: <strong>{selectedPortfolio.name}</strong>
          </Typography>
        )}
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Card>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <Grid container spacing={3}>
              {/* Common Fields */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom color="primary">
                  Basic Information
                </Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Asset Name"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="e.g., Apple Inc., Bitcoin"
                  disabled={saving}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Symbol"
                  name="symbol"
                  value={formData.symbol}
                  onChange={handleChange}
                  placeholder="e.g., AAPL, BTC"
                  disabled={saving}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  select
                  label="Asset Type"
                  name="assetType"
                  value={formData.assetType}
                  onChange={handleChange}
                  disabled={saving || isEdit}
                >
                  {ASSET_TYPES.map((type) => (
                    <MenuItem key={type} value={type}>
                      {type}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth required disabled={saving}>
                  <InputLabel>Portfolio</InputLabel>
                  <Select
                    label="Portfolio"
                    name="portfolioId"
                    value={formData.portfolioId}
                    onChange={handleChange}
                    disabled={portfolioIdParam && !isEdit} // Disable if coming from portfolio detail in create mode
                  >
                    {portfolios.map((portfolio) => (
                      <MenuItem key={portfolio.id} value={portfolio.id}>
                        {portfolio.name} {!portfolio.active && '(Inactive)'}
                      </MenuItem>
                    ))}
                  </Select>
                  {portfolioIdParam && !isEdit && (
                    <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
                      Asset will be added to the current portfolio
                    </Typography>
                  )}
                </FormControl>
              </Grid>
              
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  required
                  type="number"
                  label="Quantity"
                  name="quantity"
                  value={formData.quantity}
                  onChange={handleChange}
                  inputProps={{ step: "0.0001", min: "0" }}
                  disabled={saving}
                />
              </Grid>
              
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  required
                  type="number"
                  label="Purchase Price"
                  name="purchasePrice"
                  value={formData.purchasePrice}
                  onChange={handleChange}
                  inputProps={{ step: "0.01", min: "0" }}
                  disabled={saving}
                />
              </Grid>
              
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  required
                  type="number"
                  label="Current Price"
                  name="currentPrice"
                  value={formData.currentPrice}
                  onChange={handleChange}
                  inputProps={{ step: "0.01", min: "0" }}
                  disabled={saving}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  type="date"
                  label="Purchase Date"
                  name="purchaseDate"
                  value={formData.purchaseDate}
                  onChange={handleChange}
                  InputLabelProps={{ shrink: true }}
                  disabled={saving}
                />
              </Grid>
              
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={2}
                  label="Notes"
                  name="notes"
                  value={formData.notes}
                  onChange={handleChange}
                  disabled={saving}
                />
              </Grid>

              {/* Type-Specific Fields */}
              <Grid item xs={12}>
                <Divider sx={{ my: 2 }} />
                <Typography variant="h6" gutterBottom color="primary">
                  {formData.assetType} Specific Details
                </Typography>
              </Grid>

              {/* STOCK Fields */}
              {formData.assetType === 'STOCK' && (
                <>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Exchange"
                      name="exchange"
                      value={formData.exchange}
                      onChange={handleChange}
                      placeholder="e.g., NASDAQ, NYSE"
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Sector"
                      name="sector"
                      value={formData.sector}
                      onChange={handleChange}
                      placeholder="e.g., Technology, Finance"
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      type="number"
                      label="Dividend Yield (%)"
                      name="dividendYield"
                      value={formData.dividendYield}
                      onChange={handleChange}
                      inputProps={{ step: "0.01", min: "0" }}
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={formData.fractionalAllowed}
                          onChange={handleChange}
                          name="fractionalAllowed"
                          disabled={saving}
                        />
                      }
                      label="Fractional Shares Allowed"
                    />
                  </Grid>
                </>
              )}

              {/* CRYPTO Fields */}
              {formData.assetType === 'CRYPTO' && (
                <>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Blockchain Network"
                      name="blockchainNetwork"
                      value={formData.blockchainNetwork}
                      onChange={handleChange}
                      placeholder="e.g., Bitcoin, Ethereum"
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Wallet Address"
                      name="walletAddress"
                      value={formData.walletAddress}
                      onChange={handleChange}
                      placeholder="e.g., 0x742d35Cc6..."
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={formData.stakingEnabled}
                          onChange={handleChange}
                          name="stakingEnabled"
                          disabled={saving}
                        />
                      }
                      label="Staking Enabled"
                    />
                  </Grid>
                  {formData.stakingEnabled && (
                    <Grid item xs={12} sm={6}>
                      <TextField
                        fullWidth
                        type="number"
                        label="Staking APY (%)"
                        name="stakingApy"
                        value={formData.stakingApy}
                        onChange={handleChange}
                        inputProps={{ step: "0.1", min: "0" }}
                        disabled={saving}
                      />
                    </Grid>
                  )}
                </>
              )}

              {/* GOLD Fields */}
              {formData.assetType === 'GOLD' && (
                <>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Purity"
                      name="purity"
                      value={formData.purity}
                      onChange={handleChange}
                      placeholder="e.g., 24K, 22K, 18K"
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      type="number"
                      label="Weight (grams)"
                      name="weightInGrams"
                      value={formData.weightInGrams}
                      onChange={handleChange}
                      inputProps={{ step: "0.01", min: "0" }}
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Storage Location"
                      name="storageLocation"
                      value={formData.storageLocation}
                      onChange={handleChange}
                      placeholder="e.g., Bank Vault, Home Safe"
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Certificate Number"
                      name="certificateNumber"
                      value={formData.certificateNumber}
                      onChange={handleChange}
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={formData.isPhysical}
                          onChange={handleChange}
                          name="isPhysical"
                          disabled={saving}
                        />
                      }
                      label="Physical Gold"
                    />
                  </Grid>
                </>
              )}

              {/* MUTUAL_FUND Fields */}
              {formData.assetType === 'MUTUAL_FUND' && (
                <>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Fund Manager"
                      name="fundManager"
                      value={formData.fundManager}
                      onChange={handleChange}
                      placeholder="e.g., Vanguard, Fidelity"
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      type="number"
                      label="Expense Ratio (%)"
                      name="expenseRatio"
                      value={formData.expenseRatio}
                      onChange={handleChange}
                      inputProps={{ step: "0.01", min: "0" }}
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      type="number"
                      label="NAV Price"
                      name="navPrice"
                      value={formData.navPrice}
                      onChange={handleChange}
                      inputProps={{ step: "0.01", min: "0" }}
                      disabled={saving}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      select
                      label="Risk Level"
                      name="riskLevel"
                      value={formData.riskLevel}
                      onChange={handleChange}
                      disabled={saving}
                    >
                      <MenuItem value="Low">Low</MenuItem>
                      <MenuItem value="Moderate">Moderate</MenuItem>
                      <MenuItem value="High">High</MenuItem>
                    </TextField>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      type="number"
                      label="Minimum Investment"
                      name="minimumInvestment"
                      value={formData.minimumInvestment}
                      onChange={handleChange}
                      inputProps={{ step: "100", min: "0" }}
                      disabled={saving}
                    />
                  </Grid>
                </>
              )}

              {/* Action Buttons */}
              <Grid item xs={12}>
                <Box display="flex" gap={2} justifyContent="flex-end" sx={{ mt: 2 }}>
                  <Button
                    variant="outlined"
                    onClick={handleCancel}
                    disabled={saving}
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    variant="contained"
                    startIcon={saving ? <CircularProgress size={20} /> : <SaveIcon />}
                    disabled={saving}
                  >
                    {saving ? 'Saving...' : (isEdit ? 'Update Asset' : 'Create Asset')}
                  </Button>
                </Box>
              </Grid>
            </Grid>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}