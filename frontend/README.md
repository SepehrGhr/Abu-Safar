
<div align="center">
 <h1>AbuSafar Transportation Booking System - Frontend</h1>
 <img src="https://raw.githubusercontent.com/sepehrghr/abu-safar/main/assets/logo.png" alt="AbuSafar Logo" width="150"/>
 <p><i>A modern, responsive user interface for booking flights ✈️, buses 🚌, and trains 🚂</i></p>

 <p>
 <img src="https://img.shields.io/badge/React-18.2.0-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React 18.2.0"/>
 <img src="https://img.shields.io/badge/TypeScript-5.2.2-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript 5.2.2"/>
 <img src="https://img.shields.io/badge/Vite-5.2.0-646CFF?style=for-the-badge&logo=vite&logoColor=white" alt="Vite 5.2.0"/>
<img src="https://img.shields.io/badge/Tailwind%20CSS-3.4.4-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white" alt="Tailwind CSS 3.4.4"/>
<img src="https://img.shields.io/badge/React%20Router-6.23.1-CA4245?style=for-the-badge&logo=react-router&logoColor=white" alt="React Router 6.23.1"/>
<img src="https://img.shields.io/badge/Framer%20Motion-11.2.10-0055FF?style=for-the-badge&logo=framer&logoColor=white" alt="Framer Motion 11.2.10"/>
<img src="https://img.shields.io/badge/Docker-24.0-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
</p>
</div>

---

This is the frontend for the AbuSafar project, a comprehensive transportation booking system. This Single Page Application (SPA) is built using a modern tech stack including React, TypeScript, and Vite to create a fast, responsive, and seamless user experience. It provides a rich, interactive interface for all of the platform's travel booking functionalities.

## ✨ Core Features

* **👤 User Authentication**: Secure, modern interface for user login and registration.
* **🔍 Comprehensive Search**: An intuitive and powerful search interface to find flights, buses, and trains.
* **🎟️ Detailed Ticket Views**: Cleanly presented ticket information and details.
* **✅ Smooth Booking Flow**: A multi-step process to book tickets and make reservations.
* **Profile Management**: A dedicated section for users to manage their profile and travel history. This includes:
    * Viewing upcoming trips and past reservations.
    * The ability to cancel a reservation.
    * Tracking pending and completed payments.
* **📱 Responsive Design**: Fully responsive layout that works on desktop, tablets, and mobile devices.

---

## 🛠️ Technology Stack

| Domain                  | Technology / Concept                                                                                                        |
| :---------------------- | :-------------------------------------------------------------------------------------------------------------------------- |
| UI Framework | React 18.2.0 with TypeScript                                                                                       |
| Build Tool | Vite for a fast and lean development experience.                                                                   |
| Styling | Tailwind CSS for a utility-first CSS framework.                                                                        |
| Routing | React Router DOM for client-side routing.                                                                               |
| Animations | Framer Motion for beautiful and performant animations.                                                                  |
| API Communication | Axios for making HTTP requests to the backend API.                                                                |
| Linting | ESLint and TypeScript ESLint for maintaining code quality.                                                          |
| Containerization | Docker & Docker Compose for consistent development and deployment.                                            |

<br/>
 <p align="center">
 <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React"/>
 <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript"/>
 <img src="https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white" alt="Vite"/>
 <img src="https://img.shields.io/badge/Tailwind%20CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white" alt="Tailwind CSS"/>
 <img src="https://img.shields.io/badge/React%20Router-CA4245?style=for-the-badge&logo=react-router&logoColor=white" alt="React Router"/>
 <img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white" alt="Axios"/>
 <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
 </p>
 <br/>

---

## 🚀 Getting Started with Docker

This project is fully containerized and is designed to be run as part of the main `docker-compose` setup in the project's root directory.

### 1. Prerequisites

* Docker
* Docker Compose

Ensure both are installed and running on your system.

### 2. Environment Configuration

The application stack is configured using an `.env` file in the project root. Create this file if it doesn't exist and populate it with your configuration values.

### 3. Running the Application

From the root directory of the `Abu-Safar-phase-4` project, run the following command to start the entire application stack, including the frontend:

```bash
# Build the images and start all containers in detached mode
docker-compose up --build -d
```

The frontend will be available at `http://localhost:5173`.

### 4. Stopping the Application

To stop all the running containers, use:
```bash
docker-compose down
```
To stop the containers and remove all persistent data volumes, add the `-v` flag:
```bash
docker-compose down -v
```
---

## 🖼️ Screenshots


### <p align="center">Home Page</p>

<table> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/home.png" alt="Home Page"></td> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/home-resp.png" alt="Home Page Responsive"></td> </tr> </table>

### <p align="center">Authentication</p>

<table> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/passport.png" alt="Passport Page"></td> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/login.png" alt="Login Page"></td> </tr> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/auth-token.png" alt="Auth Token Page"></td> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/signup.png" alt="Signup Page"></td> </tr> </table>

### <p align="center">Search Tickets</p>

<table> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/search.png" alt="Search Page"></td> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/search-resp.png" alt="Search Page Responsive"></td> </tr> </table>

### <p align="center">Reserve Tickets</p>

<table> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/reserve.png" alt="Reserve Page"></td> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/reserve-resp.png" alt="Reserve Page Responsive"></td> </tr> </table>

### <p align="center">Profile Management</p>

<table> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/profile.png" alt="Profile Page"></td> <td rowspan="5"><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/profile-resp.png" alt="Profile Page Responsive" style="object-fit: contain; max-height: 100%;"></td> </tr> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/payments.png" alt="Payments Page"></td> </tr> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/cancelling.png" alt="Cancelling Page"></td> </tr> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/cancelled.png" alt="Cancelled Page"></td> </tr> <tr> <td><img src="https://raw.githubusercontent.com/SepehrGhr/Abu-Safar/main/assets/screenshots/upcoming.png" alt="Upcoming Page"></td> </tr> </table>