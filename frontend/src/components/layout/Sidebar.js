import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { 
  FiHome, FiUsers, FiBook, FiCalendar, FiFileText, 
  FiSettings, FiLogOut, FiUser, FiBookOpen 
} from 'react-icons/fi';
import Notifications from '../Notifications';

const Sidebar = () => {
  const { user, logout, isAdmin, isTeacher } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItems = [
    { path: '/dashboard', icon: <FiHome />, label: 'Dashboard', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    { path: '/students', icon: <FiUsers />, label: 'Étudiants', roles: ['ADMIN', 'TEACHER'] },
    { path: '/teachers', icon: <FiUser />, label: 'Enseignants', roles: ['ADMIN'] },
    { path: '/courses', icon: <FiBook />, label: 'Cours', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    { path: '/schedules', icon: <FiCalendar />, label: 'Emploi du temps', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    { path: '/exams', icon: <FiFileText />, label: 'Examens', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    { path: '/admin', icon: <FiSettings />, label: 'Administration', roles: ['ADMIN'] },
  ];

  const filteredNavItems = navItems.filter(item => item.roles.includes(user?.role));

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="sidebar-logo">
          <FiBookOpen style={{ marginRight: '8px' }} />
          ACADEMIX
        </div>
      </div>

      <nav className="sidebar-nav">
        {filteredNavItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          >
            {item.icon}
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div style={{ padding: '16px 24px', borderTop: '1px solid rgba(255,255,255,0.1)', marginTop: 'auto' }}>
        {/* Section Notifications + Utilisateur */}
        <div style={{ 
          display: 'flex', 
          alignItems: 'center', 
          gap: '12px', 
          marginBottom: '16px',
          padding: '12px',
          backgroundColor: 'rgba(255,255,255,0.05)',
          borderRadius: '10px'
        }}>
          {/* Composant Notifications */}
          <Notifications />
          
          {/* Info utilisateur */}
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontWeight: '600', fontSize: '0.875rem', color: 'white' }}>
              {user?.firstName} {user?.lastName}
            </div>
            <div style={{ 
              opacity: 0.7, 
              fontSize: '0.7rem', 
              textTransform: 'uppercase',
              letterSpacing: '0.5px'
            }}>
              {user?.role}
            </div>
          </div>
        </div>

        {/* Bouton Déconnexion */}
        <button 
          onClick={handleLogout}
          className="nav-item"
          style={{ 
            width: '100%', 
            border: 'none', 
            background: 'rgba(255,255,255,0.1)',
            cursor: 'pointer',
            borderRadius: '8px'
          }}
        >
          <FiLogOut />
          <span>Déconnexion</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;