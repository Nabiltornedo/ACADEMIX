import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { studentService, teacherService, courseService, examService } from '../services/api';
import { FiUsers, FiUser, FiBook, FiFileText, FiCalendar, FiTrendingUp } from 'react-icons/fi';

const Dashboard = () => {
  const { user, isAdmin, isTeacher, isStudent } = useAuth();
  const [stats, setStats] = useState({
    students: 0,
    teachers: 0,
    courses: 0,
    exams: 0
  });
  const [loading, setLoading] = useState(true);
  const [recentData, setRecentData] = useState({ students: [], courses: [], exams: [] });

  useEffect(() => {
    fetchDashboardData();
  }, []);

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

      setRecentData({
        students: studentsRes.data.slice(0, 5),
        courses: coursesRes.data.slice(0, 5),
        exams: examsRes.data.slice(0, 5)
      });
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div>
      <div className="header">
        <h1 className="page-title">Tableau de bord</h1>
        <span style={{ color: '#64748b' }}>
          Bienvenue, {user?.firstName} {user?.lastName}
        </span>
      </div>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon blue">
            <FiUsers />
          </div>
          <div className="stat-info">
            <h3>{stats.students}</h3>
            <p>Étudiants</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon green">
            <FiUser />
          </div>
          <div className="stat-info">
            <h3>{stats.teachers}</h3>
            <p>Enseignants</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon yellow">
            <FiBook />
          </div>
          <div className="stat-info">
            <h3>{stats.courses}</h3>
            <p>Cours</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon red">
            <FiFileText />
          </div>
          <div className="stat-info">
            <h3>{stats.exams}</h3>
            <p>Examens</p>
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '24px' }}>
        {/* Recent Students */}
        {(isAdmin() || isTeacher()) && (
          <div className="card">
            <div className="card-header">
              <h2 className="card-title">Étudiants récents</h2>
            </div>
            <div className="table-container">
              <table className="table">
                <thead>
                  <tr>
                    <th>Code</th>
                    <th>Nom</th>
                    <th>Email</th>
                    <th>Statut</th>
                  </tr>
                </thead>
                <tbody>
                  {recentData.students.length > 0 ? (
                    recentData.students.map((student) => (
                      <tr key={student.id}>
                        <td>{student.studentCode}</td>
                        <td>{student.firstName} {student.lastName}</td>
                        <td>{student.email}</td>
                        <td>
                          <span className={`badge badge-${student.status === 'ACTIVE' ? 'success' : 'warning'}`}>
                            {student.status}
                          </span>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="4" style={{ textAlign: 'center', color: '#64748b' }}>
                        Aucun étudiant trouvé
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Recent Courses */}
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">Cours disponibles</h2>
          </div>
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Nom</th>
                  <th>Crédits</th>
                  <th>Statut</th>
                </tr>
              </thead>
              <tbody>
                {recentData.courses.length > 0 ? (
                  recentData.courses.map((course) => (
                    <tr key={course.id}>
                      <td>{course.courseCode}</td>
                      <td>{course.name}</td>
                      <td>{course.credits}</td>
                      <td>
                        <span className={`badge badge-${course.status === 'ACTIVE' ? 'success' : 'info'}`}>
                          {course.status}
                        </span>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', color: '#64748b' }}>
                      Aucun cours trouvé
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Upcoming Exams */}
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">Examens à venir</h2>
          </div>
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Titre</th>
                  <th>Date</th>
                  <th>Type</th>
                  <th>Statut</th>
                </tr>
              </thead>
              <tbody>
                {recentData.exams.length > 0 ? (
                  recentData.exams.map((exam) => (
                    <tr key={exam.id}>
                      <td>{exam.title}</td>
                      <td>{exam.examDate}</td>
                      <td>{exam.type}</td>
                      <td>
                        <span className={`badge badge-${exam.status === 'SCHEDULED' ? 'info' : 'success'}`}>
                          {exam.status}
                        </span>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', color: '#64748b' }}>
                      Aucun examen trouvé
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
