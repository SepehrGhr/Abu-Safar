package main

import (
	"database/sql"
	"fmt"
	"log"
	"math/rand"
	"time"

	"github.com/brianvoe/gofakeit/v6"
	_ "github.com/lib/pq"
	"golang.org/x/crypto/bcrypt"
)

func main() {
	var db *sql.DB
	var err error

	connStr := "user=postgres password=98979695 dbname=postgres sslmode=disable host=localhost port=5432"
	db, err = sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	gofakeit.Seed(time.Now().UnixNano())

	// Seed in a logical order
	seedUsers(db)
	seedUserContact(db)
	seedLocationDetails(db)
	seedCompanies(db)
	seedOneWayTrips(db)
	seedRoundTrips(db)
	seedReservations(db)
	seedPayments(db)
	seedReports(db)
}

func hashPassword(password string) (string, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(hashedPassword), nil
}

// --- Seeder Functions ---

func seedUsers(db *sql.DB) {
	numUsers := 30
	fmt.Println("🌱 Seeding", numUsers, "users...")
	for i := 0; i < numUsers; i++ {
		password := gofakeit.Password(true, true, true, true, false, 12)
		hashedPassword, err := hashPassword(password)
		if err != nil {
			log.Printf("Error hashing password: %v\n", err)
			continue
		}
		_, err = db.Exec(`
            INSERT INTO users (first_name, last_name, user_role, account_status, city, hashed_password, profile_picture, sign_up_date)
            VALUES ($1, $2, 'USER', 'ACTIVE', $3, $4, 'default.png', $5)
        `, gofakeit.FirstName(), gofakeit.LastName(), gofakeit.City(), hashedPassword, gofakeit.DateRange(time.Now().AddDate(-2, 0, 0), time.Now()))
		if err != nil {
			log.Printf("Error inserting user: %v\n", err)
		}
	}
	fmt.Println("✅ Finished seeding users.")
}

func seedUserContact(db *sql.DB) {
	fmt.Println("🌱 Seeding user contacts...")
	for i := 1; i <= 30; i++ { // Assuming user IDs are 1-30
		if gofakeit.Bool() {
			_, err := db.Exec(`INSERT INTO user_contact (user_id, contact_type, contact_info) VALUES ($1, 'EMAIL', $2)`, i, gofakeit.Email())
			if err != nil {
				log.Printf("Error inserting email for user %d: %v\n", i, err)
			}
		}
		if gofakeit.Bool() {
			_, err := db.Exec(`INSERT INTO user_contact (user_id, contact_type, contact_info) VALUES ($1, 'PHONE_NUMBER', $2)`, i, gofakeit.Phone())
			if err != nil {
				log.Printf("Error inserting phone for user %d: %v\n", i, err)
			}
		}
	}
	fmt.Println("✅ Finished seeding user contacts.")
}

var locations = map[string]map[string][]string{
	"USA":    {"California": {"Los Angeles", "San Francisco"}, "New York": {"New York City"}},
	"Canada": {"Ontario": {"Toronto"}, "Quebec": {"Montreal"}},
	"Iran":   {"Tehran": {"Tehran"}, "Mazandaran": {"Sari"}},
}

func seedLocationDetails(db *sql.DB) {
	fmt.Println("🌱 Seeding locations...")
	for country, provinces := range locations {
		for province, cities := range provinces {
			for _, city := range cities {
				_, err := db.Exec(`INSERT INTO location_details (country, province, city) VALUES ($1, $2, $3)`, country, province, city)
				if err != nil {
					log.Printf("Error inserting location: %v\n", err)
				}
			}
		}
	}
	fmt.Println("✅ Finished seeding locations.")
}

func seedCompanies(db *sql.DB) {
	fmt.Println("🌱 Seeding companies...")
	companies := map[string]string{
		"Delta Airlines": "FLIGHT", "United Airlines": "FLIGHT", "Lufthansa": "FLIGHT",
		"Amtrak": "TRAIN", "Deutsche Bahn": "TRAIN", "Eurostar": "TRAIN",
		"Greyhound": "BUS", "FlixBus": "BUS", "Peter Pan": "BUS",
	}
	for name, vehicleType := range companies {
		_, err := db.Exec(`INSERT INTO companies (name, vehicle_type) VALUES ($1, $2)`, name, vehicleType)
		if err != nil {
			log.Printf("Error inserting company: %v\n", err)
		}
	}
	fmt.Println("✅ Finished seeding companies.")
}

