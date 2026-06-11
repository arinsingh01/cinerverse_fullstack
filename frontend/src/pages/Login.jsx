import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import Button from '../components/Button';
import { Film, User, Shield, Briefcase, Mail, Lock } from 'lucide-react';
import './Login.css';

const Login = () => {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('user');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Quick select preset roles for easy RBAC simulation
  const handlePresetSelect = (presetEmail, presetRole) => {
    setEmail(presetEmail);
    setPassword('••••••••');
    setRole(presetRole);
    setError('');
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Please fill in all fields');
      return;
    }
    
    setLoading(true);
    setError('');

    // Simulate small API delay
    setTimeout(() => {
      login(email, role);
      setLoading(false);
    }, 800);
  };

  return (
    <div className="login-container">
      <div className="login-card glass-panel animate-fade-in">
        {/* Brand Logo Header */}
        <div className="login-header">
          <div className="login-logo">
            <Film size={32} className="logo-icon-glow" />
            <h1>Cine<span className="gradient-text">Verse</span></h1>
          </div>
          <p className="login-subtitle">Simulated Unified Cinema Portal</p>
        </div>

        {/* Error message */}
        {error && <div className="login-error-badge">{error}</div>}

        {/* Input Form */}
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label className="form-label" htmlFor="login-email">Email Address</label>
            <div className="input-with-icon">
              <Mail className="input-icon" size={16} />
              <input
                id="login-email"
                type="email"
                className="form-input"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="login-password">Password</label>
            <div className="input-with-icon">
              <Lock className="input-icon" size={16} />
              <input
                id="login-password"
                type="password"
                className="form-input"
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          {/* Role selector dropdown to simulate authentication details */}
          <div className="form-group">
            <label className="form-label" htmlFor="login-role">Simulate Login Role</label>
            <select
              id="login-role"
              className="form-input role-dropdown"
              value={role}
              onChange={(e) => setRole(e.target.value)}
            >
              <option value="user">User / Customer</option>
              <option value="owner">Theatre Owner</option>
              <option value="admin">Administrator</option>
            </select>
          </div>

          <Button 
            type="submit" 
            variant="primary" 
            loading={loading}
            className="login-submit-btn"
          >
            Sign In
          </Button>
        </form>

        {/* Quick Demo Pre-fill Shortcuts */}
        <div className="login-presets">
          <p className="presets-title">Quick Role Simulation Presets:</p>
          <div className="preset-buttons-grid">
            <button
              type="button"
              className="preset-btn p-user"
              onClick={() => handlePresetSelect('customer@cineverse.com', 'user')}
            >
              <User size={14} />
              <span>Customer</span>
            </button>
            <button
              type="button"
              className="preset-btn p-owner"
              onClick={() => handlePresetSelect('theatre_owner@cineverse.com', 'owner')}
            >
              <Briefcase size={14} />
              <span>Theatre Owner</span>
            </button>
            <button
              type="button"
              className="preset-btn p-admin"
              onClick={() => handlePresetSelect('admin@cineverse.com', 'admin')}
            >
              <Shield size={14} />
              <span>Admin</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
