import React, { useState, useEffect } from 'react';
import { courseService, teacherService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { toast } from 'react-toastify';
import { FiPlus, FiEdit2, FiTrash2, FiX, FiVideo, FiPlay, FiEye, FiCpu, FiBookOpen } from 'react-icons/fi';

const Courses = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const isTeacher = user?.role === 'TEACHER';
  const isStudent = user?.role === 'STUDENT';
  const canManage = isAdmin || isTeacher;

  const [courses, setCourses] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Modal états
  const [showCourseModal, setShowCourseModal] = useState(false);
  const [showVideoModal, setShowVideoModal] = useState(false);
  const [showVideosListModal, setShowVideosListModal] = useState(false);
  const [showSummaryModal, setShowSummaryModal] = useState(false);
  
  const [editingCourse, setEditingCourse] = useState(null);
  const [selectedCourse, setSelectedCourse] = useState(null);
  const [videos, setVideos] = useState([]);
  const [editingVideo, setEditingVideo] = useState(null);
  
  // États pour IA
  const [summary, setSummary] = useState('');
  const [generatingSummary, setGeneratingSummary] = useState(false);
  
  const [courseForm, setCourseForm] = useState({
    courseCode: '', name: '', description: '', credits: 3, hoursPerWeek: 3, teacherId: '', semester: 1, type: 'LECTURE', maxStudents: 30
  });
  
  const [videoForm, setVideoForm] = useState({
    title: '', description: '', videoUrl: '', thumbnailUrl: '', durationMinutes: 0
  });

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    try {
      const [coursesRes, teachersRes] = await Promise.all([
        courseService.getAll(),
        teacherService.getAll().catch(() => ({ data: [] }))
      ]);
      setCourses(coursesRes.data);
      setTeachers(teachersRes.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des cours');
    } finally {
      setLoading(false);
    }
  };

  // CRUD Cours
  const handleCourseSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = { ...courseForm, teacherId: courseForm.teacherId ? parseInt(courseForm.teacherId) : null };
      if (editingCourse) {
        await courseService.update(editingCourse.id, data);
        toast.success('Cours mis à jour avec succès');
      } else {
        await courseService.create(data);
        toast.success('Cours créé avec succès');
      }
      fetchData();
      closeCourseModal();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Une erreur est survenue');
    }
  };

  const handleDeleteCourse = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer ce cours ?')) {
      try {
        await courseService.delete(id);
        toast.success('Cours supprimé avec succès');
        fetchData();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const openCourseModal = (course = null) => {
    if (course) {
      setEditingCourse(course);
      setCourseForm({
        courseCode: course.courseCode, name: course.name, description: course.description || '',
        credits: course.credits || 3, hoursPerWeek: course.hoursPerWeek || 3, teacherId: course.teacherId || '',
        semester: course.semester || 1, type: course.type || 'LECTURE', maxStudents: course.maxStudents || 30
      });
    } else {
      setEditingCourse(null);
      setCourseForm({ courseCode: '', name: '', description: '', credits: 3, hoursPerWeek: 3, teacherId: '', semester: 1, type: 'LECTURE', maxStudents: 30 });
    }
    setShowCourseModal(true);
  };

  const closeCourseModal = () => { 
    setShowCourseModal(false); 
    setEditingCourse(null); 
  };

  // Gestion des vidéos
  const openVideosListModal = async (course) => {
    setSelectedCourse(course);
    try {
      const response = isStudent 
        ? await courseService.getPublishedVideos(course.id)
        : await courseService.getVideos(course.id);
      setVideos(response.data);
    } catch (error) {
      setVideos([]);
    }
    setShowVideosListModal(true);
  };

  const openVideoModal = (video = null) => {
    if (video) {
      setEditingVideo(video);
      setVideoForm({
        title: video.title, description: video.description || '', videoUrl: video.videoUrl,
        thumbnailUrl: video.thumbnailUrl || '', durationMinutes: video.durationMinutes || 0
      });
    } else {
      setEditingVideo(null);
      setVideoForm({ title: '', description: '', videoUrl: '', thumbnailUrl: '', durationMinutes: 0 });
    }
    setShowVideoModal(true);
  };

  const closeVideoModal = () => {
    setShowVideoModal(false);
    setEditingVideo(null);
  };

  const handleVideoSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingVideo) {
        await courseService.updateVideo(editingVideo.id, videoForm);
        toast.success('Vidéo mise à jour avec succès');
      } else {
        await courseService.createVideo({
          ...videoForm,
          courseId: selectedCourse.id,
          uploadedBy: user?.id
        });
        toast.success('Vidéo ajoutée avec succès');
      }
      const response = await courseService.getVideos(selectedCourse.id);
      setVideos(response.data);
      closeVideoModal();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Erreur lors de l\'opération');
    }
  };

  const handleDeleteVideo = async (videoId) => {
    if (window.confirm('Supprimer cette vidéo ?')) {
      try {
        await courseService.deleteVideo(videoId);
        toast.success('Vidéo supprimée');
        const response = await courseService.getVideos(selectedCourse.id);
        setVideos(response.data);
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  // ==================== FONCTIONNALITÉ IA ====================
  const openSummaryModal = async (course) => {
    setSelectedCourse(course);
    setSummary('');
    setShowSummaryModal(true);
    
    // Charger le résumé existant
    try {
      const response = await courseService.getSummary(course.id);
      if (response.data.hasSummary) {
        setSummary(response.data.summary);
      }
    } catch (error) {
      console.log('Pas de résumé disponible');
    }
  };

  const generateAISummary = async () => {
    if (!selectedCourse) return;
    
    setGeneratingSummary(true);
    try {
      const response = await courseService.generateSummary(selectedCourse.id);
      if (response.data.success === 'true') {
        setSummary(response.data.summary);
        toast.success('Résumé généré avec succès !');
      } else {
        toast.error(response.data.error || 'Erreur lors de la génération');
      }
    } catch (error) {
      toast.error(error.response?.data?.error || 'Erreur lors de la génération du résumé');
    } finally {
      setGeneratingSummary(false);
    }
  };

  const closeSummaryModal = () => {
    setShowSummaryModal(false);
    setSelectedCourse(null);
    setSummary('');
  };
  // ===========================================================

  const getTeacherName = (teacherId) => {
    const teacher = teachers.find(t => t.id === teacherId);
    return teacher ? `${teacher.firstName} ${teacher.lastName}` : '-';
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Gestion des Cours</h1>
        {canManage && (
          <button className="btn btn-primary" onClick={() => openCourseModal()}>
            <FiPlus /> Ajouter un cours
          </button>
        )}
      </div>

      <div className="card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Nom</th>
                <th>Enseignant</th>
                <th>Crédits</th>
                <th>Semestre</th>
                <th>Type</th>
                <th>Ressources</th>
                {canManage && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {courses.length > 0 ? courses.map((course) => (
                <tr key={course.id}>
                  <td>{course.courseCode}</td>
                  <td>{course.name}</td>
                  <td>{getTeacherName(course.teacherId)}</td>
                  <td>{course.credits}</td>
                  <td>S{course.semester}</td>
                  <td><span className="badge badge-info">{course.type}</span></td>
                  <td>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button 
                        className="btn btn-secondary btn-sm" 
                        onClick={() => openVideosListModal(course)}
                        title="Voir les vidéos"
                      >
                        <FiVideo />
                      </button>
                      <button 
                        className="btn btn-sm" 
                        onClick={() => openSummaryModal(course)}
                        title="Résumé IA du cours"
                        style={{ backgroundColor: '#8b5cf6', color: 'white' }}
                      >
                        <FiCpu />
                      </button>
                    </div>
                  </td>
                  {canManage && (
                    <td>
                      <div className="actions">
                        <button className="btn btn-secondary btn-sm" onClick={() => openCourseModal(course)}>
                          <FiEdit2 />
                        </button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDeleteCourse(course.id)}>
                          <FiTrash2 />
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              )) : (
                <tr><td colSpan={canManage ? 8 : 7} style={{ textAlign: 'center', color: '#64748b' }}>Aucun cours trouvé</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Cours */}
      {showCourseModal && canManage && (
        <div className="modal-overlay" onClick={closeCourseModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editingCourse ? 'Modifier' : 'Ajouter'} un cours</h2>
              <button className="modal-close" onClick={closeCourseModal}><FiX /></button>
            </div>
            <form onSubmit={handleCourseSubmit}>
              <div className="form-group">
                <label className="form-label">Code du cours</label>
                <input type="text" className="form-control" value={courseForm.courseCode} 
                  onChange={(e) => setCourseForm({ ...courseForm, courseCode: e.target.value })} 
                  required disabled={!!editingCourse} />
              </div>
              <div className="form-group">
                <label className="form-label">Nom</label>
                <input type="text" className="form-control" value={courseForm.name} 
                  onChange={(e) => setCourseForm({ ...courseForm, name: e.target.value })} required />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea className="form-control" value={courseForm.description} 
                  onChange={(e) => setCourseForm({ ...courseForm, description: e.target.value })} rows="3" />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Crédits</label>
                  <input type="number" className="form-control" value={courseForm.credits} 
                    onChange={(e) => setCourseForm({ ...courseForm, credits: parseInt(e.target.value) })} min="1" max="10" />
                </div>
                <div className="form-group">
                  <label className="form-label">Heures/semaine</label>
                  <input type="number" className="form-control" value={courseForm.hoursPerWeek} 
                    onChange={(e) => setCourseForm({ ...courseForm, hoursPerWeek: parseInt(e.target.value) })} min="1" max="20" />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Enseignant</label>
                <select className="form-select" value={courseForm.teacherId} 
                  onChange={(e) => setCourseForm({ ...courseForm, teacherId: e.target.value })}>
                  <option value="">Sélectionner un enseignant</option>
                  {teachers.map(t => <option key={t.id} value={t.id}>{t.firstName} {t.lastName}</option>)}
                </select>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Semestre</label>
                  <select className="form-select" value={courseForm.semester} 
                    onChange={(e) => setCourseForm({ ...courseForm, semester: parseInt(e.target.value) })}>
                    {[1,2,3,4,5,6,7,8].map(s => <option key={s} value={s}>Semestre {s}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Type</label>
                  <select className="form-select" value={courseForm.type} 
                    onChange={(e) => setCourseForm({ ...courseForm, type: e.target.value })}>
                    <option value="LECTURE">Cours</option>
                    <option value="LAB">TP</option>
                    <option value="SEMINAR">TD</option>
                    <option value="PROJECT">Projet</option>
                  </select>
                </div>
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '20px' }}>
                <button type="button" className="btn btn-secondary" onClick={closeCourseModal}>Annuler</button>
                <button type="submit" className="btn btn-primary">{editingCourse ? 'Modifier' : 'Créer'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Liste des Vidéos */}
      {showVideosListModal && selectedCourse && (
        <div className="modal-overlay" onClick={() => setShowVideosListModal(false)}>
          <div className="modal" style={{ maxWidth: '800px', width: '90%' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">
                <FiVideo style={{ marginRight: '8px' }} />
                Vidéos - {selectedCourse.name}
              </h2>
              <button className="modal-close" onClick={() => setShowVideosListModal(false)}><FiX /></button>
            </div>
            
            {canManage && (
              <div style={{ marginBottom: '20px' }}>
                <button className="btn btn-primary" onClick={() => openVideoModal()}>
                  <FiPlus /> Ajouter une vidéo
                </button>
              </div>
            )}
            
            {videos.length > 0 ? (
              <div style={{ display: 'grid', gap: '16px', maxHeight: '400px', overflowY: 'auto' }}>
                {videos.map((video, index) => (
                  <div key={video.id} style={{ 
                    border: '1px solid #e2e8f0', 
                    borderRadius: '8px', 
                    padding: '16px',
                    display: 'flex',
                    gap: '16px',
                    alignItems: 'center'
                  }}>
                    <div style={{ 
                      width: '120px', 
                      height: '80px', 
                      backgroundColor: '#1e3a5f', 
                      borderRadius: '8px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0
                    }}>
                      <FiPlay color="white" size={24} />
                    </div>
                    <div style={{ flex: 1 }}>
                      <h4 style={{ margin: 0, marginBottom: '4px' }}>{index + 1}. {video.title}</h4>
                      <p style={{ margin: 0, color: '#64748b', fontSize: '14px' }}>{video.description}</p>
                      {video.durationMinutes > 0 && (
                        <span style={{ fontSize: '12px', color: '#94a3b8' }}>
                          Durée: {video.durationMinutes} min
                        </span>
                      )}
                    </div>
                    <div className="actions" style={{ display: 'flex', gap: '8px' }}>
                      <a 
                        href={video.videoUrl} 
                        target="_blank" 
                        rel="noopener noreferrer" 
                        className="btn btn-primary btn-sm"
                        title="Voir la vidéo"
                      >
                        <FiEye />
                      </a>
                      {canManage && (
                        <>
                          <button className="btn btn-secondary btn-sm" onClick={() => openVideoModal(video)}>
                            <FiEdit2 />
                          </button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDeleteVideo(video.id)}>
                            <FiTrash2 />
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '40px', color: '#64748b' }}>
                <FiVideo size={48} style={{ marginBottom: '16px', opacity: 0.5 }} />
                <p>Aucune vidéo disponible pour ce cours</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Modal Ajouter/Modifier Vidéo */}
      {showVideoModal && canManage && (
        <div className="modal-overlay" onClick={closeVideoModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editingVideo ? 'Modifier' : 'Ajouter'} une vidéo</h2>
              <button className="modal-close" onClick={closeVideoModal}><FiX /></button>
            </div>
            <form onSubmit={handleVideoSubmit}>
              <div className="form-group">
                <label className="form-label">Titre de la vidéo *</label>
                <input type="text" className="form-control" value={videoForm.title} 
                  onChange={(e) => setVideoForm({ ...videoForm, title: e.target.value })} required />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea className="form-control" value={videoForm.description} 
                  onChange={(e) => setVideoForm({ ...videoForm, description: e.target.value })} rows="3" />
              </div>
              <div className="form-group">
                <label className="form-label">URL de la vidéo * (YouTube, Vimeo, etc.)</label>
                <input type="url" className="form-control" value={videoForm.videoUrl} 
                  onChange={(e) => setVideoForm({ ...videoForm, videoUrl: e.target.value })} 
                  placeholder="https://www.youtube.com/watch?v=..." required />
              </div>
              <div className="form-group">
                <label className="form-label">Durée (minutes)</label>
                <input type="number" className="form-control" value={videoForm.durationMinutes} 
                  onChange={(e) => setVideoForm({ ...videoForm, durationMinutes: parseInt(e.target.value) || 0 })} 
                  min="0" />
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '20px' }}>
                <button type="button" className="btn btn-secondary" onClick={closeVideoModal}>Annuler</button>
                <button type="submit" className="btn btn-primary">{editingVideo ? 'Modifier' : 'Ajouter'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ==================== MODAL RÉSUMÉ IA ==================== */}
      {showSummaryModal && selectedCourse && (
        <div className="modal-overlay" onClick={closeSummaryModal}>
          <div className="modal" style={{ maxWidth: '700px', width: '90%' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <FiCpu style={{ color: '#8b5cf6' }} />
                Résumé IA - {selectedCourse.name}
              </h2>
              <button className="modal-close" onClick={closeSummaryModal}><FiX /></button>
            </div>
            
            {/* Bouton générer - visible seulement pour Admin/Prof */}
            {canManage && (
              <div style={{ marginBottom: '20px' }}>
                <button 
                  className="btn btn-primary" 
                  onClick={generateAISummary}
                  disabled={generatingSummary}
                  style={{ backgroundColor: '#8b5cf6', borderColor: '#8b5cf6' }}
                >
                  {generatingSummary ? (
                    <>
                      <span style={{ 
                        display: 'inline-block', 
                        width: '16px', 
                        height: '16px', 
                        border: '2px solid #fff', 
                        borderTopColor: 'transparent', 
                        borderRadius: '50%', 
                        animation: 'spin 1s linear infinite',
                        marginRight: '8px'
                      }}></span>
                      Génération en cours...
                    </>
                  ) : (
                    <>
                      <FiCpu style={{ marginRight: '8px' }} />
                      Générer le résumé IA
                    </>
                  )}
                </button>
                <p style={{ fontSize: '12px', color: '#64748b', marginTop: '8px' }}>
                  L'IA va analyser les informations du cours et ses vidéos pour générer un résumé complet.
                </p>
              </div>
            )}
            
            {/* Affichage du résumé */}
            {summary ? (
              <div style={{ 
                backgroundColor: '#f8fafc', 
                borderRadius: '8px', 
                padding: '20px',
                maxHeight: '400px',
                overflowY: 'auto',
                border: '1px solid #e2e8f0'
              }}>
                <div style={{ display: 'flex', alignItems: 'center', marginBottom: '16px', color: '#8b5cf6' }}>
                  <FiBookOpen style={{ marginRight: '8px' }} />
                  <strong>Résumé du cours</strong>
                </div>
                <div style={{ whiteSpace: 'pre-wrap', lineHeight: '1.7', color: '#334155' }}>
                  {summary}
                </div>
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '40px', color: '#64748b' }}>
                <FiCpu size={48} style={{ marginBottom: '16px', opacity: 0.5 }} />
                <p>Aucun résumé disponible pour ce cours</p>
                {canManage ? (
                  <p style={{ fontSize: '14px' }}>Cliquez sur "Générer le résumé IA" pour créer un résumé.</p>
                ) : (
                  <p style={{ fontSize: '14px' }}>Le résumé n'a pas encore été généré par l'enseignant.</p>
                )}
              </div>
            )}
          </div>
        </div>
      )}

      {/* CSS pour l'animation du spinner */}
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

export default Courses;