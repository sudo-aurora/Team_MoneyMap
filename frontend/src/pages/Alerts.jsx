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
  Alert as MuiAlert,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Card,
  CardContent,
  TablePagination,
  IconButton,
  Tooltip,
  Grid,
} from '@mui/material';
import {
  CheckCircle as AcknowledgeIcon,
  Search as InvestigateIcon,
  Close as DismissIcon,
  Done as CloseIcon,
} from '@mui/icons-material';
import { alertService } from '../services/alertService';

const severityColors = {
  HIGH: 'error',
  MEDIUM: 'warning',
  LOW: 'info',
};

const statusColors = {
  OPEN: 'error',
  ACKNOWLEDGED: 'warning',
  INVESTIGATING: 'info',
  CLOSED: 'success',
  DISMISSED: 'default',
};

export default function Alerts() {
  const [alerts, setAlerts] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [filterSeverity, setFilterSeverity] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    loadAlerts();
    loadStatistics();
  }, [filterStatus, filterSeverity, page, rowsPerPage]);

  const loadStatistics = async () => {
    try {
      const statistics = await alertService.getStatistics();
      setStats(statistics);
    } catch (err) {
      console.error('Error loading statistics:', err);
    }
  };

  const loadAlerts = async () => {
    try {
      setLoading(true);
      setError(null);
      
      let response;
      if (filterStatus !== 'ALL' && filterSeverity !== 'ALL') {
        response = await alertService.getAll(page, rowsPerPage);
      } else if (filterStatus !== 'ALL') {
        response = await alertService.getByStatus(filterStatus, page, rowsPerPage);
      } else if (filterSeverity !== 'ALL') {
        response = await alertService.getBySeverity(filterSeverity, page, rowsPerPage);
      } else {
        response = await alertService.getAll(page, rowsPerPage);
      }
      
      if (response.content) {
        setAlerts(response.content);
        setTotalElements(response.totalElements || 0);
      } else if (Array.isArray(response)) {
        setAlerts(response);
        setTotalElements(response.length);
      } else {
        setAlerts([]);
        setTotalElements(0);
      }
    } catch (err) {
      console.error('Error loading alerts:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAcknowledge = async (id) => {
    try {
      await alertService.acknowledge(id);
      loadAlerts();
      loadStatistics();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleInvestigate = async (id) => {
    try {
      await alertService.updateStatus(id, {
        status: 'INVESTIGATING',
        operatorName: 'Admin',
      });
      loadAlerts();
      loadStatistics();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleClose = async (id) => {
    try {
      await alertService.updateStatus(id, {
        status: 'CLOSED',
        operatorName: 'Admin',
        resolutionNotes: 'Resolved',
      });
      loadAlerts();
      loadStatistics();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDismiss = async (id) => {
    try {
      await alertService.updateStatus(id, {
        status: 'DISMISSED',
        operatorName: 'Admin',
        resolutionNotes: 'False positive',
      });
      loadAlerts();
      loadStatistics();
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading && !stats) {
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
          Alerts & Monitoring
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Monitor and manage transaction alerts
        </Typography>
      </Box>

      {error && (
        <MuiAlert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </MuiAlert>
      )}

      {/* Statistics Cards */}
      {stats && (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={6} sm={3}>
            <Card>
              <CardContent>
                <Typography variant="body2" color="text.secondary">
                  Open Alerts
                </Typography>
                <Typography variant="h4" color="error">
                  {stats.openCount || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Card>
              <CardContent>
                <Typography variant="body2" color="text.secondary">
                  High Severity
                </Typography>
                <Typography variant="h4" color="error">
                  {stats.highSeverityCount || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Card>
              <CardContent>
                <Typography variant="body2" color="text.secondary">
                  Investigating
                </Typography>
                <Typography variant="h4" color="warning">
                  {stats.investigatingCount || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Card>
              <CardContent>
                <Typography variant="body2" color="text.secondary">
                  Closed
                </Typography>
                <Typography variant="h4" color="success">
                  {stats.closedCount || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Filters */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box display="flex" gap={2}>
            <FormControl sx={{ minWidth: 200 }}>
              <InputLabel>Status</InputLabel>
              <Select
                value={filterStatus}
                label="Status"
                onChange={(e) => {
                  setFilterStatus(e.target.value);
                  setPage(0);
                }}
              >
                <MenuItem value="ALL">All Statuses</MenuItem>
                <MenuItem value="OPEN">Open</MenuItem>
                <MenuItem value="ACKNOWLEDGED">Acknowledged</MenuItem>
                <MenuItem value="INVESTIGATING">Investigating</MenuItem>
                <MenuItem value="CLOSED">Closed</MenuItem>
                <MenuItem value="DISMISSED">Dismissed</MenuItem>
              </Select>
            </FormControl>
            <FormControl sx={{ minWidth: 200 }}>
              <InputLabel>Severity</InputLabel>
              <Select
                value={filterSeverity}
                label="Severity"
                onChange={(e) => {
                  setFilterSeverity(e.target.value);
                  setPage(0);
                }}
              >
                <MenuItem value="ALL">All Severities</MenuItem>
                <MenuItem value="HIGH">High</MenuItem>
                <MenuItem value="MEDIUM">Medium</MenuItem>
                <MenuItem value="LOW">Low</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </CardContent>
      </Card>

      {/* Alerts Table */}
      {alerts.length === 0 ? (
        <Card>
          <CardContent>
            <Box display="flex" justifyContent="center" py={8}>
              <Typography color="text.secondary">
                No alerts found
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
                <TableCell><strong>Severity</strong></TableCell>
                <TableCell><strong>Status</strong></TableCell>
                <TableCell><strong>Message</strong></TableCell>
                <TableCell><strong>Account</strong></TableCell>
                <TableCell><strong>Created</strong></TableCell>
                <TableCell align="center"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {alerts.map((alert) => (
                <TableRow key={alert.id} hover>
                  <TableCell>
                    <Typography variant="body2" fontWeight="medium">
                      {alert.alertReference || `ALT-${alert.id}`}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={alert.severity}
                      size="small"
                      color={severityColors[alert.severity] || 'default'}
                    />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={alert.status}
                      size="small"
                      color={statusColors[alert.status] || 'default'}
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" noWrap sx={{ maxWidth: 300 }}>
                      {alert.message}
                    </Typography>
                  </TableCell>
                  <TableCell>{alert.accountId || 'N/A'}</TableCell>
                  <TableCell>
                    <Typography variant="body2" color="text.secondary">
                      {alert.createdAt ? new Date(alert.createdAt).toLocaleDateString() : 'N/A'}
                    </Typography>
                  </TableCell>
                  <TableCell align="center">
                    <Box display="flex" gap={0.5} justifyContent="center">
                      {alert.status === 'OPEN' && (
                        <Tooltip title="Acknowledge">
                          <IconButton size="small" color="warning" onClick={() => handleAcknowledge(alert.id)}>
                            <AcknowledgeIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      {(alert.status === 'ACKNOWLEDGED' || alert.status === 'OPEN') && (
                        <Tooltip title="Investigate">
                          <IconButton size="small" color="info" onClick={() => handleInvestigate(alert.id)}>
                            <InvestigateIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      {alert.status === 'INVESTIGATING' && (
                        <Tooltip title="Close">
                          <IconButton size="small" color="success" onClick={() => handleClose(alert.id)}>
                            <CloseIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      {(alert.status === 'OPEN' || alert.status === 'ACKNOWLEDGED') && (
                        <Tooltip title="Dismiss">
                          <IconButton size="small" onClick={() => handleDismiss(alert.id)}>
                            <DismissIcon fontSize="small" />
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
