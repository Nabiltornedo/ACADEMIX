import React, { useState, useEffect } from 'react';
import { authService, adminService } from '../../services/api';
import { toast } from 'react-toastify';
import { FiUsers, FiSettings, FiActivity, FiEdit2, FiTrash2 } from 'react-icons/fi';

const Admin = () => {
  const [activeTab, setActiveTab] = useState('users');
  const [users, setUsers] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (activeTab === 'users') fetchUsers();
    else if (activeTab === 'logs') fetchAuditLogs();
  }, [activeTab]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await authService.getUsers();
      setUsers(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des utilisateurs');
    } finally {
      setLoading(false);
    }
  };

  const fetchAuditLogs = async () => {
    setLoading(true);
    try {
      const response = await adminService.getAuditLogs();
      setAuditLogs(response.data);
    } catch (error) {
      console.log('Audit logs not available');
      setAuditLogs([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (id) => {
    if (window.confirm('Supprimer cet utilisateur ?')) {
      try {
        await authService.deleteUser(id);
        toast.success('Utilisateur supprimé');
        fetchUsers();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const toggleUserStatus = async (user) => {
    try {
      await authService.updateUser(user.id, { isActive: !user.isActive });
      toast.success('Statut mis à jour');
      fetchUsers();
    } catch (error) {
      toast.error('Erreur');
    }
  };

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Administration</h1>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
        <button className={`btn ${activeTab === 'users' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setActiveTab('users')}>
          <FiUsers /> Utilisateurs
        </button>
        <button className={`btn ${activeTab === 'logs' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setActiveTab('logs')}>
          <FiActivity /> Journal d'audit
        </button>
      </div>

      {loading ? (
        <div className="loading"><div className="spinner"></div></div>
      ) : (
        <>
          {/* Users Tab */}
          {activeTab === 'users' && (
            <div className="card">
              <div className="card-header">
                <h2 className="card-title">Gestion des Utilisateurs</h2>
              </div>
              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr><th>ID</th><th>Nom d'utilisateur</th><th>Email</th><th>Nom</th><th>Rôle</th><th>Statut</th><th>Actions</th></tr>
                  </thead>
                  <tbody>
                    {users.length > 0 ? users.map((user) => (
                      <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.username}</td>
                        <td>{user.email}</td>
                        <td>{user.firstName} {user.lastName}</td>
                        <td><span className={`badge badge-${user.role === 'ADMIN' ? 'danger' : user.role === 'TEACHER' ? 'info' : 'success'}`}>{user.role}</span></td>
                        <td>
                          <span className={`badge badge-${user.isActive ? 'success' : 'warning'}`} style={{ cursor: 'pointer' }} onClick={() => toggleUserStatus(user)}>
                            {user.isActive ? 'Actif' : 'Inactif'}
                          </span>
                        </td>
                        <td>
                          <div className="actions">
                            <button className="btn btn-danger btn-sm" onClick={() => handleDeleteUser(user.id)} disabled={user.role === 'ADMIN'}>
                              <FiTrash2 />
                            </button>
                          </div>
                        </td>
                      </tr>
                    )) : (
                      <tr><td colSpan="7" style={{ textAlign: 'center', color: '#64748b' }}>Aucun utilisateur</td></tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Audit Logs Tab */}
          {activeTab === 'logs' && (
            <div className="card">
              <div className="card-header">
                <h2 className="card-title">Journal d'Audit</h2>
              </div>
              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr><th>Date</th><th>Utilisateur</th><th>Action</th><th>Entité</th><th>Détails</th><th>IP</th></tr>
                  </thead>
                  <tbody>
                    {auditLogs.length > 0 ? auditLogs.map((log) => (
                      <tr key={log.id}>
                        <td>{new Date(log.createdAt).toLocaleString('fr-FR')}</td>
                        <td>{log.username || log.userId}</td>
                        <td><span className="badge badge-info">{log.action}</span></td>
                        <td>{log.entityType}</td>
                        <td>{log.details}</td>
                        <td>{log.ipAddress}</td>
                      </tr>
                    )) : (
                      <tr><td colSpan="6" style={{ textAlign: 'center', color: '#64748b' }}>Aucun log d'audit</td></tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Admin;
