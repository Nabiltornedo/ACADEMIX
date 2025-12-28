import React, { useState, useEffect } from 'react';
import { studentService } from '../../services/api';
import { toast } from 'react-toastify';
import { FiPlus, FiEdit2, FiTrash2, FiX } from 'react-icons/fi';

const Students = () => {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', email: '', phone: '', address: '', gender: 'MALE'
  });

  useEffect(() => { fetchStudents(); }, []);

  const fetchStudents = async () => {
    try {
      const response = await studentService.getAll();
      setStudents(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des étudiants');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingStudent) {
        await studentService.update(editingStudent.id, formData);
        toast.success('Étudiant mis à jour avec succès');
      } else {
        await studentService.create(formData);
        toast.success('Étudiant créé avec succès');
      }
      fetchStudents();
      closeModal();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Une erreur est survenue');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cet étudiant ?')) {
      try {
        await studentService.delete(id);
        toast.success('Étudiant supprimé avec succès');
        fetchStudents();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const openModal = (student = null) => {
    if (student) {
      setEditingStudent(student);
      setFormData({
        firstName: student.firstName, lastName: student.lastName, email: student.email,
        phone: student.phone || '', address: student.address || '', gender: student.gender || 'MALE'
      });
    } else {
      setEditingStudent(null);
      setFormData({ firstName: '', lastName: '', email: '', phone: '', address: '', gender: 'MALE' });
    }
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingStudent(null);
    setFormData({ firstName: '', lastName: '', email: '', phone: '', address: '', gender: 'MALE' });
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Gestion des Étudiants</h1>
        <button className="btn btn-primary" onClick={() => openModal()}><FiPlus /> Ajouter un étudiant</button>
      </div>

      <div className="card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr><th>Code</th><th>Nom complet</th><th>Email</th><th>Téléphone</th><th>Semestre</th><th>Statut</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {students.length > 0 ? students.map((student) => (
                <tr key={student.id}>
                  <td>{student.studentCode}</td>
                  <td>{student.firstName} {student.lastName}</td>
                  <td>{student.email}</td>
                  <td>{student.phone || '-'}</td>
                  <td>S{student.currentSemester || 1}</td>
                  <td><span className={`badge badge-${student.status === 'ACTIVE' ? 'success' : 'warning'}`}>{student.status}</span></td>
                  <td>
                    <div className="actions">
                      <button className="btn btn-secondary btn-sm" onClick={() => openModal(student)}><FiEdit2 /></button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(student.id)}><FiTrash2 /></button>
                    </div>
                  </td>
                </tr>
              )) : (
                <tr><td colSpan="7" style={{ textAlign: 'center', color: '#64748b' }}>Aucun étudiant trouvé</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editingStudent ? 'Modifier' : 'Ajouter'} un étudiant</h2>
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
                <label className="form-label">Adresse</label>
                <input type="text" className="form-control" value={formData.address} onChange={(e) => setFormData({ ...formData, address: e.target.value })} />
              </div>
              <div className="form-group">
                <label className="form-label">Genre</label>
                <select className="form-select" value={formData.gender} onChange={(e) => setFormData({ ...formData, gender: e.target.value })}>
                  <option value="MALE">Masculin</option>
                  <option value="FEMALE">Féminin</option>
                  <option value="OTHER">Autre</option>
                </select>
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={closeModal}>Annuler</button>
                <button type="submit" className="btn btn-primary">{editingStudent ? 'Modifier' : 'Créer'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Students;