func seedOneWayTrips(db *sql.DB) {
	fmt.Println("🌱 Seeding one-way trips...")
	companyIDs := getCompanyIDs(db)
	locationIDs := getLocationIDs(db)
	numTrips := 60

	for i := 0; i < numTrips; i++ {
		originLocationID, destinationLocationID := getRandomLocationPair(locationIDs)
		companyID := companyIDs[rand.Intn(len(companyIDs))]
		departureTimestamp := time.Now().Add(time.Duration(rand.Intn(1000)) * time.Hour)
		arrivalTimestamp := departureTimestamp.Add(time.Duration(rand.Intn(23)+1) * time.Hour)

		var tripID int64
		err := db.QueryRow(`
            INSERT INTO trips (origin_location_id, destination_location_id, departure_timestamp, arrival_timestamp, company_id, stop_count, total_capacity)
            VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING trip_id
        `, originLocationID, destinationLocationID, departureTimestamp, arrivalTimestamp, companyID, rand.Intn(3), rand.Intn(51)+50).Scan(&tripID)

		if err != nil {
			log.Printf("Error inserting one-way trip: %v\n", err)
			continue
		}

		var vehicleType string
		err = db.QueryRow(`SELECT vehicle_type FROM companies WHERE company_id = $1`, companyID).Scan(&vehicleType)
		if err != nil {
			log.Printf("Could not get vehicle type for company %d: %v", companyID, err)
			continue
		}

		seedTicketsForTrip(db, tripID, vehicleType)
		seedVehicleDetailsForTrip(db, tripID, vehicleType)
		seedAdditionalServicesForTrip(db, tripID)
	}
	fmt.Println("✅ Finished seeding one-way trips.")
}

func seedRoundTrips(db *sql.DB) {
	fmt.Println("🌱 Seeding round-trip data...")
	locationIDs := getLocationIDs(db)
	companyIDsByType := getCompanyIDsByType(db)
	tripTypes := []string{"TRAIN", "BUS", "FLIGHT"}
	numRoundTrips := 20

	for i := 0; i < numRoundTrips; i++ {
		originLocationID, destinationLocationID := getRandomLocationPair(locationIDs)
		tripVehicleType := tripTypes[rand.Intn(len(tripTypes))]

		validCompanyIDs := companyIDsByType[tripVehicleType]
		if len(validCompanyIDs) == 0 {
			log.Printf("⚠️ No companies found for vehicle type %s, skipping round trip.", tripVehicleType)
			continue
		}
		outgoingCompanyID := validCompanyIDs[rand.Intn(len(validCompanyIDs))]
		returnCompanyID := validCompanyIDs[rand.Intn(len(validCompanyIDs))]

		departureTimestamp := time.Now().Add(time.Duration(rand.Intn(20)+1) * 24 * time.Hour)
		arrivalTimestamp := departureTimestamp.Add(time.Duration(rand.Intn(11)+2) * time.Hour)

		var outgoingTripID int64
		err := db.QueryRow(`
            INSERT INTO trips (origin_location_id, destination_location_id, departure_timestamp, arrival_timestamp, company_id, stop_count, total_capacity)
            VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING trip_id
        `, originLocationID, destinationLocationID, departureTimestamp, arrivalTimestamp, outgoingCompanyID, rand.Intn(3), rand.Intn(51)+50).Scan(&outgoingTripID)

		if err != nil {
			log.Printf("❌ Failed to insert outgoing trip: %v", err)
			continue
		}
		seedTicketsForTrip(db, outgoingTripID, tripVehicleType)
		seedVehicleDetailsForTrip(db, outgoingTripID, tripVehicleType)
		seedAdditionalServicesForTrip(db, outgoingTripID)

		returnDepartureTimestamp := arrivalTimestamp.Add(time.Duration(rand.Intn(5)+1) * 24 * time.Hour)
		returnArrivalTimestamp := returnDepartureTimestamp.Add(time.Duration(rand.Intn(11)+2) * time.Hour)

		var returnTripID int64
		err = db.QueryRow(`
            INSERT INTO trips (origin_location_id, destination_location_id, departure_timestamp, arrival_timestamp, company_id, stop_count, total_capacity)
            VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING trip_id
        `, destinationLocationID, originLocationID, returnDepartureTimestamp, returnArrivalTimestamp, returnCompanyID, rand.Intn(3), rand.Intn(51)+50).Scan(&returnTripID)

		if err != nil {
			log.Printf("❌ Failed to insert return trip: %v", err)
			continue
		}
		seedTicketsForTrip(db, returnTripID, tripVehicleType)
		seedVehicleDetailsForTrip(db, returnTripID, tripVehicleType)
		seedAdditionalServicesForTrip(db, returnTripID)
	}
	fmt.Println("✅ Finished seeding round-trip data.")
}

