import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
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
  Tabs,
  Tab,
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
} from '@mui/icons-material';
import { assetService } from '../services/assetService';

// Simple Line Chart component with hover
const SimpleLineChart = ({ data }) => {
  const [hoveredPoint, setHoveredPoint] = useState(null);

  if (!data || data.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height={300}>
        <Typography color="text.secondary">No historical data available</Typography>
      </Box>
    );
  }

  const maxPrice = Math.max(...data.map(d => d.price));
  const minPrice = Math.min(...data.map(d => d.price));
  const priceRange = maxPrice - minPrice || 1;

  return (
    <Box sx={{ height: 300, p: 2, position: 'relative' }}>
      <svg width="100%" height="100%" viewBox="0 0 800 300">
        {/* Grid lines */}
        {[0, 1, 2, 3, 4].map(i => (
          <line
            key={i}
            x1="50"
            y1={i * 60 + 20}
            x2="750"
            y2={i * 60 + 20}
            stroke="#e0e0e0"
            strokeWidth="1"
          />
        ))}
        
        {/* Price line */}
        <polyline
          fill="none"
          stroke="#1976d2"
          strokeWidth="2"
          points={data.map((point, index) => {
            const x = 50 + (index / (data.length - 1)) * 700;
            const y = 280 - ((point.price - minPrice) / priceRange) * 240;
            return `${x},${y}`;
          }).join(' ')}
        />
        
        {/* Data points with hover */}
        {data.map((point, index) => {
          const x = 50 + (index / (data.length - 1)) * 700;
          const y = 280 - ((point.price - minPrice) / priceRange) * 240;
          return (
            <g key={index}>
              <circle
                cx={x}
                cy={y}
                r="5"
                fill="transparent"
                style={{ cursor: 'pointer' }}
                onMouseEnter={() => setHoveredPoint({ ...point, x, y })}
                onMouseLeave={() => setHoveredPoint(null)}
              />
              <circle
                cx={x}
                cy={y}
                r="3"
                fill="#1976d2"
              />
            </g>
          );
        })}
        
        {/* Y-axis labels */}
        {[maxPrice, (maxPrice + minPrice) / 2, minPrice].map((price, i) => (
          <text
            key={i}
            x="40"
            y={i * 120 + 25}
            textAnchor="end"
            fontSize="12"
            fill="#666"
          >
            ${price.toFixed(2)}
          </text>
        ))}
      </svg>
      
      {/* Hover tooltip */}
      {hoveredPoint && (
        <Box
          sx={{
            position: 'absolute',
            left: hoveredPoint.x - 60,
            top: hoveredPoint.y - 40,
            bgcolor: 'background.paper',
            border: '1px solid #ccc',
            borderRadius: 1,
            p: 1,
            boxShadow: 2,
            pointerEvents: 'none',
            zIndex: 10
          }}
        >
          <Typography variant="body2" fontWeight="bold">
            ${hoveredPoint.price.toFixed(2)}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            {hoveredPoint.date}
          </Typography>
        </Box>
      )}
    </Box>
  );
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

