import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  TextField,
  Button,
  Grid,
  CircularProgress,
  Alert,
  MenuItem,
  IconButton,
} from '@mui/material';
import { ArrowBack as BackIcon, Save as SaveIcon } from '@mui/icons-material';
import { clientService } from '../services/clientService';

const COUNTRIES = [
  { code: 'US', name: 'United States', currency: 'USD', timezone: 'America/New_York', locale: 'en_US' },
  { code: 'GB', name: 'United Kingdom', currency: 'GBP', timezone: 'Europe/London', locale: 'en_GB' },
  { code: 'IN', name: 'India', currency: 'INR', timezone: 'Asia/Kolkata', locale: 'en_IN' },
  { code: 'DE', name: 'Germany', currency: 'EUR', timezone: 'Europe/Berlin', locale: 'de_DE' },
  { code: 'CA', name: 'Canada', currency: 'CAD', timezone: 'America/Toronto', locale: 'en_CA' },
  { code: 'AU', name: 'Australia', currency: 'AUD', timezone: 'Australia/Sydney', locale: 'en_AU' },
];

export default function ClientForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    stateOrProvince: '',
    postalCode: '',
    countryCode: 'US',
    country: 'United States',
    preferredCurrency: 'USD',
    timezone: 'America/New_York',
    locale: 'en_US',
  });

  useEffect(() => {
    if (isEdit) {
      loadClient();
    }
  }, [id]);

  const loadClient = async () => {
    try {
      setLoading(true);
      const data = await clientService.getById(id);
      setFormData({
        firstName: data.firstName || '',
        lastName: data.lastName || '',
        email: data.email || '',
        phone: data.phone || '',
        address: data.address || '',
        city: data.city || '',
        stateOrProvince: data.stateOrProvince || '',
        postalCode: data.postalCode || '',
        countryCode: data.countryCode || 'US',
        country: data.country || 'United States',
        preferredCurrency: data.preferredCurrency || 'USD',
        timezone: data.timezone || 'America/New_York',
        locale: data.locale || 'en_US',
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCountryChange = (countryCode) => {
    const country = COUNTRIES.find(c => c.code === countryCode);
    if (country) {
      setFormData(prev => ({
        ...prev,
        countryCode: country.code,
        country: country.name,
        preferredCurrency: country.currency,
        timezone: country.timezone,
        locale: country.locale,
      }));
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'countryCode') {
      handleCountryChange(value);
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      setError(null);
      
      if (isEdit) {
        await clientService.update(id, formData);
      } else {
        await clientService.create(formData);
      }
      
      navigate('/clients');
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
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
      <Box display="flex" alignItems="center" gap={2} mb={3}>
        <IconButton onClick={() => navigate('/clients')}>
          <BackIcon />
        </IconButton>
        <Typography variant="h4" fontWeight="bold">
          {isEdit ? 'Edit Client' : 'Create New Client'}
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Card>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <Grid container spacing={3}>
              {/* Personal Information */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom color="primary">
                  Personal Information
                </Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="First Name"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Last Name"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  type="email"
                  label="Email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Phone"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  placeholder="+1-555-1234"
                />
              </Grid>

              {/* Location Information */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom color="primary" sx={{ mt: 2 }}>
                  Location Information
                </Typography>
              </Grid>
              
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Address"
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="City"
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="State/Province"
                  name="stateOrProvince"
                  value={formData.stateOrProvince}
                  onChange={handleChange}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Postal Code"
                  name="postalCode"
                  value={formData.postalCode}
                  onChange={handleChange}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  select
                  label="Country"
                  name="countryCode"
                  value={formData.countryCode}
                  onChange={handleChange}
                >
                  {COUNTRIES.map((country) => (
                    <MenuItem key={country.code} value={country.code}>
                      {country.name}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              {/* Regional Settings (Auto-filled based on country) */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom color="primary" sx={{ mt: 2 }}>
                  Regional Settings
                </Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  disabled
                  label="Currency"
                  value={formData.preferredCurrency}
                  helperText="Auto-filled based on country"
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  disabled
                  label="Timezone"
                  value={formData.timezone}
                  helperText="Auto-filled based on country"
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  disabled
                  label="Locale"
                  value={formData.locale}
                  helperText="Auto-filled based on country"
                />
              </Grid>

              {/* Action Buttons */}
              <Grid item xs={12}>
                <Box display="flex" gap={2} justifyContent="flex-end" sx={{ mt: 2 }}>
                  <Button
                    variant="outlined"
                    onClick={() => navigate('/clients')}
                    disabled={saving}
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    variant="contained"
                    startIcon={saving ? <CircularProgress size={20} /> : <SaveIcon />}
                    disabled={saving}
                  >
                    {saving ? 'Saving...' : (isEdit ? 'Update Client' : 'Create Client')}
                  </Button>
                </Box>
              </Grid>
            </Grid>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