func seedReservations(db *sql.DB) {
	fmt.Println("🌱 Seeding reservations...")
	type TripInfo struct {
		ID        int64
		Departure time.Time
	}
	var trips []TripInfo
	rows, err := db.Query("SELECT trip_id, departure_timestamp FROM trips")
	if err != nil {
		log.Fatalf("❌ Could not fetch trips for reservations: %v", err)
	}
	defer rows.Close()
	for rows.Next() {
		var trip TripInfo
		if err := rows.Scan(&trip.ID, &trip.Departure); err != nil {
			log.Printf("⚠️ Could not scan trip: %v", err)
			continue
		}
		trips = append(trips, trip)
	}

	if len(trips) == 0 {
		log.Println("⚠️ No trips found to create reservations for. Skipping.")
		return
	}

	ageCategories := []string{"ADULT", "CHILD", "BABY"}

	for i := 0; i < 200; i++ {
		trip := trips[rand.Intn(len(trips))]

		reservationTimestamp := gofakeit.DateRange(time.Now().AddDate(0, -1, 0), trip.Departure.Add(-time.Hour))
		expirationTimestamp := reservationTimestamp.Add(10 * time.Minute)

		var reservationID int64
		err := db.QueryRow(`
            INSERT INTO reservations (user_id, reservation_datetime, expiration_datetime, reserve_status)
            VALUES ($1, $2, $3, 'PAID') RETURNING reservation_id
        `, gofakeit.Number(1, 30), reservationTimestamp, expirationTimestamp).Scan(&reservationID)
		if err != nil {
			log.Printf("Error inserting reservation: %v\n", err)
			continue
		}

		_, err = db.Exec(`
			INSERT INTO ticket_reservation (trip_id, age, reservation_id, seat_number)
			VALUES ($1, $2, $3, $4)
		`, trip.ID, gofakeit.RandomString(ageCategories), reservationID, gofakeit.Number(1, 100))
		if err != nil {
			log.Printf("❌ Failed to insert ticket_reservation for reservation %d: %v", reservationID, err)
		}
	}
	fmt.Println("✅ Finished seeding reservations and their ticket links.")
}

func seedPayments(db *sql.DB) {
	fmt.Println("🌱 Seeding payments...")
	type ReservationInfo struct {
		ID   int64
		Time time.Time
	}
	var reservations []ReservationInfo
	rows, err := db.Query("SELECT reservation_id, reservation_datetime FROM reservations")
	if err != nil {
		log.Fatalf("❌ Could not fetch reservations for payments: %v", err)
	}
	defer rows.Close()
	for rows.Next() {
		var res ReservationInfo
		if err := rows.Scan(&res.ID, &res.Time); err != nil {
			log.Printf("⚠️ Could not scan reservation: %v", err)
			continue
		}
		reservations = append(reservations, res)
	}

	for _, res := range reservations {
		paymentTimestamp := gofakeit.DateRange(res.Time, res.Time.Add(9*time.Minute))

		_, err := db.Exec(`
            INSERT INTO payments (reservation_id, user_id, payment_status, payment_type, price, payment_timestamp)
            VALUES ($1, $2, 'SUCCESSFUL', $3, $4, $5)
        `, res.ID, gofakeit.Number(1, 30), gofakeit.RandomString([]string{"CARD", "WALLET"}), gofakeit.Price(50, 500), paymentTimestamp)
		if err != nil {
			log.Printf("Error inserting payment for reservation %d: %v\n", res.ID, err)
		}
	}
	fmt.Println("✅ Finished seeding payments.")
}

func seedReports(db *sql.DB) {
	fmt.Println("🌱 Seeding reports...")
	reportTopics := []string{"Incorrect price", "Seat not available", "Late departure"}
	reportContents := []string{"I was charged more than the shown price.", "The vehicle left much later than scheduled."}
	var reservationIDs []int64
	rows, err := db.Query("SELECT reservation_id FROM reservations")
	if err != nil {
		log.Fatalf("❌ Could not fetch reservations for reports: %v", err)
	}
	defer rows.Close()
	for rows.Next() {
		var id int64
		if err := rows.Scan(&id); err != nil {
			log.Fatal(err)
		}
		reservationIDs = append(reservationIDs, id)
	}

	if len(reservationIDs) == 0 {
		log.Println("⚠️ No reservations to report on. Skipping.")
		return
	}

	for i := 0; i < 50; i++ {
		resID := reservationIDs[rand.Intn(len(reservationIDs))]
		_, err := db.Exec(`
            INSERT INTO reports (user_id, link_type, link_id, topic, content, report_status)
            VALUES ($1, 'RESERVATION', $2, $3, $4, $5)
        `, gofakeit.Number(1, 30), resID, gofakeit.RandomString(reportTopics), gofakeit.RandomString(reportContents), gofakeit.RandomString([]string{"PENDING", "REVIEWED"}))
		if err != nil {
			log.Printf("Error inserting report: %v\n", err)
		}
	}
	fmt.Println("✅ Finished seeding reports.")
}

