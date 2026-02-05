import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  Grid,
  Chip,
  CircularProgress,
  Alert,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Divider,
} from '@mui/material';

import {
  ArrowBack as BackIcon,
  Refresh as RefreshIcon,
  Add as AddIcon,
  TrendingUp as TrendingUpIcon,
  ShowChart as ChartIcon,
  Sell as SellIcon,
} from '@mui/icons-material';

import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Legend,
  Tooltip,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
} from 'recharts';

import { portfolioService } from '../services/portfolioService';
import { assetService } from '../services/assetService';
import { tradingService } from '../services/tradingService';
import { 
  AccountBalanceWallet as WalletIcon 
} from '@mui/icons-material';

const COLORS = [
  '#1976d2', // Crypto - Blue
  '#388e3c', // Mutual Fund - Green
  '#f57c00', // Gold - Orange
  '#d32f2f', // Stock - Red
  '#9c27b0', // Purple
  '#00acc1', // Cyan
  '#ff9800', // Amber
  '#795548', // Brown
];

export default function PortfolioDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [portfolio, setPortfolio] = useState(null);
  const [assets, setAssets] = useState([]);
  const [chartData, setChartData] = useState([]);
  const [typeDistribution, setTypeDistribution] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Sell dialog state
  const [sellDialogOpen, setSellDialogOpen] = useState(false);
  const [selectedAsset, setSelectedAsset] = useState(null);
  const [sellQuantity, setSellQuantity] = useState('');
  const [sellPrice, setSellPrice] = useState('');
  const [selling, setSelling] = useState(false);
  const [sellError, setSellError] = useState(null);
  const [sellSuccess, setSellSuccess] = useState(null);

  // Wallet balance state
  const [walletBalance, setWalletBalance] = useState(0);
  const [loadingWallet, setLoadingWallet] = useState(false);

  // Current prices from Yahoo Finance
  const [currentPrices, setCurrentPrices] = useState({});

  useEffect(() => {
    loadPortfolioData();
  }, [id]);

  useEffect(() => {
    if (portfolio?.clientId) {
      loadWalletBalance(portfolio.clientId);
    }
  }, [portfolio?.clientId]);

  useEffect(() => {
    if (assets.length > 0) {
      fetchCurrentPrices();
    }
  }, [assets]);

  const loadPortfolioData = async () => {
    try {
      setLoading(true);
      setError(null);

      const portfolioData = await portfolioService.getById(id);
      setPortfolio(portfolioData);

      const assetsData = await assetService.getByPortfolioId(id);
      const assetsList = Array.isArray(assetsData) ? assetsData : [];
      setAssets(assetsList);

      // Calculate distribution
      const distribution = {};
      assetsList.forEach(asset => {
        const type = asset.assetType || 'OTHER';
        if (!distribution[type]) {
          distribution[type] = { name: type, value: 0, count: 0 };
        }
        distribution[type].value += asset.currentValue || 0;
        distribution[type].count += 1;
      });

      setTypeDistribution(Object.values(distribution));

      // Prepare chart data
      const chartArray = assetsList
        .map(asset => ({
          name: asset.symbol || asset.name,
          value: asset.currentValue || 0,
          assetType: asset.assetType,
        }))
        .sort((a, b) => b.value - a.value)
        .slice(0, 8);

      setChartData(chartArray);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadWalletBalance = async (clientIdToLoad) => {
    try {
      setLoadingWallet(true);
      console.log('Loading wallet balance for client:', clientIdToLoad);
      
      if (!clientIdToLoad) {
        console.log('No clientId provided, skipping wallet balance load');
        return;
      }
      
      const balance = await tradingService.getWalletBalance(clientIdToLoad);
      console.log('Raw balance response:', balance);
      
      // Handle BigDecimal object or string response
      let numericBalance = 0;
      if (typeof balance === 'object' && balance !== null) {
        numericBalance = parseFloat(balance.value || balance.toString() || '0');
      } else if (typeof balance === 'string') {
        numericBalance = parseFloat(balance);
      } else if (typeof balance === 'number') {
        numericBalance = balance;
      }
      
      const finalBalance = numericBalance || 0;
      console.log('Final wallet balance:', finalBalance);
      setWalletBalance(finalBalance);
    } catch (err) {
      console.error('Error loading wallet balance:', err);
      setWalletBalance(0);
    } finally {
      setLoadingWallet(false);
    }
  };

  const fetchCurrentPrices = async () => {
    const prices = {};
    
    for (const asset of assets) {
      try {
        const response = await fetch(`http://localhost:5000/stock/${asset.symbol}?period=1mo`);
        if (response.ok) {
          const data = await response.json();
          prices[asset.symbol] = data.latestPrice;
        }
      } catch (err) {
        console.error(`Error fetching price for ${asset.symbol}:`, err);
        prices[asset.symbol] = asset.currentPrice; // Fallback to DB price
      }
    }
    
    setCurrentPrices(prices);
  };

  if (loading) {
    return (
      <Box height="60vh" display="flex" justifyContent="center" alignItems="center">
        <CircularProgress />
      </Box>
    );
  }

  if (error) return <Alert severity="error">{error}</Alert>;
  if (!portfolio) return <Alert severity="warning">Portfolio not found</Alert>;

  const statCards = [
    {
      title: 'Total Value',
      value: `$${portfolio.totalValue?.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) || '0.00'}`,
      gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      icon: <TrendingUpIcon sx={{ fontSize: 40, opacity: 0.25 }} />,
    },
    {
      title: 'Total Assets',
      value: assets.length,
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      icon: <ChartIcon sx={{ fontSize: 40, opacity: 0.25 }} />,
    },
    {
      title: 'Asset Types',
      value: typeDistribution.length,
      gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      icon: <ChartIcon sx={{ fontSize: 40, opacity: 0.25 }} />,
    },
    {
      title: 'Total Profit',
      value: `$${assets.reduce(
        (sum, a) => sum + (((currentPrices[a.symbol] || a.currentPrice) - a.purchasePrice) * a.quantity),
        0
      ).toFixed(2)}`,
      gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      icon: <TrendingUpIcon sx={{ fontSize: 40, opacity: 0.25 }} />,
    },
    {
      title: 'Wallet Balance',
      value: `$${walletBalance.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      gradient: 'linear-gradient(135deg, #1976d2 0%, #2196f3 100%)',
      icon: <WalletIcon sx={{ fontSize: 40, opacity: 0.25 }} />,
    },
  ];

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  const formatCurrencyDetailed = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  };

  // Calculate portfolio stats
  const totalPortfolioValue = portfolio.totalValue || 
    assets.reduce((sum, asset) => sum + (asset.currentValue || 0), 0);
  
  const largestHolding = chartData.length > 0 ? chartData[0] : null;
  const totalShownValue = chartData.reduce((sum, item) => sum + item.value, 0);

  // Sell handler functions
  const handleSellClick = (asset) => {
    setSelectedAsset(asset);
    setSellQuantity('');
    setSellPrice(currentPrices[asset.symbol]?.toString() || asset.currentPrice?.toString() || '');
    setSellError(null);
    setSellSuccess(null);
    setSellDialogOpen(true);
  };

  const handleSellClose = () => {
    setSellDialogOpen(false);
    setSelectedAsset(null);
    setSellQuantity('');
    setSellPrice('');
    setSellError(null);
    setSellSuccess(null);
  };

  const calculateSellProceeds = () => {
    const quantity = parseFloat(sellQuantity) || 0;
    const price = parseFloat(sellPrice) || 0;
    return quantity * price;
  };

  const canSellQuantity = () => {
    if (!selectedAsset || !sellQuantity) return false;
    const quantity = parseFloat(sellQuantity) || 0;
    return quantity > 0 && quantity <= selectedAsset.quantity;
  };

  const handleSellSubmit = async () => {
    try {
      setSelling(true);
      setSellError(null);
      setSellSuccess(null);
      
      if (!selectedAsset || !sellQuantity || !sellPrice) {
        setSellError('Please fill all required fields');
        return;
      }
      
      const quantity = parseFloat(sellQuantity);
      const price = parseFloat(sellPrice);
      
      if (quantity <= 0 || price <= 0) {
        setSellError('Quantity and price must be positive');
        return;
      }
      
      if (quantity > selectedAsset.quantity) {
        setSellError('Cannot sell more than owned quantity');
        return;
      }
      
      console.log('Sell Asset Request:', {
        clientId: portfolio.clientId,
        assetId: selectedAsset.id,
        quantity,
        price
      });
      
      await tradingService.sellAsset(portfolio.clientId, selectedAsset.id, quantity, price);
      
      setSellSuccess('Asset sold successfully!');
      
      // Reload portfolio data after a short delay
      setTimeout(() => {
        loadPortfolioData();
        loadWalletBalance(portfolio.clientId);
        handleSellClose();
      }, 1500);
      
    } catch (err) {
      console.error('Sell error:', err);
      setSellError(err.message || 'Failed to sell asset');
    } finally {
      setSelling(false);
    }
  };

  return (
    <Box sx={{ width: '100%', p: { xs: 2, md: 3 } }}>

      {/* HEADER */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Box display="flex" alignItems="center" gap={2}>
          <IconButton onClick={() => navigate('/portfolios')}>
            <BackIcon />
          </IconButton>

          <Box>
            <Typography variant="h4" fontWeight="bold">
              {portfolio.name}
            </Typography>
            <Typography variant="body1" color="text.secondary">
              {portfolio.description || 'Diversified investment portfolio'}
            </Typography>
          </Box>
        </Box>

        <Chip
          label={portfolio.active ? 'Active' : 'Inactive'}
          color={portfolio.active ? 'success' : 'default'}
          sx={{ fontWeight: 'medium' }}
        />
      </Box>

      {/* STAT CARDS */}
      <Grid container spacing={3} mb={4}>
        {statCards.map((card, i) => (
          <Grid item xs={12} sm={6} lg={2.4} key={i}>
            <Card
              sx={{
                height: '100%',
                background: card.gradient,
                color: 'white',
                borderRadius: 2,
                boxShadow: 3,
              }}
            >
              <CardContent sx={{ p: 2 }}>
                <Box display="flex" justifyContent="space-between" alignItems="flex-start">
                  <Box>
                    <Typography variant="body2" sx={{ opacity: 0.9, mb: 1 }}>
                      {card.title}
                    </Typography>
                    <Typography variant="h5" fontWeight="bold">
                      {card.value}
                    </Typography>
                  </Box>
                  {card.icon}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* CHARTS SECTION - FIXED ALIGNMENT */}
      <Grid container spacing={3} mb={4}>
        {/* ASSET DISTRIBUTION PIE CHART */}
        <Grid item xs={12} lg={6}>
          <Card sx={{ height: '100%', borderRadius: 2, boxShadow: 2 }}>
            <CardContent sx={{ p: 3, height: '100%' }}>
              <Typography variant="h6" fontWeight="bold" mb={3}>
                Asset Distribution by Type
              </Typography>
              
              <Box sx={{ 
                display: 'flex', 
                flexDirection: { xs: 'column', md: 'row' },
                alignItems: 'center',
                justifyContent: 'center',
                gap: 4,
                height: 'calc(100% - 48px)' // Subtract header height
              }}>
                {/* Pie Chart */}
                <Box sx={{ 
                  flex: 1,
                  width: '100%',
                  height: 280,
                  minWidth: 280
                }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={typeDistribution}
                        dataKey="value"
                        nameKey="name"
                        cx="50%"
                        cy="50%"
                        outerRadius={100}
                        innerRadius={60}
                        paddingAngle={3}
                        label={({ name, percent }) => `${name}\n${(percent * 100).toFixed(1)}%`}
                        labelLine={false}
                      >
                        {typeDistribution.map((entry, index) => (
                          <Cell 
                            key={`cell-${index}`} 
                            fill={COLORS[index % COLORS.length]} 
                            strokeWidth={2}
                          />
                        ))}
                      </Pie>
                      <Tooltip 
                        formatter={(value) => [formatCurrencyDetailed(value), 'Value']}
                        contentStyle={{ borderRadius: 8, border: 'none', boxShadow: 3 }}
                      />
                      <Legend 
                        verticalAlign="bottom"
                        height={36}
                        formatter={(value, entry) => (
                          <span style={{ color: '#333', fontSize: '14px' }}>
                            {value}: {formatCurrency(entry.payload.value)}
                          </span>
                        )}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                </Box>

                {/* Distribution Details */}
                <Box sx={{ 
                  flex: 1,
                  width: '100%',
                  maxWidth: 300
                }}>
                  <Grid container spacing={2}>
                    {typeDistribution.map((item, index) => (
                      <Grid item xs={12} key={index}>
                        <Paper 
                          variant="outlined" 
                          sx={{ 
                            p: 2,
                            borderRadius: 2,
                            borderLeft: `4px solid ${COLORS[index % COLORS.length]}`,
                            backgroundColor: 'background.default'
                          }}
                        >
                          <Box display="flex" justifyContent="space-between" alignItems="center">
                            <Box>
                              <Typography variant="body1" fontWeight="medium">
                                {item.name}
                              </Typography>
                              <Typography variant="caption" color="text.secondary">
                                {item.count} asset{item.count !== 1 ? 's' : ''}
                              </Typography>
                            </Box>
                            <Typography variant="body1" fontWeight="bold">
                              {formatCurrencyDetailed(item.value)}
                            </Typography>
                          </Box>
                        </Paper>
                      </Grid>
                    ))}
                  </Grid>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* TOP ASSETS BAR CHART */}
        <Grid item xs={12} lg={6}>
          <Card sx={{ height: '100%', borderRadius: 2, boxShadow: 2 }}>
            <CardContent sx={{ p: 3, height: '100%' }}>
              <Typography variant="h6" fontWeight="bold" mb={3}>
                Top Assets by Value
              </Typography>
              
              <Box sx={{ 
                width: '100%', 
                height: 280,
                mb: 3
              }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart
                    data={chartData}
                    margin={{
                      top: 20,
                      right: 30,
                      left: 20,
                      bottom: 20,
                    }}
                    barSize={28}
                  >
                    <CartesianGrid 
                      strokeDasharray="3 3" 
                      stroke="#f0f0f0"
                      vertical={false}
                    />
                    <XAxis 
                      dataKey="name"
                      axisLine={false}
                      tickLine={false}
                      tick={{ fontSize: 13, fontWeight: 500 }}
                    />
                    <YAxis 
                      axisLine={false}
                      tickLine={false}
                      tickFormatter={(value) => `$${(value / 1000).toFixed(0)}K`}
                      tick={{ fontSize: 12 }}
                    />
                    <Tooltip 
                      formatter={(value) => [formatCurrencyDetailed(value), 'Value']}
                      contentStyle={{ borderRadius: 8, border: 'none', boxShadow: 3 }}
                      labelStyle={{ fontWeight: 'bold', marginBottom: 8 }}
                    />
                    <Bar 
                      dataKey="value" 
                      radius={[4, 4, 0, 0]}
                      animationDuration={1500}
                    >
                      {chartData.map((entry, index) => (
                        <Cell 
                          key={`bar-cell-${index}`} 
                          fill={COLORS[index % COLORS.length]} 
                          opacity={0.85}
                        />
                      ))}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </Box>

              {/* Stats Summary - Aligned Boxes */}
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Paper 
                    variant="outlined" 
                    sx={{ 
                      p: 2.5,
                      borderRadius: 2,
                      height: '100%',
                      backgroundColor: 'background.default',
                      borderColor: 'divider'
                    }}
                  >
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Largest Holding
                    </Typography>
                    {largestHolding ? (
                      <>
                        <Typography variant="h6" fontWeight="bold" color="primary.main" gutterBottom>
                          {largestHolding.name}
                        </Typography>
                        <Typography variant="body1" fontWeight="medium">
                          {formatCurrencyDetailed(largestHolding.value)}
                        </Typography>
                      </>
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        No assets
                      </Typography>
                    )}
                  </Paper>
                </Grid>

                <Grid item xs={6}>
                  <Paper 
                    variant="outlined" 
                    sx={{ 
                      p: 2.5,
                      borderRadius: 2,
                      height: '100%',
                      backgroundColor: 'background.default',
                      borderColor: 'divider'
                    }}
                  >
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Total Portfolio Value
                    </Typography>
                    <Typography variant="h6" fontWeight="bold" color="primary.main" gutterBottom>
                      {formatCurrencyDetailed(totalPortfolioValue)}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {assets.length} asset{assets.length !== 1 ? 's' : ''}
                    </Typography>
                  </Paper>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* ASSETS TABLE */}
      <Card sx={{ borderRadius: 2, boxShadow: 2 }}>
        <CardContent sx={{ p: 3 }}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
            <Box>
              <Typography variant="h6" fontWeight="bold">
                Asset Holdings
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Detailed view of all assets in this portfolio
              </Typography>
            </Box>

            <Box display="flex" gap={1}>
              <Button
                variant="outlined"
                startIcon={<RefreshIcon />}
                onClick={loadPortfolioData}
                size="small"
              >
                Refresh
              </Button>
              <Button
                variant="outlined"
                startIcon={loadingWallet ? <CircularProgress size={16} /> : <WalletIcon />}
                onClick={() => loadWalletBalance(portfolio.clientId)}
                size="small"
                disabled={loadingWallet || !portfolio?.clientId}
              >
                {loadingWallet ? 'Loading...' : 'Refresh Wallet'}
              </Button>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => navigate(`/buy-asset?portfolioId=${id}&clientId=${portfolio.clientId}`)}
                size="small"
              >
                Buy Asset
              </Button>
            </Box>

          </Box>

          <TableContainer component={Paper} variant="outlined" sx={{ borderRadius: 2 }}>
            <Table>
              <TableHead sx={{ backgroundColor: 'action.hover' }}>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold', py: 2, pl: 3 }}>Asset</TableCell>
                  <TableCell sx={{ fontWeight: 'bold', py: 2 }}>Type</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold', py: 2 }}>Quantity</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold', py: 2 }}>Purchase Price</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold', py: 2 }}>Current Price</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold', py: 2 }}>Current Value</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold', py: 2 }}>P/L %</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold', py: 2, pr: 3 }}>Actions</TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {assets.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={8} align="center" sx={{ py: 6 }}>
                      <Box sx={{ textAlign: 'center' }}>
                        <Typography variant="body1" color="text.secondary" gutterBottom>
                          No assets found in this portfolio
                        </Typography>
                        <Button
                          variant="contained"
                          startIcon={<AddIcon />}
                          onClick={() => navigate(`/buy-asset?portfolioId=${id}&clientId=${portfolio.clientId}`)}
                          size="small"
                          sx={{ mt: 1 }}
                        >
                          Buy Your First Asset
                        </Button>
                      </Box>
                    </TableCell>
                  </TableRow>
                ) : (
                  assets.map(asset => {
                    const currentPrice = currentPrices[asset.symbol] || asset.currentPrice;
                    const profitLoss = ((currentPrice - asset.purchasePrice) / asset.purchasePrice) * 100;
                    const isPositive = profitLoss >= 0;
                    
                    return (
                      <TableRow
                        key={asset.id}
                        hover
                        sx={{ 
                          cursor: 'pointer',
                          '&:last-child td, &:last-child th': { border: 0 }
                        }}
                        onClick={() => navigate(`/assets/${asset.id}`)}
                      >
                        <TableCell sx={{ pl: 3 }}>
                          <Box>
                            <Typography fontWeight="medium">
                              {asset.name}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {asset.symbol || '—'}
                            </Typography>
                          </Box>
                        </TableCell>

                        <TableCell>
                          <Chip 
                            label={asset.assetType} 
                            size="small" 
                            sx={{ 
                              fontWeight: 500,
                              backgroundColor: `${COLORS[asset.assetType?.length % COLORS.length]}15`,
                              color: COLORS[asset.assetType?.length % COLORS.length]
                            }}
                          />
                        </TableCell>

                        <TableCell align="right">
                          <Typography fontWeight="medium">
                            {asset.quantity?.toLocaleString()}
                          </Typography>
                        </TableCell>

                        <TableCell align="right">
                          <Typography>
                            ${asset.purchasePrice?.toLocaleString(undefined, { 
                              minimumFractionDigits: 2, 
                              maximumFractionDigits: 2 
                            })}
                          </Typography>
                        </TableCell>

                        <TableCell align="right">
                          <Typography fontWeight="medium">
                            ${currentPrices[asset.symbol]?.toLocaleString(undefined, { 
                              minimumFractionDigits: 2, 
                              maximumFractionDigits: 2 
                            }) || asset.currentPrice?.toLocaleString(undefined, { 
                              minimumFractionDigits: 2, 
                              maximumFractionDigits: 2 
                            })}
                          </Typography>
                        </TableCell>

                        <TableCell align="right">
                          <Typography variant="body1" fontWeight="bold">
                            {formatCurrencyDetailed(asset.currentValue || 0)}
                          </Typography>
                        </TableCell>

                        <TableCell align="right" sx={{ pr: 3 }}>
                          <Chip
                            label={`${isPositive ? '+' : ''}${profitLoss.toFixed(2)}%`}
                            color={isPositive ? 'success' : 'error'}
                            size="small"
                            sx={{ 
                              fontWeight: 'bold',
                              minWidth: 80
                            }}
                          />
                        </TableCell>

                        <TableCell align="center" sx={{ pr: 3 }}>
                          <Button
                            variant="outlined"
                            color="error"
                            size="small"
                            startIcon={<SellIcon />}
                            onClick={(e) => {
                              e.stopPropagation();
                              handleSellClick(asset);
                            }}
                            sx={{ minWidth: 100 }}
                          >
                            Sell
                          </Button>
                        </TableCell>
                      </TableRow>
                    );
                  })
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* SELL ASSET DIALOG */}
      <Dialog 
        open={sellDialogOpen} 
        onClose={handleSellClose}
        maxWidth="sm"
        fullWidth={false}
        PaperProps={{
          sx: { borderRadius: 3 }
        }}
      >
        <DialogTitle>
          <Typography variant="h6" fontWeight="bold">
            Sell Asset - {selectedAsset?.name}
          </Typography>
        </DialogTitle>
        
        <DialogContent>
          {sellError && (
            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setSellError(null)}>
              {sellError}
            </Alert>
          )}
          
          {sellSuccess && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {sellSuccess}
            </Alert>
          )}
          
          {selectedAsset && (
            <Box>
              <Grid container spacing={2} mb={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Owned Quantity
                  </Typography>
                  <Typography variant="h6" fontWeight="bold">
                    {selectedAsset.quantity?.toLocaleString()}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Current Price
                  </Typography>
                  <Typography variant="h6" fontWeight="bold">
                    ${currentPrices[selectedAsset.symbol]?.toLocaleString(undefined, { 
                      minimumFractionDigits: 2, 
                      maximumFractionDigits: 2 
                    }) || selectedAsset.currentPrice?.toLocaleString(undefined, { 
                      minimumFractionDigits: 2, 
                      maximumFractionDigits: 2 
                    })}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Real-time from Yahoo Finance
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    P/L on Position
                  </Typography>
                  <Typography variant="h6" fontWeight="bold" color={
                    ((currentPrices[selectedAsset.symbol] || selectedAsset.currentPrice) - selectedAsset.purchasePrice) >= 0 
                      ? 'success.main' 
                      : 'error.main'
                  }>
                    {((currentPrices[selectedAsset.symbol] || selectedAsset.currentPrice) - selectedAsset.purchasePrice) >= 0 ? '+' : ''}
                    ${(((currentPrices[selectedAsset.symbol] || selectedAsset.currentPrice) - selectedAsset.purchasePrice) * selectedAsset.quantity).toFixed(2)}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {((((currentPrices[selectedAsset.symbol] || selectedAsset.currentPrice) - selectedAsset.purchasePrice) / selectedAsset.purchasePrice) * 100).toFixed(2)}%
                  </Typography>
                </Grid>
              </Grid>
              
              <Divider sx={{ my: 2 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Quantity to Sell"
                    type="number"
                    value={sellQuantity}
                    onChange={(e) => setSellQuantity(e.target.value)}
                    placeholder="0.00"
                    inputProps={{ 
                      step: "0.01", 
                      min: "0",
                      max: selectedAsset.quantity
                    }}
                    disabled={selling}
                    error={sellQuantity && !canSellQuantity()}
                    helperText={sellQuantity && !canSellQuantity() ? 
                      "Invalid quantity" : 
                      `Maximum: ${selectedAsset.quantity}`}
                  />
                </Grid>
                
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Price per Unit"
                    type="number"
                    value={sellPrice}
                    onChange={(e) => setSellPrice(e.target.value)}
                    placeholder="0.00"
                    inputProps={{ step: "0.01", min: "0" }}
                    disabled={selling}
                  />
                </Grid>
                
                <Grid item xs={12}>
                  <Box sx={{ 
                    p: 2, 
                    backgroundColor: 'background.default', 
                    borderRadius: 2,
                    border: '1px solid',
                    borderColor: 'divider'
                  }}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Typography variant="body1">
                        <strong>Sell Proceeds:</strong>
                      </Typography>
                      <Typography variant="h6" color="primary.main">
                        {formatCurrencyDetailed(calculateSellProceeds())}
                      </Typography>
                    </Box>
                  </Box>
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        
        <DialogActions sx={{ p: 3 }}>
          <Button 
            onClick={handleSellClose} 
            disabled={selling}
          >
            Cancel
          </Button>
          <Button 
            onClick={handleSellSubmit} 
            variant="contained" 
            color="error"
            disabled={selling || !canSellQuantity() || !sellPrice}
            startIcon={selling ? <CircularProgress size={20} /> : <SellIcon />}
          >
            {selling ? 'Selling...' : 'Sell Asset'}
          </Button>
        </DialogActions>
      </Dialog>

    </Box>
  );
}