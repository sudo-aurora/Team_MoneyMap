import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  CircularProgress,
  Alert,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Card,
  CardContent,
  InputAdornment,
  TablePagination,
  IconButton,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Visibility as VisibilityIcon,
  TrendingUp as TrendingUpIcon,
} from '@mui/icons-material';
import { assetService } from '../services/assetService';

export default function Assets() {
  const navigate = useNavigate();
  const [assets, setAssets] = useState([]);
  const [assetTypes, setAssetTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterType, setFilterType] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [currentPrices, setCurrentPrices] = useState({}); // Store current prices

  useEffect(() => {
    loadAssetTypes();
    loadAssets();
  }, [filterType]);

  useEffect(() => {
    if (assets.length > 0) {
      fetchCurrentPrices();
    }
  }, [assets]);

  const loadAssetTypes = async () => {
    try {
      const types = await assetService.getTypes();
      setAssetTypes(Array.isArray(types) ? types : []);
    } catch (err) {
      console.error('Error loading asset types:', err);
    }
  };

  const loadAssets = async () => {
    try {
      setLoading(true);
      setError(null);
      
      let response;
      if (filterType === 'ALL') {
        response = await assetService.getAll(0, 1000);
      } else {
        response = await assetService.getByType(filterType);
      }
      
      let allAssets = [];
      if (response.content) {
        allAssets = response.content;
      } else if (Array.isArray(response)) {
        allAssets = response;
      }
      
      // Remove duplicates by symbol - keep only one entry per asset
      const uniqueAssets = allAssets.reduce((acc, asset) => {
        const existingIndex = acc.findIndex(a => a.symbol === asset.symbol);
        if (existingIndex === -1) {
          acc.push(asset);
        }
        return acc;
      }, []);
      
      setAssets(uniqueAssets);
    } catch (err) {
      console.error('Error loading assets:', err);
      setError(err.message);
    } finally {
      setLoading(false);
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

  const filteredAssets = assets.filter(asset =>
    asset.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    asset.symbol.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const paginatedAssets = filteredAssets.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage
  );

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
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <div>
          <Typography variant="h4" gutterBottom fontWeight="bold">
            Assets
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage portfolio assets across all clients
          </Typography>
        </div>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/assets/new')}
        >
          Add Asset
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box display="flex" gap={2} flexWrap="wrap">
            <TextField
              placeholder="Search assets..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              sx={{ flexGrow: 1, minWidth: 250 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
            />
            <FormControl sx={{ minWidth: 200 }}>
              <InputLabel>Filter by Type</InputLabel>
              <Select
                value={filterType}
                label="Filter by Type"
                onChange={(e) => setFilterType(e.target.value)}
              >
                <MenuItem value="ALL">All Types</MenuItem>
                {assetTypes.map((type) => (
                  <MenuItem key={type} value={type}>
                    {type}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>
        </CardContent>
      </Card>

      {paginatedAssets.length === 0 ? (
        <Card>
          <CardContent>
            <Box display="flex" justifyContent="center" alignItems="center" py={8}>
              <Box textAlign="center">
                <TrendingUpIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No Assets Found
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  {searchQuery ? 'Try adjusting your search' : 'Start by adding your first asset'}
                </Typography>
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={() => navigate('/assets/new')}
                >
                  Add Asset
                </Button>
              </Box>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell><strong>Asset Name</strong></TableCell>
                <TableCell><strong>Symbol</strong></TableCell>
                <TableCell><strong>Type</strong></TableCell>
                <TableCell><strong>Exchange/Blockchain</strong></TableCell>
                <TableCell><strong>Current Price</strong></TableCell>
                <TableCell><strong>24h Change</strong></TableCell>
                <TableCell align="center"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {paginatedAssets.map((asset) => (
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
                  </TableCell>
                  <TableCell>{asset.symbol}</TableCell>
                  <TableCell>
                    <Chip
                      label={asset.assetType}
                      size="small"
                      color={getTypeColor(asset.assetType)}
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" color="text.secondary">
                      {asset.exchange || asset.blockchain || 'N/A'}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Typography fontWeight="medium">
                      ${currentPrices[asset.symbol]?.toLocaleString() || asset.currentPrice?.toLocaleString()}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={`${(Math.random() * 10 - 5).toFixed(2)}%`}
                      size="small"
                      color={Math.random() > 0.5 ? 'success' : 'error'}
                    />
                  </TableCell>
                  <TableCell align="center">
                    <IconButton
                      size="small"
                      color="primary"
                      onClick={(e) => {
                        e.stopPropagation();
                        navigate(`/assets/${asset.id}`);
                      }}
                      title="View"
                    >
                      <VisibilityIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
          <TablePagination
            component="div"
            count={filteredAssets.length}
            page={page}
            onPageChange={(e, newPage) => setPage(newPage)}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={(e) => {
              setRowsPerPage(parseInt(e.target.value, 10));
              setPage(0);
            }}
            rowsPerPageOptions={[5, 10, 25, 50]}
          />
        </TableContainer>
      )}
    </Box>
  );
}
