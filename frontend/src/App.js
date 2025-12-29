import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Sidebar from './components/layout/Sidebar';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Students from './pages/students/Students';
import Teachers from './pages/teachers/Teachers';
import Courses from './pages/courses/Courses';
import Schedules from './pages/schedules/Schedules';
import Exams from './pages/exams/Exams';
import Admin from './pages/admin/Admin';
import Attendance from './pages/attendance/Attendance';
import Grades from './pages/grades/Grades';

// Protected Route Component
const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { isAuthenticated, user, loading } = useAuth();

  if (loading) {
    return <div className="loading"><div className="spinner"></div></div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

// Layout with Sidebar
const MainLayout = ({ children }) => {
  return (
    <div className="app-container">
      <Sidebar />
      <main className="main-content">
        {children}
      </main>
    </div>
  );
};

function App() {
  const { isAuthenticated } = useAuth();

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={
        isAuthenticated ? <Navigate to="/dashboard" replace /> : <Login />
      } />

      {/* Protected Routes */}
      <Route path="/dashboard" element={
        <ProtectedRoute>
          <MainLayout><Dashboard /></MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/students" element={
        <ProtectedRoute allowedRoles={['ADMIN', 'TEACHER']}>
          <MainLayout><Students /></MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/teachers" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <MainLayout><Teachers /></MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/courses" element={
        <ProtectedRoute>
          <MainLayout><Courses /></MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/schedules" element={
        <ProtectedRoute>
          <MainLayout><Schedules /></MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/exams" element={
        <ProtectedRoute>
          <MainLayout><Exams /></MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/admin" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <MainLayout><Admin /></MainLayout>
        </ProtectedRoute>
      } />

      {/* CORRIGÉ - Attendance avec MainLayout */}
      <Route path="/attendance" element={
        <ProtectedRoute>
          <MainLayout><Attendance /></MainLayout>
        </ProtectedRoute>
      } />

      {/* CORRIGÉ - Grades avec MainLayout */}
      <Route path="/grades" element={
        <ProtectedRoute>
          <MainLayout><Grades /></MainLayout>
        </ProtectedRoute>
      } />

      {/* Default redirect */}
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

export default App;