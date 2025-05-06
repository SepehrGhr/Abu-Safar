--1: users with no reserves yet
SELECT first_name, last_name
FROM users
WHERE NOT EXISTS (
  SELECT 1 FROM reservations WHERE reservations.user_id = users.user_id
);
--2: users with at least one reservations
SELECT DISTINCT ON (users.user_id) first_name, last_name
FROM users
         JOIN reservations
              ON users.user_id = reservations.user_id
WHERE reservations.reserve_status = 'PAID';
--3: sum of a users payments per month
SELECT users.first_name, users.last_name, TO_CHAR(payment_timestamp, 'Month') AS "month", SUM(price)
FROM payments
         JOIN users ON users.user_id = payments.user_id
WHERE payments.payment_status = 'SUCCESSFUL'
GROUP BY "month", users.user_id;
--4: users who reserved one ticket per city
SELECT users.first_name, users.last_name, loc.city
FROM users
         JOIN reservations rsv ON users.user_id = rsv.user_id
         JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id AND rsv.reserve_status = 'PAID'
         JOIN trips ON t_r.trip_id = trips.trip_id
         JOIN location_details loc ON loc.location_id = trips.origin_location_id
GROUP BY loc.city, users.user_id
HAVING COUNT(*) = 1
ORDER BY loc.city;

--5: user who did the most recent reservation
SELECT users.*
FROM users
         JOIN reservations rsv ON users.user_id = rsv.user_id
ORDER BY rsv.reservation_datetime DESC
LIMIT 1;

--6: users with their sum of their payments greater than average payments per users
WITH user_totals AS (SELECT user_id, SUM(price) AS total_payment
                     FROM payments
                     WHERE payment_status = 'SUCCESSFUL'
                     GROUP BY user_id),
     avg_total AS (SELECT AVG(total_payment) AS avg_payment
                   FROM user_totals)
SELECT DISTINCT ON (uc.user_id) uc.contact_info
FROM user_totals ut
         JOIN avg_total avg ON ut.total_payment > avg.avg_payment
         JOIN user_contact uc ON uc.user_id = ut.user_id;

--7: reservation count per vehicle type
SELECT trip_vehicle, COUNT(*) AS ticket_count
FROM tickets
         JOIN ticket_reservation t_r ON tickets.trip_id = t_r.trip_id AND tickets.age = t_r.age
         JOIN reservations rsv ON rsv.reservation_id = t_r.reservation_id
WHERE rsv.reserve_status = 'PAID'
GROUP BY trip_vehicle;

--8: top 3 users based on count of reservations
SELECT first_name, last_name
FROM users
         JOIN reservations rsv ON users.user_id = rsv.user_id
         JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id AND rsv.reserve_status = 'PAID'
GROUP BY users.user_id, users.first_name, users.last_name
ORDER BY COUNT(*) DESC
LIMIT 3;

--9: count of reservations made from Tehran province per city
SELECT COUNT(*) AS ticket_count, loc.city
FROM ticket_reservation t_r
         JOIN trips ON t_r.trip_id = trips.trip_id
         JOIN location_details loc ON trips.origin_location_id = loc.location_id
         JOIN reservations rsv ON rsv.reservation_id = t_r.reservation_id
    AND rsv.reserve_status = 'PAID'
WHERE loc.province = 'Tehran'
GROUP BY loc.city;
--10: the cities that the oldest user had reservations from
SELECT DISTINCT loc.city
FROM users
         JOIN reservations rsv ON users.user_id = rsv.user_id
         JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id AND rsv.reserve_status = 'PAID'
         JOIN trips ON t_r.trip_id = trips.trip_id
         JOIN location_details loc ON loc.location_id = trips.origin_location_id
WHERE users.sign_up_date =
      (SELECT MIN(sign_up_date) FROM users);

--11: name aff all system's admins
SELECT first_name, last_name
FROM users
WHERE user_role = 'ADMIN';

--12: users with at least 2 reservations
SELECT first_name, last_name
FROM users
         JOIN reservations rsv ON rsv.user_id = users.user_id AND rsv.reserve_status = 'PAID'
GROUP BY users.user_id
HAVING COUNT(*) >= 2;

--13: users with at most 2 train reservations
SELECT first_name, last_name
FROM users
         LEFT JOIN reservations rsv ON rsv.user_id = users.user_id
         LEFT JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id AND rsv.reserve_status = 'PAID'
         LEFT JOIN tickets ON tickets.trip_id = t_r.trip_id AND tickets.age = t_r.age
WHERE tickets.trip_vehicle = 'TRAIN'
   OR rsv.user_id IS NULL
