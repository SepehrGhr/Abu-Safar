CREATE TYPE user_type AS ENUM ('USER', 'ADMIN');
CREATE TYPE account_status AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TABLE users
(
    user_id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name      VARCHAR(100)   NOT NULL,
    last_name       VARCHAR(100)   NOT NULL,
    user_role       user_type      NOT NULL DEFAULT 'USER',
    account_status  account_status NOT NULL DEFAULT 'ACTIVE',
    city            VARCHAR(100)   NOT NULL,
    hashed_password VARCHAR(255)   NOT NULL,
    sign_up_date    TIMESTAMPTZ             DEFAULT NOW() NOT NULL,
    profile_picture VARCHAR(255)            DEFAULT 'default.png',
    wallet_balance  NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    birthday_date   DATE           NULL,

    CONSTRAINT valid_first_name CHECK (first_name ~* '^[A-Za-z ''-]{1,100}$'),
    CONSTRAINT valid_last_name CHECK (last_name ~* '^[A-Za-z ''-]{1,100}$'),
    CONSTRAINT positive_wallet_balance CHECK (wallet_balance >= 0.00)
);


CREATE TYPE contact_type AS ENUM ('EMAIL', 'PHONE_NUMBER');

CREATE TABLE user_contact
(
    user_id      BIGINT REFERENCES users (user_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    contact_type contact_type        NOT NULL,
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

CREATE TABLE reports
(
    report_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id       BIGINT REFERENCES users (user_id),
    link_type     report_link   NOT NULL,
    link_id       BIGINT        NOT NULL,
    topic         VARCHAR(100)  NOT NULL,
    content       TEXT          NOT NULL,
    report_status report_status NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE location_details
(
    location_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    country     VARCHAR(30) NOT NULL,
    province    VARCHAR(30) NOT NULL,
    city        VARCHAR(30) NOT NULL
);

CREATE TABLE trips
(
    trip_id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    origin_location_id      BIGINT REFERENCES location_details (location_id) NOT NULL,
    destination_location_id BIGINT REFERENCES location_details (location_id) NOT NULL,
    departure_timestamp     TIMESTAMPTZ                                      NOT NULL,
    arrival_timestamp       TIMESTAMPTZ                                      NOT NULL,
    vehicle_company         VARCHAR(100),
    stop_count              SMALLINT DEFAULT 0 CHECK (stop_count >= 0),
    total_capacity          SMALLINT                                         NOT NULL CHECK (total_capacity > 0),
    reserved_capacity       SMALLINT DEFAULT 0 CHECK (reserved_capacity >= 0),
    CONSTRAINT fill_capacity CHECK (reserved_capacity <= total_capacity),
    CONSTRAINT valid_timing CHECK (arrival_timestamp > departure_timestamp)
);

CREATE TYPE trip_type AS ENUM ('TRAIN', 'BUS', 'FLIGHT');
CREATE TYPE age_range AS ENUM ('ADULT', 'CHILD', 'BABY');

CREATE TABLE tickets
(
    trip_id      BIGINT REFERENCES trips (trip_id) NOT NULL,
    age          age_range                         NOT NULL DEFAULT 'ADULT',
    price        NUMERIC                           NOT NULL,
    CONSTRAINT positive_price CHECK (price >= 0),
    trip_vehicle trip_type                         NOT NULL,
    PRIMARY KEY (trip_id, age)
);

CREATE TYPE reserve_status AS ENUM ('RESERVED', 'CANCELLED', 'PAID');
CREATE TABLE reservations
(
    reservation_id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id              BIGINT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    reservation_datetime TIMESTAMPTZ                                         NOT NULL DEFAULT NOW(),
    expiration_datetime  TIMESTAMPTZ                                         NOT NULL DEFAULT NOW() + INTERVAL '10 minutes',
    reserve_status       reserve_status                                      NOT NULL DEFAULT 'RESERVED',
    CONSTRAINT expiration_after_reservation CHECK (expiration_datetime > reservation_datetime),
    is_round_trip        BOOLEAN                                             NOT NULL DEFAULT false,
    cancelled_by         BIGINT DEFAULT NULL REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE ticket_reservation
(
    trip_id        BIGINT REFERENCES trips (trip_id) ON DELETE CASCADE,
    age            age_range NOT NULL DEFAULT 'ADULT',
    reservation_id BIGINT REFERENCES reservations (reservation_id) ON DELETE CASCADE,
    seat_number    SMALLINT  NOT NULL CHECK (seat_number > 0),
    PRIMARY KEY (trip_id, age, reservation_id)
);

CREATE TYPE payment_status AS ENUM ('SUCCESSFUL', 'UNSUCCESSFUL', 'PENDING');
CREATE TYPE payment_means AS ENUM ('CARD', 'WALLET', 'CRYPTO');

CREATE TABLE payments
(
    payment_id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reservation_id    BIGINT REFERENCES reservations (reservation_id) ON DELETE CASCADE,
    user_id           BIGINT         REFERENCES users (user_id) ON DELETE SET NULL,
    payment_status    payment_status NOT NULL DEFAULT 'PENDING',
    payment_type      payment_means  NOT NULL DEFAULT 'CARD',
    payment_timestamp TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    price             NUMERIC        NOT NULL,
    CONSTRAINT positive_price CHECK (price >= 0)
);

CREATE TYPE service_type AS ENUM ('Internet', 'Food service', 'Bed');
CREATE TABLE additional_services
(
    trip_id      BIGINT REFERENCES trips (trip_id) ON DELETE CASCADE,
    service_type service_type NOT NULL,
    PRIMARY KEY (trip_id, service_type)
);


CREATE TYPE train_room_type AS ENUM ('4-BED', '6-BED');
CREATE TABLE trains
(
    trip_id   BIGINT PRIMARY KEY REFERENCES trips (trip_id) ON DELETE CASCADE,
    stars     SMALLINT        NOT NULL DEFAULT 3,
    CONSTRAINT train_star_range CHECK (stars >= 1 AND stars <= 5),
    room_type train_room_type NOT NULL DEFAULT '4-BED'
);

CREATE TYPE flight_class AS ENUM ('Economy class', 'Business class', 'First class');
CREATE TABLE flights
(
    trip_id           BIGINT PRIMARY KEY REFERENCES trips (trip_id) ON DELETE CASCADE,
    class             flight_class NOT NULL DEFAULT 'Economy class',
    departure_airport VARCHAR(50)  NOT NULL,
    arrival_airport   VARCHAR(50)  NOT NULL
);

CREATE TYPE bus_class AS ENUM ('VIP', 'Standard', 'Sleeper');
CREATE TYPE chair_count_type AS ENUM ('1-2', '2-2');
CREATE TABLE buses
(
    trip_id    BIGINT PRIMARY KEY REFERENCES trips (trip_id) ON DELETE CASCADE,
    class      bus_class        NOT NULL DEFAULT 'Standard',
    chair_type chair_count_type NOT NULL DEFAULT '2-2'
);

-----------------------------------------------------------------------------
CREATE INDEX idx_users_user_role ON users (user_role);
CREATE INDEX idx_users_name ON users (first_name, last_name);

CREATE INDEX idx_user_contact_user ON user_contact(contact_info);

CREATE INDEX idx_reports_user_id ON reports (user_id);
CREATE INDEX idx_reports_status ON reports (report_status);
CREATE INDEX idx_reports_type_linkid ON reports(link_type, link_id);


CREATE INDEX idx_location_location_id_city ON location_details(location_id, city);
CREATE INDEX idx_location_province_city ON location_details(province, city, location_id);

CREATE INDEX idx_departure_timestamp ON trips (departure_timestamp);

CREATE INDEX idx_trips_origin_destination_location ON trips (origin_location_id, destination_location_id);
CREATE INDEX idx_trips_origin ON trips (trip_id, origin_location_id);

CREATE VIEW ordered_trips AS
SELECT *
FROM trips
ORDER BY departure_timestamp ASC;

CREATE INDEX idx_tickets_trip_vehicle ON tickets (trip_vehicle);
CREATE INDEX idx_tickets_trip_age ON tickets(trip_id, age, trip_vehicle);
CREATE INDEX idx_tickets_trip_id ON tickets(trip_id);

CREATE INDEX idx_reservations_user_id ON reservations (user_id);
CREATE INDEX idx_reservations_reservation_datetime ON reservations (reservation_datetime);
CREATE INDEX idx_reservations_status_user_id ON reservations(reserve_status, user_id);
CREATE INDEX idx_reservations_datetime_user ON reservations(reservation_datetime DESC, user_id);
CREATE INDEX idx_reservations_user_status_id ON reservations(user_id, reserve_status, reservation_id);
CREATE INDEX idx_reservations_cancelled_by ON reservations(cancelled_by);

CREATE INDEX idx_payments_reservation_id ON payments (reservation_id);
CREATE INDEX idx_payments_user_id ON payments (user_id);
CREATE INDEX idx_payments_user_status_time ON payments(payment_status, user_id, payment_timestamp);
CREATE INDEX idx_payments_user_status ON payments(user_id, payment_status);
CREATE INDEX idx_payments_reservation_status ON payments (reservation_id, payment_status);

CREATE INDEX idx_ticket_reservation_res_id ON ticket_reservation(reservation_id, trip_id);
CREATE INDEX idx_ticket_reservation_reservation_id ON ticket_reservation(reservation_id);
CREATE INDEX idx_ticket_reservation_trip_id ON ticket_reservation(trip_id);


-----------------------------------------------------------
CREATE OR REPLACE FUNCTION increment_reserved_capacity()
    RETURNS TRIGGER AS
$$
DECLARE
    available_capacity SMALLINT;
BEGIN
    SELECT total_capacity - reserved_capacity
    INTO available_capacity
    FROM trips
    WHERE trip_id = NEW.trip_id;

    IF available_capacity <= 0 THEN
        RAISE EXCEPTION 'Trip % is fully booked.', NEW.trip_id;
    END IF;

    UPDATE trips
    SET reserved_capacity = reserved_capacity + 1
    WHERE trip_id = NEW.trip_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
---------
CREATE TRIGGER trg_increment_reserved_capacity
    AFTER INSERT
    ON ticket_reservation
    FOR EACH ROW
EXECUTE FUNCTION increment_reserved_capacity();
--------------
CREATE OR REPLACE FUNCTION update_reservation_status_to_paid()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.payment_status = 'SUCCESSFUL' THEN
        UPDATE reservations
        SET reserve_status = 'PAID'
        WHERE reservation_id = NEW.reservation_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
------------
CREATE TRIGGER trg_payment_insert_successful
    AFTER INSERT
    ON payments
    FOR EACH ROW
EXECUTE FUNCTION update_reservation_status_to_paid();
-----
CREATE TRIGGER trg_payment_update_successful
    AFTER UPDATE OF payment_status
    ON payments
    FOR EACH ROW
    WHEN (NEW.payment_status = 'SUCCESSFUL' AND OLD.payment_status IS DISTINCT FROM 'SUCCESSFUL')
EXECUTE FUNCTION update_reservation_status_to_paid();

------
CREATE OR REPLACE FUNCTION handle_reservation_cancellation()
    RETURNS TRIGGER AS
$$
DECLARE
    v_trip_id BIGINT;
BEGIN
    IF NEW.reserve_status = 'CANCELLED' AND OLD.reserve_status != 'CANCELLED' THEN

        FOR v_trip_id IN
            SELECT trip_id
            FROM ticket_reservation
            WHERE reservation_id = OLD.reservation_id
            LOOP
                UPDATE trips
                SET reserved_capacity = reserved_capacity - 1
                WHERE trips.trip_id = v_trip_id;
            END LOOP;

        DELETE FROM ticket_reservation WHERE reservation_id = OLD.reservation_id;

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reservation_cancelled
    AFTER UPDATE OF reserve_status ON reservations
    FOR EACH ROW
EXECUTE FUNCTION handle_reservation_cancellation();

---------
CREATE OR REPLACE FUNCTION create_or_update_pending_payment()
    RETURNS TRIGGER AS
$$
DECLARE
    v_total_price NUMERIC;
    v_payment_id BIGINT;
BEGIN
    --after a row is inserted into 'ticket_reservation'
    SELECT SUM(t.price) INTO v_total_price
    FROM tickets t
             JOIN ticket_reservation tr ON t.trip_id = tr.trip_id AND t.age = tr.age
    WHERE tr.reservation_id = NEW.reservation_id;

    SELECT payment_id INTO v_payment_id
    FROM payments
    WHERE reservation_id = NEW.reservation_id AND payment_status = 'PENDING';

    IF v_payment_id IS NULL THEN
        INSERT INTO payments (reservation_id, user_id, payment_status, price)
        SELECT NEW.reservation_id, r.user_id, 'PENDING'::payment_status, v_total_price
        FROM reservations r
        WHERE r.reservation_id = NEW.reservation_id;
    ELSE
        --for round trips
        UPDATE payments
        SET price = v_total_price
        WHERE payment_id = v_payment_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_create_pending_payment
    AFTER INSERT ON ticket_reservation
    FOR EACH ROW
EXECUTE FUNCTION create_or_update_pending_payment();
