 <div align="center">
 <h1>AbuSafar Transportation Booking System</h1>
 <img src="https://raw.githubusercontent.com/sepehrghr/abu-safar/main/assets/logo.png" alt="AbuSafar Logo" width="150"/>
 <p><i>A robust, enterprise-grade booking platform for flights ✈️, buses 🚌, and trains 🚂</i></p>

 <p>
 <img src="https://img.shields.io/badge/Java-23-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 23"/>
 <img src="https://img.shields.io/badge/Spring%20Boot-3.3.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot 3.3.0"/>
<img src="https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
<img src="https://img.shields.io/badge/Elasticsearch-8.x-005571?style=for-the-badge&logo=elasticsearch&logoColor=white" alt="Elasticsearch"/>
<img src="https://img.shields.io/badge/Apache%20Kafka-3.x-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Kafka"/>
<img src="https://img.shields.io/badge/Redis-7.x-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
<img src="https://img.shields.io/badge/Docker-24.0-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
<img src="https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge" alt="License"/>
</p>
</div>

 ---

 AbuSafar is a comprehensive transportation booking system, engineered to deliver a fast, reliable, and seamless travel management experience. It integrates a transactional PostgreSQL database with a powerful, real-time Elasticsearch search engine via a Debezium and Kafka pipeline. This modern, event-driven architecture ensures high performance, data integrity, and scalability, making it a complete, production-ready solution.

 ## ✨ Core Features

 * 👤 User & Authentication System:
   * Secure user registration and passwordless, OTP-based login via Email & SMS.
   * Role-Based Access Control (RBAC) distinguishing between USER and ADMIN roles.
   * Stateless authentication powered by JSON Web Tokens (JWT).
 * ⚡ Real-Time Search & Analytics Engine:
   * Powered by the Elastic Stack for lightning-fast, full-text ticket searches.
   * PostgreSQL is mirrored in real-time to Elasticsearch using Debezium (CDC) and Apache Kafka.
   * Enables advanced search capabilities like typo tolerance and complex filtering without impacting the primary database.
 * 🎫 Advanced Booking Engine:
   * Supports both one-way and round-trip reservations.
   * Real-time seat availability and capacity management.
   * 10-minute reservation hold window, powered by Redis, allowing users ample time for payment.
 * 💳 Secure Payment Gateway:
   * Integration with multiple payment methods including Wallet, Card, and Crypto.
   * Transactional processing ensures booking confirmation only upon successful payment.
 * ⚙️ Admin Management Panel:
   * Dedicated endpoints for administrators to manage the entire system.
   * Review and track user-submitted reports and feedback.
 * ✉️ Notification Service:
   * Automated, professional HTML email notifications using Thymeleaf templates for payment reminders and OTPs.

 ---

 ## 🛠️ Technology Stack & Architecture

 This project is built with a modern, enterprise-grade technology stack, demonstrating a wide range of skills in backend development.

 | Domain                  | Technology / Concept                                                                                                        |
 | :---------------------- | :-------------------------------------------------------------------------------------------------------------------------- |
 | Backend Framework | Spring Boot 3.3.0 (Java 21)                                                                                           |
 | Containerization | Docker & Docker Compose for consistent development and deployment environments.                                       |
 | Search & Analytics | Elasticsearch for high-performance, full-text search and analytics.                                                     |
 | Real-Time Data Sync | Debezium (CDC) & Apache Kafka to stream database changes from PostgreSQL to Elasticsearch in real-time.               |
 | Primary Database | PostgreSQL as the transactional source of truth, with Spring Data JDBC & JdbcTemplate.                               |
 | Database Design | Normalized to 3NF; uses Triggers, Functions, and custom ENUM types.                                         |
 | Caching & Timers | Redis for both @Cacheable application caching and Keyspace Notifications for managing reservation TTLs.             |
 | Security | Spring Security 6 (RBAC), JWT for stateless authentication.                                                         |
 | API Documentation | OpenAPI 3.0 (Swagger) for interactive and comprehensive API docs.                                                       |
 | Build & Dependencies| Apache Maven, MapStruct for DTO mapping, Lombok.                                                                |
 | Notifications | Spring Mail with Thymeleaf for dynamic HTML emails.                                                                 |
 | DevOps & Monitoring | Kibana for data visualization & AKHQ for Kafka cluster management.                                                    |
 | Data Generation | A utility written in Go is provided to populate the database with realistic test data.                                  |

 <br/>
 <p align="center">
 <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
 <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring"/>
 <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
