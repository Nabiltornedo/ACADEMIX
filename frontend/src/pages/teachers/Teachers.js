import React, { useState, useEffect } from 'react';
import { teacherService } from '../../services/api';
import { toast } from 'react-toastify';
import { FiPlus, FiEdit2, FiTrash2, FiX } from 'react-icons/fi';

const Teachers = () => {
  const [teachers, setTeachers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingTeacher, setEditingTeacher] = useState(null);
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', email: '', phone: '', department: '', specialization: ''
  });

  useEffect(() => { fetchTeachers(); }, []);

  const fetchTeachers = async () => {
    try {
      const response = await teacherService.getAll();
      setTeachers(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des enseignants');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingTeacher) {
        await teacherService.update(editingTeacher.id, formData);
        toast.success('Enseignant mis à jour avec succès');
      } else {
        await teacherService.create(formData);
        toast.success('Enseignant créé avec succès');
      }
      fetchTeachers();
      closeModal();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Une erreur est survenue');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cet enseignant ?')) {
      try {
        await teacherService.delete(id);
        toast.success('Enseignant supprimé avec succès');
        fetchTeachers();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const openModal = (teacher = null) => {
    if (teacher) {
      setEditingTeacher(teacher);
      setFormData({
        firstName: teacher.firstName, lastName: teacher.lastName, email: teacher.email,
        phone: teacher.phone || '', department: teacher.department || '', specialization: teacher.specialization || ''
      });
    } else {
      setEditingTeacher(null);
      setFormData({ firstName: '', lastName: '', email: '', phone: '', department: '', specialization: '' });
    }
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingTeacher(null);
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Gestion des Enseignants</h1>
        <button className="btn btn-primary" onClick={() => openModal()}><FiPlus /> Ajouter un enseignant</button>
      </div>

      <div className="card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr><th>Code</th><th>Nom complet</th><th>Email</th><th>Département</th><th>Spécialisation</th><th>Statut</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {teachers.length > 0 ? teachers.map((teacher) => (
                <tr key={teacher.id}>
                  <td>{teacher.teacherCode}</td>
                  <td>{teacher.firstName} {teacher.lastName}</td>
                  <td>{teacher.email}</td>
                  <td>{teacher.department || '-'}</td>
                  <td>{teacher.specialization || '-'}</td>
                  <td><span className={`badge badge-${teacher.status === 'ACTIVE' ? 'success' : 'warning'}`}>{teacher.status}</span></td>
                  <td>
                    <div className="actions">
                      <button className="btn btn-secondary btn-sm" onClick={() => openModal(teacher)}><FiEdit2 /></button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(teacher.id)}><FiTrash2 /></button>
                    </div>
                  </td>
                </tr>
              )) : (
                <tr><td colSpan="7" style={{ textAlign: 'center', color: '#64748b' }}>Aucun enseignant trouvé</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editingTeacher ? 'Modifier' : 'Ajouter'} un enseignant</h2>
              <button className="modal-close" onClick={closeModal}><FiX /></button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Prénom</label>
                <input type="text" className="form-control" value={formData.firstName} onChange={(e) => setFormData({ ...formData, firstName: e.target.value })} required />
              </div>
              <div className="form-group">
                <label className="form-label">Nom</label>
                <input type="text" className="form-control" value={formData.lastName} onChange={(e) => setFormData({ ...formData, lastName: e.target.value })} required />
              </div>
              <div className="form-group">
                <label className="form-label">Email</label>
                <input type="email" className="form-control" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} required />
              </div>
              <div className="form-group">
                <label className="form-label">Téléphone</label>
                <input type="text" className="form-control" value={formData.phone} onChange={(e) => setFormData({ ...formData, phone: e.target.value })} />
              </div>
              <div className="form-group">
                <label className="form-label">Département</label>
                <input type="text" className="form-control" value={formData.department} onChange={(e) => setFormData({ ...formData, department: e.target.value })} />
              </div>
              <div className="form-group">
                <label className="form-label">Spécialisation</label>
                <input type="text" className="form-control" value={formData.specialization} onChange={(e) => setFormData({ ...formData, specialization: e.target.value })} />
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={closeModal}>Annuler</button>
                <button type="submit" className="btn btn-primary">{editingTeacher ? 'Modifier' : 'Créer'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Teachers;
