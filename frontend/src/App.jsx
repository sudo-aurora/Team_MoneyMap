import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import theme from './theme';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Clients from './pages/Clients';
import ClientDetail from './pages/ClientDetail';
import ClientForm from './pages/ClientForm';
import Portfolios from './pages/Portfolios';
import PortfolioDetail from './pages/PortfolioDetail';
import Assets from './pages/Assets';
import AssetDetail from './pages/AssetDetail';
import AssetForm from './pages/AssetForm';
import Payments from './pages/Payments';
import Alerts from './pages/Alerts';
import Transactions from './pages/Transactions';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Dashboard />} />
            <Route path="clients" element={<Clients />} />
            <Route path="clients/new" element={<ClientForm />} />
            <Route path="clients/:id" element={<ClientDetail />} />
            <Route path="clients/:id/edit" element={<ClientForm />} />
            <Route path="portfolios" element={<Portfolios />} />
            <Route path="portfolios/:id" element={<PortfolioDetail />} />
            <Route path="assets" element={<Assets />} />
            <Route path="assets/new" element={<AssetForm />} />
            <Route path="assets/:id" element={<AssetDetail />} />
            <Route path="assets/:id/edit" element={<AssetForm />} />
            <Route path="transactions" element={<Transactions />} />
            <Route path="payments" element={<Payments />} />
            <Route path="alerts" element={<Alerts />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
