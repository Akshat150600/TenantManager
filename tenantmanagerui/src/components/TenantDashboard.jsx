import React, { useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import '../css/TenantDashboard.css';

const TenantDashboard = () => {
  const location = useLocation();
  const user = location.state?.user;

  const [formData, setFormData] = useState({
    tenantName : user?.username || '',
    unitNumber: '',
    description: '',
    department: 'PLUMBING'
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const departments = [
    { value: 'PLUMBING', label: 'Plumbing' },
    { value: 'ELECTRICAL', label: 'Electrical' },
    { value: 'HVAC', label: 'HVAC (Heating & Cooling)' },
    { value: 'CARPENTRY', label: 'Carpentry' },
    { value: 'HOUSEHOLD_FIX', label: 'Household Fix' },
    { value: 'PAINTING', label: 'Painting' },
    { value: 'CLEANING', label: 'Cleaning' },
    { value: 'PEST_CONTROL', label: 'Pest Control' },
    { value: 'APPLIANCE_REPAIR', label: 'Appliance Repair' },
    { value: 'GENERAL_MAINTENANCE', label: 'General Maintenance' }
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const token = localStorage.getItem('token');
      await axios.post(
        "http://localhost:8080/api/maintenance/create",
        {
          unitNumber: formData.unitNumber,
          description: formData.description,
          department: formData.department,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      setSuccess('Maintenance request submitted successfully!');
      setFormData({ 
        tenantName: user?.username || '',
        unitNumber: '', 
        description: '',
        department: 'PLUMBING'
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit request');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="tenant-dashboard">
      <div className="dashboard-header">
        <h2>Welcome, {user?.username || 'Tenant'}!</h2>
        <p className="user-role">Role: {user?.role || 'TENANT'}</p>
      </div>
      <div className="maintenance-section">
        <h3>Submit Maintenance Request</h3>
        <form onSubmit={handleSubmit} className="maintenance-form">
        <div className="form-group">
          <label htmlFor="unitNumber">Unit Number:</label>
          <input
            type="text"
            id="unitNumber"
            name="unitNumber"
            value={formData.unitNumber}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="department">Department:</label>
          <select
            id="department"
            name="department"
            value={formData.department}
            onChange={handleChange}
            required
          >
            {departments.map(dept => (
              <option key={dept.value} value={dept.value}>
                {dept.label}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="description">Description:</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="4"
            maxLength="2000"
            required
          />
        </div>

        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}

        <button type="submit" disabled={loading}>
          {loading ? 'Submitting...' : 'Submit Request'}
        </button>
      </form>
      </div>
    </div>
  );
};

export default TenantDashboard;