// --- Helper Functions for Seeding ---

func seedTicketsForTrip(db *sql.DB, tripID int64, vehicleType string) {
	ageCategories := []string{"BABY", "CHILD", "ADULT"}
	for _, age := range ageCategories {
		_, err := db.Exec(`
            INSERT INTO tickets (trip_id, age, price, trip_vehicle)
            VALUES ($1, $2, $3, $4)
        `, tripID, age, gofakeit.Price(50, 500), vehicleType)
		if err != nil {
			log.Printf("Error inserting ticket for trip %d: %v\n", tripID, err)
		}
	}
}

func seedVehicleDetailsForTrip(db *sql.DB, tripID int64, vehicleType string) {
	switch vehicleType {
	case "TRAIN":
		_, err := db.Exec(`INSERT INTO trains (trip_id, stars, room_type) VALUES ($1, $2, $3)`,
			tripID, gofakeit.Number(3, 5), gofakeit.RandomString([]string{"4-BED", "6-BED"}))
		if err != nil {
			log.Printf("Error inserting train details for trip %d: %v\n", tripID, err)
		}
	case "FLIGHT":
		_, err := db.Exec(`INSERT INTO flights (trip_id, class, departure_airport, arrival_airport) VALUES ($1, $2, $3, $4)`,
			tripID, gofakeit.RandomString([]string{"Economy class", "Business class"}), gofakeit.AirportIata(), gofakeit.AirportIata())
		if err != nil {
			log.Printf("Error inserting flight details for trip %d: %v\n", tripID, err)
		}
	case "BUS":
		_, err := db.Exec(`INSERT INTO buses (trip_id, class, chair_type) VALUES ($1, $2, $3)`,
			tripID, gofakeit.RandomString([]string{"VIP", "Standard"}), gofakeit.RandomString([]string{"1-2", "2-2"}))
		if err != nil {
			log.Printf("Error inserting bus details for trip %d: %v\n", tripID, err)
		}
	}
}

func seedAdditionalServicesForTrip(db *sql.DB, tripID int64) {
	services := []string{"Internet", "Food service", "Bed"}
	numServices := gofakeit.Number(1, 3)
	gofakeit.ShuffleStrings(services)
	for i := 0; i < numServices; i++ {
		_, err := db.Exec(`INSERT INTO additional_services (trip_id, service_type) VALUES ($1, $2)`, tripID, services[i])
		if err != nil {
			log.Printf("Error inserting service for trip %d: %v\n", tripID, err)
		}
	}
}

func getCompanyIDs(db *sql.DB) []int {
	rows, err := db.Query("SELECT company_id FROM companies")
	if err != nil {
		log.Fatalf("Could not get company IDs: %v", err)
	}
	defer rows.Close()
	var ids []int
	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			log.Fatal(err)
		}
		ids = append(ids, id)
	}
	return ids
}

func getLocationIDs(db *sql.DB) []int {
	rows, err := db.Query("SELECT location_id FROM location_details")
	if err != nil {
		log.Fatalf("Could not get location IDs: %v", err)
	}
	defer rows.Close()
	var ids []int
	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			log.Fatal(err)
		}
		ids = append(ids, id)
	}
	return ids
}

func getRandomLocationPair(ids []int) (int, int) {
	origin := ids[rand.Intn(len(ids))]
	destination := ids[rand.Intn(len(ids))]
	for origin == destination {
		destination = ids[rand.Intn(len(ids))]
	}
	return origin, destination
}

func getCompanyIDsByType(db *sql.DB) map[string][]int {
	rows, err := db.Query("SELECT company_id, vehicle_type FROM companies")
	if err != nil {
		log.Fatalf("Could not get company IDs by type: %v", err)
	}
	defer rows.Close()
	idsByType := make(map[string][]int)
	for rows.Next() {
		var id int
		var vehicleType string
		if err := rows.Scan(&id, &vehicleType); err != nil {
			log.Fatal(err)
		}
		idsByType[vehicleType] = append(idsByType[vehicleType], id)
	}
	return idsByType
}
