# ACADEMIX - Système d'Information Universitaire

## 📋 Description

ACADEMIX est un système d'information modulaire pour la gestion d'un établissement universitaire, basé sur une architecture microservices.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend React                          │
│                        (Port 3000)                              │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway                              │
│                        (Port 8080)                              │
└─────────────────────────────────────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        ▼                       ▼                       ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│   MS-Auth     │     │  MS-Student   │     │  MS-Teacher   │
│   (8081)      │     │    (8082)     │     │    (8083)     │
└───────────────┘     └───────────────┘     └───────────────┘
        │                       │                       │
        ▼                       ▼                       ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│  MS-Course    │     │  MS-Schedule  │     │   MS-Exam     │
│   (8084)      │     │    (8085)     │     │    (8086)     │
└───────────────┘     └───────────────┘     └───────────────┘
                                │
                                ▼
                      ┌───────────────┐
                      │   MS-Admin    │
                      │    (8087)     │
                      └───────────────┘
                                │
                                ▼
                      ┌───────────────┐
                      │   Eureka      │
                      │   Discovery   │
                      │    (8761)     │
                      └───────────────┘
```

## 📦 Microservices

| Service | Port | Description |
|---------|------|-------------|
| Discovery Server | 8761 | Service de découverte Eureka |
| API Gateway | 8080 | Passerelle API (routage, CORS) |
| MS-Auth | 8081 | Authentification et gestion des utilisateurs |
| MS-Student | 8082 | Gestion des étudiants et notes |
| MS-Teacher | 8083 | Gestion des enseignants et disponibilités |
| MS-Course | 8084 | Gestion des cours, programmes et inscriptions |
| MS-Schedule | 8085 | Planification et gestion des salles |
| MS-Exam | 8086 | Organisation des examens et résultats |
| MS-Admin | 8087 | Administration et paramètres système |

## 🛠️ Technologies

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Cloud (Eureka, Gateway)
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL

### Frontend
- React 18
- React Router 6
- Axios
- React Toastify
- React Icons

## 🚀 Installation et Démarrage

### Prérequis
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.8+

### 1. Configuration de la Base de Données

```bash
# Connectez-vous à PostgreSQL
psql -U postgres

# Exécutez le script d'initialisation
\i database/init.sql
```

Ou créez manuellement les bases de données :
```sql
CREATE DATABASE academix_auth;
CREATE DATABASE academix_student;
CREATE DATABASE academix_teacher;
CREATE DATABASE academix_course;
CREATE DATABASE academix_schedule;
CREATE DATABASE academix_exam;
CREATE DATABASE academix_admin;
```

### 2. Démarrage du Backend

**Important**: Démarrez les services dans cet ordre !

```bash
# 1. Discovery Server (obligatoire en premier)
cd backend/discovery-server
mvn spring-boot:run

# 2. API Gateway
cd backend/api-gateway
mvn spring-boot:run

# 3. Services métier (dans n'importe quel ordre)
cd backend/ms-auth
mvn spring-boot:run

cd backend/ms-student
mvn spring-boot:run

cd backend/ms-teacher
mvn spring-boot:run

cd backend/ms-course
mvn spring-boot:run

cd backend/ms-schedule
mvn spring-boot:run

cd backend/ms-exam
mvn spring-boot:run

cd backend/ms-admin
mvn spring-boot:run
```

### 3. Démarrage du Frontend

```bash
cd frontend
npm install
npm start
```

L'application sera accessible sur http://localhost:3000

## 👤 Comptes par Défaut

| Rôle | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Teacher | teacher | teacher123 |
| Student | student | student123 |

## 📡 Endpoints API

### Authentication (MS-Auth)
- `POST /api/auth/login` - Connexion
- `POST /api/auth/register` - Inscription
- `GET /api/auth/users` - Liste des utilisateurs (Admin)

### Students (MS-Student)
- `GET /api/students` - Liste des étudiants
- `POST /api/students` - Créer un étudiant
- `PUT /api/students/{id}` - Modifier un étudiant
- `DELETE /api/students/{id}` - Supprimer un étudiant
- `GET /api/students/grades/student/{id}` - Notes d'un étudiant

### Teachers (MS-Teacher)
- `GET /api/teachers` - Liste des enseignants
- `POST /api/teachers` - Créer un enseignant
- `PUT /api/teachers/{id}` - Modifier un enseignant
- `DELETE /api/teachers/{id}` - Supprimer un enseignant

### Courses (MS-Course)
- `GET /api/courses` - Liste des cours
- `POST /api/courses` - Créer un cours
- `GET /api/courses/programs` - Liste des programmes
- `POST /api/courses/enrollments` - Inscrire un étudiant

### Schedules (MS-Schedule)
- `GET /api/schedules` - Emploi du temps
- `POST /api/schedules` - Créer un créneau
- `GET /api/schedules/rooms` - Liste des salles

### Exams (MS-Exam)
- `GET /api/exams` - Liste des examens
- `POST /api/exams` - Planifier un examen
- `POST /api/exams/results` - Soumettre des résultats

## 📊 Monitoring

- **Eureka Dashboard**: http://localhost:8761
- **Actuator Health**: http://localhost:{port}/actuator/health

## 📁 Structure du Projet

```
academix/
├── backend/
│   ├── discovery-server/    # Eureka Server
│   ├── api-gateway/         # Spring Cloud Gateway
│   ├── ms-auth/             # Service d'authentification
│   ├── ms-student/          # Service étudiant
│   ├── ms-teacher/          # Service enseignant
│   ├── ms-course/           # Service cours
│   ├── ms-schedule/         # Service emploi du temps
│   ├── ms-exam/             # Service examens
│   └── ms-admin/            # Service administration
├── frontend/
│   ├── src/
│   │   ├── components/      # Composants React
│   │   ├── pages/           # Pages de l'application
│   │   ├── services/        # Services API
│   │   ├── context/         # Context React (Auth)
│   │   └── App.js           # Composant principal
│   └── package.json
├── database/
│   └── init.sql             # Script d'initialisation
└── README.md
```

## 🔒 Sécurité

- Authentification JWT
- Protection des routes par rôle (ADMIN, TEACHER, STUDENT)
- CORS configuré pour le frontend
- Mots de passe hashés (BCrypt)

## 📝 Notes

- Chaque microservice a sa propre base de données
- Les tables sont créées automatiquement au démarrage (JPA ddl-auto: update)
- Un utilisateur admin est créé automatiquement au premier démarrage

## 🎓 Projet PFA - EMSI

**Auteur**: Nabil  
**École**: EMSI (École Marocaine des Sciences de l'Ingénieur)  
**Année**: 2024-2025
