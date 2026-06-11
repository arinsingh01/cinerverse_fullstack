import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import { 
  User, Shield, Briefcase, Film, Bookmark, Settings, 
  TrendingUp, Users, Activity, Play, CreditCard, Ticket, Grid
} from 'lucide-react';
import Button from '../components/Button';
import './Dashboard.css';

const Dashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');

  const displayName = user?.email ? user.email.split('@')[0] : 'Arin';
  const roleName = user?.role || 'user';

  // Local mock history for Bookings Tab
  const mockBookings = [
    { id: 'TX-4829', movie: 'Interstellar', date: '2026-06-12', time: '19:30', seats: 'F12, F13', amount: '$29.00', status: 'Confirmed' },
    { id: 'TX-1094', movie: 'Avatar: The Way of Water', date: '2026-05-18', time: '15:00', seats: 'D5, D6', amount: '$31.50', status: 'Completed' },
    { id: 'TX-0921', movie: 'Avengers: Endgame', date: '2026-04-02', time: '21:00', seats: 'H8', amount: '$15.00', status: 'Completed' }
  ];

  // Render role-specific control console
  const renderRoleConsole = () => {
    switch (roleName) {
      case 'admin':
        return (
          <div className="role-console admin-console animate-fade-in">
            <div className="console-header">
              <Shield size={20} className="console-icon" />
              <h3>Administrator Control Panel</h3>
            </div>
            <div className="metrics-grid">
              <div className="metric-card glass-panel">
                <Users size={24} className="metric-icon c-cyan" />
                <div className="metric-details">
                  <span className="metric-title">Active Platform Users</span>
                  <span className="metric-value">1,482</span>
                </div>
              </div>
              <div className="metric-card glass-panel">
                <Activity size={24} className="metric-icon c-purple" />
                <div className="metric-details">
                  <span className="metric-title">System Health status</span>
                  <span className="metric-value">99.9%</span>
                </div>
              </div>
              <div className="metric-card glass-panel">
                <TrendingUp size={24} className="metric-icon c-magenta" />
                <div className="metric-details">
                  <span className="metric-title">Platform Revenue</span>
                  <span className="metric-value">$48,250</span>
                </div>
              </div>
            </div>
            <div className="console-actions">
              <h4>Global Management Tools</h4>
              <div className="actions-grid">
                <Button variant="glass" onClick={() => alert('Feature simulated: Accounts auditor')}>Manage Accounts</Button>
                <Button variant="glass" onClick={() => alert('Feature simulated: Log viewer')}>System Logs</Button>
                <Button variant="glass" onClick={() => alert('Feature simulated: Gateway proxy configurations')}>Gateway Configs</Button>
              </div>
            </div>
          </div>
        );

      case 'owner':
        return (
          <div className="role-console owner-console animate-fade-in">
            <div className="console-header">
              <Briefcase size={20} className="console-icon" />
              <h3>Theatre Owner Studio</h3>
            </div>
            <div className="metrics-grid">
              <div className="metric-card glass-panel">
                <Ticket size={24} className="metric-icon c-purple" />
                <div className="metric-details">
                  <span className="metric-title">Tickets Sold (Today)</span>
                  <span className="metric-value">342</span>
                </div>
              </div>
              <div className="metric-card glass-panel">
                <Play size={24} className="metric-icon c-cyan" />
                <div className="metric-details">
                  <span className="metric-title">Active Screens</span>
                  <span className="metric-value">12 / 16</span>
                </div>
              </div>
              <div className="metric-card glass-panel">
                <TrendingUp size={24} className="metric-icon c-success" />
                <div className="metric-details">
                  <span className="metric-title">Estimated Sales</span>
                  <span className="metric-value">$4,820</span>
                </div>
              </div>
            </div>
            <div className="console-actions">
              <h4>Screen & Showtime Operations</h4>
              <div className="actions-grid">
                <Button variant="glass" onClick={() => alert('Feature simulated: Showtimes manager')}>Edit Showtimes</Button>
                <Button variant="glass" onClick={() => alert('Feature simulated: Seating grid customizer')}>Configure Seating</Button>
                <Button variant="glass" onClick={() => alert('Feature simulated: Add new screen dialog')}>Add Screen</Button>
              </div>
            </div>
          </div>
        );

      case 'user':
      default:
        return (
          <div className="role-console user-console animate-fade-in">
            <div className="console-header">
              <User size={20} className="console-icon" />
              <h3>Customer Dashboard</h3>
            </div>
            <div className="metrics-grid">
              <div className="metric-card glass-panel">
                <CreditCard size={24} className="metric-icon c-cyan" />
                <div className="metric-details">
                  <span className="metric-title">Loyalty Points</span>
                  <span className="metric-value">450 pts</span>
                </div>
              </div>
              <div className="metric-card glass-panel">
                <Ticket size={24} className="metric-icon c-purple" />
                <div className="metric-details">
                  <span className="metric-title">Booked Tickets</span>
                  <span className="metric-value">3 Active</span>
                </div>
              </div>
              <div className="metric-card glass-panel">
                <Film size={24} className="metric-icon c-magenta" />
                <div className="metric-details">
                  <span className="metric-title">Favorite Genre</span>
                  <span className="metric-value">Sci-Fi</span>
                </div>
              </div>
            </div>
            <div className="console-actions">
              <h4>Quick Actions</h4>
              <div className="actions-grid">
                <Link to="/movies" style={{ width: '100%' }}>
                  <Button variant="primary" className="full-width">Book Movie Tickets</Button>
                </Link>
                <Button variant="glass" onClick={() => setActiveTab('bookings')}>View Bookings History</Button>
                <Button variant="glass" onClick={() => alert('Feature simulated: Voucher code claim')}>Claim Gift Voucher</Button>
              </div>
            </div>
          </div>
        );
    }
  };

  return (
    <div className="dashboard-container container">
      {/* Greetings Header */}
      <header className="dashboard-welcome glass-panel">
        <div className="welcome-avatar">
          {roleName === 'admin' ? <Shield size={28} /> : roleName === 'owner' ? <Briefcase size={28} /> : <User size={28} />}
        </div>
        <div className="welcome-text">
          <h2>Welcome back, <span className="gradient-text">{displayName === 'customer' || displayName === 'theatre_owner' ? 'Arin' : displayName}</span></h2>
          <p>Role Authorized: <span className="badge">{roleName.toUpperCase()}</span></p>
        </div>
      </header>

      {/* Main Grid Content */}
      <div className="dashboard-layout">
        {/* Sidebar Nav */}
        <aside className="dashboard-sidebar glass-panel">
          <button 
            className={`sidebar-tab ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            <Grid size={18} /> Overview
          </button>
          <button 
            className={`sidebar-tab ${activeTab === 'bookings' ? 'active' : ''}`}
            onClick={() => setActiveTab('bookings')}
          >
            <Ticket size={18} /> Bookings
          </button>
          <button 
            className={`sidebar-tab ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            <User size={18} /> Profile
          </button>
        </aside>

        {/* Tab Panel Content */}
        <main className="dashboard-content-panel">
          {activeTab === 'overview' && (
            <div className="dashboard-tab-content">
              {renderRoleConsole()}
            </div>
          )}

          {activeTab === 'bookings' && (
            <div className="dashboard-tab-content glass-panel p-2 animate-fade-in">
              <h3 className="tab-title">Active & Past Bookings</h3>
              <div className="bookings-table-container">
                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>Transaction ID</th>
                      <th>Movie Name</th>
                      <th>Date</th>
                      <th>Time</th>
                      <th>Seats</th>
                      <th>Amount</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {mockBookings.map((booking) => (
                      <tr key={booking.id}>
                        <td className="booking-id">{booking.id}</td>
                        <td className="booking-movie">{booking.movie}</td>
                        <td>{booking.date}</td>
                        <td>{booking.time}</td>
                        <td className="booking-seats">{booking.seats}</td>
                        <td>{booking.amount}</td>
                        <td>
                          <span className={`status-badge status-${booking.status.toLowerCase()}`}>
                            {booking.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {activeTab === 'profile' && (
            <div className="dashboard-tab-content glass-panel p-2 animate-fade-in">
              <h3 className="tab-title">Profile Settings</h3>
              <div className="profile-details-card">
                <div className="profile-header">
                  <div className="profile-avatar-large">
                    {roleName.substring(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <h4>{displayName === 'customer' || displayName === 'theatre_owner' ? 'Arin Singh' : displayName}</h4>
                    <p>{user?.email}</p>
                  </div>
                </div>
                <div className="profile-info-grid">
                  <div className="info-item">
                    <span className="info-label">Account Role</span>
                    <span className="info-value text-accent">{roleName.toUpperCase()}</span>
                  </div>
                  <div className="info-item">
                    <span className="info-label">Member Since</span>
                    <span className="info-value">June 2026</span>
                  </div>
                  <div className="info-item">
                    <span className="info-label">Default Region</span>
                    <span className="info-value">APAC</span>
                  </div>
                </div>
                <div className="profile-footer">
                  <Button variant="secondary" onClick={() => alert('Simulated Profile Edit')}>Edit Profile</Button>
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default Dashboard;
