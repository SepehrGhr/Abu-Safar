--=====================1======================--
CREATE OR REPLACE PROCEDURE get_user_reservations_by_contact(
    IN contact_input VARCHAR,
    IN contact_type_input contact_type,
    INOUT result_set refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN result_set FOR
        SELECT r.*
        FROM reservations r
                 JOIN user_contact uc ON r.user_id = uc.user_id
        WHERE uc.contact_info = contact_input
          AND uc.contact_type = contact_type_input
        ORDER BY r.reservation_datetime;
END;
$$;

--==============
BEGIN;
CALL get_user_reservations_by_contact('5406848614', 'PHONE_NUMBER', 'my_cursor');
FETCH ALL FROM my_cursor;
CLOSE my_cursor;
COMMIT;

--=====================3======================--
CREATE OR REPLACE PROCEDURE get_reservations_by_origin_city(
    IN city_name VARCHAR,
    INOUT result_cursor refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN result_cursor FOR
        SELECT DISTINCT r.*
        FROM reservations r
                 JOIN ticket_reservation tr ON r.reservation_id = tr.reservation_id
                 JOIN trips t ON tr.trip_id = t.trip_id
                 JOIN location_details l ON t.origin_location_id = l.location_id
        WHERE l.city ILIKE city_name
        ORDER BY r.reservation_datetime;
END;
$$;

--==============
ROLLBACK;
BEGIN;
CALL get_reservations_by_origin_city('Babol', 'my_cursor');
FETCH ALL FROM my_cursor;
CLOSE my_cursor;
COMMIT;

--=====================5======================--
CREATE OR REPLACE PROCEDURE get_same_city_users_by_contact(
    IN contact_input VARCHAR,
    IN contact_input_type contact_type,
    INOUT result_set refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN result_set FOR
        SELECT u.* FROM users u JOIN user_contact uc
                                     ON uc.contact_type = contact_input_type
                                         AND uc.contact_info = contact_input
                                JOIN users original_user ON original_user.user_id = uc.user_id
        WHERE u.city = original_user.city AND
                u.user_id != original_user.user_id;
END;
$$;

--===========
BEGIN;
CALL get_same_city_users_by_contact('5406848614', 'PHONE_NUMBER', 'my_cursor');
FETCH ALL FROM my_cursor;
CLOSE my_cursor;
COMMIT;

--====================6=============================--
CREATE OR REPLACE PROCEDURE get_top_n_users_with_paid_reservations_after(
    IN n INTEGER,
    IN from_date TIMESTAMPTZ,
    INOUT result_set refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN result_set FOR
        SELECT u.user_id, u.first_name, u.last_name, COUNT(*) AS paid_reservation_count
        FROM users u
                 JOIN reservations r ON r.user_id = u.user_id
        WHERE r.reserve_status = 'PAID'
          AND r.reservation_datetime > from_date
        GROUP BY u.user_id
        ORDER BY paid_reservation_count DESC
        LIMIT n;
END;
$$;

--======
BEGIN;
CALL get_top_n_users_with_paid_reservations_after(5, '2024-01-01', 'top_users_cursor');
FETCH ALL FROM top_users_cursor;
CLOSE top_users_cursor;
COMMIT;
--===================7============================--
CREATE OR REPLACE PROCEDURE get_cancelled_reservations_by_vehicle(
    IN vehicle trip_type,
    INOUT result_set refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN result_set FOR
        SELECT DISTINCT r.*
        FROM reservations r
                 JOIN ticket_reservation tr ON tr.reservation_id = r.reservation_id
                 JOIN tickets t ON t.trip_id = tr.trip_id AND t.age = tr.age
        WHERE r.reserve_status = 'CANCELLED'
          AND t.trip_vehicle = vehicle
        ORDER BY reservation_datetime;
END;
$$;

--===========
BEGIN;
CALL get_cancelled_reservations_by_vehicle('BUS', 'cancelled_cursor');
FETCH ALL FROM cancelled_cursor;
CLOSE cancelled_cursor;
COMMIT;

--===================8============================--
ROLLBACK;

CREATE OR REPLACE PROCEDURE get_users_with_most_reports_by_topic(
    IN report_topic VARCHAR,
    INOUT result_set refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN result_set FOR
        WITH topic_counts AS (
            SELECT user_id, COUNT(*) AS report_count
            FROM reports
            WHERE topic = report_topic
            GROUP BY user_id
        ),
             max_count AS (
                 SELECT MAX(report_count) AS max_val FROM topic_counts
             )
        SELECT u.user_id, u.first_name, u.last_name, tc.report_count
        FROM topic_counts tc
                 JOIN max_count mc ON tc.report_count = mc.max_val
                 JOIN users u ON u.user_id = tc.user_id;
END;
$$;

--=============

