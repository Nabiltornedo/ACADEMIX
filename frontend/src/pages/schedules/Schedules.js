import React, { useState, useEffect } from 'react';
import { scheduleService, courseService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { toast } from 'react-toastify';
import { FiPlus, FiEdit2, FiTrash2, FiX, FiMapPin } from 'react-icons/fi';

const Schedules = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  
  const [schedules, setSchedules] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Modals
  const [showScheduleModal, setShowScheduleModal] = useState(false);
  const [showRoomModal, setShowRoomModal] = useState(false);
  const [editingRoom, setEditingRoom] = useState(null);
  
  const [scheduleForm, setScheduleForm] = useState({
    courseId: '', roomId: '', dayOfWeek: 'MONDAY', startTime: '08:00', endTime: '10:00', semester: 'S1', type: 'LECTURE'
  });
  
  const [roomForm, setRoomForm] = useState({
    roomCode: '', name: '', building: '', floor: 0, capacity: 30, type: 'CLASSROOM', hasProjector: false, hasComputer: false
  });

  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
  const dayLabels = { MONDAY: 'Lundi', TUESDAY: 'Mardi', WEDNESDAY: 'Mercredi', THURSDAY: 'Jeudi', FRIDAY: 'Vendredi', SATURDAY: 'Samedi' };
  const roomTypes = ['CLASSROOM', 'LAB', 'AMPHITHEATER', 'MEETING_ROOM', 'OFFICE'];
  const roomTypeLabels = { 
    CLASSROOM: 'Salle de cours', 
    LAB: 'Laboratoire', 
    AMPHITHEATER: 'Amphithéâtre', 
    MEETING_ROOM: 'Salle de réunion', 
    OFFICE: 'Bureau' 
  };

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    try {
      const [schedulesRes, roomsRes, coursesRes] = await Promise.all([
        scheduleService.getAll(),
        scheduleService.getRooms().catch(() => ({ data: [] })),
        courseService.getAll().catch(() => ({ data: [] }))
      ]);
      setSchedules(schedulesRes.data);
      setRooms(roomsRes.data);
      setCourses(coursesRes.data);
    } catch (error) {
      toast.error('Erreur lors du chargement');
    } finally {
      setLoading(false);
    }
  };

  // CRUD Schedule
  const handleScheduleSubmit = async (e) => {
    e.preventDefault();
    try {
      await scheduleService.create({
        ...scheduleForm,
        courseId: parseInt(scheduleForm.courseId),
        roomId: scheduleForm.roomId ? parseInt(scheduleForm.roomId) : null
      });
      toast.success('Créneau créé avec succès');
      fetchData();
      setShowScheduleModal(false);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Erreur lors de la création');
    }
  };

  const handleDeleteSchedule = async (id) => {
    if (window.confirm('Supprimer ce créneau ?')) {
      try {
        await scheduleService.delete(id);
        toast.success('Créneau supprimé');
        fetchData();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  // CRUD Room
  const openRoomModal = (room = null) => {
    if (room) {
      setEditingRoom(room);
      setRoomForm({
        roomCode: room.roomCode,
        name: room.name,
        building: room.building || '',
        floor: room.floor || 0,
        capacity: room.capacity || 30,
        type: room.type || 'CLASSROOM',
        hasProjector: room.hasProjector || false,
        hasComputer: room.hasComputer || false
      });
    } else {
      setEditingRoom(null);
      setRoomForm({
        roomCode: '', name: '', building: '', floor: 0, capacity: 30, type: 'CLASSROOM', hasProjector: false, hasComputer: false
      });
    }
    setShowRoomModal(true);
  };

  const closeRoomModal = () => {
    setShowRoomModal(false);
    setEditingRoom(null);
  };

  const handleRoomSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingRoom) {
        await scheduleService.updateRoom(editingRoom.id, roomForm);
        toast.success('Salle mise à jour avec succès');
      } else {
        await scheduleService.createRoom(roomForm);
        toast.success('Salle créée avec succès');
      }
      fetchData();
      closeRoomModal();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Erreur lors de l\'opération');
    }
  };

  const handleDeleteRoom = async (id) => {
    if (window.confirm('Supprimer cette salle ?')) {
      try {
        await scheduleService.deleteRoom(id);
        toast.success('Salle supprimée');
        fetchData();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const toggleRoomAvailability = async (room) => {
    try {
      await scheduleService.updateRoom(room.id, { isAvailable: !room.isAvailable });
      toast.success('Disponibilité mise à jour');
      fetchData();
    } catch (error) {
      toast.error('Erreur');
    }
  };

  const getCourseName = (courseId) => courses.find(c => c.id === courseId)?.name || '-';
  const getRoomName = (roomId) => rooms.find(r => r.id === roomId)?.name || '-';

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Emploi du Temps</h1>
        {isAdmin && (
          <button className="btn btn-primary" onClick={() => setShowScheduleModal(true)}>
            <FiPlus /> Ajouter un créneau
          </button>
        )}
      </div>

      {/* Tableau des créneaux */}
      <div className="card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Jour</th>
                <th>Horaire</th>
                <th>Cours</th>
                <th>Salle</th>
                <th>Type</th>
                {isAdmin && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {schedules.length > 0 ? schedules.map((schedule) => (
                <tr key={schedule.id}>
                  <td>{dayLabels[schedule.dayOfWeek] || schedule.dayOfWeek}</td>
                  <td>{schedule.startTime} - {schedule.endTime}</td>
                  <td>{getCourseName(schedule.courseId)}</td>
                  <td>{getRoomName(schedule.roomId)}</td>
                  <td><span className="badge badge-info">{schedule.type}</span></td>
                  {isAdmin && (
                    <td>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDeleteSchedule(schedule.id)}>
                        <FiTrash2 />
                      </button>
                    </td>
                  )}
                </tr>
              )) : (
                <tr><td colSpan={isAdmin ? 6 : 5} style={{ textAlign: 'center', color: '#64748b' }}>Aucun créneau trouvé</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Section Salles */}
      <div className="card" style={{ marginTop: '24px' }}>
        <div className="card-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 20px', borderBottom: '1px solid #e2e8f0' }}>
          <h2 className="card-title" style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
            <FiMapPin /> Salles disponibles
          </h2>
          {isAdmin && (
            <button className="btn btn-primary btn-sm" onClick={() => openRoomModal()}>
              <FiPlus /> Ajouter une salle
            </button>
          )}
        </div>
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Nom</th>
                <th>Bâtiment</th>
                <th>Étage</th>
                <th>Capacité</th>
                <th>Type</th>
                <th>Équipements</th>
                <th>Disponible</th>
                {isAdmin && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {rooms.length > 0 ? rooms.map((room) => (
                <tr key={room.id}>
                  <td>{room.roomCode}</td>
                  <td>{room.name}</td>
                  <td>{room.building || '-'}</td>
                  <td>{room.floor || 0}</td>
                  <td>{room.capacity} places</td>
                  <td>{roomTypeLabels[room.type] || room.type}</td>
                  <td>
                    {room.hasProjector && <span className="badge badge-info" style={{ marginRight: '4px' }}>Projecteur</span>}
                    {room.hasComputer && <span className="badge badge-info">PC</span>}
                    {!room.hasProjector && !room.hasComputer && '-'}
                  </td>
                  <td>
                    <span 
                      className={`badge badge-${room.isAvailable ? 'success' : 'danger'}`}
                      style={{ cursor: isAdmin ? 'pointer' : 'default' }}
                      onClick={() => isAdmin && toggleRoomAvailability(room)}
                    >
                      {room.isAvailable ? 'Oui' : 'Non'}
                    </span>
                  </td>
                  {isAdmin && (
                    <td>
                      <div className="actions">
                        <button className="btn btn-secondary btn-sm" onClick={() => openRoomModal(room)}>
                          <FiEdit2 />
                        </button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDeleteRoom(room.id)}>
                          <FiTrash2 />
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              )) : (
                <tr><td colSpan={isAdmin ? 9 : 8} style={{ textAlign: 'center', color: '#64748b' }}>Aucune salle trouvée</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Créneau */}
      {showScheduleModal && (
        <div className="modal-overlay" onClick={() => setShowScheduleModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">Ajouter un créneau</h2>
              <button className="modal-close" onClick={() => setShowScheduleModal(false)}><FiX /></button>
            </div>
            <form onSubmit={handleScheduleSubmit}>
              <div className="form-group">
                <label className="form-label">Cours *</label>
                <select className="form-select" value={scheduleForm.courseId} 
                  onChange={(e) => setScheduleForm({ ...scheduleForm, courseId: e.target.value })} required>
                  <option value="">Sélectionner un cours</option>
                  {courses.map(c => <option key={c.id} value={c.id}>{c.courseCode} - {c.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Salle</label>
                <select className="form-select" value={scheduleForm.roomId} 
                  onChange={(e) => setScheduleForm({ ...scheduleForm, roomId: e.target.value })}>
                  <option value="">Sélectionner une salle</option>
                  {rooms.filter(r => r.isAvailable).map(r => <option key={r.id} value={r.id}>{r.roomCode} - {r.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Jour</label>
                <select className="form-select" value={scheduleForm.dayOfWeek} 
                  onChange={(e) => setScheduleForm({ ...scheduleForm, dayOfWeek: e.target.value })}>
                  {days.map(d => <option key={d} value={d}>{dayLabels[d]}</option>)}
                </select>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Début</label>
                  <input type="time" className="form-control" value={scheduleForm.startTime} 
                    onChange={(e) => setScheduleForm({ ...scheduleForm, startTime: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Fin</label>
                  <input type="time" className="form-control" value={scheduleForm.endTime} 
                    onChange={(e) => setScheduleForm({ ...scheduleForm, endTime: e.target.value })} required />
                </div>
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '20px' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowScheduleModal(false)}>Annuler</button>
                <button type="submit" className="btn btn-primary">Créer</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Salle */}
      {showRoomModal && (
        <div className="modal-overlay" onClick={closeRoomModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editingRoom ? 'Modifier' : 'Ajouter'} une salle</h2>
              <button className="modal-close" onClick={closeRoomModal}><FiX /></button>
            </div>
            <form onSubmit={handleRoomSubmit}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Code de la salle *</label>
                  <input type="text" className="form-control" value={roomForm.roomCode} 
                    onChange={(e) => setRoomForm({ ...roomForm, roomCode: e.target.value })} 
                    required disabled={!!editingRoom} placeholder="Ex: A101" />
                </div>
                <div className="form-group">
                  <label className="form-label">Nom *</label>
                  <input type="text" className="form-control" value={roomForm.name} 
                    onChange={(e) => setRoomForm({ ...roomForm, name: e.target.value })} 
                    required placeholder="Ex: Salle de cours 1" />
                </div>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Bâtiment</label>
                  <input type="text" className="form-control" value={roomForm.building} 
                    onChange={(e) => setRoomForm({ ...roomForm, building: e.target.value })} 
                    placeholder="Ex: Bâtiment A" />
                </div>
                <div className="form-group">
                  <label className="form-label">Étage</label>
                  <input type="number" className="form-control" value={roomForm.floor} 
                    onChange={(e) => setRoomForm({ ...roomForm, floor: parseInt(e.target.value) || 0 })} 
                    min="0" max="20" />
                </div>
                <div className="form-group">
                  <label className="form-label">Capacité</label>
                  <input type="number" className="form-control" value={roomForm.capacity} 
                    onChange={(e) => setRoomForm({ ...roomForm, capacity: parseInt(e.target.value) || 30 })} 
                    min="1" max="500" />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Type de salle</label>
                <select className="form-select" value={roomForm.type} 
                  onChange={(e) => setRoomForm({ ...roomForm, type: e.target.value })}>
                  {roomTypes.map(t => <option key={t} value={t}>{roomTypeLabels[t]}</option>)}
                </select>
              </div>
              <div style={{ display: 'flex', gap: '24px', marginTop: '16px' }}>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                  <input type="checkbox" checked={roomForm.hasProjector} 
                    onChange={(e) => setRoomForm({ ...roomForm, hasProjector: e.target.checked })} />
                  <span>Projecteur</span>
                </label>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                  <input type="checkbox" checked={roomForm.hasComputer} 
                    onChange={(e) => setRoomForm({ ...roomForm, hasComputer: e.target.checked })} />
                  <span>Ordinateur</span>
                </label>
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '20px' }}>
                <button type="button" className="btn btn-secondary" onClick={closeRoomModal}>Annuler</button>
                <button type="submit" className="btn btn-primary">{editingRoom ? 'Modifier' : 'Créer'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Schedules;