<img src="https://img.shields.io/badge/Elasticsearch-8.x-005571?style=for-the-badge&logo=elasticsearch&logoColor=white" alt="Elasticsearch"/>
<img src="https://img.shields.io/badge/Apache%20Kafka-3.x-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Kafka"/>
 <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
 <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
<img src="https://img.shields.io/badge/Debezium-2.5-6E35F4?style=for-the-badge" alt="Debezium"/>
 <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
 <img src="https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
 </p>
 <br/>

 ---

 ## 🚀 Getting Started with Docker

 This project is fully containerized. Follow these steps to get the entire stack—application, database, cache, and search engine—running with a single command.

 ### 1. Prerequisites

 * Docker
 * Docker Compose

 Ensure both are installed and running on your system.

 ### 2. Environment Configuration

 The application stack is configured using an .env file in the project root. Create this file and populate it with your configuration values.

 ### 3. Running the Application

 With Docker running and the .env file configured, start the entire application stack:

 bash  # Build the images and start all containers in detached mode  docker-compose up --build -d  

 After a few moments, the full ecosystem will be running. The services are available at the following locations:

 | Service                   | URL                       | Description                                  |
 | :------------------------ | :------------------------ | :------------------------------------------- |
 | AbuSafar Backend | http://localhost:8888   | The main application API.                    |
 | Kibana (Elasticsearch) | http://localhost:5601   | Visualize, explore, and manage search data.  |
 | AKHQ (Kafka) | http://localhost:8081   | Monitor Kafka topics and Debezium connectors.|
 | PostgreSQL Database | localhost:5432          | Primary transactional database.              |
 | Redis | localhost:6379          | Cache and session store.                     |

 ### 4. Stopping the Application

 To stop all the running containers, use:
 bash  docker-compose down  
 To stop the containers and remove all persistent data volumes, add the -v flag:
 bash  docker-compose down -v  

 ---

 ## 📄 API Documentation

 This project's API is fully documented using the OpenAPI 3.0 standard.

 ### 1. Local Swagger UI

 Once the application is running, you can access the built-in Swagger UI to test the endpoints directly:

 > http://localhost:8888/swagger-ui.html

 ### 2. Live Interactive Documentation

 A standalone, interactive documentation page is also hosted on GitHub Pages:

 > View Live API Documentation

 ---

 ## 🔬 Testing the API

 The easiest way to explore and test the API is via the integrated Swagger UI. Once the application is running, navigate to:

 > http://localhost:8888/swagger-ui.html

 The UI provides a full list of endpoints, their required parameters, and allows you to execute requests directly from your browser.

 ---

 ## 📖 API Endpoint Overview

 The AbuSafar API is logically grouped by functionality. For complete details, refer to the Swagger UI.

 | Tag                    | Description                                | Key Endpoints                                                                    |
 | :--------------------- | :----------------------------------------- | :------------------------------------------------------------------------------- |
 | User Authentication| User sign-up and login.                    | POST /api/auth/signup, POST /api/auth/login/otp/verify                       |
 | Ticket Search | Public endpoints for finding tickets.      | POST /api/tickets/search, POST /api/tickets/select                           |
 | Ticket Reservation | Create one-way and two-way reservations.   | POST /api/booking/reserve/one_way                                              |
 | Payment Processing | Finalize bookings by processing payments.  | POST /api/payment/pay                                                          |
 | Booking Cancellation| Calculate penalties and cancel bookings.   | POST /api/booking/cancel/confirm                                               |
 | Booking History | View a user's past and upcoming trips.     | GET /api/bookings/history                                                      |
 | Admin Management | Endpoints for administrative operations.   | GET /api/admin/reservations/cancelled, PUT /api/admin/reservations/change-seat |
