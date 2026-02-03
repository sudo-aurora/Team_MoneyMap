import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  CircularProgress,
  Alert,
  InputAdornment,
  Card,
  CardContent,
  TablePagination,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as VisibilityIcon,
  Search as SearchIcon,
  CheckCircle as ActiveIcon,
  Cancel as InactiveIcon,
} from '@mui/icons-material';
import ConfirmDialog from '../components/ConfirmDialog';
import { clientService } from '../services/clientService';

export default function Clients() {
  const navigate = useNavigate();
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [clientToDelete, setClientToDelete] = useState(null);

  useEffect(() => {
    loadClients();
  }, [page, rowsPerPage]);

  const loadClients = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await clientService.getAll(page, rowsPerPage);
      
      // Handle both paginated and non-paginated responses
      if (response.content) {
        setClients(response.content);
        setTotalElements(response.totalElements || 0);
      } else if (Array.isArray(response)) {
        setClients(response);
        setTotalElements(response.length);
      } else {
        setClients([]);
        setTotalElements(0);
      }
    } catch (err) {
      console.error('Error loading clients:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      loadClients();
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const response = await clientService.search(searchQuery, page, rowsPerPage);
      
      if (response.content) {
        setClients(response.content);
        setTotalElements(response.totalElements || 0);
      } else if (Array.isArray(response)) {
        setClients(response);
        setTotalElements(response.length);
      }
    } catch (err) {
      console.error('Error searching clients:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
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
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <div>
          <Typography variant="h4" gutterBottom fontWeight="bold">
            Clients
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage your client portfolio
          </Typography>
        </div>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/clients/new')}
        >
          Add Client
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Search Bar */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <TextField
            fullWidth
            placeholder="Search clients by name or email..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
              endAdornment: searchQuery && (
                <InputAdornment position="end">
                  <Button onClick={handleSearch}>Search</Button>
                </InputAdornment>
              ),
            }}
          />
        </CardContent>
      </Card>

      {/* Clients Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell><strong>Name</strong></TableCell>
              <TableCell><strong>Email</strong></TableCell>
              <TableCell><strong>Phone</strong></TableCell>
              <TableCell><strong>Location</strong></TableCell>
              <TableCell><strong>Currency</strong></TableCell>
              <TableCell><strong>Status</strong></TableCell>
              <TableCell align="right"><strong>Actions</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {clients.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center">
                  <Typography color="text.secondary" py={4}>
                    No clients found. Click "Add Client" to create one.
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              clients.map((client) => (
                <TableRow key={client.id} hover>
                  <TableCell>
                    <Typography variant="body1" fontWeight="medium">
                      {client.fullName || `${client.firstName} ${client.lastName}`}
                    </Typography>
                  </TableCell>
                  <TableCell>{client.email}</TableCell>
                  <TableCell>{client.phone || 'N/A'}</TableCell>
                  <TableCell>{getClientLocation(client)}</TableCell>
                  <TableCell>
                    <Chip 
                      label={client.preferredCurrency || 'USD'} 
                      size="small" 
                      color="primary" 
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell>
                    {client.active ? (
                      <Chip
                        icon={<ActiveIcon />}
                        label="Active"
                        color="success"
                        size="small"
                      />
                    ) : (
                      <Chip
                        icon={<InactiveIcon />}
                        label="Inactive"
                        color="default"
                        size="small"
                      />
                    )}
                  </TableCell>
                  <TableCell align="right">
                    <IconButton
                      size="small"
                      color="primary"
                      onClick={() => navigate(`/clients/${client.id}`)}
                      title="View Details"
                    >
                      <VisibilityIcon />
                    </IconButton>
                    <IconButton
                      size="small"
                      color="primary"
                      onClick={() => navigate(`/clients/${client.id}/edit`)}
                      title="Edit"
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton
                      size="small"
                      color="error"
                      onClick={(e) => {
                        e.stopPropagation();
                        setClientToDelete(client);
                        setDeleteDialogOpen(true);
                      }}
                      title="Delete"
                    >
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
        <TablePagination
          component="div"
          count={totalElements}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          rowsPerPageOptions={[5, 10, 25, 50]}
        />
      </TableContainer>

      <ConfirmDialog
        open={deleteDialogOpen}
        title="Delete Client"
        message={`Are you sure you want to delete ${clientToDelete?.firstName} ${clientToDelete?.lastName}? This will also delete their portfolio and all associated assets. This action cannot be undone.`}
        onConfirm={async () => {
          try {
            await clientService.delete(clientToDelete.id);
            setDeleteDialogOpen(false);
            setClientToDelete(null);
            loadClients();
          } catch (err) {
            setError(err.message);
            setDeleteDialogOpen(false);
          }
        }}
        onCancel={() => {
          setDeleteDialogOpen(false);
          setClientToDelete(null);
        }}
      />
    </Box>
  );
}
