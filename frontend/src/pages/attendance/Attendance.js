import React, { useState, useEffect } from 'react';
import { attendanceService, courseService, studentService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { toast } from 'react-toastify';
import { FiCheck, FiX, FiClock, FiUserCheck, FiAlertCircle, FiCalendar, FiFilter } from 'react-icons/fi';

const Attendance = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const isTeacher = user?.role === 'TEACHER';
  const isStudent = user?.role === 'STUDENT';
  const canManage = isAdmin || isTeacher;

  const [attendances, setAttendances] = useState([]);
  const [courses, setCourses] = useState([]);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState(null);
  const [studentId, setStudentId] = useState(null); // ID de l'étudiant dans ms-student

  // Filtres
  const [selectedCourse, setSelectedCourse] = useState('');
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

  // Modals
  const [showMarkModal, setShowMarkModal] = useState(false);
  const [showQRModal, setShowQRModal] = useState(false);
  const [qrCodeData, setQrCodeData] = useState(null);

  // Form
  const [bulkAttendance, setBulkAttendance] = useState([]);

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
          const [attendanceRes, statsRes] = await Promise.all([
            attendanceService.getStudentAttendance(studentId),
            attendanceService.getStudentStats(studentId)
          ]);
          setAttendances(attendanceRes.data);
          setStats(statsRes.data);
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
        const attendanceRes = await attendanceService.getAll();
        setAttendances(attendanceRes.data);
      }
    } catch (error) {
      toast.error('Erreur lors du chargement');
    } finally {
      setLoading(false);
    }
  };

  const generateQRCode = async () => {
    if (!selectedCourse) {
      toast.error('Veuillez sélectionner un cours');
      return;
    }
    try {
      const course = courses.find(c => c.id === parseInt(selectedCourse));
      const response = await attendanceService.generateQRCode(selectedCourse, course?.name);
      setQrCodeData(response.data);
      setShowQRModal(true);
      toast.success('QR Code généré!');
    } catch (error) {
      toast.error('Erreur lors de la génération du QR Code');
    }
  };

  const openMarkModal = () => {
    if (!selectedCourse) {
      toast.error('Veuillez sélectionner un cours');
      return;
    }
    setBulkAttendance(students.map(s => ({
      studentId: s.id,
      studentName: `${s.firstName} ${s.lastName}`,
      status: 'PRESENT'
    })));
    setShowMarkModal(true);
  };

  const handleBulkSubmit = async () => {
    try {
      await attendanceService.markBulkAttendance({
        courseId: parseInt(selectedCourse),
        attendanceDate: selectedDate,
        markedBy: user?.id,
        students: bulkAttendance.map(s => ({
          studentId: s.studentId,
          status: s.status
        }))
      });
      toast.success('Présences enregistrées!');
      setShowMarkModal(false);
      fetchData();
    } catch (error) {
      toast.error('Erreur lors de l\'enregistrement');
    }
  };

  const getStatusBadge = (status) => {
    const styles = {
      PRESENT: { bg: '#dcfce7', color: '#166534', icon: <FiCheck /> },
      ABSENT: { bg: '#fee2e2', color: '#991b1b', icon: <FiX /> },
      LATE: { bg: '#fef3c7', color: '#92400e', icon: <FiClock /> },
      EXCUSED: { bg: '#dbeafe', color: '#1e40af', icon: <FiAlertCircle /> }
    };
    const style = styles[status] || styles.ABSENT;
    return (
      <span style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: '4px',
        padding: '4px 12px',
        borderRadius: '20px',
        backgroundColor: style.bg,
        color: style.color,
        fontSize: '12px',
        fontWeight: '600'
      }}>
        {style.icon} {status}
      </span>
    );
  };

  const getCourseName = (courseId) => courses.find(c => c.id === courseId)?.name || '-';
  const getStudentName = (stdId) => {
    const student = students.find(s => s.id === stdId);
    return student ? `${student.firstName} ${student.lastName}` : '-';
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Gestion des Présences</h1>
      </div>

      {/* Statistiques étudiant */}
      {isStudent && stats && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '16px', marginBottom: '24px' }}>
          <div className="card" style={{ padding: '20px', textAlign: 'center' }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', color: '#166534' }}>{stats.presentCount}</div>
            <div style={{ color: '#64748b', fontSize: '14px' }}>Présent</div>
          </div>
          <div className="card" style={{ padding: '20px', textAlign: 'center' }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', color: '#991b1b' }}>{stats.absentCount}</div>
            <div style={{ color: '#64748b', fontSize: '14px' }}>Absent</div>
          </div>
          <div className="card" style={{ padding: '20px', textAlign: 'center' }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', color: '#92400e' }}>{stats.lateCount}</div>
            <div style={{ color: '#64748b', fontSize: '14px' }}>Retard</div>
          </div>
          <div className="card" style={{ padding: '20px', textAlign: 'center' }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', color: '#1e3a5f' }}>{stats.attendanceRate}%</div>
            <div style={{ color: '#64748b', fontSize: '14px' }}>Taux présence</div>
          </div>
        </div>
      )}

      {/* Filtres et actions (Admin/Prof) */}
      {canManage && (
        <div className="card" style={{ padding: '20px', marginBottom: '24px' }}>
          <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', alignItems: 'flex-end' }}>
            <div style={{ flex: 1, minWidth: '200px' }}>
              <label className="form-label"><FiFilter /> Cours</label>
              <select 
                className="form-select" 
                value={selectedCourse} 
                onChange={(e) => setSelectedCourse(e.target.value)}
              >
                <option value="">Sélectionner un cours</option>
                {courses.map(c => <option key={c.id} value={c.id}>{c.courseCode} - {c.name}</option>)}
              </select>
            </div>
            <div style={{ minWidth: '200px' }}>
              <label className="form-label"><FiCalendar /> Date</label>
              <input 
                type="date" 
                className="form-control" 
                value={selectedDate} 
                onChange={(e) => setSelectedDate(e.target.value)} 
              />
            </div>
            <button className="btn btn-primary" onClick={openMarkModal}>
              <FiUserCheck /> Marquer présences
            </button>
            <button className="btn btn-secondary" onClick={generateQRCode} style={{ backgroundColor: '#8b5cf6', color: 'white' }}>
              📱 Générer QR Code
            </button>
          </div>
        </div>
      )}

      {/* Liste des présences */}
      <div className="card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                {!isStudent && <th>Étudiant</th>}
                <th>Cours</th>
                <th>Date</th>
                <th>Heure</th>
                <th>Statut</th>
                <th>Justifié</th>
              </tr>
            </thead>
            <tbody>
              {attendances.length > 0 ? attendances.map((att) => (
                <tr key={att.id}>
                  {!isStudent && <td>{getStudentName(att.studentId)}</td>}
                  <td>{getCourseName(att.courseId)}</td>
                  <td>{att.attendanceDate}</td>
                  <td>{att.checkInTime || '-'}</td>
                  <td>{getStatusBadge(att.status)}</td>
                  <td>
                    {att.isJustified ? (
                      <span style={{ color: '#166534' }}>✓ Oui</span>
                    ) : att.status === 'ABSENT' ? (
                      <span style={{ color: '#991b1b' }}>✗ Non</span>
                    ) : '-'}
                  </td>
                </tr>
              )) : (
                <tr><td colSpan={isStudent ? 5 : 6} style={{ textAlign: 'center', color: '#64748b' }}>Aucune présence enregistrée</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Marquer présences */}
      {showMarkModal && (
        <div className="modal-overlay" onClick={() => setShowMarkModal(false)}>
          <div className="modal" style={{ maxWidth: '600px' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">Marquer les présences</h2>
              <button className="modal-close" onClick={() => setShowMarkModal(false)}><FiX /></button>
            </div>
            <div style={{ marginBottom: '16px' }}>
              <strong>Cours:</strong> {courses.find(c => c.id === parseInt(selectedCourse))?.name}<br/>
              <strong>Date:</strong> {selectedDate}
            </div>
            <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
              {bulkAttendance.map((student, index) => (
                <div key={student.studentId} style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  padding: '12px',
                  borderBottom: '1px solid #e2e8f0'
                }}>
                  <span>{student.studentName}</span>
                  <select
                    value={student.status}
                    onChange={(e) => {
                      const updated = [...bulkAttendance];
                      updated[index].status = e.target.value;
                      setBulkAttendance(updated);
                    }}
                    style={{
                      padding: '6px 12px',
                      borderRadius: '6px',
                      border: '1px solid #e2e8f0'
                    }}
                  >
                    <option value="PRESENT">✓ Présent</option>
                    <option value="ABSENT">✗ Absent</option>
                    <option value="LATE">⏰ Retard</option>
                    <option value="EXCUSED">📋 Excusé</option>
                  </select>
                </div>
              ))}
            </div>
            <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '20px' }}>
              <button className="btn btn-secondary" onClick={() => setShowMarkModal(false)}>Annuler</button>
              <button className="btn btn-primary" onClick={handleBulkSubmit}>Enregistrer</button>
            </div>
          </div>
        </div>
      )}

      {/* Modal QR Code */}
      {showQRModal && qrCodeData && (
        <div className="modal-overlay" onClick={() => setShowQRModal(false)}>
          <div className="modal" style={{ maxWidth: '400px', textAlign: 'center' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">📱 QR Code Présence</h2>
              <button className="modal-close" onClick={() => setShowQRModal(false)}><FiX /></button>
            </div>
            <div style={{ padding: '20px' }}>
              <div style={{
                width: '200px',
                height: '200px',
                margin: '0 auto 20px',
                backgroundColor: '#f1f5f9',
                borderRadius: '12px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '14px',
                color: '#64748b',
                border: '2px dashed #cbd5e1'
              }}>
                <div>
                  <div style={{ fontSize: '48px', marginBottom: '8px' }}>📱</div>
                  <div>QR Code</div>
                  <div style={{ fontSize: '10px', marginTop: '8px', wordBreak: 'break-all' }}>
                    {qrCodeData.qrCode}
                  </div>
                </div>
              </div>
              <p style={{ color: '#64748b', fontSize: '14px' }}>
                <strong>Cours:</strong> {qrCodeData.courseName}<br/>
                <strong>Date:</strong> {qrCodeData.date}<br/>
                <strong>Expire:</strong> {new Date(qrCodeData.expiresAt).toLocaleTimeString('fr-FR')}
              </p>
              <p style={{ fontSize: '12px', color: '#94a3b8' }}>
                Les étudiants peuvent scanner ce code pour marquer leur présence.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Attendance;