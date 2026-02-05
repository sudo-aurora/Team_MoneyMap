import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
  TextField,
  InputAdornment,
  Avatar,
  Divider,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  MenuItem,
  Stack,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
  CheckCircle as ActiveIcon,
  Cancel as InactiveIcon,
  Person as PersonIcon,
  AccountBalance as PortfolioIcon,
  Phone as PhoneIcon,
  Email as EmailIcon,
  LocationOn as LocationIcon,
  ArrowForward as ArrowForwardIcon,
  Close as CloseIcon,
  Save as SaveIcon,
} from '@mui/icons-material';
import { clientService } from '../services/clientService';

const COUNTRIES = [
  { code: 'US', name: 'United States', currency: 'USD', timezone: 'America/New_York', locale: 'en_US' },
  { code: 'GB', name: 'United Kingdom', currency: 'GBP', timezone: 'Europe/London', locale: 'en_GB' },
  { code: 'IN', name: 'India', currency: 'INR', timezone: 'Asia/Kolkata', locale: 'en_IN' },
  { code: 'DE', name: 'Germany', currency: 'EUR', timezone: 'Europe/Berlin', locale: 'de_DE' },
  { code: 'CA', name: 'Canada', currency: 'CAD', timezone: 'America/Toronto', locale: 'en_CA' },
  { code: 'AU', name: 'Australia', currency: 'AUD', timezone: 'Australia/Sydney', locale: 'en_AU' },
];

