import React from 'react';

function Navbar() {
  return (
    <header className="navbar">
      <div className="navbar-brand">
        <span className="brand-logo">⚡</span>
        <span className="brand-title">Employee Task Manager</span>
      </div>
      <div className="navbar-status">
        <span className="status-dot"></span>
        <span className="status-text">Backend API: http://localhost:8080/api</span>
      </div>
    </header>
  );
}

export default Navbar;
