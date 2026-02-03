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
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Card,
  CardContent,
  TablePagination,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  CheckCircle as ValidateIcon,
  Send as SendIcon,
  Done as CompleteIcon,
} from '@mui/icons-material';
import { paymentService } from '../services/paymentService';

const statusColors = {
  CREATED: 'default',
  VALIDATED: 'info',
  SENT: 'warning',
  COMPLETED: 'success',
  FAILED: 'error',
};

export default function Payments() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    loadPayments();
  }, [filterStatus, page, rowsPerPage]);

  const loadPayments = async () => {
    try {
      setLoading(true);
      setError(null);
      
      let response;
      if (filterStatus === 'ALL') {
        response = await paymentService.getAll(page, rowsPerPage);
      } else {
        response = await paymentService.getByStatus(filterStatus, page, rowsPerPage);
      }
      
      if (response.content) {
        setPayments(response.content);
        setTotalElements(response.totalElements || 0);
      } else if (Array.isArray(response)) {
        setPayments(response);
        setTotalElements(response.length);
      } else {
        setPayments([]);
        setTotalElements(0);
      }
    } catch (err) {
      console.error('Error loading payments:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleValidate = async (id) => {
    try {
      await paymentService.validate(id);
      loadPayments();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleSend = async (id) => {
    try {
      await paymentService.send(id);
      loadPayments();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleComplete = async (id) => {
    try {
      await paymentService.complete(id);
      loadPayments();
    } catch (err) {
      setError(err.message);
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
      <Box mb={3}>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          Payments
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Track and manage payment transactions
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <FormControl sx={{ minWidth: 200 }}>
            <InputLabel>Filter by Status</InputLabel>
            <Select
              value={filterStatus}
              label="Filter by Status"
              onChange={(e) => {
                setFilterStatus(e.target.value);
                setPage(0);
              }}
            >
              <MenuItem value="ALL">All Statuses</MenuItem>
              <MenuItem value="CREATED">Created</MenuItem>
              <MenuItem value="VALIDATED">Validated</MenuItem>
              <MenuItem value="SENT">Sent</MenuItem>
              <MenuItem value="COMPLETED">Completed</MenuItem>
              <MenuItem value="FAILED">Failed</MenuItem>
            </Select>
          </FormControl>
        </CardContent>
      </Card>

      {payments.length === 0 ? (
        <Card>
          <CardContent>
            <Box display="flex" justifyContent="center" py={8}>
              <Typography color="text.secondary">
                No payments found
              </Typography>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell><strong>Reference</strong></TableCell>
                <TableCell><strong>From</strong></TableCell>
                <TableCell><strong>To</strong></TableCell>
                <TableCell align="right"><strong>Amount</strong></TableCell>
                <TableCell><strong>Currency</strong></TableCell>
                <TableCell><strong>Status</strong></TableCell>
                <TableCell><strong>Description</strong></TableCell>
                <TableCell align="center"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {payments.map((payment) => (
                <TableRow key={payment.id} hover>
                  <TableCell>
                    <Typography variant="body2" fontWeight="medium">
                      {payment.paymentReference || payment.reference || `PAY-${payment.id}`}
                    </Typography>
                  </TableCell>
                  <TableCell>{payment.sourceAccount}</TableCell>
                  <TableCell>{payment.destinationAccount}</TableCell>
                  <TableCell align="right">
                    <Typography fontWeight="medium">
                      ${payment.amount?.toLocaleString()}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Chip label={payment.currency} size="small" variant="outlined" />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={payment.status}
                      size="small"
                      color={statusColors[payment.status] || 'default'}
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" color="text.secondary" noWrap sx={{ maxWidth: 200 }}>
                      {payment.description || 'N/A'}
                    </Typography>
                  </TableCell>
                  <TableCell align="center">
                    <Box display="flex" gap={0.5} justifyContent="center">
                      {payment.status === 'CREATED' && (
                        <Tooltip title="Validate">
                          <IconButton size="small" color="info" onClick={() => handleValidate(payment.id)}>
                            <ValidateIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      {payment.status === 'VALIDATED' && (
                        <Tooltip title="Send">
                          <IconButton size="small" color="warning" onClick={() => handleSend(payment.id)}>
                            <SendIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      {payment.status === 'SENT' && (
                        <Tooltip title="Complete">
                          <IconButton size="small" color="success" onClick={() => handleComplete(payment.id)}>
                            <CompleteIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
          <TablePagination
            component="div"
            count={totalElements}
            page={page}
            onPageChange={(e, newPage) => setPage(newPage)}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={(e) => {
              setRowsPerPage(parseInt(e.target.value, 10));
              setPage(0);
            }}
            rowsPerPageOptions={[5, 10, 25, 50]}
          />
        </TableContainer>
      )}
    </Box>
  );
}
