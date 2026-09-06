# Employee Task Management System

A robust full-stack web application designed for organizations to streamline employee management, project tracking, and task delegation. Built with **Spring Boot 3**, **Spring Data JPA**, **MySQL**, and a modern **React (Vite)** frontend.

---

## 📌 Features

- **Employee Management (CRUD)**: Create, view, update, and delete employee profiles with departments and roles.
- **Project Tracking (CRUD)**: Manage projects with status workflows (`PLANNED`, `IN_PROGRESS`, `COMPLETED`) and date range validations.
- **Task Delegation & Assignment (CRUD)**: Assign tasks to specific employees and projects with priorities (`LOW`, `MEDIUM`, `HIGH`) and deadlines.
- **Multi-Attribute Task Filtering**: Dynamically filter tasks simultaneously across Status, Priority, Employee, and Project.
- **Dashboard & Live Analytics**: Real-time statistics overview cards, CSS-only status/priority distribution bars, and recent task activity tracking.
- **Enterprise-Ready REST APIs**: Layered architecture with global exception handling and unified HTTP response codes.
- **Responsive Modern UI**: Modern dark-mode inspired glassmorphism design with clean typography and fluid interactions.

---

## 📸 Screenshots

### 1. Dashboard Overview
<img width="1917" height="930" alt="image" src="https://github.com/user-attachments/assets/c9c7178f-67bf-48ff-ae8c-4b5867771847" />
<img width="1917" height="787" alt="image" src="https://github.com/user-attachments/assets/21b11bb2-3103-4f59-bbea-f30a33bd8a59" />


### 2. Employee Management
<img width="1917" height="912" alt="image" src="https://github.com/user-attachments/assets/077dfb9a-34b3-4342-98d3-538cd277564e" />


### 3. Project Management
<img width="1917" height="926" alt="image" src="https://github.com/user-attachments/assets/a6f70955-768b-4e1e-8317-ddf0b915f9bf" />


### 4. Task Management & Dynamic Filtering
<img width="1917" height="857" alt="image" src="https://github.com/user-attachments/assets/5846d503-0489-40c6-be60-45332a4d60af" />
<img width="1917" height="927" alt="image" src="https://github.com/user-attachments/assets/bac6e9e4-56af-4800-b41e-f4db5d088b67" />


---

## 🛠️ Tech Stack

### Backend
- **Java 21**
- **Spring Boot 3.4.3**
- **Spring Web (REST APIs)**
- **Spring Data JPA & Hibernate**
- **MySQL 8.x**
- **Maven**

### Frontend
- **React 18**
- **Vite**
- **Axios**
- **Vanilla CSS (Custom Design System)**

---

## 🏛️ System Architecture

```text
React Frontend (Vite, Axios)
            ↓  [HTTP / JSON]
Spring Boot REST Controllers
            ↓
Service Layer (Business Logic & Validation)
            ↓
Data Access Layer (Spring Data JPA Repositories)
            ↓
JPA / Hibernate ORM
            ↓  [JDBC]
MySQL Database
```

---

## 🗄️ Database Relationships

```text
┌────────────────┐          ┌──────────────┐          ┌────────────────┐
│   employees    │ 1      * │    tasks     │ *      1 │    projects    │
├────────────────┼──────────┼──────────────┼──────────┼────────────────┤
│ id (PK)        │          │ id (PK)      │          │ id (PK)        │
│ name           │          │ title        │          │ name           │
│ email          │          │ description  │          │ description    │
│ department     │          │ priority     │          │ start_date     │
│ role           │          │ status       │          │ end_date       │
└────────────────┘          │ deadline     │          │ status         │
                            │ employee_id  │          └────────────────┘
                            │ project_id   │
                            └──────────────┘
```

- Each **Task** is associated with exactly one **Employee** (Assignee) and one **Project**.
- One **Employee** can have multiple assigned **Tasks** (`1-to-Many`).
- One **Project** can contain multiple **Tasks** (`1-to-Many`).

---

## 🔌 REST API Endpoints

### Employees
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/employees` | Retrieve all employees |
| `GET` | `/api/employees/{id}` | Retrieve employee by ID |
| `POST` | `/api/employees` | Create a new employee |
| `PUT` | `/api/employees/{id}` | Update existing employee |
| `DELETE` | `/api/employees/{id}` | Delete employee by ID |

### Projects
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/projects` | Retrieve all projects |
| `GET` | `/api/projects/{id}` | Retrieve project by ID |
| `POST` | `/api/projects` | Create a new project |
| `PUT` | `/api/projects/{id}` | Update existing project |
| `DELETE` | `/api/projects/{id}` | Delete project by ID |

### Tasks
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/tasks` | Retrieve tasks (supports optional filters: `?status=&priority=&employeeId=&projectId=`) |
| `GET` | `/api/tasks/{id}` | Retrieve task by ID |
| `POST` | `/api/tasks` | Create a new task |
| `PUT` | `/api/tasks/{id}` | Update existing task |
| `DELETE` | `/api/tasks/{id}` | Delete task by ID |

---

## 🚀 Getting Started & Local Setup

### Prerequisites
- **JDK 21** or later
- **Node.js** (v18+ / v20+) & **npm**
- **MySQL Server** (v8.0 or later)

---

### 1. Database Setup
Ensure your MySQL server is running. Create the database (or let Spring Boot create it automatically):

```sql
CREATE DATABASE IF NOT EXISTS employee_task_management;
```

---

### 2. Backend Setup (Spring Boot)

1. Open a terminal in the project root:
   ```bash
   cd EmployeeTaskManagement
   ```

2. Supply your MySQL password using the `DB_PASSWORD` environment variable:

   **PowerShell (Windows):**
   ```powershell
   $env:DB_USERNAME="root"
   $env:DB_PASSWORD="your_mysql_password"
   ```

   **Command Prompt (CMD):**
   ```cmd
   set DB_USERNAME=root
   set DB_PASSWORD=your_mysql_password
   ```

   **Linux / macOS:**
   ```bash
   export DB_USERNAME="root"
   export DB_PASSWORD="your_mysql_password"
   ```

3. Run the Spring Boot application:
   ```bash
   # Windows
   .\mvnw.cmd spring-boot:run

   # Linux / macOS
   ./mvnw spring-boot:run
   ```
   > Backend starts on `http://localhost:8080` (API base: `http://localhost:8080/api`).

---

### 3. Frontend Setup (React + Vite)

1. Open a second terminal and navigate to `frontend`:
   ```bash
   cd frontend
   ```

2. Install dependencies (if not already installed):
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```
   > Frontend will be available at `http://localhost:5173`.

---

## 🧪 Running Tests

To run the automated backend test suite (Unit & MockMvc Integration tests):
```bash
.\mvnw.cmd test
```

To build the frontend production bundle:
```bash
cd frontend
npm run build
```

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
