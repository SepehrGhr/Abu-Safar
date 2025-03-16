CREATE TYPE user_type AS ENUM ('USER', 'ADMIN');
CREATE TYPE account_status AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TABLE users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    user_role user_type NOT NULL DEFAULT 'USER',
    account_status account_status NOT NULL DEFAULT 'ACTIVE',
    city VARCHAR(100) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    sign_up_date TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    profile_picture VARCHAR(255) DEFAULT 'default.png',
    CONSTRAINT valid_first_name CHECK (first_name ~* '^[A-Za-z \-]{1,100}$'),
    CONSTRAINT valid_last_name CHECK (last_name ~* '^[A-Za-z \-]{1,100}$')
);


CREATE TYPE contact_type AS ENUM ('EMAIL', 'PHONE_NUMBER');

CREATE TABLE user_contact (
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    contact_type contact_type NOT NULL,
    contact_info VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (user_id, contact_type),
    CONSTRAINT valid_email_phone CHECK (
        (contact_type = 'EMAIL' AND contact_info ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
        OR
        (contact_type = 'PHONE_NUMBER' AND contact_info ~ '^\+?[1-9][0-9\s().-]{7,20}$')
    )
);

CREATE TYPE report_link AS ENUM ('RESERVATION', 'TICKET');
CREATE TYPE report_status AS ENUM ('PENDING', 'REVIEWED');

CREATE TABLE reports (
    report_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id),
    link_type report_link NOT NULL,
    link_id BIGINT NOT NULL,
    topic VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    report_status report_status NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE location_details(
	location_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	country VARCHAR(30) NOT NULL,
	province VARCHAR(30) NOT NULL,
	city VARCHAR(30) NOT NULL
);

CREATE TABLE trips (
    trip_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    origin_location_id BIGINT REFERENCES location_details(location_id) NOT NULL,
    destination_location_id BIGINT REFERENCES location_details(location_id) NOT NULL,
    departure_timestamp TIMESTAMPTZ NOT NULL,
    arrival_timestamp TIMESTAMPTZ NOT NULL,
    vehicle_company VARCHAR(100),
    stop_count SMALLINT DEFAULT 0 CHECK (stop_count >= 0),
    total_capacity SMALLINT NOT NULL CHECK (total_capacity > 0),
    reserved_capacity SMALLINT DEFAULT 0 CHECK (reserved_capacity >= 0),
    CONSTRAINT fill_capacity CHECK (reserved_capacity <= total_capacity),
    CONSTRAINT valid_timing CHECK (arrival_timestamp > departure_timestamp)
);

CREATE TYPE trip_type AS ENUM ('TRAIN', 'BUS', 'FLIGHT');
CREATE TYPE age_range AS ENUM ('ADULT', 'CHILD', 'BABY');

CREATE TABLE tickets(
	trip_id BIGINT REFERENCES trips(trip_id) NOT NULL,
	age age_range NOT NULL DEFAULT 'ADULT',
	price NUMERIC NOT NULL,
	CONSTRAINT positive_price CHECK (price >= 0),
	trip_vehicle trip_type NOT NULL,
	PRIMARY KEY (trip_id, age)
);

CREATE TYPE reserve_status AS ENUM ('RESERVED', 'CANCELLED', 'PAID');
CREATE TABLE reservations (
    reservation_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE NOT NULL,
    reservation_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expiration_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW() + INTERVAL '10 minutes',
    reserve_status reserve_status NOT NULL DEFAULT 'RESERVED',
    CONSTRAINT expiration_after_reservation CHECK (expiration_datetime > reservation_datetime)
);

CREATE TABLE one_way_reservation (
    reservation_id BIGINT PRIMARY KEY REFERENCES reservations(reservation_id) ON DELETE CASCADE NOT NULL,
    trip_id BIGINT NOT NULL,
    age age_range NOT NULL,
    FOREIGN KEY (trip_id, age) REFERENCES tickets(trip_id, age) ON DELETE CASCADE,
    chair_number SMALLINT NOT NULL CHECK (chair_number > 0)
);

CREATE TABLE two_way_reservation (
    reservation_id BIGINT PRIMARY KEY REFERENCES reservations(reservation_id) ON DELETE CASCADE NOT NULL,
    ticket_one_trip_id BIGINT NOT NULL,
    ticket_one_age age_range NOT NULL,
    ticket_two_trip_id BIGINT NOT NULL,
    ticket_two_age age_range NOT NULL,
    FOREIGN KEY (ticket_one_trip_id, ticket_one_age) REFERENCES tickets(trip_id, age) ON DELETE CASCADE,
    FOREIGN KEY (ticket_two_trip_id, ticket_two_age) REFERENCES tickets(trip_id, age) ON DELETE CASCADE,
    chair_number_one SMALLINT NOT NULL CHECK (chair_number_one > 0),
    chair_number_two SMALLINT NOT NULL CHECK (chair_number_two > 0)
);

CREATE TYPE payment_status AS ENUM ('SUCCESSFUL', 'UNSUCCESSFUL', 'PENDING');
CREATE TYPE payment_means AS ENUM ('CARD', 'WALLET', 'CRYPTO');

CREATE TABLE payments(
	payment_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	reservation_id BIGINT REFERENCES reservations (reservation_id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users (user_id) ON DELETE SET NULL,
	payment_status payment_status NOT NULL DEFAULT 'PENDING',
	payment_type payment_means NOT NULL DEFAULT 'CARD',
	payment_timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    price NUMERIC NOT NULL,
	CONSTRAINT positive_price CHECK (price >= 0)
);

CREATE TYPE service_type AS ENUM ('Internet', 'Food service', 'Bed');
CREATE TABLE additional_services(
	trip_id BIGINT PRIMARY KEY REFERENCES trips(trip_id) ON DELETE CASCADE,
	service_type service_type NOT NULL
);

CREATE TABLE trains(
	trip_id BIGINT PRIMARY KEY REFERENCES trips(trip_id) ON DELETE CASCADE,
	stars SMALLINT NOT NULL DEFAULT 3,
    CONSTRAINT train_star_range CHECK (stars >= 1 AND stars <= 5)
);

CREATE TYPE flight_class AS ENUM ('Economy class', 'Business class', 'First class');
CREATE TABLE flights(
	trip_id BIGINT PRIMARY KEY REFERENCES trips(trip_id) ON DELETE CASCADE,
	class flight_class NOT NULL DEFAULT 'Economy class',
	departure_airport VARCHAR(50) NOT NULL,
	arrival_airport VARCHAR(50) NOT NULL
);

CREATE TYPE bus_class AS ENUM ('VIP', 'Standard', 'Sleeper');
CREATE TYPE chair_count_type AS ENUM ('1-2', '2-2');
CREATE TABLE buses(
	trip_id BIGINT PRIMARY KEY REFERENCES trips(trip_id) ON DELETE CASCADE,
	class bus_class NOT NULL DEFAULT 'Standard',
	chair_type chair_count_type NOT NULL DEFAULT '2-2'
);

CREATE INDEX idx_users_user_role ON users (user_role);
CREATE INDEX idx_users_name ON users (first_name, last_name);

CREATE INDEX idx_reports_user_id ON reports(user_id);
CREATE INDEX idx_reports_status ON reports(report_status);

CREATE INDEX idx_location_details_city ON location_details(city);

CREATE INDEX idx_departure_timestamp ON trips (departure_timestamp);

CREATE INDEX idx_trips_origin_destination_location ON trips(origin_location_id, destination_location_id);

CREATE VIEW ordered_trips AS
SELECT * FROM trips
ORDER BY departure_timestamp ASC;

CREATE INDEX idx_tickets_trip_vehicle ON tickets(trip_vehicle);

CREATE INDEX idx_reservations_user_id ON reservations(user_id);
CREATE INDEX idx_reservations_reservation_datetime ON reservations(reservation_datetime);

CREATE INDEX idx_one_way_reservation_trip_age ON one_way_reservation(trip_id, age);

CREATE INDEX idx_two_way_reservation_ticket_one ON two_way_reservation(ticket_one_trip_id, ticket_one_age);
CREATE INDEX idx_two_way_reservation_ticket_two ON two_way_reservation(ticket_two_trip_id, ticket_two_age);

CREATE INDEX idx_payments_reservation_id ON payments(reservation_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);




