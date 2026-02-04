import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Chip,
  Button,
  CircularProgress,
  Alert,
  TextField,
  Autocomplete,
} from '@mui/material';
import {
  Visibility as ViewIcon,
  TrendingUp as TrendingUpIcon,
} from '@mui/icons-material';
import { portfolioService } from '../services/portfolioService';

export default function Portfolios() {
  const navigate = useNavigate();
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchValue, setSearchValue] = useState(null);

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
      {/* Header + Search */}
      <Box
        mb={3}
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        flexWrap="wrap"
        gap={2}
      >
        <Box>
          <Typography variant="h4" fontWeight="bold">
            Portfolios
          </Typography>

          <Typography variant="body1" color="text.secondary">
            Manage client portfolios and view performance
          </Typography>
        </Box>

        {/* ⭐ SEARCH BAR */}
        <Autocomplete
          sx={{ width: 320 }}
          options={portfolios}
          getOptionLabel={(option) => option.name || ''}
          value={searchValue}
          onChange={(event, newValue) => {
            if (newValue) {
              navigate(`/portfolios/${newValue.id}`);

              // ⭐ clears search after click
              setSearchValue(null);
            }
          }}
          renderInput={(params) => (
            <TextField {...params} label="Search portfolios..." size="small" />
          )}
        />
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

                <Typography variant="h6" color="text.secondary">
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
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: {
              xs: '1fr',
              sm: '1fr 1fr',
              md: '1fr 1fr 1fr',
            },
            gap: 3,
          }}
        >
          {portfolios.map((portfolio) => (
            <Card
              key={portfolio.id}
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                transition: '0.25s',
                '&:hover': {
                  transform: 'translateY(-6px)',
                  boxShadow: 6,
                },
              }}
            >
              <CardContent sx={{ flexGrow: 1 }}>
                <Box
                  display="flex"
                  justifyContent="space-between"
                  alignItems="flex-start"
                  mb={2}
                >
                  <Typography variant="h6" fontWeight="bold">
                    {portfolio.name}
                  </Typography>

                  <Chip
                    label={portfolio.active ? 'Active' : 'Inactive'}
                    color={portfolio.active ? 'success' : 'default'}
                    size="small"
                  />
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Total Value
                  </Typography>

                  <Typography variant="h4" color="primary" fontWeight="bold">
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
          ))}
        </Box>
      )}
    </Box>
  );
}
