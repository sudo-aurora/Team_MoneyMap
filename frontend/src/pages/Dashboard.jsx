import { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  CircularProgress,
  Alert,
  Button,
  Container,
} from '@mui/material';
import {
  People as PeopleIcon,
  AccountBalance as AccountBalanceIcon,
  TrendingUp as TrendingUpIcon,
  Warning as WarningIcon,
  Assessment as AssessmentIcon,
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

// Custom colors for asset types
const getAssetColor = (assetName) => {
  const assetNameUpper = assetName?.toUpperCase() || '';
  
  if (assetNameUpper.includes('GOLD') || assetNameUpper.includes('PRECIOUS METAL')) {
    return '#FFD700'; // Gold color for gold assets
  } else if (assetNameUpper.includes('CRYPTO') || assetNameUpper.includes('BITCOIN') || assetNameUpper.includes('ETHEREUM')) {
    return '#8247E5'; // Purple for crypto
  } else if (assetNameUpper.includes('STOCK') || assetNameUpper.includes('EQUITY')) {
    return '#1976d2'; // Blue for stocks
  } else if (assetNameUpper.includes('MUTUAL FUND') || assetNameUpper.includes('FUND')) {
    return '#388e3c'; // Green for mutual funds
  } else if (assetNameUpper.includes('BOND') || assetNameUpper.includes('DEBT')) {
    return '#f57c00'; // Orange for bonds
  } else if (assetNameUpper.includes('REAL ESTATE') || assetNameUpper.includes('PROPERTY')) {
    return '#d32f2f'; // Red for real estate
  } else if (assetNameUpper.includes('CASH') || assetNameUpper.includes('LIQUID')) {
    return '#00bcd4'; // Cyan for cash
  } else {
    return '#9c27b0'; // Purple for others
  }
};

const StatCard = ({ title, value, icon, color, subtitle, trend }) => (
  <Card sx={{ 
    height: '100%', 
    position: 'relative', 
    overflow: 'visible',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    borderRadius: 2,
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
    transition: 'transform 0.2s, box-shadow 0.2s',
    '&:hover': {
      transform: 'translateY(-2px)',
      boxShadow: '0 6px 16px rgba(0, 0, 0, 0.12)',
    }
  }}>
    <CardContent sx={{ p: 3 }}>
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
            justifyContent: 'center',
            boxShadow: `0 4px 12px ${color === 'primary' ? '#1976d240' : color === 'success' ? '#388e3c40' : color === 'warning' ? '#f57c0040' : '#d32f2f40'}`,
            ml: 1,
            flexShrink: 0
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

      const totalAssetValue = distributionData.reduce((sum, item) => sum + item.value, 0);

      setStats({
        totalClients: clientsData.length,
        activeClients: activeClientCount || clientsData.filter(c => c.active !== false).length,
        totalPortfolios: portfoliosData.length,
        totalAssetValue: totalAssetValue,
        totalAlerts: alertCount,
      });

      setAssetDistribution(distributionData.filter(item => item.value > 0));

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
    <Container maxWidth="xl" sx={{ py: 3, px: { xs: 2, sm: 3, md: 4 } }}>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          Portfolio Dashboard
        </Typography>
      </Box>

      {/* Stats Cards - Full width grid */}
      <Grid 
        container 
        spacing={3} 
        sx={{ mb: 5 }}
      >
        {[
          {
            title: "Total Clients",
            value: stats.totalClients,
            subtitle: "Registered clients",
            icon: <PeopleIcon sx={{ color: 'white', fontSize: 24 }} />,
            color: "primary"
          },
          {
            title: "Active Clients",
            value: stats.activeClients,
            subtitle: "Currently active",
            icon: <PeopleIcon sx={{ color: 'white', fontSize: 24 }} />,
            color: "success"
          },
          {
            title: "Portfolios",
            value: stats.totalPortfolios,
            subtitle: "Total portfolios",
            icon: <AssessmentIcon sx={{ color: 'white', fontSize: 24 }} />,
            color: "info"
          },
          {
            title: "Total AUM",
            value: formatCurrency(stats.totalAssetValue),
            subtitle: "Assets under management",
            trend: 5.2,
            icon: <AccountBalanceIcon sx={{ color: 'white', fontSize: 24 }} />,
            color: "secondary"
          },
          {
            title: "Alerts",
            value: stats.totalAlerts,
            subtitle: "Pending alerts",
            icon: <WarningIcon sx={{ color: 'white', fontSize: 24 }} />,
            color: "warning"
          }
        ].map((stat, index) => (
          <Grid item xs={12} sm={6} md={4} lg={2.4} key={index}>
            <StatCard {...stat} />
          </Grid>
        ))}
      </Grid>

      {/* Asset Allocation Chart - In a card with padding */}
      <Card sx={{ 
        borderRadius: 2,
        boxShadow: '0 4px 20px rgba(0, 0, 0, 0.08)',
        overflow: 'visible'
      }}>
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ 
            display: 'flex', 
            flexDirection: 'column', 
            alignItems: 'center',
            justifyContent: 'center',
          }}>
            <Typography variant="h6" gutterBottom fontWeight={600} sx={{ mb: 1 }}>
              Asset Allocation
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 4, textAlign: 'center' }}>
              Distribution by asset type
            </Typography>
            
            {assetDistribution.length > 0 ? (
              <Box sx={{ 
                width: '100%', 
                height: 400,
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                px: { xs: 0, sm: 2, md: 4 }
              }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={assetDistribution}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ percent, name }) => 
                        `${(percent * 100).toFixed(0)}%\n${name.length > 12 ? name.substring(0, 10) + '...' : name}`
                      }
                      outerRadius={120}
                      fill="#8884d8"
                      dataKey="value"
                      paddingAngle={1}
                    >
                      {assetDistribution.map((entry, index) => (
                        <Cell 
                          key={`cell-${index}`} 
                          fill={getAssetColor(entry.name)} 
                          stroke="#fff"
                          strokeWidth={2}
                        />
                      ))}
                    </Pie>
                    <Tooltip 
                      formatter={(value) => [formatCurrency(value), 'Value']}
                      labelFormatter={(name) => `Asset: ${name}`}
                      contentStyle={{ 
                        borderRadius: 8,
                        border: 'none',
                        boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)'
                      }}
                    />
                    <Legend 
                      wrapperStyle={{ 
                        paddingTop: 20,
                        textAlign: 'center'
                      }}
                      layout="horizontal"
                      verticalAlign="bottom"
                      align="center"
                      iconType="circle"
                      iconSize={10}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </Box>
            ) : (
              <Box 
                display="flex" 
                justifyContent="center" 
                alignItems="center" 
                height={300}
                sx={{ width: '100%' }}
              >
                <Typography color="text.secondary">No asset data available</Typography>
              </Box>
            )}
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
}