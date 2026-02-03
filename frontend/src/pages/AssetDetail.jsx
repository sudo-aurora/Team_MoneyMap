import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Chip,
  CircularProgress,
  Alert,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Divider,
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
} from '@mui/icons-material';
import { assetService } from '../services/assetService';
import ConfirmDialog from '../components/ConfirmDialog';

const getTypeColor = (type) => {
  const colors = {
    STOCK: 'primary',
    CRYPTO: 'secondary',
    GOLD: 'warning',
    MUTUAL_FUND: 'info',
  };
  return colors[type] || 'default';
};

export default function AssetDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [asset, setAsset] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  useEffect(() => {
    loadAsset();
  }, [id]);

  const loadAsset = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await assetService.getByIdWithTransactions(id);
      setAsset(data);
    } catch (err) {
      console.error('Error loading asset:', err);
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

  if (error) {
    return (
      <Box>
        <IconButton onClick={() => navigate('/assets')}>
          <BackIcon />
        </IconButton>
        <Alert severity="error" sx={{ mt: 2 }}>
          Error loading asset: {error}
        </Alert>
      </Box>
    );
  }

  if (!asset) {
    return (
      <Box>
        <IconButton onClick={() => navigate('/assets')}>
          <BackIcon />
        </IconButton>
        <Alert severity="warning" sx={{ mt: 2 }}>
          Asset not found
        </Alert>
      </Box>
    );
  }

  const profitLoss = (asset.currentValue || 0) - (asset.purchasePrice * asset.quantity);
  const profitLossPercent = ((profitLoss / (asset.purchasePrice * asset.quantity)) * 100) || 0;

  return (
    <Box>
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box display="flex" alignItems="center" gap={2}>
          <IconButton onClick={() => navigate('/assets')}>
            <BackIcon />
          </IconButton>
          <div>
            <Typography variant="h4" fontWeight="bold">
              {asset.name}
            </Typography>
            <Box display="flex" gap={1} alignItems="center" mt={0.5}>
              <Typography variant="body2" color="text.secondary">
                {asset.symbol}
              </Typography>
              <Chip
                label={asset.assetType}
                size="small"
                color={getTypeColor(asset.assetType)}
                variant="outlined"
              />
            </Box>
          </div>
        </Box>
        <Box display="flex" gap={1}>
          <IconButton 
            color="primary"
            onClick={() => navigate(`/assets/${id}/edit`)}
          >
            <EditIcon />
          </IconButton>
          <IconButton 
            color="error"
            onClick={() => setDeleteDialogOpen(true)}
          >
            <DeleteIcon />
          </IconButton>
        </Box>
      </Box>

      <ConfirmDialog
        open={deleteDialogOpen}
        title="Delete Asset"
        message={`Are you sure you want to delete ${asset?.name}? This will also delete all associated transactions. This action cannot be undone.`}
        onConfirm={async () => {
          try {
            await assetService.delete(id);
            if (asset?.portfolioId) {
              navigate(`/portfolios/${asset.portfolioId}`);
            } else {
              navigate('/assets');
            }
          } catch (err) {
            setError(err.message);
            setDeleteDialogOpen(false);
          }
        }}
        onCancel={() => setDeleteDialogOpen(false)}
      />

      <Grid container spacing={3}>
        {/* Value Cards */}
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Current Price
              </Typography>
              <Typography variant="h4" fontWeight="bold" color="primary">
                ${asset.currentPrice?.toLocaleString()}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Quantity
              </Typography>
              <Typography variant="h4" fontWeight="bold">
                {asset.quantity?.toFixed(4)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Current Value
              </Typography>
              <Typography variant="h4" fontWeight="bold" color="secondary.main">
                ${asset.currentValue?.toLocaleString()}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={3}>
          <Card sx={{ bgcolor: profitLoss >= 0 ? 'success.light' : 'error.light' }}>
            <CardContent>
              <Typography variant="body2" gutterBottom>
                Profit/Loss
              </Typography>
              <Box display="flex" alignItems="center" gap={1}>
                {profitLoss >= 0 ? <TrendingUpIcon /> : <TrendingDownIcon />}
                <Typography variant="h5" fontWeight="bold">
                  ${Math.abs(profitLoss).toLocaleString()}
                </Typography>
              </Box>
              <Typography variant="body2">
                {profitLossPercent >= 0 ? '+' : ''}{profitLossPercent.toFixed(2)}%
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Asset Details */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Asset Information
              </Typography>
              <Divider sx={{ my: 2 }} />
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Type
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    {asset.assetType}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Symbol
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    {asset.symbol}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Purchase Price
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    ${asset.purchasePrice?.toLocaleString()}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Purchase Date
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    {asset.purchaseDate ? new Date(asset.purchaseDate).toLocaleDateString() : 'N/A'}
                  </Typography>
                </Grid>
                {asset.notes && (
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      Notes
                    </Typography>
                    <Typography variant="body1">
                      {asset.notes}
                    </Typography>
                  </Grid>
                )}
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Type-Specific Details */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {asset.assetType} Details
              </Typography>
              <Divider sx={{ my: 2 }} />
              <Grid container spacing={2}>
                {/* STOCK specific */}
                {asset.assetType === 'STOCK' && (
                  <>
                    {asset.exchange && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Exchange</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.exchange}</Typography>
                      </Grid>
                    )}
                    {asset.sector && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Sector</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.sector}</Typography>
                      </Grid>
                    )}
                    {asset.dividendYield != null && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Dividend Yield</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.dividendYield}%</Typography>
                      </Grid>
                    )}
                  </>
                )}

                {/* CRYPTO specific */}
                {asset.assetType === 'CRYPTO' && (
                  <>
                    {asset.blockchainNetwork && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Blockchain</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.blockchainNetwork}</Typography>
                      </Grid>
                    )}
                    {asset.stakingEnabled != null && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Staking</Typography>
                        <Typography variant="body1" fontWeight="medium">
                          {asset.stakingEnabled ? `Enabled (${asset.stakingApy}% APY)` : 'Not Available'}
                        </Typography>
                      </Grid>
                    )}
                  </>
                )}

                {/* GOLD specific */}
                {asset.assetType === 'GOLD' && (
                  <>
                    {asset.purity && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Purity</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.purity}</Typography>
                      </Grid>
                    )}
                    {asset.weightInGrams && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Weight</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.weightInGrams}g</Typography>
                      </Grid>
                    )}
                    {asset.storageLocation && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Storage</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.storageLocation}</Typography>
                      </Grid>
                    )}
                  </>
                )}

                {/* MUTUAL_FUND specific */}
                {asset.assetType === 'MUTUAL_FUND' && (
                  <>
                    {asset.fundManager && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Fund Manager</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.fundManager}</Typography>
                      </Grid>
                    )}
                    {asset.expenseRatio != null && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Expense Ratio</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.expenseRatio}%</Typography>
                      </Grid>
                    )}
                    {asset.riskLevel && (
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Risk Level</Typography>
                        <Typography variant="body1" fontWeight="medium">{asset.riskLevel}</Typography>
                      </Grid>
                    )}
                  </>
                )}
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Transactions */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Transaction History
              </Typography>
              {asset.transactions && asset.transactions.length > 0 ? (
                <TableContainer>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell><strong>Date</strong></TableCell>
                        <TableCell><strong>Type</strong></TableCell>
                        <TableCell align="right"><strong>Quantity</strong></TableCell>
                        <TableCell align="right"><strong>Price/Unit</strong></TableCell>
                        <TableCell align="right"><strong>Fees</strong></TableCell>
                        <TableCell align="right"><strong>Total</strong></TableCell>
                        <TableCell><strong>Notes</strong></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {asset.transactions.map((txn) => (
                        <TableRow key={txn.id} hover>
                          <TableCell>
                            {txn.transactionDate ? new Date(txn.transactionDate).toLocaleDateString() : 'N/A'}
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={txn.transactionType}
                              size="small"
                              color={txn.transactionType === 'BUY' ? 'success' : 'error'}
                            />
                          </TableCell>
                          <TableCell align="right">{txn.quantity}</TableCell>
                          <TableCell align="right">${txn.pricePerUnit?.toLocaleString()}</TableCell>
                          <TableCell align="right">${txn.fees?.toLocaleString()}</TableCell>
                          <TableCell align="right">
                            <Typography fontWeight="medium">
                              ${txn.totalAmount?.toLocaleString()}
                            </Typography>
                          </TableCell>
                          <TableCell>{txn.notes || '-'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <Box display="flex" justifyContent="center" py={4}>
                  <Typography color="text.secondary">
                    No transactions recorded for this asset
                  </Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
