-- ACADEMIX Database Initialization Script
-- Execute this script as PostgreSQL superuser (postgres)

-- Create databases for each microservice
CREATE DATABASE academix_auth;
CREATE DATABASE academix_student;
CREATE DATABASE academix_teacher;
CREATE DATABASE academix_course;
CREATE DATABASE academix_schedule;
CREATE DATABASE academix_exam;
CREATE DATABASE academix_admin;

-- Grant privileges (optional, if using a specific user)
-- GRANT ALL PRIVILEGES ON DATABASE academix_auth TO your_user;
-- GRANT ALL PRIVILEGES ON DATABASE academix_student TO your_user;
-- GRANT ALL PRIVILEGES ON DATABASE academix_teacher TO your_user;
-- GRANT ALL PRIVILEGES ON DATABASE academix_course TO your_user;
-- GRANT ALL PRIVILEGES ON DATABASE academix_schedule TO your_user;
-- GRANT ALL PRIVILEGES ON DATABASE academix_exam TO your_user;
-- GRANT ALL PRIVILEGES ON DATABASE academix_admin TO your_user;

-- Verify databases created
\l
