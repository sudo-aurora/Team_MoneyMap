import { useState, useEffect } from 'react';
import {
  Box,
  Grid,
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
  Stack,
  Paper,
  alpha,
} from '@mui/material';
import {
  People as PeopleIcon,
  AccountBalance as AccountBalanceIcon,
  TrendingUp as TrendingUpIcon,
  Warning as WarningIcon,
  Assessment as AssessmentIcon,
  Refresh as RefreshIcon,
  AccountCircle as AccountCircleIcon,
  TrendingDown as TrendingDownIcon,
} from '@mui/icons-material';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Legend,
  Tooltip,
} from 'recharts';
import { clientService } from '../services/clientService';
import { assetService } from '../services/assetService';
import { portfolioService } from '../services/portfolioService';
import { transactionService } from '../services/transactionService';
import { alertService } from '../services/alertService';

const COLORS = ['#1976d2', '#388e3c', '#f57c00', '#9c27b0', '#00bcd4', '#FF6B6B', '#4ECDC4', '#45B7D1'];
const DONUT_COLORS = ['#1976d2', '#388e3c', '#f57c00', '#9c27b0', '#00bcd4', '#FF6B6B', '#4ECDC4', '#45B7D1'];

const StatCard = ({ title, value, icon, color, subtitle, trend }) => (
  <Paper 
    elevation={0}
    sx={{ 
      height: '100%', 
      p: 3,
      borderRadius: 2,
      backgroundColor: alpha(color === 'primary' ? '#1976d2' : 
                          color === 'success' ? '#388e3c' : 
                          color === 'info' ? '#00bcd4' :
                          color === 'secondary' ? '#9c27b0' :
                          color === 'warning' ? '#f57c00' : '#d32f2f', 0.05),
      border: `1px solid ${alpha(color === 'primary' ? '#1976d2' : 
                color === 'success' ? '#388e3c' : 
                color === 'info' ? '#00bcd4' :
                color === 'secondary' ? '#9c27b0' :
                color === 'warning' ? '#f57c00' : '#d32f2f', 0.1)}`,
      transition: 'all 0.2s',
      '&:hover': {
        transform: 'translateY(-2px)',
        boxShadow: 2,
      }
    }}
  >
    <Stack direction="row" justifyContent="space-between" alignItems="flex-start" height="100%">
      <Box flex={1}>
        <Typography color="text.secondary" gutterBottom variant="body2" fontWeight={500}>
          {title}
        </Typography>
        <Typography variant="h4" fontWeight="bold" sx={{ mb: 0.5 }}>
          {value}
        </Typography>
        {subtitle && (
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
            {subtitle}
          </Typography>
        )}
        {trend && (
          <Stack direction="row" alignItems="center" spacing={0.5} mt={1}>
            {trend > 0 ? (
              <TrendingUpIcon sx={{ fontSize: 16, color: 'success.main' }} />
            ) : (
              <TrendingDownIcon sx={{ fontSize: 16, color: 'error.main' }} />
            )}
            <Typography 
              variant="caption" 
              color={trend > 0 ? 'success.main' : 'error.main'}
              fontWeight={600}
            >
              {trend > 0 ? '+' : ''}{trend}%
            </Typography>
          </Stack>
        )}
      </Box>
      <Box
        sx={{
          backgroundColor: `${color}.main`,
          borderRadius: '50%',
          width: 48,
          height: 48,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          ml: 1,
        }}
      >
        {icon}
      </Box>
    </Stack>
  </Paper>
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

      // Mock data for demonstration - replace with your actual data
      const mockAssetDistribution = [
        { name: 'STOCK', value: 1859000, percentage: 31.5 },
        { name: 'CRYPTO', value: 2155000, percentage: 36.5 },
        { name: 'GOLD', value: 925000, percentage: 15.7 },
        { name: 'MUTUAL FUND', value: 981826, percentage: 16.3 },
      ];

      const mockClientDistribution = [
        { name: 'Jennifer Johnson', value: 499100, percentage: 23.1 },
        { name: 'Emma Thomas', value: 385000, percentage: 17.8 },
        { name: 'Anna Meyer', value: 312000, percentage: 14.4 },
        { name: 'George Jones', value: 285000, percentage: 13.2 },
        { name: 'Isla Thomas', value: 254000, percentage: 11.8 },
        { name: 'Klaus Becker', value: 212000, percentage: 9.8 },
        { name: 'Sophie Meyer', value: 214000, percentage: 9.9 },
      ];

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

      const totalAssetValue = mockAssetDistribution.reduce((sum, item) => sum + item.value, 0);

      setStats({
        totalClients: clientsData.length,
        activeClients: activeClientCount || clientsData.filter(c => c.active !== false).length,
        totalPortfolios: portfoliosData.length,
        totalAssetValue: totalAssetValue,
        totalAlerts: alertCount,
      });

      setAssetDistribution(mockAssetDistribution);
      setTopClients(topClientsSorted);
      setRecentTransactions(transactionsData);
      setClientPortfolioDistribution(mockClientDistribution);

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

  const formatCompactCurrency = (value) => {
    if (!value && value !== 0) return '$0';
    if (value >= 1000000) {
      return `$${(value / 1000000).toFixed(1)}M`;
    } else if (value >= 1000) {
      return `$${(value / 1000).toFixed(1)}K`;
    }
    return `$${value.toFixed(0)}`;
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
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
        <Paper elevation={3} sx={{ p: 2, bgcolor: 'background.paper' }}>
          <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
            {payload[0].name}
          </Typography>
          <Typography variant="body2" color="primary">
            Value: {formatCurrency(payload[0].value)}
          </Typography>
          {payload[0].payload.percentage && (
            <Typography variant="body2" color="text.secondary">
              {payload[0].payload.percentage}% of total
            </Typography>
          )}
        </Paper>
      );
    }
    return null;
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="70vh">
        <Stack alignItems="center" spacing={2}>
          <CircularProgress size={60} />
          <Typography variant="h6" color="text.secondary">
            Loading Dashboard...
          </Typography>
        </Stack>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ maxWidth: 600, mx: 'auto', mt: 4 }}>
        <Alert 
          severity="error" 
          action={
            <Button color="inherit" size="small" onClick={loadDashboardData}>
              RETRY
            </Button>
          }
        >
          <Typography variant="h6">Error loading dashboard</Typography>
          <Typography variant="body2">{error}</Typography>
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ pb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ xs: 'flex-start', sm: 'center' }}>
          <Box>
            <Typography variant="h4" fontWeight="bold" gutterBottom>
              Portfolio Dashboard
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Real-time overview of your portfolio management system
            </Typography>
          </Box>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={loadDashboardData}
            sx={{ mt: { xs: 2, sm: 0 } }}
          >
            Refresh Data
          </Button>
        </Stack>
      </Box>

      {/* Stats Cards */}
      <Box sx={{ mb: 6 }}>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={6} md={2.4}>
            <StatCard
              title="Total Clients"
              value={stats.totalClients}
              subtitle="Registered clients"
              icon={<PeopleIcon sx={{ color: 'white', fontSize: 22 }} />}
              color="primary"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={2.4}>
            <StatCard
              title="Active Clients"
              value={stats.activeClients}
              subtitle="Currently active"
              icon={<PeopleIcon sx={{ color: 'white', fontSize: 22 }} />}
              color="success"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={2.4}>
            <StatCard
              title="Portfolios"
              value={stats.totalPortfolios}
              subtitle="Total portfolios"
              icon={<AssessmentIcon sx={{ color: 'white', fontSize: 22 }} />}
              color="info"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={2.4}>
            <StatCard
              title="Total AUM"
              value={formatCompactCurrency(stats.totalAssetValue)}
              subtitle="Assets under management"
              trend={5.2}
              icon={<AccountBalanceIcon sx={{ color: 'white', fontSize: 22 }} />}
              color="secondary"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={2.4}>
            <StatCard
              title="Alerts"
              value={stats.totalAlerts}
              subtitle="Pending alerts"
              icon={<WarningIcon sx={{ color: 'white', fontSize: 22 }} />}
              color="warning"
            />
          </Grid>
        </Grid>
      </Box>

      {/* Charts Row - Clean Design without Boxes */}
      <Grid container spacing={6} sx={{ mb: 6 }}>
        {/* Asset Allocation */}
        <Grid item xs={12} lg={6}>
          <Box>
            <Typography variant="h5" fontWeight={600} gutterBottom color="text.primary">
              Asset Allocation
            </Typography>
            <Typography variant="body1" color="text.secondary" gutterBottom sx={{ mb: 3 }}>
              Distribution of assets by type across all portfolios
            </Typography>
            
            <Typography variant="h6" color="primary" sx={{ mb: 4 }}>
              Total Value: <Box component="span" sx={{ fontWeight: 700 }}>{formatCurrency(stats.totalAssetValue)}</Box>
            </Typography>

            <Box sx={{ height: 380, position: 'relative' }}>
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={assetDistribution}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    outerRadius={140}
                    fill="#8884d8"
                    dataKey="value"
                    label={({ name, percentage }) => `${name}: ${percentage}%`}
                  >
                    {assetDistribution.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip content={<CustomTooltip />} />
                </PieChart>
              </ResponsiveContainer>
            </Box>

            {/* Legend */}
            <Box sx={{ mt: 4 }}>
              <Grid container spacing={2}>
                {assetDistribution.map((item, index) => (
                  <Grid item xs={6} sm={3} key={item.name}>
                    <Stack direction="row" alignItems="center" spacing={1.5}>
                      <Box
                        sx={{
                          width: 12,
                          height: 12,
                          borderRadius: '50%',
                          backgroundColor: COLORS[index % COLORS.length],
                        }}
                      />
                      <Box>
                        <Typography variant="body2" fontWeight={500}>
                          {item.name}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {item.percentage}% • {formatCompactCurrency(item.value)}
                        </Typography>
                      </Box>
                    </Stack>
                  </Grid>
                ))}
              </Grid>
            </Box>
          </Box>
        </Grid>

        {/* Client Distribution */}
        <Grid item xs={12} lg={6}>
          <Box>
            <Typography variant="h5" fontWeight={600} gutterBottom color="text.primary">
              Client Distribution
            </Typography>
            <Typography variant="body1" color="text.secondary" gutterBottom sx={{ mb: 3 }}>
              Portfolio value distribution among clients
            </Typography>
            
            {topClients.length > 0 && (
              <Typography variant="h6" color="primary" sx={{ mb: 4 }}>
                Top Client: <Box component="span" sx={{ fontWeight: 700 }}>
                  {topClients[0]?.firstName} {topClients[0]?.lastName} - {formatCompactCurrency(topClients[0]?.totalValue || 0)}
                </Box>
              </Typography>
            )}

            <Box sx={{ height: 380, position: 'relative' }}>
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={clientPortfolioDistribution}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    innerRadius={60}
                    outerRadius={140}
                    fill="#8884d8"
                    dataKey="value"
                    label={({ name, percentage }) => `${percentage}%`}
                  >
                    {clientPortfolioDistribution.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={DONUT_COLORS[index % DONUT_COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip content={<CustomTooltip />} />
                  
                  {/* Center Text */}
                  <text 
                    x="50%" 
                    y="50%" 
                    textAnchor="middle" 
                    dominantBaseline="middle"
                    style={{ fontSize: '20px', fontWeight: 'bold', fill: '#1976d2' }}
                  >
                    {clientPortfolioDistribution.length} Clients
                  </text>
                </PieChart>
              </ResponsiveContainer>
            </Box>

            {/* Client List */}
            <Box sx={{ mt: 4 }}>
              <Typography variant="subtitle1" fontWeight={600} gutterBottom>
                Client Portfolio Values
              </Typography>
              <Stack spacing={1.5}>
                {clientPortfolioDistribution.map((client, index) => (
                  <Stack 
                    key={index}
                    direction="row" 
                    justifyContent="space-between" 
                    alignItems="center"
                    sx={{
                      p: 1.5,
                      borderRadius: 1,
                      backgroundColor: alpha(DONUT_COLORS[index % DONUT_COLORS.length], 0.05),
                      border: `1px solid ${alpha(DONUT_COLORS[index % DONUT_COLORS.length], 0.1)}`,
                    }}
                  >
                    <Stack direction="row" alignItems="center" spacing={1.5}>
                      <Box
                        sx={{
                          width: 10,
                          height: 10,
                          borderRadius: '50%',
                          backgroundColor: DONUT_COLORS[index % DONUT_COLORS.length],
                        }}
                      />
                      <Typography variant="body2" fontWeight={500}>
                        {client.name}
                      </Typography>
                    </Stack>
                    <Box sx={{ textAlign: 'right' }}>
                      <Typography variant="body2" fontWeight={600}>
                        {formatCompactCurrency(client.value)}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {client.percentage}%
                      </Typography>
                    </Box>
                  </Stack>
                ))}
              </Stack>
            </Box>
          </Box>
        </Grid>
      </Grid>

      {/* Tables Row */}
      <Grid container spacing={4}>
        {/* Top Clients Table */}
        <Grid item xs={12} md={6}>
          <Box>
            <Typography variant="h5" fontWeight={600} gutterBottom color="text.primary" sx={{ mb: 2 }}>
              Top Clients by AUM
            </Typography>
            {topClients.length > 0 ? (
              <TableContainer component={Paper} elevation={0} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider' }}>
                <Table>
                  <TableHead sx={{ bgcolor: 'action.hover' }}>
                    <TableRow>
                      <TableCell><strong>Rank</strong></TableCell>
                      <TableCell><strong>Client</strong></TableCell>
                      <TableCell align="right"><strong>Portfolio Value</strong></TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {topClients.map((client, index) => (
                      <TableRow key={client.id} hover>
                        <TableCell>
                          <Box
                            sx={{
                              width: 28,
                              height: 28,
                              borderRadius: '50%',
                              backgroundColor: index < 3 ? '#1976d2' : 'grey.100',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              color: index < 3 ? 'white' : 'text.primary',
                              fontWeight: 'bold',
                              fontSize: 13,
                            }}
                          >
                            {index + 1}
                          </Box>
                        </TableCell>
                        <TableCell>
                          <Stack direction="row" alignItems="center" spacing={1.5}>
                            <AccountCircleIcon 
                              sx={{ 
                                color: COLORS[index % COLORS.length],
                                fontSize: 32
                              }} 
                            />
                            <Box>
                              <Typography variant="body2" fontWeight={600}>
                                {client.firstName || 'Unknown'} {client.lastName || ''}
                              </Typography>
                              <Typography variant="caption" color="text.secondary">
                                {client.email || 'No email'}
                              </Typography>
                            </Box>
                          </Stack>
                        </TableCell>
                        <TableCell align="right">
                          <Typography variant="body2" fontWeight={700} color="primary">
                            {formatCompactCurrency(client.totalValue)}
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            ) : (
              <Box display="flex" justifyContent="center" alignItems="center" height={200}>
                <Typography color="text.secondary" variant="body1">
                  No client data available
                </Typography>
              </Box>
            )}
          </Box>
        </Grid>

        {/* Recent Transactions Table */}
        <Grid item xs={12} md={6}>
          <Box>
            <Typography variant="h5" fontWeight={600} gutterBottom color="text.primary" sx={{ mb: 2 }}>
              Recent Transactions
            </Typography>
            {recentTransactions.length > 0 ? (
              <TableContainer component={Paper} elevation={0} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider' }}>
                <Table>
                  <TableHead sx={{ bgcolor: 'action.hover' }}>
                    <TableRow>
                      <TableCell><strong>Type</strong></TableCell>
                      <TableCell><strong>Asset</strong></TableCell>
                      <TableCell align="right"><strong>Amount</strong></TableCell>
                      <TableCell><strong>Date</strong></TableCell>
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
                            sx={{ fontWeight: 600 }}
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
                        <TableCell>
                          <Typography variant="body2">
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
                <Typography color="text.secondary" variant="body1">
                  No recent transactions
                </Typography>
              </Box>
            )}
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
}