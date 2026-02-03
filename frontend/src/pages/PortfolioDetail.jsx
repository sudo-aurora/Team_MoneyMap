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
  Divider,
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Refresh as RefreshIcon,
  Add as AddIcon,
  TrendingUp as TrendingUpIcon,
  ShowChart as ChartIcon,
} from '@mui/icons-material';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';
import { portfolioService } from '../services/portfolioService';
import { assetService } from '../services/assetService';

const COLORS = ['#1976d2', '#388e3c', '#f57c00', '#d32f2f', '#9c27b0', '#00acc1'];

export default function PortfolioDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [portfolio, setPortfolio] = useState(null);
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [chartData, setChartData] = useState([]);
  const [typeDistribution, setTypeDistribution] = useState([]);

  useEffect(() => {
    loadPortfolioData();
    
    // Auto-refresh every 30 seconds
    const interval = setInterval(() => {
      loadPortfolioData();
    }, 30000);
    
    return () => clearInterval(interval);
  }, [id]);

  const loadPortfolioData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const portfolioData = await portfolioService.getById(id);
      setPortfolio(portfolioData);
      
      const assetsData = await assetService.getByPortfolioId(id);
      const assetsList = Array.isArray(assetsData) ? assetsData : [];
      setAssets(assetsList);
      
      // Calculate type distribution
      const distribution = {};
      assetsList.forEach(asset => {
        const type = asset.assetType;
        if (!distribution[type]) {
          distribution[type] = { name: type, value: 0, count: 0 };
        }
        distribution[type].value += asset.currentValue || 0;
        distribution[type].count += 1;
      });
      setTypeDistribution(Object.values(distribution));
      
      // Prepare bar chart data
      const chartDataArray = assetsList.map(asset => ({
        name: asset.symbol,
        value: asset.currentValue || 0,
        profit: ((asset.currentValue || 0) - (asset.purchasePrice * asset.quantity)) || 0,
      })).sort((a, b) => b.value - a.value).slice(0, 10); // Top 10 assets
      setChartData(chartDataArray);
      
    } catch (err) {
      console.error('Error loading portfolio:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleRecalculate = async () => {
    try {
      setLoading(true);
      await portfolioService.recalculate(id);
      await loadPortfolioData();
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/portfolios')} sx={{ mb: 2 }}>
          Back to Portfolios
        </Button>
        <Alert severity="error">Error loading portfolio: {error}</Alert>
      </Box>
    );
  }

  if (!portfolio) {
    return (
      <Box>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/portfolios')} sx={{ mb: 2 }}>
          Back to Portfolios
        </Button>
        <Alert severity="warning">Portfolio not found</Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box display="flex" alignItems="center" gap={2}>
          <IconButton onClick={() => navigate('/portfolios')}>
            <BackIcon />
          </IconButton>
          <div>
            <Typography variant="h4" fontWeight="bold">
              {portfolio.name}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Portfolio ID: {portfolio.id}
            </Typography>
          </div>
        </Box>
        <Box display="flex" gap={1}>
          <Chip label={portfolio.active ? 'Active' : 'Inactive'} color={portfolio.active ? 'success' : 'default'} />
          <Button startIcon={<RefreshIcon />} onClick={handleRecalculate} variant="outlined">
            Recalculate
          </Button>
        </Box>
      </Box>

      <Grid container spacing={3}>
        {/* Stats Cards */}
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="body2" sx={{ opacity: 0.9 }} gutterBottom>
                    Total Value
                  </Typography>
                  <Typography variant="h4" fontWeight="bold">
                    ${portfolio.totalValue?.toLocaleString() || '0.00'}
                  </Typography>
                </Box>
                <TrendingUpIcon sx={{ fontSize: 48, opacity: 0.3 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', color: 'white' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="body2" sx={{ opacity: 0.9 }} gutterBottom>
                    Total Assets
                  </Typography>
                  <Typography variant="h4" fontWeight="bold">
                    {assets.length}
                  </Typography>
                </Box>
                <ChartIcon sx={{ fontSize: 48, opacity: 0.3 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', color: 'white' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="body2" sx={{ opacity: 0.9 }} gutterBottom>
                    Asset Types
                  </Typography>
                  <Typography variant="h4" fontWeight="bold">
                    {typeDistribution.length}
                  </Typography>
                </Box>
                <ChartIcon sx={{ fontSize: 48, opacity: 0.3 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', color: 'white' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="body2" sx={{ opacity: 0.9 }} gutterBottom>
                    Total Profit
                  </Typography>
                  <Typography variant="h4" fontWeight="bold">
                    ${assets.reduce((sum, a) => sum + (((a.currentValue || 0) - (a.purchasePrice * a.quantity)) || 0), 0).toFixed(2)}
                  </Typography>
                </Box>
                <TrendingUpIcon sx={{ fontSize: 48, opacity: 0.3 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Charts Section */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom fontWeight="bold">
                Asset Distribution by Type
              </Typography>
              <Divider sx={{ mb: 2 }} />
              {typeDistribution.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={typeDistribution}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {typeDistribution.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(value) => `$${value.toLocaleString()}`} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <Box display="flex" justifyContent="center" alignItems="center" height={300}>
                  <Typography color="text.secondary">No data available</Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom fontWeight="bold">
                Top 10 Assets by Value
              </Typography>
              <Divider sx={{ mb: 2 }} />
              {chartData.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip formatter={(value) => `$${value.toLocaleString()}`} />
                    <Bar dataKey="value" fill="#1976d2" />
                  </BarChart>
                </ResponsiveContainer>
              ) : (
                <Box display="flex" justifyContent="center" alignItems="center" height={300}>
                  <Typography color="text.secondary">No data available</Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h6" fontWeight="bold">
                  Assets
                </Typography>
                <Button
                  startIcon={<AddIcon />}
                  variant="contained"
                  size="small"
                  onClick={() => navigate(`/assets/new?portfolioId=${id}`)}
                >
                  Add Asset
                </Button>
              </Box>

              {assets.length === 0 ? (
                <Box display="flex" justifyContent="center" py={4}>
                  <Typography color="text.secondary">
                    No assets in this portfolio. Click "Add Asset" to create one.
                  </Typography>
                </Box>
              ) : (
                <TableContainer>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell><strong>Asset</strong></TableCell>
                        <TableCell><strong>Type</strong></TableCell>
                        <TableCell align="right"><strong>Quantity</strong></TableCell>
                        <TableCell align="right"><strong>Current Price</strong></TableCell>
                        <TableCell align="right"><strong>Value</strong></TableCell>
                        <TableCell align="right"><strong>P/L %</strong></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {assets.map((asset) => (
                        <TableRow 
                          key={asset.id} 
                          hover 
                          sx={{ cursor: 'pointer' }}
                          onClick={() => navigate(`/assets/${asset.id}`)}
                        >
                          <TableCell>
                            <Typography variant="body2" fontWeight="medium">
                              {asset.name}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {asset.symbol}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Chip label={asset.assetType} size="small" color="primary" variant="outlined" />
                          </TableCell>
                          <TableCell align="right">{asset.quantity?.toLocaleString()}</TableCell>
                          <TableCell align="right">${asset.currentPrice?.toLocaleString()}</TableCell>
                          <TableCell align="right">
                            <Typography fontWeight="medium">
                              ${asset.currentValue?.toLocaleString()}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">
                            <Chip
                              label={`${asset.profitLossPercentage?.toFixed(2) || 0}%`}
                              size="small"
                              color={(asset.profitLossPercentage || 0) >= 0 ? 'success' : 'error'}
                            />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
