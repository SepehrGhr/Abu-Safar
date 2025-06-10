# AbuSafar 🚀

A modern, full-featured transportation booking platform for trains, buses, and flights. Built for scalability, security, and performance, AbuSafar is designed to handle real-world booking, payment, and user management scenarios with a robust backend and a clean API.

---

## 📝 Table of Contents
- [About the Project](#about-the-project)
- [Tech Stack & Architecture](#tech-stack--architecture)
- [Features](#features)
- [Database & Caching](#database--caching)
- [API Overview](#api-overview)
- [How to Deploy & Run](#how-to-deploy--run)
- [API Usage & Testing](#api-usage--testing)
- [ER Diagram](#er-diagram)
- [License](#license)

---

## About the Project

AbuSafar is a robust, full-featured transportation booking system supporting trains, buses, and flights. It provides a seamless experience for users to search, reserve, and pay for tickets, with advanced features for admins and a secure, scalable backend.

---

## Tech Stack & Architecture

| Layer         | Technology                                                                 |
|---------------|----------------------------------------------------------------------------|
| **Backend**   | Java 17, Spring Boot, Spring Data JPA, Spring Security, Spring Cache       |
| **Database**  | PostgreSQL (3NF, indexed, Flyway-ready)                                    |
| **Caching**   | Redis (Spring Cache integration)                                           |
| **API Docs**  | OpenAPI 3.0 (Swagger), YAML                                               |
| **Testing**   | JUnit 5, Spring Boot Test                                                  |
| **DevOps**    | Maven, HikariCP, Dotenv, Docker-ready (optional)                          |
| **Other**     | draw.io (ERD), Gmail SMTP (email), SMS API (OTP)                          |

---

## Features

- **User Authentication**: OTP-based login, JWT tokens, role-based access (User/Admin)
- **Profile Management**: Update info, manage contacts, profile picture
- **Ticket Search & Booking**: One-way & round-trip, seat selection, age-based pricing
- **Payment Processing**: Wallet, card, crypto (extensible)
- **Booking Management**: View, cancel, and manage reservations
- **Admin Tools**: Manage users, reservations, reports, and payments
- **Reports & Feedback**: User-submitted reports, admin review
- **Location Management**: Country, province, city hierarchy
- **Performance**: Redis caching, indexed queries, connection pooling
- **Security**: Password hashing, JWT, input validation, role-based access

---

## Database & Caching

- **PostgreSQL**: All data is stored in a normalized, 3NF schema with strategic indexing for performance.
- **Redis**: Used for caching frequently accessed data and OTP/session management.
- **Migration**: SQL scripts in `/db` (see `AbuSafar.sql`). Flyway-ready for CI/CD.
- **Seed Data**: Optional Go-based seeder in `/seed_data`.

---

## API Overview

All endpoints are documented in [openapi.yml](openapi.yml).
Here's a summary of the main API groups:

| Group                | Description                                      | Example Endpoints                |
|----------------------|--------------------------------------------------|----------------------------------|
| **Auth**             | User sign-up, OTP login, JWT                     | `/api/auth/signup`, `/api/auth/login/otp/verify` |
| **Profile**          | Update/view user profile                         | `/api/profile/update`            |
| **Ticket Search**    | Search/select tickets                            | `/api/tickets/search`, `/api/tickets/select` |
| **Booking**          | Reserve, view, cancel bookings                   | `/api/booking/reserve/one_way`, `/api/bookings/history` |
| **Payment**          | Pay for reservations                             | `/api/payment/pay`               |
| **Reports**          | Submit/view user reports                         | `/api/reports/submit`, `/api/admin/reports` |
| **Admin**            | Manage users, reservations, reports, payments    | `/api/admin/reservations/{id}`   |
| **Location**         | Get cities, provinces, countries                 | `/api/locations/cities`          |

**Authentication:**
- Most endpoints require a Bearer JWT token (see OpenAPI docs for details).
- OTP-based login for secure, passwordless authentication.

---

## How to Deploy & Run

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 13+
- Redis 6+
- (Optional) Docker

### 2. Database Setup
```bash
# Create the database and user in PostgreSQL
psql -U postgres
CREATE DATABASE abusafar;
CREATE USER abusafar_user WITH ENCRYPTED PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE abusafar TO abusafar_user;

# Run schema and seed scripts
psql -U abusafar_user -d abusafar -f db/AbuSafar.sql
psql -U abusafar_user -d abusafar -f db/TestData.sql
```

### 3. Redis Setup
```bash
# Start Redis (default port 6379)
redis-server
```

### 4. Configure Environment
- Copy `.env.example` to `.env` and fill in your secrets (DB, JWT, email, SMS, etc.)
- Or set environment variables directly.

### 5. Build & Run
```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run
```
- The app will be available at [http://localhost:8080](http://localhost:8080)

---

## API Usage & Testing

### API Documentation
- Full OpenAPI/Swagger docs: [openapi.yml](openapi.yml)
- Use Swagger UI or [Postman](https://www.postman.com/) to import the OpenAPI spec.

### Example: User Signup & OTP Login
1. **Sign Up**
   - `POST /api/auth/signup`
   - Body: `{ "email": "...", "phoneNumber": "...", ... }`
2. **Request OTP**
   - `POST /api/auth/login/otp/request`
   - Body: `{ "email": "..." }`
3. **Verify OTP**
   - `POST /api/auth/login/otp/verify`
   - Body: `{ "email": "...", "otp": "123456" }`
   - Response: JWT token
4. **Authenticated Requests**
   - Add `Authorization: Bearer <token>` header

### Testing APIs
- **Automated Tests:**
  Run all tests with:
  ```bash
  ./mvnw test
  ```
  Uses JUnit 5 and Spring Boot Test.
- **Manual Testing:**
  Use Postman or Swagger UI with the OpenAPI spec.

---

## ER Diagram
- See [`db_design/ERD.png`](db_design/ERD.png) or [`db_design/AbuErd.drawio.svg`](db_design/AbuErd.drawio.svg)
- Designed in draw.io, normalized to 3NF, with all relationships and constraints.

---

## License
This project is licensed under the Apache 2.0 License.

---

**Made with ❤️ by the AbuSafar Team**
