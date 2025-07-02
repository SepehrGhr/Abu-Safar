<div align="center">
  <h1>AbuSafar Transportation Booking System</h1>
  <img src="https://raw.githubusercontent.com/sepehrghr/abu-safar/main/assets/logo.png" alt="AbuSafar Logo" width="150"/>
  <p><i>A robust, enterprise-grade booking platform for flights ✈️, buses 🚌, and trains 🚂</i></p>

  <p>
    <img src="https://img.shields.io/badge/Java-23-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 23"/>
    <img src="https://img.shields.io/badge/Spring%20Boot-3.3.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot 3.3.0"/>
    <img src="https://img.shields.io/badge/PostgreSQL-14.5-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
    <img src="https://img.shields.io/badge/Redis-7.2-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
    <img src="https://img.shields.io/badge/Docker-24.0-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
    <img src="https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge" alt="License"/>
  </p>
</div>

---

**AbuSafar** is a sophisticated transportation booking system designed to provide a seamless experience for users to search, book, and manage their travel. Built with a modern, scalable architecture, it emphasizes data integrity, performance, and security, making it a production-ready solution.

## ✨ Core Features

* **👤 User & Authentication System**:
  * Secure user registration and passwordless, **OTP-based login** via Email & SMS.
  * **Role-Based Access Control (RBAC)** distinguishing between `USER` and `ADMIN` roles.
  * Stateless authentication powered by **JSON Web Tokens (JWT)**.
* **🎫 Advanced Booking Engine**:
  * Supports both **one-way** and **round-trip** reservations.
  * Comprehensive search with filters for transportation type, price, class, and more.
  * Real-time seat availability and capacity management enforced by database triggers.
  * **10-minute reservation hold window**, powered by Redis, allowing users ample time for payment.
* **💳 Secure Payment Gateway**:
  * Integration with multiple payment methods including **Wallet**, **Card**, and **Crypto**.
  * Transactional processing ensures booking confirmation only upon successful payment.
* **⚙️ Admin Management Panel**:
  * Dedicated endpoints for administrators to manage the entire system.
  * View and manage user reservations, including changing seat numbers or cancelling bookings.
  * Review and track user-submitted reports and feedback.
* **✉️ Notification Service**:
  * Automated, professional HTML email notifications using **Thymeleaf** templates for payment reminders and OTPs.

---

## 🛠️ Technology Stack & Architecture

This project is built with a modern, enterprise-grade technology stack, demonstrating a wide range of skills in backend development.

| Domain                 | Technology / Concept                                                                                                        |
| :--------------------- | :-------------------------------------------------------------------------------------------------------------------------- |
| **Backend Framework** | **Spring Boot 3.3.0** (`Java 23`)                                                                                           |
| **Containerization** | **Docker** & **Docker Compose** for consistent development and deployment environments.                                     |
| **Security** | **Spring Security 6** (RBAC), **JWT** for stateless authentication                                                          |
| **Database** | **PostgreSQL** with **Spring Data JDBC** & `JdbcTemplate` for performance                                                   |
| **Database Design** | Normalized to **3NF**; uses **Triggers**, **Functions**, and custom **ENUM** types                                          |
| **Caching & Timers** | **Redis** for both `@Cacheable` application caching and **Keyspace Notifications** for managing reservation TTLs            |
| **API Documentation** | **OpenAPI 3.0 (Swagger)** for interactive and comprehensive API docs                                                        |
| **Build & Dependencies**| **Apache Maven**, **MapStruct** for DTO mapping, **Lombok** |
| **Notifications** | **Spring Mail** with **Thymeleaf** for dynamic HTML emails                                                                  |
| **Data Generation** | A utility written in **Go** is provided to populate the database with realistic test data                                   |

<br/>
<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring"/>
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
  <img src="https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
</p>
<br/>

---

## 🚀 Getting Started with Docker

This project is fully containerized. Follow these steps to get the entire stack—application, database, and cache—running with a single command.

### 1. Prerequisites

* **Docker**
* **Docker Compose**

Ensure both are installed and running on your system.

### 2. Environment Configuration

The application stack is configured using an `.env` file in the project root.

1.  Create a file named `.env` in the root of the project.
2.  Copy the following content into the file and replace the placeholder values with your actual configuration.

```dotenv
# PostgreSQL Database Settings
# These are used by docker-compose to initialize the database
DB_NAME=abusafar_db
DB_USER=your_db_user
DB_PASSWORD=your_db_password

# JWT Secret (must be a long, secure, random string)
JWT_SECRET=your-super-secret-jwt-key-that-is-long-enough

# Email (for OTP and Notifications, e.g., using a Gmail App Password)
EMAIL_PASSWORD=your-gmail-app-password

# SMS Service (Optional, for OTP via SMS)
SMS_URL=your_sms_provider_url
SMS_API_KEY=your_sms_provider_api_key
SMS_TEMPLATE=your_sms_provider_template_id
```

