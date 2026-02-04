import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import theme from './theme';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import UnifiedClientPortfolio from './pages/UnifiedClientPortfolio';
import ClientDetail from './pages/ClientDetail';
import ClientForm from './pages/ClientForm';
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
            {/* Dashboard */}
            <Route index element={<Dashboard />} />
            
            {/* Unified Client Portfolio Dashboard - Both routes use the same component */}
            {/* The component determines the view based on the route */}
            <Route path="clients" element={<UnifiedClientPortfolio />} />
            <Route path="portfolios" element={<UnifiedClientPortfolio />} />
            
            {/* Individual Client Pages */}
            <Route path="clients/new" element={<ClientForm />} />
            <Route path="clients/:id" element={<ClientDetail />} />
            <Route path="clients/:id/edit" element={<ClientForm />} />
            
            {/* Individual Portfolio Page */}
            <Route path="portfolios/new" element={<ClientForm />} /> {/* You may want a PortfolioForm */}
            <Route path="portfolios/:id" element={<PortfolioDetail />} />
            
            {/* Assets Management */}
            <Route path="assets" element={<Assets />} />
            <Route path="assets/new" element={<AssetForm />} />
            <Route path="assets/:id" element={<AssetDetail />} />
            <Route path="assets/:id/edit" element={<AssetForm />} />
            
            {/* Other Modules */}
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