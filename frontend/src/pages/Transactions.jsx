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
import { transactionService } from '../services/transactionService';

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
  }, [page, rowsPerPage, filterType]);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Load transactions from the API
      const response = await transactionService.getAll(page, rowsPerPage);
      const transactionsData = response.content || response || [];
      setTransactions(transactionsData);
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
          {transactions.length === 0 ? (
            <Alert severity="info">
              <Typography variant="body2">
                No transactions found. Buy or sell assets to see transaction history.
              </Typography>
            </Alert>
          ) : (
            <TableContainer component={Paper} variant="outlined">
              <Table>
                <TableHead sx={{ backgroundColor: 'action.hover' }}>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 'bold' }}>Date</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Type</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Asset</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Price</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Total Amount</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Notes</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {paginatedTransactions.map((transaction) => (
                    <TableRow key={transaction.id}>
                      <TableCell>
                        {new Date(transaction.transactionDate).toLocaleDateString()}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={transaction.transactionType}
                          color={typeColors[transaction.transactionType] || 'default'}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        {transaction.assetSymbol || transaction.assetName || 'N/A'}
                      </TableCell>
                      <TableCell>{transaction.quantity?.toLocaleString()}</TableCell>
                      <TableCell>
                        ${transaction.pricePerUnit?.toLocaleString(undefined, {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2
                        })}
                      </TableCell>
                      <TableCell>
                        <Typography
                          color={transaction.transactionType === 'BUY' ? 'error.main' : 'success.main'}
                          fontWeight="bold"
                        >
                          {transaction.transactionType === 'BUY' ? '-' : '+'}
                          ${transaction.totalAmount?.toLocaleString(undefined, {
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2
                          })}
                        </Typography>
                      </TableCell>
                      <TableCell>{transaction.notes || '-'}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
          
          {transactions.length > 0 && (
            <TablePagination
              rowsPerPageOptions={[5, 10, 25]}
              component="div"
              count={transactions.length}
              rowsPerPage={rowsPerPage}
              page={page}
              onPageChange={(event, newPage) => setPage(newPage)}
              onRowsPerPageChange={(event) => {
                setRowsPerPage(parseInt(event.target.value, 10));
                setPage(0);
              }}
            />
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
