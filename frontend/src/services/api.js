import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth Service
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (data) => api.post('/auth/register', data),
  getUsers: () => api.get('/auth/users'),
  getUserById: (id) => api.get(`/auth/users/${id}`),
  updateUser: (id, data) => api.put(`/auth/users/${id}`, data),
  deleteUser: (id) => api.delete(`/auth/users/${id}`),
};

// Student Service
export const studentService = {
  getAll: () => api.get('/students'),
  getById: (id) => api.get(`/students/${id}`),
  create: (data) => api.post('/students', data),
  update: (id, data) => api.put(`/students/${id}`, data),
  delete: (id) => api.delete(`/students/${id}`),
  getGrades: (studentId) => api.get(`/students/grades/student/${studentId}`),
};

// Teacher Service
export const teacherService = {
  getAll: () => api.get('/teachers'),
  getById: (id) => api.get(`/teachers/${id}`),
  create: (data) => api.post('/teachers', data),
  update: (id, data) => api.put(`/teachers/${id}`, data),
  delete: (id) => api.delete(`/teachers/${id}`),
};

// Course Service
export const courseService = {
  getAll: () => api.get('/courses'),
  getById: (id) => api.get(`/courses/${id}`),
  create: (data) => api.post('/courses', data),
  update: (id, data) => api.put(`/courses/${id}`, data),
  delete: (id) => api.delete(`/courses/${id}`),
  // Programs
  getPrograms: () => api.get('/courses/programs'),
  createProgram: (data) => api.post('/courses/programs', data),
  // Enrollments
  getEnrollments: () => api.get('/courses/enrollments'),
  createEnrollment: (data) => api.post('/courses/enrollments', data),
  // Videos
  getVideos: (courseId) => api.get(`/courses/videos/course/${courseId}`),
  getPublishedVideos: (courseId) => api.get(`/courses/videos/course/${courseId}/published`),
  getVideoById: (id) => api.get(`/courses/videos/${id}`),
  createVideo: (data) => api.post('/courses/videos', data),
  updateVideo: (id, data) => api.put(`/courses/videos/${id}`, data),
  deleteVideo: (id) => api.delete(`/courses/videos/${id}`),
  // AI Summary - NOUVEAU
  generateSummary: (courseId) => api.post(`/courses/ai/generate-summary/${courseId}`),
  getSummary: (courseId) => api.get(`/courses/ai/summary/${courseId}`),
};

// Schedule Service
export const scheduleService = {
  getAll: () => api.get('/schedules'),
  getById: (id) => api.get(`/schedules/${id}`),
  create: (data) => api.post('/schedules', data),
  update: (id, data) => api.put(`/schedules/${id}`, data),
  delete: (id) => api.delete(`/schedules/${id}`),
  // Rooms - CRUD complet
  getRooms: () => api.get('/schedules/rooms'),
  getAvailableRooms: () => api.get('/schedules/rooms/available'),
  getRoomById: (id) => api.get(`/schedules/rooms/${id}`),
  createRoom: (data) => api.post('/schedules/rooms', data),
  updateRoom: (id, data) => api.put(`/schedules/rooms/${id}`, data),
  deleteRoom: (id) => api.delete(`/schedules/rooms/${id}`),
};

// Exam Service
export const examService = {
  getAll: () => api.get('/exams'),
  getById: (id) => api.get(`/exams/${id}`),
  create: (data) => api.post('/exams', data),
  update: (id, data) => api.put(`/exams/${id}`, data),
  delete: (id) => api.delete(`/exams/${id}`),
  // Results
  getResults: (examId) => api.get(`/exams/${examId}/results`),
  submitResult: (data) => api.post('/exams/results', data),
};

// Admin Service
export const adminService = {
  getSettings: () => api.get('/admin/settings'),
  updateSetting: (key, data) => api.put(`/admin/settings/${key}`, data),
  getAuditLogs: () => api.get('/admin/audit-logs'),
  getDashboardStats: () => api.get('/admin/dashboard/stats'),
};

// Notification Service - NOUVEAU
export const notificationService = {
  getAll: (userId) => api.get(`/auth/notifications/user/${userId}`),
  getUnread: (userId) => api.get(`/auth/notifications/user/${userId}/unread`),
  getUnreadCount: (userId) => api.get(`/auth/notifications/user/${userId}/count`),
  markAsRead: (id) => api.put(`/auth/notifications/${id}/read`),
  markAllAsRead: (userId) => api.put(`/auth/notifications/user/${userId}/read-all`),
  delete: (id) => api.delete(`/auth/notifications/${id}`),
};

// Attendance Service
export const attendanceService = {
  getAll: () => api.get('/students/attendance'),
  generateQRCode: (courseId, courseName) => api.post(`/students/attendance/qr-code/generate?courseId=${courseId}&courseName=${encodeURIComponent(courseName || '')}`),
  scanQRCode: (data) => api.post('/students/attendance/qr-code/scan', data),
  markAttendance: (data) => api.post('/students/attendance/mark', data),
  markBulkAttendance: (data) => api.post('/students/attendance/mark-bulk', data),
  justifyAbsence: (data) => api.post('/students/attendance/justify', data),
  getStudentAttendance: (studentId) => api.get(`/students/attendance/student/${studentId}`),
  getCourseAttendance: (courseId, date) => api.get(`/students/attendance/course/${courseId}?date=${date}`),
  getStudentStats: (studentId) => api.get(`/students/attendance/stats/student/${studentId}`),
  getStudentStatsByCourse: (studentId, courseId) => api.get(`/students/attendance/stats/student/${studentId}/course/${courseId}`),
};

// Grade Service
export const gradeService = {
  getAll: () => api.get('/students/grades'),
  create: (data) => api.post('/students/grades', data),
  update: (id, data) => api.put(`/students/grades/${id}`, data),
  delete: (id) => api.delete(`/students/grades/${id}`),
  getStudentGrades: (studentId) => api.get(`/students/grades/student/${studentId}`),
  getStudentGradesByCourse: (studentId, courseId) => api.get(`/students/grades/student/${studentId}/course/${courseId}`),
  getCourseGrades: (courseId) => api.get(`/students/grades/course/${courseId}`),
  getStudentAverages: (studentId) => api.get(`/students/grades/student/${studentId}/averages`),
  getCourseAverage: (studentId, courseId) => api.get(`/students/grades/student/${studentId}/course/${courseId}/average`),
  getSemesterReport: (studentId, semester, academicYear) => api.get(`/students/grades/student/${studentId}/semester/${semester}/report?academicYear=${academicYear}`),
};

export default api;