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
  IconButton,
  CircularProgress,
  Alert,
  Card,
  CardContent,
  TablePagination,
  TextField,
  InputAdornment,
  List,
  ListItem,
  ListItemText,
  ListItemButton,
  Divider,
} from '@mui/material';

import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as VisibilityIcon,
  Search as SearchIcon,
  CheckCircle as ActiveIcon,
  Cancel as InactiveIcon,
  Person as PersonIcon,
} from '@mui/icons-material';

import ConfirmDialog from '../components/ConfirmDialog';
import { clientService } from '../services/clientService';

export default function Clients() {
  const navigate = useNavigate();

  const [clients, setClients] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [searching, setSearching] = useState(false);
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

  // SEARCH - Shows results directly below
  const handleSearchChange = async (e) => {
    const value = e.target.value;
    setSearchQuery(value);

    if (!value || value.trim() === '') {
      setSearchResults([]);
      setSearching(false);
      return;
    }

    setSearching(true);
    
    try {
      const response = await clientService.search(value, 0, 50);

      if (response.content) {
        setSearchResults(response.content);
      } else if (Array.isArray(response)) {
        setSearchResults(response);
      } else {
        setSearchResults([]);
      }

    } catch (err) {
      console.error("Search error:", err);
      setSearchResults([]);
    } finally {
      setSearching(false);
    }
  };

  const handleClearSearch = () => {
    setSearchQuery('');
    setSearchResults([]);
  };

  const getClientDisplayName = (client) => {
    return client.fullName || `${client.firstName || ''} ${client.lastName || ''}`.trim();
  };

  const getClientLocation = (client) => {
    const parts = [];
    if (client.city) parts.push(client.city);
    if (client.country) parts.push(client.country);
    return parts.join(', ') || 'N/A';
  };

  const handleClientClick = (client) => {
    navigate(`/clients/${client.id}`);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
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
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <div>
          <Typography variant="h4" fontWeight="bold">
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

      {/* DIRECT SEARCH - No dropdown */}
      <Card sx={{ mb: 3 }}>
        <CardContent sx={{ pb: searchResults.length > 0 ? 0 : 2 }}>
          <TextField
            fullWidth
            placeholder="Search clients by name..."
            value={searchQuery}
            onChange={handleSearchChange}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
              endAdornment: searchQuery && (
                <InputAdornment position="end">
                  <Button size="small" onClick={handleClearSearch}>
                    Clear
                  </Button>
                </InputAdornment>
              ),
            }}
          />

          {/* Search Results Display - Shows directly below search */}
          {searching && (
            <Box display="flex" justifyContent="center" py={2}>
              <CircularProgress size={24} />
            </Box>
          )}

          {searchResults.length > 0 && (
            <Box mt={2}>
              <Typography variant="subtitle2" color="text.secondary" mb={1}>
                Search Results ({searchResults.length})
              </Typography>
              <List sx={{ 
                bgcolor: 'background.paper', 
                border: '1px solid #e0e0e0',
                borderRadius: 1,
                maxHeight: 300,
                overflow: 'auto'
              }}>
                {searchResults.map((client, index) => (
                  <div key={client.id}>
                    {index > 0 && <Divider />}
                    <ListItem disablePadding>
                      <ListItemButton onClick={() => handleClientClick(client)}>
                        <Box sx={{ mr: 2, color: 'primary.main' }}>
                          <PersonIcon />
                        </Box>
                        <ListItemText 
                          primary={
                            <Typography fontWeight="medium">
                              {getClientDisplayName(client)}
                            </Typography>
                          }
                          secondary={
                            <>
                              <Typography variant="body2" color="text.secondary">
                                {client.email}
                              </Typography>
                              <Typography variant="caption" color="text.secondary">
                                {getClientLocation(client)} • {client.phone || 'No phone'}
                              </Typography>
                            </>
                          }
                        />
                        <Chip
                          label={client.active ? "Active" : "Inactive"}
                          size="small"
                          color={client.active ? "success" : "default"}
                          variant="outlined"
                        />
                      </ListItemButton>
                    </ListItem>
                  </div>
                ))}
              </List>
            </Box>
          )}

          {searchQuery && !searching && searchResults.length === 0 && (
            <Typography color="text.secondary" align="center" py={2}>
              No clients found matching "{searchQuery}"
            </Typography>
          )}
        </CardContent>
      </Card>

      {/* MAIN CLIENTS TABLE - Only show when no search */}
      {!searchQuery && (
        <>
          <Typography variant="h6" gutterBottom>
            All Clients
          </Typography>
          
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
                        No clients found.
                      </Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  clients.map((client) => (
                    <TableRow key={client.id} hover>
                      <TableCell>
                        <Typography fontWeight="medium">
                          {getClientDisplayName(client)}
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
                          <Chip icon={<ActiveIcon />} label="Active" color="success" size="small" />
                        ) : (
                          <Chip icon={<InactiveIcon />} label="Inactive" size="small" />
                        )}
                      </TableCell>

                      <TableCell align="right">
                        <IconButton
                          size="small"
                          color="primary"
                          onClick={() => navigate(`/clients/${client.id}`)}
                        >
                          <VisibilityIcon />
                        </IconButton>

                        <IconButton
                          size="small"
                          color="primary"
                          onClick={() => navigate(`/clients/${client.id}/edit`)}
                        >
                          <EditIcon />
                        </IconButton>

                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => {
                            setClientToDelete(client);
                            setDeleteDialogOpen(true);
                          }}
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
        </>
      )}

      {/* DELETE DIALOG */}
      <ConfirmDialog
        open={deleteDialogOpen}
        title="Delete Client"
        message={`Are you sure you want to delete ${clientToDelete?.firstName} ${clientToDelete?.lastName}?`}
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