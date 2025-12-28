import React, { useState, useEffect } from 'react';
import { examService, courseService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { toast } from 'react-toastify';
import { FiPlus, FiEdit2, FiTrash2, FiX } from 'react-icons/fi';

const Exams = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const isTeacher = user?.role === 'TEACHER';
  const canManage = isAdmin || isTeacher;

  const [exams, setExams] = useState([]);
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingExam, setEditingExam] = useState(null);
  const [formData, setFormData] = useState({
    title: '', courseId: '', examDate: '', startTime: '08:00', endTime: '10:00', type: 'MIDTERM', maxScore: 20, passingScore: 10, instructions: ''
  });

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    try {
      const [examsRes, coursesRes] = await Promise.all([
        examService.getAll(),
        courseService.getAll().catch(() => ({ data: [] }))
      ]);
      setExams(examsRes.data);
      setCourses(coursesRes.data);
    } catch (error) {
      toast.error('Erreur lors du chargement');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = { ...formData, courseId: parseInt(formData.courseId), maxScore: parseFloat(formData.maxScore), passingScore: parseFloat(formData.passingScore) };
      if (editingExam) {
        await examService.update(editingExam.id, data);
        toast.success('Examen mis à jour avec succès');
      } else {
        await examService.create(data);
        toast.success('Examen créé avec succès');
      }
      fetchData();
      closeModal();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Erreur');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Supprimer cet examen ?')) {
      try {
        await examService.delete(id);
        toast.success('Examen supprimé');
        fetchData();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const openModal = (exam = null) => {
    if (exam) {
      setEditingExam(exam);
      setFormData({
        title: exam.title, courseId: exam.courseId, examDate: exam.examDate, startTime: exam.startTime || '08:00',
        endTime: exam.endTime || '10:00', type: exam.type || 'MIDTERM', maxScore: exam.maxScore || 20,
        passingScore: exam.passingScore || 10, instructions: exam.instructions || ''
      });
    } else {
      setEditingExam(null);
      setFormData({ title: '', courseId: '', examDate: '', startTime: '08:00', endTime: '10:00', type: 'MIDTERM', maxScore: 20, passingScore: 10, instructions: '' });
    }
    setShowModal(true);
  };

  const closeModal = () => { setShowModal(false); setEditingExam(null); };
  const getCourseName = (courseId) => courses.find(c => c.id === courseId)?.name || '-';

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Gestion des Examens</h1>
        {canManage && (
          <button className="btn btn-primary" onClick={() => openModal()}>
            <FiPlus /> Planifier un examen
          </button>
        )}
      </div>

      <div className="card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Titre</th>
                <th>Cours</th>
                <th>Date</th>
                <th>Horaire</th>
                <th>Type</th>
                <th>Note max</th>
                <th>Statut</th>
                {canManage && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {exams.length > 0 ? exams.map((exam) => (
                <tr key={exam.id}>
                  <td>{exam.examCode}</td>
                  <td>{exam.title}</td>
                  <td>{getCourseName(exam.courseId)}</td>
                  <td>{exam.examDate}</td>
                  <td>{exam.startTime} - {exam.endTime}</td>
                  <td><span className="badge badge-info">{exam.type}</span></td>
                  <td>{exam.maxScore}</td>
                  <td><span className={`badge badge-${exam.status === 'SCHEDULED' ? 'warning' : exam.status === 'COMPLETED' ? 'success' : 'info'}`}>{exam.status}</span></td>
                  {canManage && (
                    <td>
                      <div className="actions">
                        <button className="btn btn-secondary btn-sm" onClick={() => openModal(exam)}><FiEdit2 /></button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(exam.id)}><FiTrash2 /></button>
                      </div>
                    </td>
                  )}
                </tr>
              )) : (
                <tr><td colSpan={canManage ? 9 : 8} style={{ textAlign: 'center', color: '#64748b' }}>Aucun examen trouvé</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && canManage && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editingExam ? 'Modifier' : 'Planifier'} un examen</h2>
              <button className="modal-close" onClick={closeModal}><FiX /></button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Titre</label>
                <input type="text" className="form-control" value={formData.title} onChange={(e) => setFormData({ ...formData, title: e.target.value })} required />
              </div>
              <div className="form-group">
                <label className="form-label">Cours</label>
                <select className="form-select" value={formData.courseId} onChange={(e) => setFormData({ ...formData, courseId: e.target.value })} required>
                  <option value="">Sélectionner un cours</option>
                  {courses.map(c => <option key={c.id} value={c.id}>{c.courseCode} - {c.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Date</label>
                <input type="date" className="form-control" value={formData.examDate} onChange={(e) => setFormData({ ...formData, examDate: e.target.value })} required />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Début</label>
                  <input type="time" className="form-control" value={formData.startTime} onChange={(e) => setFormData({ ...formData, startTime: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Fin</label>
                  <input type="time" className="form-control" value={formData.endTime} onChange={(e) => setFormData({ ...formData, endTime: e.target.value })} required />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Type</label>
                <select className="form-select" value={formData.type} onChange={(e) => setFormData({ ...formData, type: e.target.value })}>
                  <option value="MIDTERM">Contrôle</option>
                  <option value="FINAL">Examen final</option>
                  <option value="QUIZ">Quiz</option>
                  <option value="PRACTICAL">TP</option>
                </select>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Note maximale</label>
                  <input type="number" className="form-control" value={formData.maxScore} onChange={(e) => setFormData({ ...formData, maxScore: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Note de passage</label>
                  <input type="number" className="form-control" value={formData.passingScore} onChange={(e) => setFormData({ ...formData, passingScore: e.target.value })} />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Instructions</label>
                <textarea className="form-control" value={formData.instructions} onChange={(e) => setFormData({ ...formData, instructions: e.target.value })} rows="3" />
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={closeModal}>Annuler</button>
                <button type="submit" className="btn btn-primary">{editingExam ? 'Modifier' : 'Créer'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Exams;