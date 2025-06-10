# AbuSafar Transportation Booking System 🚀

[![API Documentation](https://img.shields.io/badge/API-Documentation-blue)](openapi.json)

A comprehensive transportation booking platform that allows users to book various types of transportation including trains 🚂, buses 🚌, and flights ✈️.

**Note:** If GitHub is not rendering the API documentation correctly, you can view the `openapi.json` file with a tool like the online [Swagger Editor](https://editor.swagger.io/).

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Database Schema](#database-schema)
- [Technologies Used](#technologies-used)
- [Entity Tables](#entity-tables)
- [Getting Started](#getting-started)

## Overview 🌟

AbuSafar is a sophisticated transportation booking system that enables users to:
- Book one-way and round-trip tickets
- Choose from multiple transportation types
- Make secure payments
- Access additional services
- Submit reports and feedback
- Manage their bookings and profile

## Features 🎯

### User Management 👤
- User registration and authentication
- Profile management with contact information
- Role-based access (User/Admin)
- Profile picture support

### Booking System 🎫
- One-way and round-trip reservations
- Multiple transportation options:
  - Trains (with star ratings)
  - Buses (VIP/Standard/Sleeper classes)
  - Flights (Economy/Business/First class)
- Age-based pricing (Adult/Child/Baby)
- Seat selection
- Booking expiration system

### Additional Features 💫
- Location management with country/province/city
- Payment processing with multiple payment methods
- Additional services (Internet/Food/Bed)
- Report submission system
- Capacity management
- Booking status tracking

## Database Schema 📊

The database is designed using PostgreSQL and consists of multiple interconnected tables. The ER diagram was created using draw.io for clear visualization of relationships.

### Core Tables Overview

| Category | Tables |
|----------|---------|
| User Management | `users`, `user_contact` |
| Transportation | `trips`, `trains`, `buses`, `flights` |
| Booking | `reservations`, `one_way_reservation`, `two_way_reservation` |
| Payments | `payments` |
| Location | `location_details` |
| Additional | `reports`, `additional_services`, `tickets` |

## Technologies Used 🛠️

- **Database**: PostgreSQL
- **ER Diagram**: draw.io
- **Data Types**: Custom ENUM types for better data integrity
- **Indexing**: Optimized queries with strategic indexes

## Entity Tables 📝

### Transportation Options

| Type | Features |
|------|-----------|
| 🚂 Train | Star rating (1-5) |
| 🚌 Bus | VIP, Standard, Sleeper options |
| ✈️ Flight | Economy, Business, First class |

### Booking Types

| Type | Description |
|------|-------------|
| One-way | Single trip booking |
| Round-trip | Two-way journey booking |

### Payment Methods 💳

- Card
- Wallet
- Crypto

## Getting Started 🚀

1. Install PostgreSQL on your system
2. Clone this repository
3. Run the SQL scripts to set up the database schema
4. Configure your application to connect to the database

## Security Features 🔒

- Password hashing
- Input validation
- Data integrity constraints
- Role-based access control

## Performance Optimizations ⚡

- Database is normalized to Third Normal Form (3NF) to:
  - Eliminate data redundancy
  - Ensure data integrity
  - Reduce data anomalies
  - Optimize storage efficiency

### Strategic Indexing 📈
- User-focused indexes:
  - `idx_users_user_role`: Fast role-based queries
  - `idx_users_name`: Efficient name searches
- Booking-related indexes:
  - `idx_reservations_user_id`: Quick user booking lookups
  - `idx_reservations_reservation_datetime`: Time-based queries
  - `idx_one_way_reservation_trip_age`: Optimized one-way booking searches
  - `idx_two_way_reservation_ticket_one/two`: Efficient round-trip queries
- Trip management indexes:
  - `idx_departure_timestamp`: Fast departure time searches
  - `idx_trips_origin_destination_location`: Location-based queries
- Payment tracking:
  - `idx_payments_reservation_id`: Quick payment lookups
  - `idx_payments_user_id`: User payment history
- Additional indexes:
  - `idx_location_details_city`: City-based searches
  - `idx_tickets_trip_vehicle`: Vehicle type filtering
  - `idx_reports_status`: Report status tracking

These indexes significantly improve query performance for common operations while maintaining optimal database structure through normalization.

---

*Note: This project uses PostgreSQL for database management and draw.io for ER diagram creation. The schema is designed with scalability and performance in mind.* 
