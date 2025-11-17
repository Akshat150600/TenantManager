import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import '../css/AdminDashboard.css';

const AdminDashboard = () => {
  const location = useLocation();
  const user = location.state?.user;
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('ALL');
  const [departmentFilter, setDepartmentFilter] = useState('ALL');

  const departments = [
    { value: 'ALL', label: 'All Departments' },
    { value: 'PLUMBING', label: 'Plumbing' },
    { value: 'ELECTRICAL', label: 'Electrical' },
    { value: 'HVAC', label: 'HVAC' },
    { value: 'CARPENTRY', label: 'Carpentry' },
    { value: 'HOUSEHOLD_FIX', label: 'Household Fix' },
    { value: 'PAINTING', label: 'Painting' },
    { value: 'CLEANING', label: 'Cleaning' },
    { value: 'PEST_CONTROL', label: 'Pest Control' },
    { value: 'APPLIANCE_REPAIR', label: 'Appliance Repair' },
    { value: 'GENERAL_MAINTENANCE', label: 'General Maintenance' }
  ];

  const fetchMaintenanceRequests = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const token = localStorage.getItem('token');
      const params = [];
      
      if (filter !== 'ALL') {
        params.push(`status=${filter}`);
      }
      if (departmentFilter !== 'ALL') {
        params.push(`department=${departmentFilter}`);
      }
      
      const queryString = params.length > 0 ? '?' + params.join('&') : '';
      const url = `http://localhost:8080/api/admin/maintenance${queryString}`;
      
      console.log('Fetching with URL:', url);
      console.log('Filters - Status:', filter, 'Department:', departmentFilter);
      
      const response = await axios.get(url, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Response data:', response.data);
      setRequests(response.data);
    } catch (err) {
      console.error('Error:', err);
      setError(err.response?.data?.message || 'Failed to fetch requests');
    } finally {
      setLoading(false);
    }
  }, [filter, departmentFilter]);

  useEffect(() => {
    fetchMaintenanceRequests();
  }, [fetchMaintenanceRequests]);

  const handleApprove = async (requestId) => {
    try {
      const token = localStorage.getItem('token');
      await axios.put(
        `http://localhost:8080/api/admin/maintenance/${requestId}/approve`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      fetchMaintenanceRequests(); // Refresh the list
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to approve request');
      console.error('Error:', err);
    }
  };

  const handleReject = async (requestId) => {
    try {
      const token = localStorage.getItem('token');
      await axios.put(
        `http://localhost:8080/api/admin/maintenance/${requestId}/reject`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      fetchMaintenanceRequests(); // Refresh the list
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to reject request');
      console.error('Error:', err);
    }
  };

  const handleStatusChange = async (requestId, newStatus) => {
    try {
      const token = localStorage.getItem('token');
      await axios.put(
        `http://localhost:8080/api/admin/maintenance/${requestId}/status`,
        { status: newStatus },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      fetchMaintenanceRequests(); // Refresh the list
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update status');
      console.error('Error:', err);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'OPEN': return 'status-open';
      case 'IN_PROGRESS': return 'status-in-progress';
      case 'RESOLVED': return 'status-resolved';
      case 'REJECTED': return 'status-rejected';
      default: return '';
    }
  };

  return (
    <div className="admin-dashboard">
      <div className="dashboard-header">
        <div className="header-info">
          <h2>Admin Dashboard</h2>
          <p>Welcome, {user?.username || 'Admin'}!</p>
        </div>
      </div>

      <div className="dashboard-content">
        <div className="filters">
          <div className="filter-group">
            <label>Filter by Status:</label>
            <select value={filter} onChange={(e) => setFilter(e.target.value)}>
              <option value="ALL">All Requests</option>
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Filter by Department:</label>
            <select value={departmentFilter} onChange={(e) => setDepartmentFilter(e.target.value)}>
              {departments.map(dept => (
                <option key={dept.value} value={dept.value}>
                  {dept.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <div className="loading">Loading requests...</div>
        ) : (
          <div className="requests-container">
            {requests.length === 0 ? (
              <p className="no-requests">No maintenance requests found.</p>
            ) : (
              <table className="requests-table">
                <thead>
                  <tr>
                    <th>Tenant</th>
                    <th>Unit Number</th>
                    <th>Department</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th>Approved</th>
                    <th>Created At</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {requests.map((request) => (
                    <tr key={request.id}>
                      <td>{request.tenantName}</td>
                      <td>{request.unitNumber}</td>
                      <td>
                        <span className="department-badge">
                          {request.department?.replace(/_/g, ' ')}
                        </span>
                      </td>
                      <td className="description-cell">{request.description}</td>
                      <td>
                        <select
                          value={request.status}
                          onChange={(e) => handleStatusChange(request.id, e.target.value)}
                          className={`status-badge ${getStatusBadgeClass(request.status)}`}
                        >
                          <option value="OPEN">Open</option>
                          <option value="IN_PROGRESS">In Progress</option>
                          <option value="RESOLVED">Resolved</option>
                          <option value="REJECTED">Rejected</option>
                        </select>
                      </td>
                      <td>
                        <span className={request.approved ? 'approved-yes' : 'approved-no'}>
                          {request.approved ? 'Yes' : 'No'}
                        </span>
                      </td>
                      <td>{new Date(request.createdAt).toLocaleDateString()}</td>
                      <td className="actions-cell">
                        {!request.approved && (
                          <>
                            <button
                              className="btn-approve"
                              onClick={() => handleApprove(request.id)}
                            >
                              Approve
                            </button>
                            <button
                              className="btn-reject"
                              onClick={() => handleReject(request.id)}
                            >
                              Reject
                            </button>
                          </>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;