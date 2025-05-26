INSERT INTO users (first_name, last_name, user_role, account_status, city, hashed_password) VALUES
('John', 'Doe', 'USER', 'ACTIVE', 'New York', 'hashed_password_1'),
('Jane', 'Smith', 'ADMIN', 'ACTIVE', 'Los Angeles', 'hashed_password_2'),
('Alice', 'Johnson', 'USER', 'INACTIVE', 'Chicago', 'hashed_password_3'),
('Bob', 'Brown', 'USER', 'ACTIVE', 'Houston', 'hashed_password_4');

INSERT INTO user_contact (user_id, contact_type, contact_info) VALUES
(1, 'EMAIL', 'john.doe@example.com'),
(1, 'PHONE_NUMBER', '+1234567890'),
(2, 'EMAIL', 'jane.smith@example.com'),
(3, 'PHONE_NUMBER', '+987654321'),
(4, 'EMAIL', 'bob.brown@example.com');

INSERT INTO location_details (country, province, city) VALUES
('USA', 'New York', 'New York City'),
('USA', 'California', 'Los Angeles'),
('USA', 'Illinois', 'Chicago'),
('USA', 'Texas', 'Houston'),
('USA', 'Nevada', 'Las Vegas');

INSERT INTO trips (origin_location_id, destination_location_id, departure_timestamp, arrival_timestamp, vehicle_company, stop_count, total_capacity, reserved_capacity) VALUES
(1, 2, '2023-12-01 08:00:00', '2023-12-01 12:00:00', 'Greyhound', 2, 50, 10),
(2, 3, '2023-12-02 09:00:00', '2023-12-02 15:00:00', 'Amtrak', 3, 100, 20),
(3, 4, '2023-12-03 10:00:00', '2023-12-03 14:00:00', 'Delta Airlines', 0, 200, 50),
(4, 5, '2023-12-04 11:00:00', '2023-12-04 13:00:00', 'Southwest Airlines', 1, 150, 30);

INSERT INTO tickets (trip_id, age, price, trip_vehicle) VALUES
(1, 'ADULT', 50.00, 'BUS'),
(1, 'CHILD', 25.00, 'BUS'),
(2, 'ADULT', 80.00, 'TRAIN'),
(2, 'CHILD', 40.00, 'TRAIN'),
(3, 'ADULT', 200.00, 'FLIGHT'),
(3, 'CHILD', 100.00, 'FLIGHT'),
(4, 'ADULT', 150.00, 'FLIGHT'),
(4, 'CHILD', 75.00, 'FLIGHT');

INSERT INTO reservations (user_id, reservation_datetime, expiration_datetime, reserve_status) VALUES
(1, '2023-11-25 10:00:00', '2023-11-25 10:10:00', 'RESERVED'),
(2, '2023-11-26 11:00:00', '2023-11-26 11:10:00', 'PAID'),
(3, '2023-11-27 12:00:00', '2023-11-27 12:10:00', 'CANCELLED');

INSERT INTO one_way_reservation (reservation_id, trip_id, age, chair_number) VALUES
(1, 1, 'ADULT', 5),
(2, 2, 'CHILD', 10);

INSERT INTO two_way_reservation (reservation_id, ticket_one_trip_id, ticket_one_age, ticket_two_trip_id, ticket_two_age, chair_number_one, chair_number_two) VALUES
(3, 3, 'ADULT', 4, 'ADULT', 15, 20);

INSERT INTO payments (reservation_id, user_id, payment_status, payment_type, price) VALUES
(1, 1, 'PENDING', 'CARD', 50.00),
(2, 2, 'SUCCESSFUL', 'WALLET', 80.00),
(3, 3, 'UNSUCCESSFUL', 'CRYPTO', 200.00);

INSERT INTO additional_services (trip_id, service_type) VALUES
(1, 'Internet'),
(2, 'Food service'),
(3, 'Bed');

INSERT INTO trains (trip_id, stars) VALUES
(2, 4);

INSERT INTO flights (trip_id, class, departure_airport, arrival_airport) VALUES
(3, 'Economy class', 'JFK', 'LAX'),
(4, 'Business class', 'LAX', 'ORD');

INSERT INTO buses (trip_id, class, chair_type) VALUES
(1, 'VIP', '1-2');

INSERT INTO reports (user_id, link_type, link_id, topic, content, report_status) VALUES
(1, 'RESERVATION', 1, 'Issue with reservation', 'My reservation was not confirmed.', 'PENDING'),
(2, 'TICKET', 2, 'Ticket price discrepancy', 'The ticket price was higher than advertised.', 'REVIEWED');