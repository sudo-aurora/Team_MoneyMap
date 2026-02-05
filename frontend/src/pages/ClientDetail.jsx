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
  Divider,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  AccountBalance as PortfolioIcon,
  Phone as PhoneIcon,
  Email as EmailIcon,
  LocationOn as LocationIcon,
  Language as LanguageIcon,
  AttachMoney as CurrencyIcon,
  TrendingUp as TrendingUpIcon,
  Visibility as ViewIcon,
  AccountBalanceWallet as WalletIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import { clientService } from '../services/clientService';
import { assetService } from '../services/assetService';
import { tradingService } from '../services/tradingService';
import ConfirmDialog from '../components/ConfirmDialog';

const COLORS = ['#1976d2', '#388e3c', '#f57c00', '#d32f2f'];

export default function ClientDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [client, setClient] = useState(null);
  const [portfolio, setPortfolio] = useState(null);
  const [assets, setAssets] = useState([]);
  const [assetDistribution, setAssetDistribution] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [walletBalance, setWalletBalance] = useState(0);
  const [depositDialogOpen, setDepositDialogOpen] = useState(false);
  const [depositAmount, setDepositAmount] = useState('');
  const [depositing, setDepositing] = useState(false);
  const [depositSuccess, setDepositSuccess] = useState(null);
  const [depositError, setDepositError] = useState(null);

  useEffect(() => {
    loadClientData();
  }, [id]);

  const loadClientData = async () => {
    try {
      setLoading(true);
      setError(null);
      const clientData = await clientService.getWithPortfolios(id);
      setClient(clientData);
      
      // Load wallet balance
      loadWalletBalance();
      
      // Extract portfolio if exists (one-to-one relationship)
      if (clientData.portfolios && clientData.portfolios.length > 0) {
        setPortfolio(clientData.portfolios[0]);
      }
    } catch (err) {
      console.error('Error loading client:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadWalletBalance = async () => {
    try {
      const balance = await tradingService.getWalletBalance(id);
      console.log('Raw balance response:', balance);
      
      // Handle BigDecimal object or string response
      let numericBalance = 0;
      if (typeof balance === 'object' && balance !== null) {
        numericBalance = parseFloat(balance.value || balance.toString() || '0');
      } else if (typeof balance === 'string') {
        numericBalance = parseFloat(balance);
      } else if (typeof balance === 'number') {
        numericBalance = balance;
      }
      
      setWalletBalance(numericBalance || 0);
    } catch (err) {
      console.error('Error loading wallet balance:', err);
      setWalletBalance(0);
    }
  };

  const handleDepositClick = () => {
    setDepositAmount('');
    setDepositError(null);
    setDepositSuccess(null);
    setDepositDialogOpen(true);
  };

  const handleDepositClose = () => {
    setDepositDialogOpen(false);
    setDepositAmount('');
    setDepositError(null);
    setDepositSuccess(null);
  };

  const handleDepositSubmit = async () => {
    try {
      setDepositing(true);
      setDepositError(null);
      setDepositSuccess(null);
      
      const amount = parseFloat(depositAmount);
      
      if (!amount || amount <= 0) {
        setDepositError('Please enter a valid amount greater than 0');
        return;
      }
      
      console.log('Depositing funds:', { clientId: id, amount });
      
      await tradingService.addToWallet(id, amount);
      
      setDepositSuccess(`Successfully deposited $${amount.toFixed(2)} to wallet`);
      
      // Reload wallet balance after successful deposit
      setTimeout(() => {
        loadWalletBalance();
        handleDepositClose();
      }, 1500);
      
    } catch (err) {
      console.error('Deposit error:', err);
      setDepositError(err.message || 'Failed to deposit funds');
    } finally {
      setDepositing(false);
    }
  };
  // const loadAssets = async (portfolioId) => {
  //   try {
  //     const assetsData = await assetService.getByPortfolioId(portfolioId);
  //     setAssets(assetsData);
      
  //     // Calculate asset distribution for pie chart
  //     const distribution = assetsData.map(asset => ({
  //       name: asset.name || asset.symbol,
  //       value: parseFloat(asset.value) || 0
  //     }));
  //     setAssetDistribution(distribution);
  //   } catch (err) {
  //     console.error('Error loading assets:', err);
  //     setError(err.message);
  //   }
  // };

  // const loadWalletBalance = async () => {
  //   try {
  //     console.log('Loading wallet balance for client:', id);
      
  //     // Validate clientId before making API call
  //     if (!id || id === '' || id === ':') {
  //       console.log('Invalid clientId, skipping wallet balance load');
  //       return;
  //     }
      
  //     const balance = await tradingService.getWalletBalance(id);
  //     console.log('Raw balance response:', balance);
  //     console.log('Balance type:', typeof balance);
  //     console.log('Balance value:', balance);
      
  //     // Convert BigDecimal to number and handle potential string values
  //     const numericBalance = typeof balance === 'string' ? parseFloat(balance) : balance;
  //     const finalBalance = numericBalance || 0;
      
  //     console.log('Final numeric balance:', finalBalance);
  //     setWalletBalance(finalBalance);
  //   } catch (err) {
  //     console.error('Error loading wallet balance:', err);
  //     console.error('Error details:', err.response?.data);
  //     setWalletBalance(0);
  //   }
  // };

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
        <Button
          startIcon={<BackIcon />}
          onClick={() => navigate('/clients')}
          sx={{ mb: 2 }}
        >
          Back to Clients
        </Button>
        <Alert severity="error">
          Error loading client: {error}
        </Alert>
      </Box>
    );
  }

  if (!client) {
    return (
      <Box>
        <Button
          startIcon={<BackIcon />}
          onClick={() => navigate('/clients')}
          sx={{ mb: 2 }}
        >
          Back to Clients
        </Button>
        <Alert severity="warning">
          Client not found
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box display="flex" alignItems="center" gap={2}>
          <IconButton onClick={() => navigate('/clients')}>
            <BackIcon />
          </IconButton>
          <div>
            <Typography variant="h4" fontWeight="bold">
              {client.fullName || `${client.firstName} ${client.lastName}`}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {client.email}
            </Typography>
          </div>
        </Box>
        <Box display="flex" gap={1}>
          {client.active ? (
            <Chip label="Active" color="success" />
          ) : (
            <Chip label="Inactive" color="default" />
          )}
          <Button
            variant="contained"
            startIcon={<EditIcon />}
            onClick={() => navigate(`/clients/${id}/edit`)}
          >
            Edit Client
          </Button>
          <Button
            variant="outlined"
            color="error"
            startIcon={<DeleteIcon />}
            onClick={() => setDeleteDialogOpen(true)}
          >
            Delete
          </Button>
        </Box>
      </Box>

      <ConfirmDialog
        open={deleteDialogOpen}
        title="Delete Client"
        message={`Are you sure you want to delete ${client?.fullName || 'this client'}? This action cannot be undone.`}
        onConfirm={async () => {
          try {
            await clientService.delete(id);
            navigate('/clients');
          } catch (err) {
            setError(err.message);
            setDeleteDialogOpen(false);
          }
        }}
        onCancel={() => setDeleteDialogOpen(false)}
      />

      <Grid container spacing={3}>
        {/* Contact Information */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent sx={{ height: '100%' }}>
              <Typography variant="h6" gutterBottom fontWeight="bold">
                Contact Information
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box display="flex" alignItems="center" gap={2} mb={2}>
                <EmailIcon color="action" />
                <div>
                  <Typography variant="body2" color="text.secondary">
                    Email
                  </Typography>
                  <Typography variant="body1">
                    {client.email}
                  </Typography>
                </div>
              </Box>

              <Box display="flex" alignItems="center" gap={2} mb={2}>
                <PhoneIcon color="action" />
                <div>
                  <Typography variant="body2" color="text.secondary">
                    Phone
                  </Typography>
                  <Typography variant="body1">
                    {client.phone || 'N/A'}
                  </Typography>
                </div>
              </Box>

              <Box display="flex" alignItems="center" gap={2}>
                <LocationIcon color="action" />
                <div>
                  <Typography variant="body2" color="text.secondary">
                    Address
                  </Typography>
                  <Typography variant="body1">
                    {client.address || 'N/A'}
                  </Typography>
                  <Typography variant="body2">
                    {[client.city, client.stateOrProvince, client.postalCode]
                      .filter(Boolean)
                      .join(', ')}
                  </Typography>
                  <Typography variant="body2">
                    {client.country}
                  </Typography>
                </div>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Wallet Information */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <Typography variant="h6" gutterBottom fontWeight="bold">
                Wallet Information
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box display="flex" alignItems="center" gap={2} mb={2}>
                <WalletIcon color="action" />
                <div>
                  <Typography variant="body2" color="text.secondary">
                    Current Balance
                  </Typography>
                  <Typography variant="h5" color="primary.main" fontWeight="bold">
                    ${walletBalance.toLocaleString(undefined, { 
                      minimumFractionDigits: 2, 
                      maximumFractionDigits: 2 
                    })}
                  </Typography>
                </div>
              </Box>

              <Box mt="auto">
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={handleDepositClick}
                  fullWidth
                >
                  Add Funds
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Regional Settings */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent sx={{ height: '100%' }}>
              <Typography variant="h6" gutterBottom fontWeight="bold">
                Regional Settings
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box display="flex" alignItems="center" gap={2}>
                <LanguageIcon color="action" />
                <div>
                  <Typography variant="body2" color="text.secondary">
                    Country & Currency
                  </Typography>
                  <Typography variant="body1">
                    {client.country} ({client.preferredCurrency})
                  </Typography>
                </div>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Portfolio Information */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Box display="flex" alignItems="center" gap={1}>
                  <PortfolioIcon color="primary" />
                  <Typography variant="h6" fontWeight="bold">
                    Investment Portfolio
                  </Typography>
                </Box>
                {portfolio && (
                  <Button
                    variant="outlined"
                    size="small"
                    onClick={() => navigate(`/portfolios/${portfolio.id}`)}
                  >
                    View Details
                  </Button>
                )}
              </Box>
              <Divider sx={{ mb: 2 }} />
              
              {portfolio ? (
                <Box>
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6}>
                      <Typography variant="body2" color="text.secondary">
                        Portfolio Name
                      </Typography>
                      <Typography variant="body1" fontWeight="medium">
                        {portfolio.name}
                      </Typography>
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <Typography variant="body2" color="text.secondary">
                        Total Value
                      </Typography>
                      <Typography variant="h6" color="primary" fontWeight="bold">
                        ${portfolio.totalValue?.toLocaleString() || '0.00'}
                      </Typography>
                    </Grid>
                    <Grid item xs={12}>
                      <Typography variant="body2" color="text.secondary">
                        Description
                      </Typography>
                      <Typography variant="body1">
                        {portfolio.description || 'No description available'}
                      </Typography>
                    </Grid>
                    {assetDistribution.length > 0 && (
                      <Grid item xs={12}>
                        <Divider sx={{ my: 2 }} />
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                          Asset Distribution
                        </Typography>
                        <ResponsiveContainer width="100%" height={200}>
                          <PieChart>
                            <Pie
                              data={assetDistribution}
                              cx="50%"
                              cy="50%"
                              labelLine={false}
                              label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                              outerRadius={70}
                              fill="#8884d8"
                              dataKey="value"
                            >
                              {assetDistribution.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                              ))}
                            </Pie>
                            <Tooltip formatter={(value) => `$${value.toLocaleString()}`} />
                          </PieChart>
                        </ResponsiveContainer>
                        <Box display="flex" justifyContent="center" mt={1}>
                          <Button
                            variant="text"
                            size="small"
                            startIcon={<ViewIcon />}
                            onClick={() => navigate(`/portfolios/${portfolio.id}`)}
                          >
                            View Full Portfolio
                          </Button>
                        </Box>
                      </Grid>
                    )}
                  </Grid>
                </Box>
              ) : (
                <Box display="flex" justifyContent="center" py={4}>
                  <Box textAlign="center">
                    <Typography color="text.secondary" gutterBottom>
                      No investment portfolio found for this client
                    </Typography>
                    <Button
                      variant="contained"
                      size="small"
                      onClick={() => navigate(`/portfolios/new?clientId=${id}`)}
                    >
                      Create Portfolio
                    </Button>
                  </Box>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Deposit Funds Dialog */}
      <Dialog 
        open={depositDialogOpen} 
        onClose={handleDepositClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          <Typography variant="h6" fontWeight="bold">
            Add Funds to Wallet
          </Typography>
        </DialogTitle>
        
        <DialogContent>
          {depositError && (
            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setDepositError(null)}>
              {depositError}
            </Alert>
          )}
          
          {depositSuccess && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {depositSuccess}
            </Alert>
          )}
          
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Current Balance: <strong>${walletBalance.toLocaleString(undefined, { 
              minimumFractionDigits: 2, 
              maximumFractionDigits: 2 
            })}</strong>
          </Typography>
          
          <TextField
            fullWidth
            label="Amount to Add"
            type="number"
            value={depositAmount}
            onChange={(e) => setDepositAmount(e.target.value)}
            placeholder="0.00"
            inputProps={{ 
              step: "0.01", 
              min: "0.01" 
            }}
            disabled={depositing}
            margin="normal"
          />
        </DialogContent>
        
        <DialogActions sx={{ p: 3 }}>
          <Button 
            onClick={handleDepositClose} 
            disabled={depositing}
          >
            Cancel
          </Button>
          <Button 
            onClick={handleDepositSubmit} 
            variant="contained" 
            color="primary"
            disabled={depositing || !depositAmount || parseFloat(depositAmount) <= 0}
            startIcon={depositing ? <CircularProgress size={20} /> : <AddIcon />}
          >
            {depositing ? 'Adding Funds...' : 'Add Funds'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
