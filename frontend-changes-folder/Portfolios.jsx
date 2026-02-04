import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Chip,
  Button,
  CircularProgress,
  Alert,
  TextField,
  InputAdornment,
} from '@mui/material';
import {
  Visibility as ViewIcon,
  TrendingUp as TrendingUpIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { portfolioService } from '../services/portfolioService';

export default function Portfolios() {
  const navigate = useNavigate();
  const [portfolios, setPortfolios] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadPortfolios();
  }, []);

  const loadPortfolios = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await portfolioService.getAll(0, 100);

      if (response.content) {
        setPortfolios(response.content);
      } else if (Array.isArray(response)) {
        setPortfolios(response);
      } else {
        setPortfolios([]);
      }
    } catch (err) {
      console.error('Error loading portfolios:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // ✅ Filter portfolios by first or last name starting with search term
  const filteredPortfolios = useMemo(() => {
    const term = searchTerm.toLowerCase().trim();
    if (!term) return portfolios;

    return portfolios.filter((portfolio) => {
      const names = portfolio.name?.toLowerCase().split(' ') || [];
      // Match if any part of the name starts with the search term
      return names.some((part) => part.startsWith(term));
    });
  }, [portfolios, searchTerm]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box mb={3}>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          Portfolios
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Manage client portfolios and view performance
        </Typography>
      </Box>

      {/* 🔍 Search Bar */}
      <Box mb={3} maxWidth={400}>
        <TextField
          fullWidth
          placeholder="Search client by name..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon color="action" />
              </InputAdornment>
            ),
          }}
        />
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {filteredPortfolios.length === 0 ? (
        <Card>
          <CardContent>
            <Box display="flex" justifyContent="center" alignItems="center" py={8}>
              <Box textAlign="center">
                <TrendingUpIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No Matching Portfolios
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Try searching with a different client name
                </Typography>
              </Box>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {filteredPortfolios.map((portfolio) => (
            <Grid item xs={12} sm={6} md={4} key={portfolio.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Box display="flex" justifyContent="space-between" alignItems="start" mb={2}>
                    <Typography variant="h6" fontWeight="bold">
                      {portfolio.name}
                    </Typography>

                    <Chip
                      label={portfolio.active ? 'Active' : 'Inactive'}
                      color={portfolio.active ? 'success' : 'default'}
                      size="small"
                    />
                  </Box>

                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      Total Value
                    </Typography>
                    <Typography variant="h5" color="primary" fontWeight="bold">
                      ${portfolio.totalValue?.toLocaleString() || '0.00'}
                    </Typography>
                  </Box>
                </CardContent>

                <Box sx={{ p: 2, pt: 0 }}>
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<ViewIcon />}
                    onClick={() => navigate(`/portfolios/${portfolio.id}`)}
                  >
                    View Details
                  </Button>
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
}
