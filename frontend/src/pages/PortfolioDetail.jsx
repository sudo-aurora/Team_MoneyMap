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
} from '@mui/material';

import {
  ArrowBack as BackIcon,
  Refresh as RefreshIcon,
  Add as AddIcon,
  TrendingUp as TrendingUpIcon,
  ShowChart as ChartIcon,
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

  useEffect(() => {
    loadPortfolioData();
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
        (sum, a) => sum + ((a.currentValue || 0) - (a.purchasePrice * a.quantity)),
        0
      ).toFixed(2)}`,
      gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      icon: <TrendingUpIcon sx={{ fontSize: 40, opacity: 0.25 }} />,
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
              Portfolio ID: {portfolio.id} • {portfolio.description || 'Diversified investment portfolio'}
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
          <Grid item xs={12} sm={6} lg={3} key={i}>
            <Card
              sx={{
                height: '100%',
                background: card.gradient,
                color: 'white',
                borderRadius: 2,
                boxShadow: 3,
              }}
            >
              <CardContent sx={{ p: 3 }}>
                <Box display="flex" justifyContent="space-between" alignItems="flex-start">
                  <Box>
                    <Typography variant="body2" sx={{ opacity: 0.9, mb: 1 }}>
                      {card.title}
                    </Typography>
                    <Typography variant="h4" fontWeight="bold">
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
                  <TableCell align="right" sx={{ fontWeight: 'bold', py: 2, pr: 3 }}>P/L %</TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {assets.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={7} align="center" sx={{ py: 6 }}>
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
                    const profitLoss = asset.profitLossPercentage || 0;
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
                            ${asset.currentPrice?.toLocaleString(undefined, { 
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
                      </TableRow>
                    );
                  })
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

    </Box>
  );
}