export default function UnifiedClientPortfolio() {
  const navigate = useNavigate();
  
  const [clients, setClients] = useState([]);
  const [filteredClients, setFilteredClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  
  // State for dialogs
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [clientToDelete, setClientToDelete] = useState(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [clientToEdit, setClientToEdit] = useState(null);
  const [editFormData, setEditFormData] = useState(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadClients();
  }, []);

  useEffect(() => {
    filterClients();
  }, [searchQuery, clients]);

  const loadClients = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await clientService.getAll(0, 100);

      if (response.content) {
        setClients(response.content);
      } else if (Array.isArray(response)) {
        setClients(response);
      } else {
        setClients([]);
      }

    } catch (err) {
      console.error('Error loading clients:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filterClients = () => {
    if (!searchQuery || searchQuery.trim() === '') {
      setFilteredClients(clients);
      return;
    }

    const query = searchQuery.toLowerCase().trim();
    
    const filtered = clients.filter(client => {
      const firstName = (client.firstName || '').toLowerCase();
      const lastName = (client.lastName || '').toLowerCase();
      const email = (client.email || '').toLowerCase();
      
      // Check if first name starts with query
      if (firstName.startsWith(query)) return true;
      
      // Check if last name starts with query
      if (lastName.startsWith(query)) return true;
      
      // Check if email starts with query
      if (email.startsWith(query)) return true;
      
      return false;
    });

    setFilteredClients(filtered);
  };

  const handleDeleteClient = async () => {
    try {
      await clientService.delete(clientToDelete.id);
      setDeleteDialogOpen(false);
      setClientToDelete(null);
      loadClients();
    } catch (err) {
      setError(err.message);
      setDeleteDialogOpen(false);
    }
  };

  const handleEditClient = (client) => {
    setClientToEdit(client);
    setEditFormData({
      firstName: client.firstName || '',
      lastName: client.lastName || '',
      email: client.email || '',
      phone: client.phone || '',
      address: client.address || '',
      city: client.city || '',
      stateOrProvince: client.stateOrProvince || '',
      postalCode: client.postalCode || '',
      countryCode: client.countryCode || 'US',
      country: client.country || 'United States',
      preferredCurrency: client.preferredCurrency || 'USD',
      timezone: client.timezone || 'America/New_York',
      locale: client.locale || 'en_US',
    });
    setEditDialogOpen(true);
  };

  const handleSaveEdit = async () => {
    try {
      setSaving(true);
      await clientService.update(clientToEdit.id, editFormData);
      setEditDialogOpen(false);
      setClientToEdit(null);
      setEditFormData(null);
      loadClients();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const getClientDisplayName = (client) => {
    return client.fullName || `${client.firstName || ''} ${client.lastName || ''}`.trim() || 'Unknown Client';
  };

  const getClientInitials = (client) => {
    const firstName = client.firstName || '';
    const lastName = client.lastName || '';
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase() || 'U';
  };

  const getClientLocation = (client) => {
    const parts = [];
    if (client.city) parts.push(client.city);
    if (client.country) parts.push(client.country);
    return parts.join(', ') || 'N/A';
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
      {/* HEADER */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <div>
          <Typography variant="h4" fontWeight="bold">
            Client & Portfolio Management
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage clients and their investment portfolios
          </Typography>
        </div>

        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/clients/new')}
          size="large"
        >
          Add Client
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* SEARCH BAR */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <TextField
            fullWidth
            placeholder="Search clients (starts with first name, last name, or email)..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="action" />
                </InputAdornment>
              ),
              endAdornment: searchQuery && (
                <InputAdornment position="end">
                  <IconButton size="small" onClick={() => setSearchQuery('')}>
                    <CloseIcon fontSize="small" />
                  </IconButton>
                </InputAdornment>
              ),
            }}
            sx={{
              '& .MuiOutlinedInput-root': {
                '& fieldset': {
                  borderColor: 'transparent',
                },
                '&:hover fieldset': {
                  borderColor: 'transparent',
                },
                '&.Mui-focused fieldset': {
                  borderColor: 'primary.main',
                },
              },
            }}
          />
        </CardContent>
      </Card>

      {/* RESULTS COUNT */}
      <Box mb={2}>
        <Typography variant="body2" color="text.secondary">
          {filteredClients.length} {filteredClients.length === 1 ? 'client' : 'clients'} found
          {searchQuery && ` matching "${searchQuery}"`}
        </Typography>
      </Box>

      {/* CLIENT CARDS GRID */}
      {filteredClients.length === 0 ? (
        <Card>
          <CardContent>
            <Box textAlign="center" py={6}>
              <PersonIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
              <Typography variant="h6" color="text.secondary" gutterBottom>
                {searchQuery ? 'No clients found' : 'No clients yet'}
              </Typography>
              <Typography variant="body2" color="text.secondary" mb={3}>
                {searchQuery 
                  ? 'Try adjusting your search criteria'
                  : 'Get started by adding your first client'
                }
              </Typography>
              {!searchQuery && (
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={() => navigate('/clients/new')}
                >
                  Add Client
                </Button>
              )}
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {filteredClients.map((client) => (
            <Grid item xs={12} md={6} lg={4} key={client.id}>
              <Card 
                sx={{ 
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  transition: 'all 0.3s',
                  '&:hover': {
                    boxShadow: 6,
                    transform: 'translateY(-4px)',
                  },
                }}
              >
                <CardContent sx={{ flexGrow: 1, p: 3 }}>
                  {/* CLIENT HEADER */}
                  <Box display="flex" alignItems="flex-start" mb={2}>
                    <Avatar
                      sx={{
                        width: 56,
                        height: 56,
                        bgcolor: 'primary.main',
                        fontSize: '1.5rem',
                        fontWeight: 'bold',
                      }}
                    >
                      {getClientInitials(client)}
                    </Avatar>
                    <Box ml={2} flexGrow={1}>
                      <Typography variant="h6" fontWeight="bold">
                        {getClientDisplayName(client)}
                      </Typography>
                      <Chip
                        icon={client.active ? <ActiveIcon /> : <InactiveIcon />}
                        label={client.active ? 'Active' : 'Inactive'}
                        color={client.active ? 'success' : 'default'}
                        size="small"
                        sx={{ mt: 0.5 }}
                      />
                    </Box>
                    <Box>
                      <Tooltip title="Edit">
                        <IconButton
                          size="small"
                          onClick={() => handleEditClient(client)}
                        >
                          <EditIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Delete">
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => {
                            setClientToDelete(client);
                            setDeleteDialogOpen(true);
                          }}
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    </Box>
                  </Box>

                  <Divider sx={{ my: 2 }} />

                  {/* CLIENT INFO */}
                  <Stack spacing={1.5}>
                    <Box display="flex" alignItems="center" gap={1}>
                      <EmailIcon fontSize="small" color="action" />
                      <Typography variant="body2" color="text.secondary" noWrap>
                        {client.email}
                      </Typography>
                    </Box>

                    {client.phone && (
                      <Box display="flex" alignItems="center" gap={1}>
                        <PhoneIcon fontSize="small" color="action" />
                        <Typography variant="body2" color="text.secondary">
                          {client.phone}
                        </Typography>
                      </Box>
                    )}

                    <Box display="flex" alignItems="center" gap={1}>
                      <LocationIcon fontSize="small" color="action" />
                      <Typography variant="body2" color="text.secondary">
                        {getClientLocation(client)}
                      </Typography>
                    </Box>
                  </Stack>

                  <Divider sx={{ my: 2 }} />

                  {/* NAVIGATION BUTTONS - Removed redundant "View Portfolio" */}
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<PersonIcon />}
                    endIcon={<ArrowForwardIcon />}
                    onClick={() => navigate(`/clients/${client.id}`)}
                    sx={{
                      justifyContent: 'space-between',
                      textTransform: 'none',
                      py: 1.5,
                    }}
                  >
                    <Box textAlign="left">
                      <Typography variant="caption" display="block" color="text.secondary">
                        View Client Details
                      </Typography>
                    </Box>
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Delete Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Delete Client</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete <strong>{clientToDelete ? getClientDisplayName(clientToDelete) : 'this client'}</strong>?
            This action cannot be undone and will also delete all associated portfolios and assets.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleDeleteClient} color="error" variant="contained">
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Edit Client</DialogTitle>
        <DialogContent>
          {editFormData && (
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="First Name"
                  name="firstName"
                  value={editFormData.firstName}
                  onChange={(e) => setEditFormData({...editFormData, firstName: e.target.value})}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Last Name"
                  name="lastName"
                  value={editFormData.lastName}
                  onChange={(e) => setEditFormData({...editFormData, lastName: e.target.value})}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="email"
                  label="Email"
                  name="email"
                  value={editFormData.email}
                  onChange={(e) => setEditFormData({...editFormData, email: e.target.value})}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Phone"
                  name="phone"
                  value={editFormData.phone}
                  onChange={(e) => setEditFormData({...editFormData, phone: e.target.value})}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Address"
                  name="address"
                  value={editFormData.address}
                  onChange={(e) => setEditFormData({...editFormData, address: e.target.value})}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="City"
                  name="city"
                  value={editFormData.city}
                  onChange={(e) => setEditFormData({...editFormData, city: e.target.value})}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="State/Province"
                  name="stateOrProvince"
                  value={editFormData.stateOrProvince}
                  onChange={(e) => setEditFormData({...editFormData, stateOrProvince: e.target.value})}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Postal Code"
                  name="postalCode"
                  value={editFormData.postalCode}
                  onChange={(e) => setEditFormData({...editFormData, postalCode: e.target.value})}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  select
                  label="Country"
                  name="countryCode"
                  value={editFormData.countryCode}
                  onChange={(e) => {
                    const country = COUNTRIES.find(c => c.code === e.target.value);
                    if (country) {
                      setEditFormData({
                        ...editFormData,
                        countryCode: country.code,
                        country: country.name,
                        preferredCurrency: country.currency,
                        timezone: country.timezone,
                        locale: country.locale,
                      });
                    }
                  }}
                >
                  {COUNTRIES.map((country) => (
                    <MenuItem key={country.code} value={country.code}>
                      {country.name}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
          <Button 
            onClick={handleSaveEdit} 
            variant="contained"
            disabled={saving}
            startIcon={saving ? <CircularProgress size={20} /> : <SaveIcon />}
          >
            {saving ? 'Saving...' : 'Save Changes'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}