export default function AssetDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [asset, setAsset] = useState(null);
  const [historicalData, setHistoricalData] = useState([]);
  const [timePeriod, setTimePeriod] = useState('1M'); // 1W, 1M, 1Y
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [apiData, setApiData] = useState(null); // Store real API data

  useEffect(() => {
    loadAsset();
  }, [id]);

  useEffect(() => {
    if (asset) {
      generateHistoricalData();
    }
  }, [asset, timePeriod]);

  const loadAsset = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await assetService.getByIdWithTransactions(id);
      setAsset(data);
    } catch (err) {
      console.error('Error loading asset:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const generateHistoricalData = async () => {
    try {
      // Use the Yahoo Finance API from your Flask app
      const periodMap = {
        '1W': '5d',
        '1M': '1mo', 
        '1Y': '1y'
      };
      
      const response = await fetch(`http://localhost:5000/stock/${asset?.symbol}?period=${periodMap[timePeriod]}`);
      if (response.ok) {
        const data = await response.json();
        console.log('Real API data:', data);
        setApiData(data); // Store the API data
        
        // Transform the data to match our chart format
        const chartData = data.historicalData.map(item => ({
          date: item.date,
          price: item.close,
          volume: item.volume
        }));
        
        setHistoricalData(chartData);
        console.log('Transformed chart data:', chartData.length, 'points');
      } else {
        console.log('API failed, using dummy data');
        setApiData(null);
        setHistoricalData(generateDummyData());
      }
    } catch (err) {
      console.error('Error fetching real data:', err);
      setApiData(null);
      setHistoricalData(generateDummyData());
    }
  };

  const generateDummyData = () => {
    const data = [];
    let days = 30;
    
    if (timePeriod === '1W') days = 7;
    else if (timePeriod === '1M') days = 30;
    else if (timePeriod === '1Y') days = 365;
    
    // Generate realistic price progression
    const basePrice = asset?.purchasePrice || 100;
    const currentPrice = asset?.currentPrice || basePrice * 1.1; // Assume 10% gain if no data
    
    console.log('Generating data for period:', timePeriod, 'days:', days, 'basePrice:', basePrice, 'currentPrice:', currentPrice);
    
    for (let i = days; i >= 0; i--) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      
      // Calculate price based on time progression
      const progress = i / days; // 0 to 1 (past to present)
      const randomVariation = (Math.random() - 0.5) * 0.1; // +/- 5% random variation
      const trendPrice = basePrice + (currentPrice - basePrice) * (1 - progress);
      const price = trendPrice * (1 + randomVariation);
      
      data.push({
        date: date.toISOString().split('T')[0],
        price: Math.max(price, basePrice * 0.5), // Don't go below 50% of purchase price
        volume: Math.floor(Math.random() * 1000000) + 100000
      });
    }
    
    console.log('Generated', data.length, 'data points');
    return data;
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
        <IconButton onClick={() => navigate('/assets')}>
          <BackIcon />
        </IconButton>
        <Alert severity="error" sx={{ mt: 2 }}>
          Error loading asset: {error}
        </Alert>
      </Box>
    );
  }

  if (!asset) {
    return (
      <Box>
        <IconButton onClick={() => navigate('/assets')}>
          <BackIcon />
        </IconButton>
        <Alert severity="warning" sx={{ mt: 2 }}>
          Asset not found
        </Alert>
      </Box>
    );
  }

  const profitLoss = apiData 
    ? (apiData.latestPrice * (asset?.quantity || 1)) - (asset?.purchasePrice * (asset?.quantity || 1))
    : asset?.quantity && asset?.purchasePrice 
    ? (asset.currentValue || (asset.currentPrice * asset.quantity)) - (asset.purchasePrice * asset.quantity)
    : (asset.currentPrice || 0) - (asset.purchasePrice || 0);
  const profitLossPercent = apiData
    ? ((apiData.returnPercentage / 100) * (asset?.purchasePrice * (asset?.quantity || 1))) || 0
    : asset?.quantity && asset?.purchasePrice
    ? ((profitLoss / (asset.purchasePrice * asset.quantity)) * 100) || 0
    : asset?.purchasePrice ? ((profitLoss / asset.purchasePrice) * 100) || 0 : 0;

  return (
    <Box>
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box display="flex" alignItems="center" gap={2}>
          <IconButton onClick={() => navigate('/assets')}>
            <BackIcon />
          </IconButton>
          <div>
            <Typography variant="h4" fontWeight="bold">
              {asset.name}
            </Typography>
            <Box display="flex" gap={1} alignItems="center" mt={0.5}>
              <Typography variant="body2" color="text.secondary">
                {asset.symbol}
              </Typography>
              <Chip
                label={asset.assetType}
                size="small"
                color={getTypeColor(asset.assetType)}
                variant="outlined"
              />
            </Box>
          </div>
        </Box>
      </Box>

      <Grid container spacing={3}>
        {/* Top Row - General Asset Info Cards */}
        <Grid item xs={12}>
          <Box display="flex" gap={2} flexWrap="wrap">
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Current Price
                </Typography>
                <Typography variant="h5" fontWeight="bold" color="primary">
                  ${apiData?.latestPrice?.toLocaleString() || asset.currentPrice?.toLocaleString()}
                </Typography>
              </CardContent>
            </Card>

            <Card sx={{ minWidth: 200, flex: 1, bgcolor: (apiData?.returnPercentage || 0) >= 0 ? 'success.light' : 'error.light' }}>
              <CardContent>
                <Typography variant="body2" gutterBottom>
                  {timePeriod} Performance
                </Typography>
                <Box display="flex" alignItems="center" gap={1}>
                  {(apiData?.returnPercentage || 0) >= 0 ? <TrendingUpIcon /> : <TrendingDownIcon />}
                  <Typography variant="h5" fontWeight="bold">
                    {apiData?.returnPercentage?.toFixed(2) || '0.00'}%
                  </Typography>
                </Box>
                <Typography variant="body2">
                  Selected period
                </Typography>
              </CardContent>
            </Card>

            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Exchange
                </Typography>
                <Typography variant="h5" fontWeight="bold">
                  {apiData?.metadata?.exchange || asset.exchange || 'N/A'}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {asset.assetType}
                </Typography>
              </CardContent>
            </Card>
          </Box>
        </Grid>

        {/* Bottom Row - Full Width Chart with Tabs */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h6" gutterBottom fontWeight="bold">
                  Historical Performance
                </Typography>
                <Tabs value={timePeriod} onChange={(e, newValue) => setTimePeriod(newValue)}>
                  <Tab label="1W" value="1W" />
                  <Tab label="1M" value="1M" />
                  <Tab label="1Y" value="1Y" />
                </Tabs>
              </Box>
              <SimpleLineChart key={`${timePeriod}-${historicalData.length}`} data={historicalData} />
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
