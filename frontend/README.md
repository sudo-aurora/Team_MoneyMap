# MoneyMap Frontend

React frontend for the MoneyMap Portfolio Manager application.

## Technology Stack

- **React 19** - UI library
- **Vite** - Build tool and dev server
- **Material-UI (MUI)** - Component library
- **Recharts** - Charts and visualizations
- **React Router** - Navigation
- **Axios** - HTTP client

## Prerequisites

- Node.js 18+ (or 20+ recommended)
- Backend API running on http://localhost:8181

## Installation

```bash
cd frontend
npm install
```

## Running the Application

### Development Mode

```bash
npm run dev
```

The application will run on http://localhost:3000

### Build for Production

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

## Features Implemented

### ✅ Phase 1 Complete
- ✅ Dashboard with statistics and charts
- ✅ Clients list with search and pagination
- ✅ Client detail view with portfolio information
- ✅ Professional Material-UI design
- ✅ Responsive layout with mobile navigation

### ✅ Phase 2 Complete
- ✅ Portfolios page - Grid view with cards
- ✅ Portfolio detail - Assets list and value tracking
- ✅ Assets page - Table with type filters (STOCK/CRYPTO/GOLD/MUTUAL_FUND)
- ✅ Payments page - Lifecycle management (CREATED → VALIDATED → SENT → COMPLETED)
- ✅ Alerts dashboard - Status management with statistics
- ✅ Complete API integration for all modules

### 🚧 Future Enhancements
- Asset create/edit forms with type-specific fields
- Client create/edit forms
- Transaction management
- Real-time updates with WebSocket
- Advanced filtering and sorting
- Export functionality (PDF/Excel)

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   └── Layout.jsx          # Main layout with navigation
│   ├── pages/
│   │   ├── Dashboard.jsx       # Dashboard with charts
│   │   ├── Clients.jsx         # Client list
│   │   └── ClientDetail.jsx    # Client details
│   ├── services/
│   │   ├── api.js              # Axios configuration
│   │   ├── clientService.js    # Client API calls
│   │   ├── portfolioService.js # Portfolio API calls
│   │   └── assetService.js     # Asset API calls
│   ├── App.jsx                 # Main app with routing
│   ├── main.jsx                # Entry point
│   └── theme.js                # MUI theme configuration
├── vite.config.js              # Vite config with API proxy
└── package.json
```

## API Integration

The frontend communicates with the backend through:
- **Base URL:** `/api/v1` (proxied to http://localhost:8181)
- **CORS:** Enabled for localhost:3000
- **Response Format:** Automatically extracts data from ApiResponseDTO wrapper

## Color Scheme

- **Primary Blue:** #1976d2 (Trust, stability)
- **Success Green:** #388e3c (Growth, profit)
- **Error Red:** #d32f2f (Loss, alerts)
- **Warning Orange:** #f57c00 (Warnings)
- **Background:** #f5f5f5 (Light gray)

## Troubleshooting

### Backend Not Running
Make sure the Spring Boot backend is running:
```bash
cd ../portfoliomanager
.\mvnw.cmd spring-boot:run
```

### CORS Errors
The backend has CORS enabled for localhost:3000. If you see CORS errors, verify the backend CORS configuration.

### API Proxy Not Working
Check `vite.config.js` - it should proxy `/api/*` requests to `http://localhost:8181`.

## Next Steps for Development

1. Start backend: `cd portfoliomanager && .\mvnw.cmd spring-boot:run`
2. Populate database: `cd portfoliomanager/scripts && python seed_data.py`
3. Start frontend: `cd frontend && npm run dev`
4. Open http://localhost:3000

---

Built with React + Material-UI for MoneyMap Portfolio Manager
