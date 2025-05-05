--1
SELECT first_name, last_name FROM users LEFT JOIN reservations 
 ON users.user_id = reservations.user_id WHERE reservation_id IS NULL;
--2
SELECT DISTINCT ON (users.user_id) first_name, last_name FROM users JOIN reservations 
 ON users.user_id = reservations.user_id
 WHERE reservations.reserve_status = 'PAID';
--3
SELECT users.first_name, users.last_name, TO_CHAR(payment_timestamp, 'Month') AS "month", SUM(price) FROM payments 
	JOIN users ON users.user_id = payments.user_id
	WHERE payments.payment_status = 'SUCCESSFUL'
	GROUP BY  "month", users.user_id;
--4
SELECT users.first_name, users.last_name, loc.city FROM users 
	JOIN reservations rsv ON users.user_id = rsv.user_id
	JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id AND rsv.reserve_status = 'PAID'
	JOIN trips ON t_r.trip_id = trips.trip_id
	JOIN location_details loc ON loc.location_id = trips.origin_location_id
		GROUP BY loc.city, users.user_id 
		HAVING COUNT(*) = 1
		ORDER BY loc.city;

--5
SELECT users.* FROM users
	JOIN reservations rsv ON users.user_id = rsv.user_id
	ORDER BY rsv.reservation_datetime DESC LIMIT 1;

--6
SELECT DISTINCT ON (user_contact.user_id) user_contact.contact_info FROM user_contact
	JOIN payments ON user_contact.user_id = payments.user_id 
	GROUP BY user_contact.contact_info, user_contact.user_id
	HAVING SUM(price) > (
		SELECT AVG(user_price_sum) FROM (SELECT SUM(price) AS user_price_sum FROM
		users JOIN payments ON users.user_id = payments.user_id GROUP BY users.user_id
	));

--7
SELECT trip_vehicle, COUNT(*) AS ticket_count FROM tickets GROUP BY trip_vehicle;

--8
SELECT first_name, last_name FROM users
	JOIN reservations rsv ON users.user_id = rsv.user_id
	JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id
	GROUP BY users.user_id, users.first_name, users.last_name
	ORDER BY COUNT(*) DESC LIMIT 3;

--9
SELECT COUNT(*), loc.city FROM ticket_reservation t_r
	JOIN trips ON t_r.trip_id = trips.trip_id
	JOIN location_details loc ON trips.origin_location_id = loc.location_id
	WHERE loc.state = 'TEHRAN'
	GROUP BY loc.city;

--10
SELECT loc.city FROM users 
	JOIN reservations rsv ON users.user_id = rsv.user_id
	JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id
	JOIN trips ON t_r.trip_id = trips.trip_id
	JOIN location_details loc ON loc.location_id = trips.origin_location_id
		GROUP BY loc.city, users.user_id 
		WHERE users.sign_up_date = 
		(SELECT MIN(sign_up_date) FROM users);

--11
SELECT first_name, last_name FROM users WHERE user_role = 'ADMIN';

--12
SELECT first_name, last_name FROM users
	JOIN reservations rsv ON rsv.user_id = users.user_id
	JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id
		GROUP BY users.user_id, first_name, last_name
			WHERE COUNT(*) >= 2;

--13
SELECT first_name, last_name FROM users
	JOIN reservations rsv ON rsv.user_id = users.user_id
	JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id
	JOIN tickets ON tickets.trip_id = t_r.trip_id AND tickets.age = t_r.age
		GROUP BY tickets.trip_vehicle, first_name, last_name
		HAVING COUNT(*) <= 2;

--14
SELECT DISTINCT ON (user_contact.user_id) user_contact.contact_info FROM user_contact
	JOIN reservations rsv ON user_contact.user_id = rsv.user_id 
	JOIN ticket_reservation t_r ON rsv.reservation_id = t_r.reservation_id
	JOIN tickets tck ON tck.trip_id = t_r.trip_id
	GROUP BY user_contact.contact_info, user_contact.user_id, tck.trip_vehicle
	...

--15
SELECT trips.* FROM reservations 
	JOIN ticket_reservation t_r ON reservations.reservation_id = t_r.reservation_id
	JOIN trips ON trips.trip_id = t_r.trip_id
	WHERE reservations.reservation_datetime::date = CURRENT_DATE
	ORDER BY reservations.reservation_datetime ASC;

--16
SELECT trips.* FROM reservations
	JOIN ticket_reservation t_r ON reservations.reservation_id = t_r.reservation_id
	JOIN trips ON trips.trip_id = t_r.trip_id
	GROUP BY tickets.trip_id, tickets.age
	WHERE reservations.report_status = 'PAID', COUNT()
	



--20
DELETE 

--21
UPDATE tickets SET price = price * 90 / 100
	FROM trips WHERE trips.trip_id = tickets.trip_id
	AND tickets.trip_vehicle = 'FLIGHT' AND trips.vehicle_company = 'MAHAN';

--22
SELECT topic, COUNT(*) FROM reports WHERE link_type = 'TICKET'
	AND link_id = (SELECT link_id FROM reports WHERE link_type = 'TICKET'
		GROUP BY link_id ORDER BY count(*) DESC LIMIT 1)
		GROUP BY topic; ?top same 1s?