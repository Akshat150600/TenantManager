import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import './App.css';
import { LoginPage } from './components/LoginPage';
import TenantDashboard from './components/TenantDashboard';
import AdminDashboard from './components/AdminDashboard';
import Header from './components/Header';

function AppContent() {
  const location = useLocation();
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Get user from location state or localStorage
    if (location.state?.user) {
      setUser(location.state.user);
      // Update localStorage when user comes from navigation
      localStorage.setItem('user', JSON.stringify(location.state.user));
    } else {
      // Try to get user from localStorage if available
      const storedUser = localStorage.getItem('user');
      if (storedUser) {
        try {
          setUser(JSON.parse(storedUser));
        } catch (e) {
          // If parsing fails, clear invalid data
          localStorage.removeItem('user');
          setUser(null);
        }
      } else {
        setUser(null);
      }
    }
  }, [location]);

  const handleLogout = () => {
    // Clear user state
    setUser(null);
  };

  return (
    <div className="App">
      <Header user={user} onLogout={handleLogout} />
      <Routes>
        <Route path="/" element={<LoginPage setUser={setUser} />} />
        <Route path="/admin-dashboard" element={<AdminDashboard />} />
        <Route path="/tenant-dashboard" element={<TenantDashboard />} />
      </Routes>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

export default App;
