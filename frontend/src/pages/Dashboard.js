import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { studentService, teacherService, courseService, examService, attendanceService, gradeService } from '../services/api';
import { 
  Chart as ChartJS, 
  CategoryScale, 
  LinearScale, 
  PointElement, 
  LineElement, 
  BarElement,
  ArcElement,
  Title, 
  Tooltip, 
  Legend,
  Filler
} from 'chart.js';
import { Line, Bar, Doughnut } from 'react-chartjs-2';
import { 
  FiUsers, FiBook, FiCalendar, FiFileText, FiTrendingUp, 
  FiClock, FiCheckCircle, FiAlertCircle, FiAward, FiUserCheck 
} from 'react-icons/fi';

// Enregistrer les composants Chart.js
ChartJS.register(
  CategoryScale, LinearScale, PointElement, LineElement, 
  BarElement, ArcElement, Title, Tooltip, Legend, Filler
);

const Dashboard = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const isTeacher = user?.role === 'TEACHER';
  const isStudent = user?.role === 'STUDENT';

  const [stats, setStats] = useState({
    students: 0,
    teachers: 0,
    courses: 0,
    exams: 0
  });
  const [courses, setCourses] = useState([]);
  const [exams, setExams] = useState([]);
  const [upcomingEvents, setUpcomingEvents] = useState([]);
  const [studentStats, setStudentStats] = useState(null);
  const [studentGrades, setStudentGrades] = useState([]);
  const [attendanceStats, setAttendanceStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [studentId, setStudentId] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  // Trouver le studentId pour les étudiants
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

  // Charger les données de l'étudiant
  useEffect(() => {
    const loadStudentData = async () => {
      if (isStudent && studentId) {
        try {
          const [gradesRes, attendanceRes] = await Promise.all([
            gradeService.getStudentGrades(studentId),
            attendanceService.getStudentStats(studentId)
          ]);
          setStudentGrades(gradesRes.data);
          setAttendanceStats(attendanceRes.data);
        } catch (error) {
          console.error('Error loading student data:', error);
        }
      }
    };
    loadStudentData();
  }, [studentId, isStudent]);

  const fetchDashboardData = async () => {
    try {
      const [studentsRes, teachersRes, coursesRes, examsRes] = await Promise.all([
        studentService.getAll().catch(() => ({ data: [] })),
        teacherService.getAll().catch(() => ({ data: [] })),
        courseService.getAll().catch(() => ({ data: [] })),
        examService.getAll().catch(() => ({ data: [] }))
      ]);

      setStats({
        students: studentsRes.data.length,
        teachers: teachersRes.data.length,
        courses: coursesRes.data.length,
        exams: examsRes.data.length
      });

      setCourses(coursesRes.data);
      setExams(examsRes.data);

      // Calculer les événements à venir
      const today = new Date();
      const upcoming = examsRes.data
        .filter(exam => new Date(exam.examDate) >= today)
        .sort((a, b) => new Date(a.examDate) - new Date(b.examDate))
        .slice(0, 5);
      setUpcomingEvents(upcoming);

    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  // Données pour le graphique des notes (étudiant)
  const gradesChartData = {
    labels: studentGrades.slice(0, 10).map(g => g.courseName || `Cours ${g.courseId}`),
    datasets: [{
      label: 'Notes (/20)',
      data: studentGrades.slice(0, 10).map(g => (g.score / g.maxScore) * 20),
      backgroundColor: 'rgba(30, 58, 95, 0.8)',
      borderColor: '#1e3a5f',
      borderWidth: 2,
      borderRadius: 8,
    }]
  };

  // Données pour le graphique de présence (étudiant)
  const attendanceChartData = {
    labels: ['Présent', 'Absent', 'Retard', 'Excusé'],
    datasets: [{
      data: [
        attendanceStats?.presentCount || 0,
        attendanceStats?.absentCount || 0,
        attendanceStats?.lateCount || 0,
        attendanceStats?.excusedCount || 0
      ],
      backgroundColor: ['#22c55e', '#ef4444', '#f59e0b', '#3b82f6'],
      borderWidth: 0,
    }]
  };

  // Données pour le graphique des examens par mois (admin/teacher)
  const getExamsByMonth = () => {
    const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc'];
    const examCounts = new Array(12).fill(0);
    
    exams.forEach(exam => {
      const month = new Date(exam.examDate).getMonth();
      examCounts[month]++;
    });

    return {
      labels: months,
      datasets: [{
        label: 'Examens planifiés',
        data: examCounts,
        fill: true,
        backgroundColor: 'rgba(30, 58, 95, 0.1)',
        borderColor: '#1e3a5f',
        tension: 0.4,
        pointBackgroundColor: '#1e3a5f',
      }]
    };
  };

  // Données pour la répartition des cours par statut
  const courseStatusData = {
    labels: ['Actifs', 'Inactifs', 'Terminés'],
    datasets: [{
      data: [
        courses.filter(c => c.status === 'ACTIVE').length,
        courses.filter(c => c.status === 'INACTIVE').length,
        courses.filter(c => c.status === 'COMPLETED').length || 0
      ],
      backgroundColor: ['#22c55e', '#64748b', '#3b82f6'],
      borderWidth: 0,
    }]
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: { padding: 20, usePointStyle: true }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        grid: { color: 'rgba(0,0,0,0.05)' }
      },
      x: {
        grid: { display: false }
      }
    }
  };

  const doughnutOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: { padding: 15, usePointStyle: true }
      }
    },
    cutout: '70%'
  };

  // Calculer la progression du semestre
  const getSemesterProgress = () => {
    const startDate = new Date('2024-09-01');
    const endDate = new Date('2025-01-31');
    const today = new Date();
    const total = endDate - startDate;
    const elapsed = today - startDate;
    return Math.min(Math.max((elapsed / total) * 100, 0), 100);
  };

  if (loading) {
    return <div className="loading"><div className="spinner"></div></div>;
  }

  return (
    <div>
      {/* Header */}
      <div className="header" style={{ marginBottom: '24px' }}>
        <div>
          <h1 className="page-title">Tableau de bord</h1>
          <p style={{ color: '#64748b', marginTop: '4px' }}>
            Bienvenue, {user?.firstName} {user?.lastName}
          </p>
        </div>
        <div style={{ 
          padding: '12px 24px', 
          backgroundColor: '#1e3a5f', 
          color: 'white', 
          borderRadius: '12px',
          display: 'flex',
          alignItems: 'center',
          gap: '8px'
        }}>
          <FiCalendar />
          {new Date().toLocaleDateString('fr-FR', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
          })}
        </div>
      </div>

      {/* Cartes statistiques (Admin/Teacher) */}
      {(isAdmin || isTeacher) && (
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', 
          gap: '20px', 
          marginBottom: '24px' 
        }}>
          <StatCard 
            icon={<FiUsers />} 
            value={stats.students} 
            label="Étudiants" 
            color="#3b82f6" 
            bgColor="#dbeafe"
          />
          <StatCard 
            icon={<FiUserCheck />} 
            value={stats.teachers} 
            label="Enseignants" 
            color="#22c55e" 
            bgColor="#dcfce7"
          />
          <StatCard 
            icon={<FiBook />} 
            value={stats.courses} 
            label="Cours" 
            color="#f59e0b" 
            bgColor="#fef3c7"
          />
          <StatCard 
            icon={<FiFileText />} 
            value={stats.exams} 
            label="Examens" 
            color="#ef4444" 
            bgColor="#fee2e2"
          />
        </div>
      )}

      {/* Cartes statistiques (Étudiant) */}
      {isStudent && attendanceStats && (
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', 
          gap: '20px', 
          marginBottom: '24px' 
        }}>
          <StatCard 
            icon={<FiCheckCircle />} 
            value={attendanceStats.presentCount || 0} 
            label="Présences" 
            color="#22c55e" 
            bgColor="#dcfce7"
          />
          <StatCard 
            icon={<FiAlertCircle />} 
            value={attendanceStats.absentCount || 0} 
            label="Absences" 
            color="#ef4444" 
            bgColor="#fee2e2"
          />
          <StatCard 
            icon={<FiAward />} 
            value={studentGrades.length} 
            label="Notes" 
            color="#3b82f6" 
            bgColor="#dbeafe"
          />
          <StatCard 
            icon={<FiTrendingUp />} 
            value={`${attendanceStats.attendanceRate || 0}%`} 
            label="Taux de présence" 
            color="#8b5cf6" 
            bgColor="#ede9fe"
          />
        </div>
      )}

      {/* Progression du semestre */}
      <div className="card" style={{ padding: '24px', marginBottom: '24px' }}>
        <h3 style={{ marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <FiTrendingUp color="#1e3a5f" />
          Progression du Semestre
        </h3>
        <div style={{ 
          backgroundColor: '#e2e8f0', 
          borderRadius: '10px', 
          height: '24px', 
          overflow: 'hidden',
          position: 'relative'
        }}>
          <div style={{ 
            width: `${getSemesterProgress()}%`, 
            height: '100%', 
            background: 'linear-gradient(90deg, #1e3a5f, #3b82f6)',
            borderRadius: '10px',
            transition: 'width 1s ease-in-out'
          }} />
          <span style={{ 
            position: 'absolute', 
            right: '12px', 
            top: '50%', 
            transform: 'translateY(-50%)',
            fontWeight: '600',
            fontSize: '12px'
          }}>
            {getSemesterProgress().toFixed(0)}%
          </span>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px', fontSize: '12px', color: '#64748b' }}>
          <span>Début: 1 Sep 2024</span>
          <span>Fin: 31 Jan 2025</span>
        </div>
      </div>

      {/* Graphiques */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', 
        gap: '24px', 
        marginBottom: '24px' 
      }}>
        {/* Graphique principal */}
        <div className="card" style={{ padding: '24px' }}>
          <h3 style={{ marginBottom: '20px' }}>
            {isStudent ? '📊 Mes Notes' : '📈 Examens par Mois'}
          </h3>
          <div style={{ height: '300px' }}>
            {isStudent ? (
              studentGrades.length > 0 ? (
                <Bar data={gradesChartData} options={chartOptions} />
              ) : (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: '#64748b' }}>
                  Aucune note disponible
                </div>
              )
            ) : (
              <Line data={getExamsByMonth()} options={chartOptions} />
            )}
          </div>
        </div>

        {/* Graphique circulaire */}
        <div className="card" style={{ padding: '24px' }}>
          <h3 style={{ marginBottom: '20px' }}>
            {isStudent ? '📅 Mes Présences' : '📚 Statut des Cours'}
          </h3>
          <div style={{ height: '300px' }}>
            {isStudent ? (
              attendanceStats && (attendanceStats.presentCount > 0 || attendanceStats.absentCount > 0) ? (
                <Doughnut data={attendanceChartData} options={doughnutOptions} />
              ) : (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: '#64748b' }}>
                  Aucune donnée de présence
                </div>
              )
            ) : (
              <Doughnut data={courseStatusData} options={doughnutOptions} />
            )}
          </div>
        </div>
      </div>

      {/* Section inférieure */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', 
        gap: '24px' 
      }}>
        {/* Prochains événements */}
        <div className="card" style={{ padding: '24px' }}>
          <h3 style={{ marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <FiClock color="#f59e0b" />
            Prochains Examens
          </h3>
          {upcomingEvents.length > 0 ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {upcomingEvents.map((event, index) => (
                <div 
                  key={event.id} 
                  style={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    gap: '16px',
                    padding: '16px',
                    backgroundColor: index === 0 ? '#fef3c7' : '#f8fafc',
                    borderRadius: '12px',
                    border: index === 0 ? '2px solid #f59e0b' : '1px solid #e2e8f0'
                  }}
                >
                  <div style={{ 
                    width: '50px', 
                    height: '50px', 
                    backgroundColor: index === 0 ? '#f59e0b' : '#1e3a5f',
                    color: 'white',
                    borderRadius: '10px',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '12px',
                    fontWeight: '600'
                  }}>
                    <span style={{ fontSize: '16px' }}>
                      {new Date(event.examDate).getDate()}
                    </span>
                    <span style={{ fontSize: '10px', textTransform: 'uppercase' }}>
                      {new Date(event.examDate).toLocaleDateString('fr-FR', { month: 'short' })}
                    </span>
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontWeight: '600', marginBottom: '4px' }}>{event.title}</div>
                    <div style={{ fontSize: '12px', color: '#64748b' }}>
                      {event.startTime} - {event.endTime} • {event.type}
                    </div>
                  </div>
                  {index === 0 && (
                    <span style={{ 
                      padding: '4px 12px', 
                      backgroundColor: '#f59e0b', 
                      color: 'white',
                      borderRadius: '20px',
                      fontSize: '11px',
                      fontWeight: '600'
                    }}>
                      Prochain
                    </span>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <div style={{ textAlign: 'center', color: '#64748b', padding: '40px' }}>
              <FiCalendar size={48} style={{ marginBottom: '16px', opacity: 0.3 }} />
              <p>Aucun examen à venir</p>
            </div>
          )}
        </div>

        {/* Cours disponibles */}
        <div className="card" style={{ padding: '24px' }}>
          <h3 style={{ marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <FiBook color="#3b82f6" />
            Cours Disponibles
          </h3>
          {courses.length > 0 ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {courses.slice(0, 5).map((course) => (
                <div 
                  key={course.id} 
                  style={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'space-between',
                    padding: '16px',
                    backgroundColor: '#f8fafc',
                    borderRadius: '12px',
                    border: '1px solid #e2e8f0'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <div style={{ 
                      width: '40px', 
                      height: '40px', 
                      backgroundColor: '#1e3a5f',
                      color: 'white',
                      borderRadius: '10px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '14px',
                      fontWeight: '600'
                    }}>
                      {course.courseCode?.substring(0, 2) || 'C'}
                    </div>
                    <div>
                      <div style={{ fontWeight: '600' }}>{course.name}</div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>
                        {course.credits} crédits • {course.courseCode}
                      </div>
                    </div>
                  </div>
                  <span style={{ 
                    padding: '4px 12px', 
                    backgroundColor: course.status === 'ACTIVE' ? '#dcfce7' : '#fee2e2',
                    color: course.status === 'ACTIVE' ? '#166534' : '#991b1b',
                    borderRadius: '20px',
                    fontSize: '11px',
                    fontWeight: '600'
                  }}>
                    {course.status}
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <div style={{ textAlign: 'center', color: '#64748b', padding: '40px' }}>
              <FiBook size={48} style={{ marginBottom: '16px', opacity: 0.3 }} />
              <p>Aucun cours disponible</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

// Composant StatCard
const StatCard = ({ icon, value, label, color, bgColor }) => (
  <div className="card" style={{ 
    padding: '24px', 
    display: 'flex', 
    alignItems: 'center', 
    gap: '16px',
    transition: 'transform 0.2s, box-shadow 0.2s',
    cursor: 'pointer'
  }}
  onMouseEnter={(e) => {
    e.currentTarget.style.transform = 'translateY(-4px)';
    e.currentTarget.style.boxShadow = '0 10px 40px rgba(0,0,0,0.12)';
  }}
  onMouseLeave={(e) => {
    e.currentTarget.style.transform = 'translateY(0)';
    e.currentTarget.style.boxShadow = '0 4px 20px rgba(0,0,0,0.08)';
  }}
  >
    <div style={{ 
      width: '60px', 
      height: '60px', 
      backgroundColor: bgColor,
      borderRadius: '16px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      color: color,
      fontSize: '24px'
    }}>
      {icon}
    </div>
    <div>
      <div style={{ fontSize: '32px', fontWeight: '700', color: '#1e3a5f' }}>{value}</div>
      <div style={{ color: '#64748b', fontSize: '14px' }}>{label}</div>
    </div>
  </div>
);

export default Dashboard;