GROUP BY users.user_id
HAVING COUNT(*) <= 2;

--14: contact info of users with reservation made from all vehicle types
SELECT DISTINCT ON (user_contact.user_id) user_contact.contact_info
FROM user_contact
         JOIN (SELECT uv.user_id
               FROM (SELECT users.user_id, tck.trip_vehicle
                     FROM users
                              JOIN reservations rsv ON USERS.user_id = rsv.user_id
                              JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id
                              JOIN tickets tck ON tck.trip_id = t_r.trip_id
                     GROUP BY USERS.user_id, tck.trip_vehicle) as uv
               GROUP BY uv.user_id
               HAVING COUNT(*) = 3) AS uv_count
              ON uv_count.user_id = user_contact.user_id;

--15: reservations made today ordered by time
SELECT rsv.*
FROM reservations rsv
WHERE rsv.reservation_datetime::date = CURRENT_DATE
  AND rsv.reserve_status = 'PAID'
ORDER BY rsv.reservation_datetime;

--16: second most-sold ticket
WITH t AS (SELECT t_r.trip_id AS trip_id, t_r.age as age, COUNT(*) AS counts
           FROM reservations rsv
                    JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id
               AND rsv.reserve_status = 'PAID'
                    JOIN trips ON trips.trip_id = t_r.trip_id
           GROUP BY t_r.trip_id, t_r.age),
     second_highest AS (SELECT DISTINCT counts
                        FROM t
                        ORDER BY counts DESC
                        LIMIT 1 OFFSET 1)
SELECT trips.*, t.age
FROM t
         JOIN trips ON t.trip_id = trips.trip_id
WHERE counts = (SELECT counts FROM second_highest);

--17: admin with most cancelled reservations and cancellation percentage
WITH cancel_counts AS (SELECT users.user_id, users.first_name, users.last_name, COUNT(*) AS cancel_count
                       FROM users
                                JOIN reservations ON users.user_id = reservations.cancelled_by
                       WHERE users.user_role = 'ADMIN'
                       GROUP BY users.user_id),
     total_admin_cancels AS (SELECT SUM(cancel_count) AS total_cancels
                             FROM cancel_counts)
SELECT *,
       ROUND(100.0 * cancel_count / (SELECT total_cancels FROM total_admin_cancels), 2) AS cancel_percentage
FROM cancel_counts
WHERE cancel_count = (SELECT MAX(cancel_count) FROM cancel_counts);

--18: change the last name of the user with the most cancelled reservations to 'Reddington'
WITH cancelled AS (
    SELECT users.user_id, COUNT(*) AS count
    FROM users
             JOIN reservations rsv ON users.user_id = rsv.user_id
    WHERE rsv.reserve_status = 'CANCELLED'
    GROUP BY users.user_id
),
     max_cancelled_user AS (
         SELECT user_id
         FROM cancelled
         WHERE count = (SELECT MAX(count) FROM cancelled)
     )
UPDATE users
SET last_name = 'REDDINGTON'
WHERE user_id IN (SELECT user_id FROM max_cancelled_user);


--19: delete all cancelled reservations of the user with last name 'Reddington'
DELETE
FROM reservations rvs
    USING users u
WHERE u.user_id = rvs.user_id
  AND u.last_name = 'Reddington'
  AND rvs.reserve_status = 'CANCELLED';

--20: delete all cancelled reservations
DELETE
FROM reservations rvs
WHERE rvs.reserve_status = 'CANCELLED';

--21: lower the price of the tickets from 'Mahan' company that were sold yesterday by 10%
UPDATE tickets t
SET price = price * 0.9
WHERE t.trip_id IN (SELECT tr.trip_id
                    FROM ticket_reservation tr
                             JOIN reservations rsv ON tr.reservation_id = rsv.reservation_id
                             JOIN trips trip ON trip.trip_id = tr.trip_id
                    WHERE rsv.reserve_status = 'PAID'
                      AND DATE(rsv.reservation_datetime) = CURRENT_DATE - 1
                      AND trip.vehicle_company = 'MAHAN');


--22: topic and count of reports for the most reported ticket
WITH report_counts AS (SELECT link_id, COUNT(*) AS cnt
                       FROM reports
                       WHERE link_type = 'TICKET'
                       GROUP BY link_id),
     max_reports AS (SELECT MAX(cnt) AS max_count
                     FROM report_counts)
SELECT r.topic, rc.cnt
FROM reports r
         JOIN report_counts rc ON r.link_id = rc.link_id
         JOIN max_reports mr ON rc.cnt = mr.max_count;
