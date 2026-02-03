import { useState, useEffect } from 'react';
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
  IconButton,
} from '@mui/material';
import {
  Visibility as ViewIcon,
  TrendingUp as TrendingUpIcon,
  Person as PersonIcon,
} from '@mui/icons-material';
import { portfolioService } from '../services/portfolioService';

export default function Portfolios() {
  const navigate = useNavigate();
  const [portfolios, setPortfolios] = useState([]);
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
            Portfolios
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage client portfolios and view performance
          </Typography>
        </div>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {portfolios.length === 0 ? (
        <Card>
          <CardContent>
            <Box display="flex" justifyContent="center" alignItems="center" py={8}>
              <Box textAlign="center">
                <TrendingUpIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No Portfolios Found
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Portfolios will appear here once clients are assigned
                </Typography>
              </Box>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {portfolios.map((portfolio) => (
            <Grid item xs={12} sm={6} md={4} key={portfolio.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Box display="flex" justifyContent="space-between" alignItems="start" mb={2}>
                    <Typography variant="h6" fontWeight="bold" gutterBottom>
                      {portfolio.name}
                    </Typography>
                    <Chip
                      label={portfolio.active ? 'Active' : 'Inactive'}
                      color={portfolio.active ? 'success' : 'default'}
                      size="small"
                    />
                  </Box>

                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2, minHeight: 40 }}>
                    {portfolio.description || 'No description'}
                  </Typography>

                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      Total Value
                    </Typography>
                    <Typography variant="h5" color="primary" fontWeight="bold">
                      ${portfolio.totalValue?.toLocaleString() || '0.00'}
                    </Typography>
                  </Box>

                  <Box display="flex" alignItems="center" gap={1}>
                    <PersonIcon fontSize="small" color="action" />
                    <Typography variant="body2" color="text.secondary">
                      Client ID: {portfolio.clientId || 'N/A'}
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
