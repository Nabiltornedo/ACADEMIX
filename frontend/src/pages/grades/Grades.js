import React, { useState, useEffect } from 'react';
import { gradeService, courseService, studentService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { toast } from 'react-toastify';
import { FiPlus, FiEdit2, FiTrash2, FiX, FiAward, FiTrendingUp, FiBookOpen } from 'react-icons/fi';

const Grades = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const isTeacher = user?.role === 'TEACHER';
  const isStudent = user?.role === 'STUDENT';
  const canManage = isAdmin || isTeacher;

  const [grades, setGrades] = useState([]);
  const [courses, setCourses] = useState([]);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [averages, setAverages] = useState(null);
  const [studentId, setStudentId] = useState(null); // ID de l'étudiant dans ms-student

  const [showModal, setShowModal] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);
  const [editingGrade, setEditingGrade] = useState(null);
  const [semesterReport, setSemesterReport] = useState(null);

  const [formData, setFormData] = useState({
    studentId: '', courseId: '', gradeType: 'EXAM', score: '', maxScore: 20, coefficient: 1, semester: 1, academicYear: '2024-2025', comments: ''
  });

  useEffect(() => {
    fetchData();
  }, []);

  // Trouver le studentId basé sur l'email de l'utilisateur connecté
  useEffect(() => {
    const findStudentId = async () => {
      if (isStudent && user?.email) {
        try {
          const response = await studentService.getAll();
          const student = response.data.find(s => s.email === user.email);
          if (student) {
            setStudentId(student.id);
          }
        } catch (error) {
          console.error('Error finding student:', error);
        }
      }
    };
    findStudentId();
  }, [user, isStudent]);

  // Charger les données de l'étudiant une fois le studentId trouvé
  useEffect(() => {
    const loadStudentData = async () => {
      if (isStudent && studentId) {
        try {
          const [gradesRes, avgRes] = await Promise.all([
            gradeService.getStudentGrades(studentId),
            gradeService.getStudentAverages(studentId)
          ]);
          setGrades(gradesRes.data);
          setAverages(avgRes.data);
        } catch (error) {
          console.error('Error loading student data:', error);
        }
      }
    };
    loadStudentData();
  }, [studentId, isStudent]);

  const fetchData = async () => {
    try {
      const [coursesRes, studentsRes] = await Promise.all([
        courseService.getAll().catch(() => ({ data: [] })),
        studentService.getAll().catch(() => ({ data: [] }))
      ]);
      setCourses(coursesRes.data);
      setStudents(studentsRes.data);

      if (!isStudent) {
        const gradesRes = await gradeService.getAll();
        setGrades(gradesRes.data);
      }
    } catch (error) {
      toast.error('Erreur lors du chargement');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        studentId: parseInt(formData.studentId),
        courseId: parseInt(formData.courseId),
        examId: null,
        gradeType: formData.gradeType,
        score: parseFloat(formData.score),
        maxScore: parseFloat(formData.maxScore) || 20.0,
        coefficient: parseFloat(formData.coefficient) || 1.0,
        semester: parseInt(formData.semester),
        academicYear: formData.academicYear || '2024-2025',
        comments: formData.comments || '',
        gradedBy: user?.id || null
      };

      if (editingGrade) {
        await gradeService.update(editingGrade.id, {
          score: data.score,
          maxScore: data.maxScore,
          comments: data.comments
        });
        toast.success('Note mise à jour!');
      } else {
        await gradeService.create(data);
        toast.success('Note ajoutée!');
      }
      fetchData();
      closeModal();
    } catch (error) {
      console.error('Error:', error.response?.data || error);
      toast.error('Erreur lors de l\'enregistrement');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Supprimer cette note?')) {
      try {
        await gradeService.delete(id);
        toast.success('Note supprimée');
        fetchData();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const openModal = (grade = null) => {
    if (grade) {
      setEditingGrade(grade);
      setFormData({
        studentId: grade.studentId,
        courseId: grade.courseId,
        gradeType: grade.gradeType,
        score: grade.score,
        maxScore: grade.maxScore,
        coefficient: grade.coefficient,
        semester: grade.semester || 1,
        academicYear: grade.academicYear || '2024-2025',
        comments: grade.comments || ''
      });
    } else {
      setEditingGrade(null);
      setFormData({
        studentId: '', courseId: '', gradeType: 'EXAM', score: '', maxScore: 20, coefficient: 1, semester: 1, academicYear: '2024-2025', comments: ''
      });
    }
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingGrade(null);
  };

  const viewSemesterReport = async (stdId, semester) => {
    try {
      const idToUse = stdId || studentId;
      const response = await gradeService.getSemesterReport(idToUse, semester, '2024-2025');
      setSemesterReport(response.data);
      setShowReportModal(true);
    } catch (error) {
      toast.error('Erreur lors du chargement du bulletin');
    }
  };

  const getCourseName = (courseId) => courses.find(c => c.id === courseId)?.name || '-';
  const getStudentName = (stdId) => {
    const student = students.find(s => s.id === stdId);
    return student ? `${student.firstName} ${student.lastName}` : '-';
  };

  const getMentionColor = (mention) => {
    if (mention === 'Très Bien') return '#166534';
    if (mention === 'Bien') return '#1e40af';
    if (mention === 'Assez Bien') return '#92400e';
    if (mention === 'Passable') return '#64748b';
    return '#991b1b';
  };

  const getGradeTypeBadge = (type) => {
    const colors = {
      EXAM: '#1e3a5f',
      MIDTERM: '#7c3aed',
      QUIZ: '#0891b2',
      HOMEWORK: '#059669',
      PROJECT: '#d97706',
      PRACTICAL: '#db2777',
      PARTICIPATION: '#64748b'
    };
    return (
      <span style={{
        padding: '4px 10px',
        borderRadius: '12px',
        backgroundColor: colors[type] || '#64748b',
        color: 'white',
        fontSize: '11px',
        fontWeight: '600'
      }}>
        {type}
      </span>
    );
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Gestion des Notes</h1>
        {canManage && (
          <button className="btn btn-primary" onClick={() => openModal()}>
            <FiPlus /> Ajouter une note
          </button>
        )}
      </div>

      {/* Statistiques étudiant */}
      {isStudent && averages && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '24px' }}>
          <div className="card" style={{ padding: '24px', textAlign: 'center', background: 'linear-gradient(135deg, #1e3a5f 0%, #2d4a6f 100%)', color: 'white' }}>
            <FiAward size={32} style={{ marginBottom: '8px' }} />
            <div style={{ fontSize: '36px', fontWeight: 'bold' }}>{averages.overallAverage || '-'}</div>
            <div style={{ opacity: 0.8 }}>Moyenne Générale</div>
          </div>
          <div className="card" style={{ padding: '24px', textAlign: 'center' }}>
            <FiTrendingUp size={32} style={{ marginBottom: '8px', color: getMentionColor(averages.mention) }} />
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: getMentionColor(averages.mention) }}>{averages.mention || '-'}</div>
            <div style={{ color: '#64748b' }}>Mention</div>
          </div>
          <div className="card" style={{ padding: '24px', textAlign: 'center' }}>
            <button 
              className="btn btn-primary" 
              onClick={() => viewSemesterReport(studentId, 1)}
              style={{ width: '100%' }}
            >
              <FiBookOpen /> Voir mon bulletin
            </button>
          </div>
        </div>
      )}

      {/* Liste des notes */}
      <div className="card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                {!isStudent && <th>Étudiant</th>}
                <th>Cours</th>
                <th>Type</th>
                <th>Note</th>
                <th>Sur 20</th>
                <th>Coef</th>
                <th>Semestre</th>
                {canManage && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {grades.length > 0 ? grades.map((grade) => (
                <tr key={grade.id}>
                  {!isStudent && <td>{getStudentName(grade.studentId)}</td>}
                  <td>{getCourseName(grade.courseId)}</td>
                  <td>{getGradeTypeBadge(grade.gradeType)}</td>
                  <td style={{ fontWeight: '600' }}>{grade.score}/{grade.maxScore}</td>
                  <td style={{ 
                    fontWeight: '600', 
                    color: grade.normalizedScore >= 10 ? '#166534' : '#991b1b' 
                  }}>
                    {grade.normalizedScore}
                  </td>
                  <td>{grade.coefficient}</td>
                  <td>S{grade.semester}</td>
                  {canManage && (
                    <td>
                      <div className="actions">
                        <button className="btn btn-secondary btn-sm" onClick={() => openModal(grade)}>
                          <FiEdit2 />
                        </button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(grade.id)}>
                          <FiTrash2 />
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              )) : (
                <tr><td colSpan={isStudent ? 6 : 8} style={{ textAlign: 'center', color: '#64748b' }}>Aucune note trouvée</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Ajouter/Modifier Note */}
      {showModal && canManage && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editingGrade ? 'Modifier' : 'Ajouter'} une note</h2>
              <button className="modal-close" onClick={closeModal}><FiX /></button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Étudiant</label>
                <select className="form-select" value={formData.studentId} onChange={(e) => setFormData({...formData, studentId: e.target.value})} required>
                  <option value="">Sélectionner un étudiant</option>
                  {students.map(s => <option key={s.id} value={s.id}>{s.firstName} {s.lastName}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Cours</label>
                <select className="form-select" value={formData.courseId} onChange={(e) => setFormData({...formData, courseId: e.target.value})} required>
                  <option value="">Sélectionner un cours</option>
                  {courses.map(c => <option key={c.id} value={c.id}>{c.courseCode} - {c.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Type</label>
                <select className="form-select" value={formData.gradeType} onChange={(e) => setFormData({...formData, gradeType: e.target.value})}>
                  <option value="EXAM">Examen</option>
                  <option value="MIDTERM">Contrôle</option>
                  <option value="QUIZ">Quiz</option>
                  <option value="HOMEWORK">Devoir</option>
                  <option value="PROJECT">Projet</option>
                  <option value="PRACTICAL">TP</option>
                  <option value="PARTICIPATION">Participation</option>
                </select>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Note</label>
                  <input type="number" step="0.25" className="form-control" value={formData.score} onChange={(e) => setFormData({...formData, score: e.target.value})} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Sur</label>
                  <input type="number" className="form-control" value={formData.maxScore} onChange={(e) => setFormData({...formData, maxScore: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Coefficient</label>
                  <input type="number" step="0.5" className="form-control" value={formData.coefficient} onChange={(e) => setFormData({...formData, coefficient: e.target.value})} />
                </div>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Semestre</label>
                  <select className="form-select" value={formData.semester} onChange={(e) => setFormData({...formData, semester: e.target.value})}>
                    {[1,2,3,4,5,6,7,8].map(s => <option key={s} value={s}>Semestre {s}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Année</label>
                  <input type="text" className="form-control" value={formData.academicYear} onChange={(e) => setFormData({...formData, academicYear: e.target.value})} />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Commentaires</label>
                <textarea className="form-control" value={formData.comments} onChange={(e) => setFormData({...formData, comments: e.target.value})} rows="2" />
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '20px' }}>
                <button type="button" className="btn btn-secondary" onClick={closeModal}>Annuler</button>
                <button type="submit" className="btn btn-primary">{editingGrade ? 'Modifier' : 'Ajouter'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Bulletin */}
      {showReportModal && semesterReport && (
        <div className="modal-overlay" onClick={() => setShowReportModal(false)}>
          <div className="modal" style={{ maxWidth: '800px' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header" style={{ background: 'linear-gradient(135deg, #1e3a5f 0%, #2d4a6f 100%)', color: 'white' }}>
              <h2 className="modal-title">📄 Bulletin - Semestre {semesterReport.semester}</h2>
              <button className="modal-close" onClick={() => setShowReportModal(false)} style={{ color: 'white' }}><FiX /></button>
            </div>
            <div style={{ padding: '24px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px', padding: '16px', backgroundColor: '#f8fafc', borderRadius: '8px' }}>
                <div>
                  <strong>Année académique:</strong> {semesterReport.academicYear}<br/>
                  <strong>Semestre:</strong> {semesterReport.semester}
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '32px', fontWeight: 'bold', color: '#1e3a5f' }}>{semesterReport.semesterAverage || '-'}/20</div>
                  <div style={{ color: getMentionColor(semesterReport.mention), fontWeight: '600' }}>{semesterReport.mention}</div>
                </div>
              </div>

              <table className="table" style={{ marginBottom: '20px' }}>
                <thead>
                  <tr>
                    <th>Matière</th>
                    <th>Notes</th>
                    <th>Moyenne</th>
                    <th>Moy. Classe</th>
                    <th>Crédits</th>
                    <th>Statut</th>
                  </tr>
                </thead>
                <tbody>
                  {semesterReport.courses?.map((course) => (
                    <tr key={course.courseId}>
                      <td>{course.courseName || getCourseName(course.courseId)}</td>
                      <td>
                        {course.grades?.map((g, i) => (
                          <span key={i} style={{ marginRight: '8px' }}>
                            {g.score}/{g.maxScore}
                          </span>
                        ))}
                      </td>
                      <td style={{ fontWeight: '600', color: course.average >= 10 ? '#166534' : '#991b1b' }}>
                        {course.average || '-'}
                      </td>
                      <td>{course.classAverage || '-'}</td>
                      <td>{course.credits || 3}</td>
                      <td>
                        <span style={{
                          padding: '4px 8px',
                          borderRadius: '12px',
                          backgroundColor: course.status === 'PASSED' ? '#dcfce7' : '#fee2e2',
                          color: course.status === 'PASSED' ? '#166534' : '#991b1b',
                          fontSize: '11px',
                          fontWeight: '600'
                        }}>
                          {course.status === 'PASSED' ? 'Validé' : 'Non validé'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '16px', backgroundColor: '#f1f5f9', borderRadius: '8px' }}>
                <div><strong>Crédits obtenus:</strong> {semesterReport.earnedCredits}/{semesterReport.totalCredits}</div>
                <div><strong>Moyenne générale:</strong> {semesterReport.semesterAverage}/20</div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Grades;