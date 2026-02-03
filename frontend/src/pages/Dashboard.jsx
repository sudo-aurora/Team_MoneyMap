import { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  CircularProgress,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Button,
} from '@mui/material';
import {
  People as PeopleIcon,
  AccountBalance as AccountBalanceIcon,
  TrendingUp as TrendingUpIcon,
  Warning as WarningIcon,
  Assessment as AssessmentIcon,
  Refresh as RefreshIcon,
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
import { clientService } from '../services/clientService';
import { assetService } from '../services/assetService';
import { portfolioService } from '../services/portfolioService';
import { transactionService } from '../services/transactionService';
import { alertService } from '../services/alertService';

const COLORS = ['#1976d2', '#388e3c', '#f57c00', '#d32f2f', '#9c27b0', '#00bcd4'];
const DONUT_COLORS = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E2'];

const StatCard = ({ title, value, icon, color, subtitle, trend }) => (
  <Card sx={{ height: '100%', position: 'relative', overflow: 'visible' }}>
    <CardContent>
      <Box display="flex" justifyContent="space-between" alignItems="flex-start">
        <Box flex={1}>
          <Typography color="text.secondary" gutterBottom variant="body2" fontWeight={500}>
            {title}
          </Typography>
          <Typography variant="h4" fontWeight="bold" sx={{ mb: 0.5 }}>
            {value}
          </Typography>
          {subtitle && (
            <Typography variant="caption" color="text.secondary">
              {subtitle}
            </Typography>
          )}
          {trend && (
            <Box display="flex" alignItems="center" mt={1}>
              <TrendingUpIcon 
                sx={{ 
                  fontSize: 16, 
                  color: trend > 0 ? 'success.main' : 'error.main',
                  mr: 0.5 
                }} 
              />
              <Typography 
                variant="caption" 
                color={trend > 0 ? 'success.main' : 'error.main'}
                fontWeight={600}
              >
                {trend > 0 ? '+' : ''}{trend}%
              </Typography>
            </Box>
          )}
        </Box>
        <Box
          sx={{
            backgroundColor: `${color}.main`,
            borderRadius: 2,
            p: 1.5,
            display: 'flex',
            alignItems: 'center',
            boxShadow: `0 4px 12px ${color === 'primary' ? '#1976d240' : color === 'success' ? '#388e3c40' : color === 'warning' ? '#f57c0040' : '#d32f2f40'}`,
          }}
        >
          {icon}
        </Box>
      </Box>
    </CardContent>
  </Card>
);

export default function Dashboard() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState({
    totalClients: 0,
    activeClients: 0,
    totalPortfolios: 0,
    totalAssetValue: 0,
    totalAlerts: 0,
  });
  const [assetDistribution, setAssetDistribution] = useState([]);
  const [topClients, setTopClients] = useState([]);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [portfolioPerformance, setPortfolioPerformance] = useState([]);
  const [clientPortfolioDistribution, setClientPortfolioDistribution] = useState([]);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      console.log('Starting dashboard data load...');

      let activeClientCount = 0;
      let clientsData = [];
      let portfoliosData = [];
      let assetTypes = [];
      let transactionsData = [];
      let alertCount = 0;

      try {
        activeClientCount = await clientService.getActiveCount();
        console.log('Active client count:', activeClientCount);
      } catch (err) {
        console.error('Failed to get active client count:', err);
      }

      try {
        const clientsResponse = await clientService.getAll(0, 100);
        clientsData = Array.isArray(clientsResponse) ? clientsResponse : 
                     clientsResponse?.content || [];
        console.log('Clients fetched:', clientsData.length);
      } catch (err) {
        console.error('Failed to fetch clients:', err);
      }

      try {
        const portfoliosResponse = await portfolioService.getAll(0, 100);
        portfoliosData = Array.isArray(portfoliosResponse) ? portfoliosResponse :
                         portfoliosResponse?.content || [];
        console.log('Portfolios fetched:', portfoliosData.length);
      } catch (err) {
        console.error('Failed to fetch portfolios:', err);
      }

      try {
        assetTypes = await assetService.getTypes();
        console.log('Asset types:', assetTypes);
      } catch (err) {
        console.error('Failed to fetch asset types:', err);
      }

      try {
        const transResponse = await transactionService.getAll(0, 10);
        transactionsData = Array.isArray(transResponse) ? transResponse :
                          transResponse?.content || [];
        console.log('Transactions fetched:', transactionsData.length);
      } catch (err) {
        console.error('Failed to fetch transactions:', err);
      }

      try {
        if (alertService && alertService.getCountByStatus) {
          alertCount = await alertService.getCountByStatus('OPEN');
        } else if (alertService && alertService.getStatistics) {
          const alertStats = await alertService.getStatistics();
          alertCount = alertStats.openCount || 0;
        }
      } catch (err) {
        console.log('Alert service not available or failed:', err);
      }

      const distributionPromises = assetTypes.map(async (type) => {
        try {
          const value = await assetService.getTotalValueByType(type);
          return {
            name: type.replace(/_/g, ' '),
            value: parseFloat(value) || 0,
            type: type,
          };
        } catch (err) {
          console.error(`Failed to get value for ${type}:`, err);
          return { name: type.replace(/_/g, ' '), value: 0, type: type };
        }
      });
      
      const distributionData = await Promise.all(distributionPromises);
      console.log('Asset distribution:', distributionData);

      const clientPromises = clientsData.slice(0, 10).map(async (client) => {
        try {
          let totalValue = 0;
          let portfolioCount = 0;

          const clientPortfolios = await portfolioService.getByClientId(client.id);
          const portfoliosList = Array.isArray(clientPortfolios) ? clientPortfolios : [];
          portfolioCount = portfoliosList.length;

          try {
            totalValue = await portfolioService.getTotalValueByClient(client.id);
          } catch {
            totalValue = portfoliosList.reduce((sum, p) => sum + (p.totalValue || 0), 0);
          }

          return {
            ...client,
            totalValue: parseFloat(totalValue) || 0,
            portfolioCount: portfolioCount,
          };
        } catch (err) {
          console.error(`Failed to process client ${client.id}:`, err);
          return { ...client, totalValue: 0, portfolioCount: 0 };
        }
      });

      const clientsWithValues = await Promise.all(clientPromises);
      console.log('Clients with values:', clientsWithValues);

      const topClientsSorted = clientsWithValues
        .sort((a, b) => b.totalValue - a.totalValue)
        .slice(0, 5);

      const clientDistribution = clientsWithValues
        .filter(client => client.totalValue > 0)
        .sort((a, b) => b.totalValue - a.totalValue)
        .slice(0, 8)
        .map(client => ({
          name: `${client.firstName || ''} ${client.lastName || ''}`.trim() || 'Unknown',
          value: client.totalValue,
          portfolios: client.portfolioCount,
        }));

      const performanceData = portfoliosData
        .filter(p => p.totalValue > 0)
        .sort((a, b) => (b.totalValue || 0) - (a.totalValue || 0))
        .slice(0, 6)
        .map((portfolio) => ({
          name: (portfolio.name || 'Unnamed').substring(0, 12),
          value: portfolio.totalValue || 0,
        }));

      const totalAssetValue = distributionData.reduce((sum, item) => sum + item.value, 0);

      setStats({
        totalClients: clientsData.length,
        activeClients: activeClientCount || clientsData.filter(c => c.active !== false).length,
        totalPortfolios: portfoliosData.length,
        totalAssetValue: totalAssetValue,
        totalAlerts: alertCount,
      });

      setAssetDistribution(distributionData.filter(item => item.value > 0));
      setTopClients(topClientsSorted);
      setRecentTransactions(transactionsData);
      setPortfolioPerformance(performanceData);
      setClientPortfolioDistribution(clientDistribution);

      console.log('Dashboard data loaded successfully');
    } catch (err) {
      console.error('Error loading dashboard:', err);
      setError(err.message || 'Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (value) => {
    if (!value && value !== 0) return '$0';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      });
    } catch {
      return dateString;
    }
  };

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      return (
        <Box sx={{ 
          bgcolor: 'background.paper', 
          p: 1.5, 
          border: '1px solid #e0e0e0',
          borderRadius: 1,
          boxShadow: 2 
        }}>
          <Typography variant="body2" fontWeight="bold">
            {payload[0].name}
          </Typography>
          <Typography variant="body2" color="primary">
            Value: {formatCurrency(payload[0].value)}
          </Typography>
          {payload[0].payload.portfolios !== undefined && (
            <Typography variant="caption" color="text.secondary">
              Portfolios: {payload[0].payload.portfolios}
            </Typography>
          )}
        </Box>
      );
    }
    return null;
  };

  if (loading) {
    return (
      <Box display="flex" flexDirection="column" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress size={60} />
        <Typography variant="h6" color="text.secondary" sx={{ mt: 2 }}>
          Loading Dashboard...
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mb: 2 }}>
        <Typography variant="h6">Error loading dashboard</Typography>
        <Typography variant="body2">{error}</Typography>
        <Button onClick={loadDashboardData} sx={{ mt: 1 }} variant="contained" size="small">
          Retry
        </Button>
      </Alert>
    );
  }

  return (
    <Box sx={{ pb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          Portfolio Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Real-time overview of your portfolio management system
        </Typography>
      </Box>

      {/* Stats Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Total Clients"
            value={stats.totalClients}
            subtitle="Registered clients"
            icon={<PeopleIcon sx={{ color: 'white', fontSize: 28 }} />}
            color="primary"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Active Clients"
            value={stats.activeClients}
            subtitle="Currently active"
            icon={<PeopleIcon sx={{ color: 'white', fontSize: 28 }} />}
            color="success"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Portfolios"
            value={stats.totalPortfolios}
            subtitle="Total portfolios"
            icon={<AssessmentIcon sx={{ color: 'white', fontSize: 28 }} />}
            color="info"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Total AUM"
            value={formatCurrency(stats.totalAssetValue)}
            subtitle="Assets under management"
            trend={5.2}
            icon={<AccountBalanceIcon sx={{ color: 'white', fontSize: 28 }} />}
            color="secondary"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Alerts"
            value={stats.totalAlerts}
            subtitle="Pending alerts"
            icon={<WarningIcon sx={{ color: 'white', fontSize: 28 }} />}
            color="warning"
          />
        </Grid>
      </Grid>

{/* Charts Row - FULL WIDTH CARDS */}
<Grid container spacing={3} sx={{ mb: 4, width: '100%' }}>
  {/* Asset Distribution Pie Chart */}
  <Grid item xs={12} md={6} lg={4} sx={{ display: 'flex' }}>
    <Card sx={{ height: 550, width: '100%' }}>
      <CardContent sx={{ p: 4, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Typography variant="h6" gutterBottom fontWeight={600}>
          Asset Allocation
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Distribution by asset type
        </Typography>
        <Box sx={{ flex: 1, minHeight: 0 }}>
          {assetDistribution.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={assetDistribution}
                  cx="50%"
                  cy="45%"
                  labelLine={false}
                  label={({ percent }) =>
                    percent > 0.05 ? `${(percent * 100).toFixed(0)}%` : ''
                  }
                  outerRadius={110}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {assetDistribution.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => formatCurrency(value)} />
                <Legend wrapperStyle={{ bottom: 0 }} />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <Box display="flex" justifyContent="center" alignItems="center" height="100%">
              <Typography color="text.secondary">No asset data available</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  </Grid>

  {/* Client Portfolio Distribution - DONUT CHART */}
  <Grid item xs={12} md={6} lg={4} sx={{ display: 'flex' }}>
    <Card sx={{ height: 550, width: '100%' }}>
      <CardContent sx={{ p: 4, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Typography variant="h6" gutterBottom fontWeight={600}>
          Client Distribution
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Portfolio value by client
        </Typography>
        <Box sx={{ flex: 1, minHeight: 0 }}>
          {clientPortfolioDistribution.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={clientPortfolioDistribution}
                  cx="50%"
                  cy="45%"
                  labelLine={false}
                  innerRadius={65}
                  outerRadius={110}
                  fill="#8884d8"
                  paddingAngle={2}
                  dataKey="value"
                >
                  {clientPortfolioDistribution.map((entry, index) => (
                    <Cell 
                      key={`cell-${index}`} 
                      fill={DONUT_COLORS[index % DONUT_COLORS.length]} 
                    />
                  ))}
                </Pie>
                <Tooltip content={<CustomTooltip />} />
                <Legend wrapperStyle={{ bottom: 0 }} />
                <text 
                  x="50%" 
                  y="45%" 
                  textAnchor="middle" 
                  dominantBaseline="middle"
                >
                  <tspan 
                    x="50%" 
                    dy="-0.1em" 
                    style={{ fontSize: '32px', fontWeight: 'bold', fill: '#333' }}
                  >
                    {stats.totalPortfolios}
                  </tspan>
                  <tspan 
                    x="50%" 
                    dy="1.3em" 
                    style={{ fontSize: '14px', fill: '#999' }}
                  >
                    Portfolios
                  </tspan>
                </text>
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <Box display="flex" justifyContent="center" alignItems="center" height="100%">
              <Typography color="text.secondary">No client distribution data</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  </Grid>

  {/* Portfolio Performance Bar Chart */}
  <Grid item xs={12} md={12} lg={4} sx={{ display: 'flex' }}>
    <Card sx={{ height: 550, width: '100%' }}>
      <CardContent sx={{ p: 4, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Typography variant="h6" gutterBottom fontWeight={600}>
          Top Portfolios
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Portfolio values comparison
        </Typography>
        <Box sx={{ flex: 1, minHeight: 0 }}>
          {portfolioPerformance.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <BarChart 
                data={portfolioPerformance}
                margin={{ top: 20, right: 20, left: 10, bottom: 60 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="name" 
                  angle={-45} 
                  textAnchor="end" 
                  interval={0}
                  tick={{ fontSize: 12 }}
                />
                <YAxis tick={{ fontSize: 12 }} />
                <Tooltip formatter={(value) => formatCurrency(value)} />
                <Bar dataKey="value" fill="#1976d2" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <Box display="flex" justifyContent="center" alignItems="center" height="100%">
              <Typography color="text.secondary">No portfolio data available</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  </Grid>
</Grid>

      {/* Tables Row */}
      <Grid container spacing={3}>
        {/* Top Clients Table */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom fontWeight={600}>
                Top Clients by AUM
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Clients with highest portfolio values
              </Typography>
              {topClients.length > 0 ? (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell><strong>Client</strong></TableCell>
                        <TableCell><strong>Email</strong></TableCell>
                        <TableCell align="right"><strong>Portfolio Value</strong></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {topClients.map((client, index) => (
                        <TableRow key={client.id} hover>
                          <TableCell>
                            <Box display="flex" alignItems="center">
                              <Box
                                sx={{
                                  width: 32,
                                  height: 32,
                                  borderRadius: '50%',
                                  bgcolor: COLORS[index % COLORS.length],
                                  display: 'flex',
                                  alignItems: 'center',
                                  justifyContent: 'center',
                                  mr: 1.5,
                                  color: 'white',
                                  fontWeight: 'bold',
                                  fontSize: 14,
                                }}
                              >
                                {(client.firstName || 'U')[0]}{(client.lastName || 'N')[0]}
                              </Box>
                              <Typography variant="body2" fontWeight={500}>
                                {client.firstName || 'Unknown'} {client.lastName || ''}
                              </Typography>
                            </Box>
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2" color="text.secondary">
                              {client.email || 'No email'}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">
                            <Typography variant="body2" fontWeight={600} color="success.main">
                              {formatCurrency(client.totalValue)}
                            </Typography>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <Box display="flex" justifyContent="center" alignItems="center" height={200}>
                  <Typography color="text.secondary">No client data available</Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Transactions Table */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom fontWeight={600}>
                Recent Transactions
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Latest portfolio activities
              </Typography>
              {recentTransactions.length > 0 ? (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell><strong>Type</strong></TableCell>
                        <TableCell><strong>Asset</strong></TableCell>
                        <TableCell align="right"><strong>Amount</strong></TableCell>
                        <TableCell align="right"><strong>Date</strong></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {recentTransactions.map((transaction) => (
                        <TableRow key={transaction.id} hover>
                          <TableCell>
                            <Chip
                              label={transaction.transactionType || 'N/A'}
                              size="small"
                              color={
                                transaction.transactionType === 'BUY'
                                  ? 'success'
                                  : transaction.transactionType === 'SELL'
                                  ? 'error'
                                  : transaction.transactionType === 'DIVIDEND'
                                  ? 'info'
                                  : 'default'
                              }
                              sx={{ fontWeight: 600, fontSize: 11 }}
                            />
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              Asset #{transaction.assetId || 'N/A'}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">
                            <Typography variant="body2" fontWeight={600}>
                              {formatCurrency(transaction.totalAmount || 0)}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">
                            <Typography variant="caption" color="text.secondary">
                              {formatDate(transaction.transactionDate)}
                            </Typography>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <Box display="flex" justifyContent="center" alignItems="center" height={200}>
                  <Typography color="text.secondary">No recent transactions</Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}