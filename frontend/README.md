# Employee Task Management Frontend

A modern React frontend for the Employee Task Management System built with **Vite** and **Axios**.

## Tech Stack
- **Framework**: React 18
- **Build Tool**: Vite 6
- **HTTP Client**: Axios (configured for Spring Boot REST API at `http://localhost:8080/api`)
- **Language**: JavaScript (JSX)

---

## Project Structure
```
frontend/
├── src/
│   ├── components/       # Reusable layout components (Navbar, Sidebar)
│   ├── pages/            # View pages (Dashboard, Employees, Projects, Tasks)
│   ├── services/         # Axios API client (api.js)
│   ├── App.jsx           # Main application shell with tab navigation
│   ├── index.css         # Global design system and layout styling
│   └── main.jsx          # React DOM entry point
├── index.html            # Single page application HTML shell
├── package.json          # Dependencies & npm scripts
├── vite.config.js        # Vite configuration
└── README.md
```

---

## Setup & Running Instructions

### 1. Install Dependencies
```bash
npm install
```

### 2. Start Vite Development Server
```bash
npm run dev
```
The application will run locally at: `http://localhost:5173`

### 3. Build for Production
```bash
npm run build
```
The compiled production bundle will be generated in `frontend/dist/`.

---

## Backend Integration
The frontend is pre-configured via `src/services/api.js` to communicate with the Spring Boot backend REST API running at `http://localhost:8080/api`.
