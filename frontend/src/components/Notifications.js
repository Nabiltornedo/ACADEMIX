import React, { useState, useEffect, useRef } from 'react';
import { notificationService } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { FiBell, FiX, FiCheckCircle, FiBook, FiClock, FiAlertCircle } from 'react-icons/fi';

const Notifications = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    if (user?.id) {
      fetchUnreadCount();
      const interval = setInterval(fetchUnreadCount, 30000);
      return () => clearInterval(interval);
    }
  }, [user]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const fetchUnreadCount = async () => {
    try {
      const response = await notificationService.getUnreadCount(user.id);
      setUnreadCount(response.data.count);
    } catch (error) {
      console.log('Notifications non disponibles');
    }
  };

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const response = await notificationService.getAll(user.id);
      setNotifications(response.data);
    } catch (error) {
      console.log('Erreur chargement notifications');
    } finally {
      setLoading(false);
    }
  };

  const toggleDropdown = () => {
    if (!showDropdown) {
      fetchNotifications();
    }
    setShowDropdown(!showDropdown);
  };

  const handleMarkAsRead = async (id) => {
    try {
      await notificationService.markAsRead(id);
      setNotifications(notifications.map(n => 
        n.id === id ? { ...n, isRead: true } : n
      ));
      setUnreadCount(Math.max(0, unreadCount - 1));
    } catch (error) {
      console.error('Erreur');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead(user.id);
      setNotifications(notifications.map(n => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch (error) {
      console.error('Erreur');
    }
  };

  const handleDelete = async (id) => {
    try {
      await notificationService.delete(id);
      const notification = notifications.find(n => n.id === id);
      if (notification && !notification.isRead) {
        setUnreadCount(Math.max(0, unreadCount - 1));
      }
      setNotifications(notifications.filter(n => n.id !== id));
    } catch (error) {
      console.error('Erreur');
    }
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'EXAM_REMINDER_24H':
        return <FiBook style={{ color: '#f59e0b' }} size={18} />;
      case 'EXAM_REMINDER_2H':
        return <FiAlertCircle style={{ color: '#ef4444' }} size={18} />;
      case 'COURSE_REMINDER_2H':
        return <FiClock style={{ color: '#3b82f6' }} size={18} />;
      default:
        return <FiBell style={{ color: '#6b7280' }} size={18} />;
    }
  };

  const getNotificationColor = (type) => {
    switch (type) {
      case 'EXAM_REMINDER_24H': return '#fef3c7';
      case 'EXAM_REMINDER_2H': return '#fee2e2';
      case 'COURSE_REMINDER_2H': return '#dbeafe';
      default: return '#f3f4f6';
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return "À l'instant";
    if (minutes < 60) return `Il y a ${minutes} min`;
    if (hours < 24) return `Il y a ${hours}h`;
    if (days < 7) return `Il y a ${days}j`;
    return date.toLocaleDateString('fr-FR');
  };

  return (
    <div style={{ position: 'static' }} ref={dropdownRef}>
      {/* Bouton notification */}
      <button
        onClick={toggleDropdown}
        style={{
          background: 'rgba(255,255,255,0.1)',
          border: 'none',
          cursor: 'pointer',
          position: 'relative',
          padding: '10px',
          borderRadius: '10px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: 'background 0.2s'
        }}
      >
        <FiBell size={20} color="white" />
        {unreadCount > 0 && (
          <span style={{
            position: 'absolute',
            top: '2px',
            right: '2px',
            backgroundColor: '#ef4444',
            color: 'white',
            borderRadius: '50%',
            width: '18px',
            height: '18px',
            fontSize: '10px',
            fontWeight: 'bold',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            border: '2px solid #1e3a5f'
          }}>
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown - POSITION FIXE À GAUCHE DE L'ÉCRAN */}
      {showDropdown && (
        <div style={{
          position: 'fixed',
          top: '50%',
          left: '250px',
          transform: 'translateY(-50%)',
          width: '380px',
          maxHeight: '500px',
          backgroundColor: 'white',
          borderRadius: '16px',
          boxShadow: '0 20px 60px rgba(0,0,0,0.3)',
          zIndex: 9999,
          overflow: 'hidden'
        }}>
          {/* Header */}
          <div style={{
            padding: '16px 20px',
            borderBottom: '1px solid #e2e8f0',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            background: 'linear-gradient(135deg, #1e3a5f 0%, #2d4a6f 100%)',
            color: 'white'
          }}>
            <h3 style={{ margin: 0, fontSize: '15px', fontWeight: '600', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <FiBell size={18} /> Notifications
              {unreadCount > 0 && (
                <span style={{
                  backgroundColor: '#ef4444',
                  borderRadius: '12px',
                  padding: '2px 10px',
                  fontSize: '12px'
                }}>
                  {unreadCount}
                </span>
              )}
            </h3>
            {unreadCount > 0 && (
              <button
                onClick={handleMarkAllAsRead}
                style={{
                  background: 'rgba(255,255,255,0.2)',
                  border: 'none',
                  color: 'white',
                  cursor: 'pointer',
                  fontSize: '12px',
                  padding: '6px 12px',
                  borderRadius: '8px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px'
                }}
              >
                <FiCheckCircle size={14} />
                Tout lire
              </button>
            )}
          </div>

          {/* Liste des notifications */}
          <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
            {loading ? (
              <div style={{ padding: '40px', textAlign: 'center', color: '#64748b' }}>
                Chargement...
              </div>
            ) : notifications.length > 0 ? (
              notifications.map((notification) => (
                <div
                  key={notification.id}
                  style={{
                    padding: '14px 18px',
                    borderBottom: '1px solid #f1f5f9',
                    backgroundColor: notification.isRead ? 'white' : getNotificationColor(notification.type),
                    cursor: 'pointer',
                    transition: 'background-color 0.2s'
                  }}
                  onClick={() => !notification.isRead && handleMarkAsRead(notification.id)}
                >
                  <div style={{ display: 'flex', gap: '12px' }}>
                    <div style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '10px',
                      backgroundColor: 'white',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0,
                      boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
                    }}>
                      {getNotificationIcon(notification.type)}
                    </div>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ 
                        fontWeight: notification.isRead ? '400' : '600', 
                        fontSize: '14px',
                        marginBottom: '4px',
                        color: '#1e293b'
                      }}>
                        {notification.title}
                      </div>
                      <div style={{ 
                        fontSize: '12px', 
                        color: '#64748b',
                        lineHeight: '1.4',
                        display: '-webkit-box',
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden'
                      }}>
                        {notification.message}
                      </div>
                      <div style={{ fontSize: '11px', color: '#94a3b8', marginTop: '6px' }}>
                        {formatDate(notification.createdAt)}
                      </div>
                    </div>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDelete(notification.id);
                      }}
                      style={{
                        background: 'none',
                        border: 'none',
                        color: '#94a3b8',
                        cursor: 'pointer',
                        padding: '4px',
                        borderRadius: '4px',
                        alignSelf: 'flex-start'
                      }}
                    >
                      <FiX size={16} />
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <div style={{ padding: '50px 20px', textAlign: 'center', color: '#94a3b8' }}>
                <FiBell size={40} style={{ marginBottom: '12px', opacity: 0.4 }} />
                <p style={{ margin: 0, fontSize: '14px' }}>Aucune notification</p>
                <p style={{ margin: '6px 0 0', fontSize: '12px' }}>Vous serez notifié des examens et cours à venir</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Notifications;