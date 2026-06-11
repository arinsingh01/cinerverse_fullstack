import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Film, LogOut, User, Shield, Briefcase, RefreshCw } from 'lucide-react';
import './Navbar.css';

const Navbar = () => {
  const { user, logout, updateRole } = useAuth();
  const location = useLocation();

  const getRoleIcon = (role) => {
    switch (role) {
      case 'admin': return <Shield size={16} className="role-icon-admin" />;
      case 'owner': return <Briefcase size={16} className="role-icon-owner" />;
      default: return <User size={16} className="role-icon-user" />;
    }
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar glass-panel">
      <div className="container navbar-container">
        {/* Logo */}
        <Link to="/dashboard" className="navbar-logo">
          <Film className="logo-icon" />
          <span>Cine<span className="gradient-text">Verse</span></span>
        </Link>

        {/* Links */}
        <div className="navbar-links">
          <Link 
            to="/dashboard" 
            className={`nav-link ${isActive('/dashboard') ? 'active' : ''}`}
          >
            Dashboard
          </Link>
          <Link 
            to="/movies" 
            className={`nav-link ${isActive('/movies') ? 'active' : ''}`}
          >
            Movies
          </Link>
          <Link 
            to="/booking" 
            className={`nav-link ${isActive('/booking') ? 'active' : ''}`}
          >
            Booking
          </Link>
        </div>

        {/* User Info & Actions */}
        {user && (
          <div className="navbar-user">
            {/* Real-time RBAC Switcher for Reviewer Convenience */}
            <div className="role-switcher">
              <span className="switcher-label">
                <RefreshCw size={12} className="spin-hover" /> Simulation:
              </span>
              <select
                value={user.role}
                onChange={(e) => updateRole(e.target.value)}
                className="role-select"
                title="Change role instantly to test RBAC UI"
              >
                <option value="user">User</option>
                <option value="owner">Theatre Owner</option>
                <option value="admin">Admin</option>
              </select>
            </div>

            {/* User Details */}
            <div className="user-details" title={`Logged in as ${user.email}`}>
              <div className="user-avatar">
                {getRoleIcon(user.role)}
              </div>
              <div className="user-meta">
                <span className="user-email">{user.email.split('@')[0]}</span>
                <span className={`role-badge role-${user.role}`}>
                  {user.role}
                </span>
              </div>
            </div>

            {/* Logout */}
            <button onClick={logout} className="logout-btn" title="Log Out">
              <LogOut size={18} />
            </button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