**Note**: The Spring Boot application inside the Docker container is automatically configured to connect to the database and Redis services via the Docker network.

### 3. Running the Application

With Docker running and the `.env` file configured, start the entire application stack:

```bash
# Build the images and start the containers in detached mode
docker-compose up --build -d
```

The services will be launched:
* **AbuSafar Application**: Available at `http://localhost:8080`
* **PostgreSQL Database**: Accessible on host port `5432`
* **Redis**: Accessible on host port `6379`

The database schema (`db/AbuSafar.sql`) is automatically initialized the first time the database container starts.

### 4. (Optional) Populating with Test Data

If you want to populate the database with sample data, run the following command after the containers are up and running:

```bash
# Execute the TestData.sql script inside the running postgres container
docker-compose exec -T abusafar-db psql -U ${DB_USER} -d ${DB_NAME} < db/TestData.sql
```

*Note: This command may require the `DB_USER` and `DB_NAME` environment variables to be present in your shell, or you can replace them with the literal values from your `.env` file.*

Alternatively, you can configure and run the Go data seeder located in `seed_data/`.

### 5. Stopping the Application

To stop all the running containers, use:
```bash
docker-compose down
```
To stop the containers and remove the persistent database volume, add the `-v` flag:
```bash
docker-compose down -v
```

---

## 📄 API Documentation

This project's API is fully documented using the OpenAPI 3.0 standard. You can explore and interact with all the endpoints in two ways:

### 1. Live Interactive Documentation (Recommended)

A standalone, interactive documentation page is generated from our `openapi.yml` and is hosted on GitHub Pages. This is the easiest way for anyone to view the API, with no local setup required.

> [**View Live API Documentation**](https://SepehrGhr.github.io/Abu-Safar/api-doc.html)

### 2. Local Swagger UI

Once the application is running on your local machine, you can access the built-in Swagger UI to test the endpoints directly:

> [**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

---

## 🔬 Testing the API

You can test the API using two primary methods:

### 1. Swagger UI (Recommended)

The easiest way to explore and test the API is via the integrated Swagger UI. Once the application is running, navigate to:

> [**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

The UI provides a full list of endpoints, their required parameters, and allows you to execute requests directly from your browser.

### 2. API Client (Postman / Insomnia)

You can also use an API client like Postman. Here is a typical workflow:

1.  **Register a User**: Send a `POST` request to `/api/auth/signup`.
2.  **Request OTP**: Send a `POST` request to `/api/auth/login/otp/request` with the user's email or phone.
3.  **Login with OTP**: Send a `POST` request to `/api/auth/login/otp/verify`. The response will contain an `accessToken`.
4.  **Access Protected Endpoints**: For any protected endpoint, add an `Authorization` header with the value `Bearer <your_accessToken>`.

#### Example: Get Booking History with `curl`

```bash
# First, log in to get a token (replace with your actual login flow)
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login/otp/verify \
-H "Content-Type: application/json" \
-d '{"contactInfo": "user@example.com", "otp": "123456"}' | jq -r '.data.accessToken')

# Then, use the token to access a protected route
curl -X GET http://localhost:8080/api/bookings/history \
-H "Authorization: Bearer $TOKEN"
```

---

## 📖 API Endpoint Overview

The AbuSafar API is logically grouped by functionality. For complete details, refer to the [Swagger UI](http://localhost:8080/swagger-ui.html).

| Tag                    | Description                                | Key Endpoints                                                                    |
| :--------------------- | :----------------------------------------- | :------------------------------------------------------------------------------- |
| **User Authentication**| User sign-up and login.                    | `POST /api/auth/signup`, `POST /api/auth/login/otp/verify`                         |
| **Ticket Search** | Public endpoints for finding tickets.      | `POST /api/tickets/search`, `POST /api/tickets/select`                             |
| **Ticket Reservation** | Create one-way and two-way reservations.   | `POST /api/booking/reserve/one_way`                                              |
| **Payment Processing** | Finalize bookings by processing payments.  | `POST /api/payment/pay`                                                          |
| **Booking Cancellation**| Calculate penalties and cancel bookings.   | `POST /api/booking/cancel/confirm`                                               |
| **Booking History** | View a user's past and upcoming trips.     | `GET /api/bookings/history`                                                      |
| **Admin Management** | Endpoints for administrative operations.   | `GET /api/admin/reservations/cancelled`, `PUT /api/admin/reservations/change-seat` |
