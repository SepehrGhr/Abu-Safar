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
        contact_info ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,63}$' 
        OR 
        contact_info ~ '^\+?[0-9\s().-]{7,20}$'
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

CREATE TABLE trips(
	trip_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	origin_location_id BIGINT REFERENCES location_details (location_id) NOT NULL,
	destination_location_id BIGINT REFERENCES location_details (location_id) NOT NULL,
	departure_date DATE NOT NULL,
	departure_time TIME NOT NULL,
	arrival_date DATE NOT NULL,
	arrival_time TIME NOT NULL,
	CONSTRAINT arrival_after_departure CHECK (arrival_date >= departure_date AND arrival_time > departure_time),
	vehicle_company VARCHAR(30), 
	stop_count int DEFAULT 0,
	total_capacity int NOT NULL,
	reserved_capacity int DEFAULT 0, 
    CONSTRAINT fill_capacity CHECK (reserved_capacity <= total_capacity)
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