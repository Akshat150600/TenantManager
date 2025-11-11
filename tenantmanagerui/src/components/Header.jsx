import React from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/Header.css';

const Header = ({ user, onLogout }) => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('token');
      
      if (token) {
        // Call logout API to invalidate session in Redis
        await axios.post('/api/auth/logout', {}, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      }
    } catch (error) {
      console.error('Logout API error:', error);
      // Continue with logout even if API call fails
    } finally {
      // Clear token and user data
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      delete axios.defaults.headers.common['Authorization'];
      
      // Call parent's logout handler to update state
      if (onLogout) {
        onLogout();
      }
      
      // Navigate to login page
      navigate('/');
    }
  };

  return (
    <header className="app-header">
      <div className="header-container">
        <div className="header-left">
          <h1 className="app-title">Tenant Manager</h1>
        </div>
        
        {user && (
          <div className="header-right">
            <div className="user-info">
              <span className="user-greeting">Welcome, <strong>{user.username}</strong></span>
              <span className="user-role">{user.role}</span>
            </div>
            <button className="logout-btn" onClick={handleLogout}>
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;