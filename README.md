# 🎓 ACADEMIX - Système de Gestion Universitaire

<div align="center">

![ACADEMIX Logo](https://img.shields.io/badge/ACADEMIX-University%20Management-1e3a5f?style=for-the-badge&logo=graduation-cap&logoColor=white)

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue?style=flat-square&logo=react)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-336791?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

**Un système complet de gestion universitaire basé sur une architecture microservices**

[Fonctionnalités](#-fonctionnalités) • [Installation](#-installation) • [Technologies](#-technologies) • [Auteurs](#-auteurs)

</div>

---

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [API Documentation](#-api-documentation)
- [Auteurs](#-auteurs)
- [Encadrement](#-encadrement)
- [License](#-license)

---

## 📖 À propos

**ACADEMIX** est une application web complète de gestion universitaire développée dans le cadre du projet de fin d'année (PFA) à l'École Marocaine des Sciences de l'Ingénieur (EMSI). 

Le système permet de gérer efficacement tous les aspects d'un établissement universitaire : étudiants, enseignants, cours, examens, emplois du temps, présences et notes.

### 🎯 Objectifs du projet

- Moderniser la gestion administrative universitaire
- Automatiser les processus de suivi des étudiants
- Fournir des tableaux de bord analytiques en temps réel
- Assurer une architecture scalable et maintenable

---

## ✨ Fonctionnalités

### 🔐 Authentification & Sécurité
- Authentification JWT sécurisée
- Gestion des rôles (Admin, Enseignant, Étudiant)
- Protection des routes par rôle
- Session management

### 👥 Gestion des Utilisateurs
- **Étudiants** : Inscription, profil, historique académique
- **Enseignants** : Gestion des profils, spécialisations
- **Administrateurs** : Contrôle total du système

### 📚 Gestion Académique
- **Cours** : Création, modification, attribution aux enseignants
- **Examens** : Planification, types (Contrôle, Final, Quiz, TP)
- **Emploi du temps** : Gestion des créneaux horaires

### ✅ Suivi des Étudiants
- **Présences** : Marquage manuel ou par QR Code
- **Notes** : Saisie, calcul des moyennes, mentions
- **Bulletins** : Génération des relevés de notes par semestre

### 🔔 Notifications
- Rappels d'examens (24h et 2h avant)
- Notifications de cours
- Alertes système

### 📊 Tableau de Bord
- Statistiques en temps réel
- Graphiques interactifs (Chart.js)
- Progression du semestre
- Événements à venir

---

## 🏗️ Architecture
```
ACADEMIX/
├── 📁 backend/
│   ├── 📁 discovery-server/     # Eureka Server (Port 8761)
│   ├── 📁 api-gateway/          # API Gateway (Port 8080)
│   ├── 📁 ms-auth/              # Service Authentification (Port 8081)
│   ├── 📁 ms-student/           # Service Étudiants (Port 8082)
│   ├── 📁 ms-teacher/           # Service Enseignants (Port 8083)
│   ├── 📁 ms-course/            # Service Cours (Port 8084)
│   ├── 📁 ms-exam/              # Service Examens (Port 8085)
│   ├── 📁 ms-schedule/          # Service Emploi du temps (Port 8086)
│   └── 📁 ms-admin/             # Service Administration (Port 8087)
│
├── 📁 frontend/                  # Application React (Port 3000)
│   ├── 📁 src/
│   │   ├── 📁 components/       # Composants réutilisables
│   │   ├── 📁 pages/            # Pages de l'application
│   │   ├── 📁 context/          # Context API (Auth)
│   │   ├── 📁 services/         # Services API
│   │   └── 📁 styles/           # Fichiers CSS
│   └── 📄 package.json
│
├── 📁 database/                  # Scripts SQL
├── 📄 .gitignore
└── 📄 README.md
```

### 🔄 Diagramme d'Architecture
```
                                    ┌─────────────────┐
                                    │   Frontend      │
                                    │   React :3000   │
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │   API Gateway   │
                                    │     :8080       │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
           ┌──────────────┐        ┌──────────────┐        ┌──────────────┐
           │   ms-auth    │        │  ms-student  │        │  ms-teacher  │
           │    :8081     │        │    :8082     │        │    :8083     │
           └──────────────┘        └──────────────┘        └──────────────┘
                    │                        │                        │
                    ▼                        ▼                        ▼
           ┌──────────────┐        ┌──────────────┐        ┌──────────────┐
           │  PostgreSQL  │        │  PostgreSQL  │        │  PostgreSQL  │
           │ academix_auth│        │academix_student│      │academix_teacher│
           └──────────────┘        └──────────────┘        └──────────────┘
```

---

## 🚀 Technologies

### Backend

| Technologie | Version | Description |
|-------------|---------|-------------|
| Java | 17 | Langage de programmation |
| Spring Boot | 3.2.x | Framework backend |
| Spring Cloud | 2023.x | Microservices (Eureka, Gateway) |
| Spring Security | 6.x | Sécurité & JWT |
| Spring Data JPA | 3.x | ORM & Persistance |
| PostgreSQL | 14+ | Base de données |
| Maven | 3.8+ | Gestion des dépendances |
| Lombok | 1.18.x | Réduction du boilerplate |

### Frontend

| Technologie | Version | Description |
|-------------|---------|-------------|
| React | 18.x | Bibliothèque UI |
| React Router | 6.x | Routing |
| Axios | 1.x | Client HTTP |
| Chart.js | 4.x | Graphiques |
| React Toastify | 9.x | Notifications |
| React Icons | 4.x | Icônes |

### Outils & DevOps

| Outil | Usage |
|-------|-------|
| Git | Versioning |
| GitHub | Repository |
| IntelliJ IDEA | IDE Backend |
| VS Code | IDE Frontend |
| Postman | Test API |
| pgAdmin | Administration BDD |

---

## 💻 Installation

### Prérequis

Assurez-vous d'avoir installé :

- ☕ **Java JDK 17+** - [Télécharger](https://adoptium.net/)
- 📦 **Node.js 18+** - [Télécharger](https://nodejs.org/)
- 🐘 **PostgreSQL 14+** - [Télécharger](https://www.postgresql.org/download/)
- 🔧 **Maven 3.8+** - [Télécharger](https://maven.apache.org/download.cgi)
- 🐙 **Git** - [Télécharger](https://git-scm.com/)

### 1️⃣ Cloner le repository
```bash
git clone https://github.com/Nabiltornedo/ACADEMIX.git
cd ACADEMIX
```

### 2️⃣ Configuration de la base de données

Connectez-vous à PostgreSQL et créez les bases de données :
```sql
-- Créer les bases de données
CREATE DATABASE academix_auth;
CREATE DATABASE academix_student;
CREATE DATABASE academix_teacher;
CREATE DATABASE academix_course;
CREATE DATABASE academix_exam;
CREATE DATABASE academix_schedule;
CREATE DATABASE academix_admin;
```

### 3️⃣ Configuration des microservices

Chaque microservice a son fichier `application.yml` dans `src/main/resources/`. Modifiez les informations de connexion PostgreSQL si nécessaire :
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/academix_[service]
    username: postgres
    password: votre_mot_de_passe
```

### 4️⃣ Démarrer les services Backend

**Important** : Démarrez les services dans cet ordre !
```bash
# Terminal 1 - Discovery Server (Eureka)
cd backend/discovery-server
mvn spring-boot:run

# Terminal 2 - API Gateway
cd backend/api-gateway
mvn spring-boot:run

# Terminal 3 - Auth Service
cd backend/ms-auth
mvn spring-boot:run

# Terminal 4 - Student Service
cd backend/ms-student
mvn spring-boot:run

# Terminal 5 - Teacher Service
cd backend/ms-teacher
mvn spring-boot:run

# Terminal 6 - Course Service
cd backend/ms-course
mvn spring-boot:run

# Terminal 7 - Exam Service
cd backend/ms-exam
mvn spring-boot:run

# Terminal 8 - Schedule Service
cd backend/ms-schedule
mvn spring-boot:run

# Terminal 9 - Admin Service
cd backend/ms-admin
mvn spring-boot:run
```

### 5️⃣ Démarrer le Frontend
```bash
cd frontend
npm install
npm start
```

### 6️⃣ Accéder à l'application

| Service | URL |
|---------|-----|
| 🌐 **Application** | http://localhost:3000 |
| 🔍 **Eureka Dashboard** | http://localhost:8761 |
| 🚪 **API Gateway** | http://localhost:8080 |

---

## ⚙️ Configuration

### Variables d'environnement

Créez un fichier `.env` dans le dossier `frontend/` :
```env
REACT_APP_API_URL=http://localhost:8080/api
```

### Ports des services

| Service | Port |
|---------|------|
| Discovery Server | 8761 |
| API Gateway | 8080 |
| ms-auth | 8081 |
| ms-student | 8082 |
| ms-teacher | 8083 |
| ms-course | 8084 |
| ms-exam | 8085 |
| ms-schedule | 8086 |
| ms-admin | 8087 |
| Frontend | 3000 |

---

## 🔑 Utilisation

### Comptes par défaut

| Rôle | Username | Password | Accès |
|------|----------|----------|-------|
| 👑 **Admin** | `admin` | `admin123` | Accès complet |
| 👨‍🏫 **Enseignant** | `adam` | `123456` | Gestion cours, notes, présences |
| 👨‍🎓 **Étudiant** | `nabil` | `123456` | Consultation personnelle |

### Permissions par rôle

| Fonctionnalité | Admin | Enseignant | Étudiant |
|----------------|:-----:|:----------:|:--------:|
| Gérer les étudiants | ✅ | 👁️ | ❌ |
| Gérer les enseignants | ✅ | ❌ | ❌ |
| Gérer les cours | ✅ | ✅ | 👁️ |
| Planifier les examens | ✅ | ❌ | 👁️ |
| Marquer les présences | ✅ | ✅ | ❌ |
| Saisir les notes | ✅ | ✅ | ❌ |
| Voir son bulletin | ✅ | ✅ | ✅ |
| Administration | ✅ | ❌ | ❌ |

---

## 📡 API Documentation

### Authentification
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### Étudiants
```http
GET    /api/students          # Liste des étudiants
POST   /api/students          # Créer un étudiant
GET    /api/students/{id}     # Détails d'un étudiant
PUT    /api/students/{id}     # Modifier un étudiant
DELETE /api/students/{id}     # Supprimer un étudiant
```

### Examens
```http
GET    /api/exams             # Liste des examens
POST   /api/exams             # Planifier un examen
GET    /api/exams/{id}        # Détails d'un examen
PUT    /api/exams/{id}        # Modifier un examen
DELETE /api/exams/{id}        # Supprimer un examen
```

### Notes
```http
GET    /api/students/grades                           # Toutes les notes
POST   /api/students/grades                           # Ajouter une note
GET    /api/students/grades/student/{id}              # Notes d'un étudiant
GET    /api/students/grades/student/{id}/averages     # Moyennes d'un étudiant
GET    /api/students/grades/student/{id}/semester/{s}/report  # Bulletin
```

### Présences
```http
GET    /api/students/attendance                       # Toutes les présences
POST   /api/students/attendance/mark                  # Marquer présence
POST   /api/students/attendance/qr-code/generate      # Générer QR Code
GET    /api/students/attendance/student/{id}          # Présences d'un étudiant
GET    /api/students/attendance/stats/student/{id}    # Statistiques présence
```

---

## 👨‍💻 Auteurs

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Nabiltornedo">
        <img src="https://github.com/Nabiltornedo.png" width="100px;" alt="Nabil ER-RAIS"/><br />
        <sub><b>Nabil ER-RAIS</b></sub>
      </a><br />
      <a href="mailto:nabil.errais.003@gmail.com">📧 Email</a>
    </td>
    <td align="center">
      <a href="https://github.com/adam-md">
        <img src="https://github.com/adam-md.png" width="100px;" alt="Adam MORCHID"/><br />
        <sub><b>Adam MORCHID</b></sub>
      </a><br />
      <a href="https://github.com/adam-md">🐙 GitHub</a>
    </td>
  </tr>
</table>

---

## 👨‍🏫 Encadrement

<div align="center">

**Projet encadré par**

### Mr. JAADOUNI

*Professeur à l'École Marocaine des Sciences de l'Ingénieur (EMSI)*

</div>

---

## 🎓 Contexte Académique

<div align="center">

| | |
|---|---|
| **Établissement** | École Marocaine des Sciences de l'Ingénieur (EMSI) |
| **Projet** | Projet de Fin d'Année (PFA) |
| **Filière** | Génie Informatique |
| **Année Universitaire** | 2025-2026 |

</div>

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. 🍴 Forkez le projet
2. 🌿 Créez une branche (`git checkout -b feature/AmazingFeature`)
3. 💾 Committez vos changements (`git commit -m 'Add AmazingFeature'`)
4. 📤 Pushez la branche (`git push origin feature/AmazingFeature`)
5. 🔃 Ouvrez une Pull Request

---

## 📄 License

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.
```
MIT License

Copyright (c) 2025 Nabil ER-RAIS & Adam MORCHID

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

<div align="center">

**⭐ Si ce projet vous a été utile, n'hésitez pas à lui donner une étoile !**

Made with ❤️ by [Nabil ER-RAIS](https://github.com/Nabiltornedo) & [Adam MORCHID](https://github.com/adam-md)

**EMSI - 2025/2026**

</div>