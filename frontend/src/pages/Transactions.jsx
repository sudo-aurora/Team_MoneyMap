import { useState, useEffect } from 'react';
import {
  Box,
  Typography,
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
  TablePagination,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Card,
  CardContent,
} from '@mui/material';
import { clientService } from '../services/clientService';

const TRANSACTION_TYPES = ['ALL', 'BUY', 'SELL', 'DIVIDEND', 'INTEREST', 'TRANSFER_IN', 'TRANSFER_OUT'];

const typeColors = {
  BUY: 'success',
  SELL: 'error',
  DIVIDEND: 'info',
  INTEREST: 'info',
  TRANSFER_IN: 'success',
  TRANSFER_OUT: 'warning',
};

export default function Transactions() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterType, setFilterType] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  useEffect(() => {
    loadTransactions();
  }, []);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Fetch all clients to get their transactions
      const clientsResponse = await clientService.getAll(0, 100);
      const clients = clientsResponse.content || clientsResponse || [];
      
      // For demo purposes, we'll show a message that transactions are linked to assets
      setTransactions([]);
    } catch (err) {
      console.error('Error loading transactions:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredTransactions = filterType === 'ALL' 
    ? transactions 
    : transactions.filter(t => t.transactionType === filterType);

  const paginatedTransactions = filteredTransactions.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage
  );

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom fontWeight="bold">
        Transactions
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        View all asset transactions across portfolios
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <FormControl sx={{ minWidth: 200 }}>
            <InputLabel>Transaction Type</InputLabel>
            <Select
              value={filterType}
              label="Transaction Type"
              onChange={(e) => setFilterType(e.target.value)}
            >
              {TRANSACTION_TYPES.map((type) => (
                <MenuItem key={type} value={type}>
                  {type}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Alert severity="info">
            <Typography variant="body2">
              <strong>Transactions are linked to specific assets.</strong>
            </Typography>
            <Typography variant="body2" sx={{ mt: 1 }}>
              To view transactions for an asset:
            </Typography>
            <ul style={{ marginTop: 8, marginBottom: 0 }}>
              <li>Go to <strong>Portfolios</strong> page</li>
              <li>Click on a portfolio to see its assets</li>
              <li>Click on an asset to view its transaction history</li>
            </ul>
          </Alert>
        </CardContent>
      </Card>
    </Box>
  );
}
