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

const (
	numUsers        = 2000
	numTrips        = 100
	numReservations = 3000
	numReports      = 800
)

func main() {
	var db *sql.DB
	var err error

	connStr := "user=postgres password=ilovebvb09@ dbname=AbuSafar sslmode=disable host=localhost port=3036"
	db, err = sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	gofakeit.Seed(time.Now().UnixNano())

	seedUsers(db)
	seedUserContact(db)
	seedLocationDetails(db)
	seedCompanies(db)
	seedTrips(db)
	seedTickets(db)
	seedReservations(db)
	processPayments(db)
	seedAdditionalServices(db)
	seedTransportDetails(db)
	seedReports(db)
}

// hashPassword generates a bcrypt hash of the given password string.
func hashPassword(password string) (string, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(hashedPassword), nil
}

// seedUsers populates the 'users' table with random data.
func seedUsers(db *sql.DB) {
	for i := 1; i <= numUsers; i++ {
		password := gofakeit.Password(true, true, true, true, false, 12)
		hashedPassword, err := hashPassword(password)
		if err != nil {
			log.Printf("Error hashing password for user %d: %v\n", i, err)
			continue
		}

		_, err = db.Exec(`
            INSERT INTO users (first_name, last_name, user_role, account_status, city, hashed_password, profile_picture, sign_up_date)
            VALUES ($1, $2, 'USER', 'ACTIVE', $3, $4, 'default.png', $5)
        `, gofakeit.FirstName(), gofakeit.LastName(), gofakeit.City(), hashedPassword, gofakeit.DateRange(time.Now().AddDate(-2, 0, 0), time.Now()))

		if err != nil {
			log.Printf("Error inserting user %d: %v\n", i, err)
		}
	}
	fmt.Printf("✅ Seeded %d users.\n", numUsers)
}

// seedUserContact populates 'user_contact' with guaranteed unique contact info.
func seedUserContact(db *sql.DB) {
	for i := 1; i <= numUsers; i++ {
		email := fmt.Sprintf("user%d_%s", i, gofakeit.Email())
		phone := fmt.Sprintf("+49151%04d%04d", i, gofakeit.Number(0, 9999))

		_, err := db.Exec(`INSERT INTO user_contact (user_id, contact_type, contact_info) VALUES ($1, 'EMAIL', $2)`, i, email)
		if err != nil {
			log.Printf("Error inserting email for user %d: %v\n", i, err)
		}

		_, err = db.Exec(`INSERT INTO user_contact (user_id, contact_type, contact_info) VALUES ($1, 'PHONE_NUMBER', $2)`, i, phone)
		if err != nil {
			log.Printf("Error inserting phone number for user %d: %v\n", i, err)
		}
	}
	fmt.Println("✅ Seeded user_contact data with unique info.")
}

var locations = map[string]map[string][]string{
	"USA":      {"California": {"Los Angeles", "San Francisco", "San Diego"}, "New York": {"New York City", "Buffalo", "Rochester"}, "Texas": {"Austin", "Houston", "Dallas"}},
	"Canada":   {"Ontario": {"Toronto", "Ottawa", "Hamilton"}, "Quebec": {"Montreal", "Quebec City", "Gatineau"}, "British Columbia": {"Vancouver", "Victoria", "Kelowna"}},
	"Germany":  {"Berlin": {"Berlin City", "Potsdam"}, "Bavaria": {"Munich", "Nuremberg", "Augsburg"}},
	"Iran":     {"Mazandaran": {"Sari", "Babol", "Ramsar"}, "Tehran": {"Tehran", "Damavand"}, "Markazi": {"Arak", "Saveh"}},
}

// seedLocationDetails populates the 'location_details' table.
func seedLocationDetails(db *sql.DB) {
	var count int
	for country, provinces := range locations {
		for province, cities := range provinces {
			for _, city := range cities {
				_, err := db.Exec(`INSERT INTO location_details (country, province, city) VALUES ($1, $2, $3)`, country, province, city)
				if err != nil {
					log.Printf("Error inserting location (%s, %s, %s): %v\n", country, province, city, err)
				}
				count++
			}
		}
	}
	fmt.Printf("✅ Seeded %d location_details.\n", count)
}

// seedCompanies populates the 'companies' table.
func seedCompanies(db *sql.DB) {
	companies := map[string][]string{
		"TRAIN":  {"Deutsche Bahn", "Amtrak", "Eurostar", "Indian Railways", "China Railways"},
		"BUS":    {"Greyhound", "FlixBus", "National Express", "Megabus", "Eurolines"},
		"FLIGHT": {"Lufthansa", "Emirates Airline", "Delta Air Lines", "Qatar Express", "Southwest Express"},
	}

	var companyCount int
	for vehicleType, names := range companies {
		for _, name := range names {
			_, err := db.Exec(`INSERT INTO companies (name, vehicle_type, cancellation_penalty_rate, description) VALUES ($1, $2, $3, $4)`, name, vehicleType, gofakeit.Float64Range(5.00, 25.00), gofakeit.Sentence(20))
			if err != nil {
				log.Printf("Error inserting company %s: %v\n", name, err)
				continue
			}
			companyCount++
		}
	}
	fmt.Printf("✅ Seeded %d companies.\n", companyCount)
}

// seedTrips populates the 'trips' table using company IDs.
func seedTrips(db *sql.DB) {
	var locationIDs []int
	rows, err := db.Query("SELECT location_id FROM location_details")
	if err != nil {
		log.Fatalf("Failed to query location_details: %v", err)
	}
	defer rows.Close()
	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			log.Fatalf("Failed to scan location_id: %v", err)
		}
		locationIDs = append(locationIDs, id)
	}
	if len(locationIDs) < 2 {
		log.Fatal("Not enough locations in the database to create trips. Need at least 2.")
		return
	}

	rows, err = db.Query("SELECT company_id, vehicle_type FROM companies")
	if err != nil {
		log.Fatalf("Failed to query companies: %v", err)
	}
	defer rows.Close()

	companiesByType := make(map[string][]int)
	for rows.Next() {
		var id int
		var vehicleType string
		if err := rows.Scan(&id, &vehicleType); err != nil {
			log.Fatalf("Failed to scan company: %v", err)
		}
		companiesByType[vehicleType] = append(companiesByType[vehicleType], id)
	}
	if len(companiesByType) == 0 {
		log.Fatal("No companies found. Please seed companies first.")
		return
	}

	tripTypes := []string{"TRAIN", "BUS", "FLIGHT"}
	for i := 1; i <= numTrips; i++ {
		rand.Shuffle(len(locationIDs), func(i, j int) { locationIDs[i], locationIDs[j] = locationIDs[j], locationIDs[i] })
		originLocationID := locationIDs[0]
		destinationLocationID := locationIDs[1]

		departureTimestamp := time.Now().Add(time.Duration(rand.Intn(1000)) * time.Hour)
		arrivalTimestamp := departureTimestamp.Add(time.Hour * time.Duration(rand.Intn(24)+1))
		selectedType := tripTypes[rand.Intn(len(tripTypes))]
		companyIDs := companiesByType[selectedType]
		if len(companyIDs) == 0 {
			log.Printf("Warning: No companies for type %s, skipping trip creation.", selectedType)
			continue
		}
		selectedCompanyID := companyIDs[rand.Intn(len(companyIDs))]

		_, err := db.Exec(`INSERT INTO trips (origin_location_id, destination_location_id, departure_timestamp, arrival_timestamp, company_id, stop_count, total_capacity) VALUES ($1, $2, $3, $4, $5, $6, $7)`, originLocationID, destinationLocationID, departureTimestamp, arrivalTimestamp, selectedCompanyID, rand.Intn(3), rand.Intn(51)+50)
		if err != nil {
			log.Printf("Error inserting trip: %v\n", err)
		}
	}
	fmt.Printf("✅ Seeded %d trips.\n", numTrips)
}

// seedTickets populates 'tickets', ensuring vehicle type matches the trip's company.
func seedTickets(db *sql.DB) {
	ageCategories := []string{"BABY", "CHILD", "ADULT"}
	for tripID := 1; tripID <= numTrips; tripID++ {
		var vehicleType string
		err := db.QueryRow(`SELECT c.vehicle_type FROM trips t JOIN companies c ON t.company_id = c.company_id WHERE t.trip_id = $1`, tripID).Scan(&vehicleType)
		if err != nil {
			continue
		}

		basePrice := float64(rand.Intn(101) + 100)
		priceMap := map[string]float64{"BABY": basePrice, "CHILD": basePrice + 50, "ADULT": basePrice + 150}

		for _, age := range ageCategories {
			_, err := db.Exec(`INSERT INTO tickets (trip_id, age, price, trip_vehicle) VALUES ($1, $2, $3, $4)`, tripID, age, priceMap[age], vehicleType)
			if err != nil {
				log.Printf("Error inserting ticket for trip %d (%s): %v", tripID, age, err)
			}
		}
	}
	fmt.Println("✅ Seeded tickets with consistent vehicle types.")
}

// seedReservations creates reservations, respecting the total capacity of each trip.
func seedReservations(db *sql.DB) {
	tripCapacities := make(map[int]int)
	rows, err := db.Query(`SELECT trip_id, total_capacity FROM trips`)
	if err != nil {
		log.Fatalf("❌ Could not fetch trip capacities: %v", err)
		return
	}
	defer rows.Close()
	for rows.Next() {
		var tripID, capacity int
		if err := rows.Scan(&tripID, &capacity); err != nil {
			log.Printf("Warning: Failed to scan trip capacity: %v", err)
			continue
		}
		tripCapacities[tripID] = capacity
	}

	reservationsMade := make(map[int]int)
	ageCategories := []string{"ADULT", "CHILD", "BABY"}
	successfulReservations := 0

	for i := 0; i < numReservations; i++ {
		var selectedTripID = -1
		tripIDs := make([]int, 0, len(tripCapacities))
		for id := range tripCapacities {
			tripIDs = append(tripIDs, id)
		}

		if len(tripIDs) == 0 {
			log.Println("No trips found in the database. Stopping reservation seeding.")
			break
		}

		rand.Shuffle(len(tripIDs), func(i, j int) { tripIDs[i], tripIDs[j] = tripIDs[j], tripIDs[i] })

		for _, tripID := range tripIDs {
			if reservationsMade[tripID] < tripCapacities[tripID] {
				selectedTripID = tripID
				break
			}
		}

		if selectedTripID == -1 {
			log.Println("Could not find any trip with available capacity. Stopping reservation seeding.")
			break
		}

		// 5. Create the reservation.
		var reservationID int64
		err := db.QueryRow(`INSERT INTO reservations (user_id) VALUES ($1) RETURNING reservation_id`, gofakeit.Number(1, numUsers)).Scan(&reservationID)
		if err != nil {
			log.Printf("❌ Failed to insert reservation: %v", err)
			continue
		}

		_, err = db.Exec(`INSERT INTO ticket_reservation (trip_id, age, reservation_id, seat_number) VALUES ($1, $2, $3, $4)`, selectedTripID, ageCategories[gofakeit.Number(0, 2)], reservationID, gofakeit.Number(1, 150))
		if err != nil {
			log.Printf("❌ Failed to insert ticket_reservation for reservation %d: %v", reservationID, err)
			continue
		}

		reservationsMade[selectedTripID]++
		successfulReservations++
	}
	fmt.Printf("✅ Seeded %d reservations respecting trip capacities.\n", successfulReservations)
}

// processPayments simulates payments being completed by updating existing 'PENDING' records.
func processPayments(db *sql.DB) {
	rows, err := db.Query(`SELECT payment_id FROM payments WHERE payment_status = 'PENDING'`)
	if err != nil {
		log.Fatalf("❌ Failed to query pending payments: %v", err)
	}
	defer rows.Close()

	var pendingPaymentIDs []int
	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			log.Printf("Warning: failed to scan pending payment ID: %v", err)
			continue
		}
		pendingPaymentIDs = append(pendingPaymentIDs, id)
	}

	if len(pendingPaymentIDs) == 0 {
		fmt.Println("✅ No pending payments to process.")
		return
	}

	rand.Shuffle(len(pendingPaymentIDs), func(i, j int) {
		pendingPaymentIDs[i], pendingPaymentIDs[j] = pendingPaymentIDs[j], pendingPaymentIDs[i]
	})
	successCount := int(float64(len(pendingPaymentIDs)) * 0.8)
	var processedCount int

	for i := 0; i < successCount; i++ {
		paymentID := pendingPaymentIDs[i]

		_, err := db.Exec(`UPDATE payments SET payment_status = 'SUCCESSFUL' WHERE payment_id = $1`, paymentID)
		if err != nil {
			log.Printf("❌ Failed to update payment %d to SUCCESSFUL: %v", paymentID, err)
			continue
		}
		processedCount++
	}
	fmt.Printf("✅ Processed %d payments to 'SUCCESSFUL' status.\n", processedCount)
}

// seedAdditionalServices adds extra services to trips.
func seedAdditionalServices(db *sql.DB) {
	services := []string{"Internet", "Food service", "Bed"}
	for tripID := 1; tripID <= numTrips; tripID++ {
		numServices := gofakeit.Number(0, 3)
		gofakeit.ShuffleStrings(services)
		for i := 0; i < numServices; i++ {
			_, err := db.Exec(`INSERT INTO additional_services (trip_id, service_type) VALUES ($1, $2)`, tripID, services[i])
			if err != nil {
			}
		}
	}
	fmt.Printf("✅ Seeded additional_services for up to %d trips.\n", numTrips)
}

// seedTransportDetails adds specific details (trains, flights, buses) for each trip.
func seedTransportDetails(db *sql.DB) {
	airportNames := []string{"JFK International", "LAX", "Heathrow", "Charles de Gaulle", "Dubai Intl"}
	for tripID := 1; tripID <= numTrips; tripID++ {
		var tripType string
		err := db.QueryRow(`SELECT trip_vehicle FROM tickets WHERE trip_id = $1 LIMIT 1`, tripID).Scan(&tripType)
		if err != nil {
			continue
		}

		switch tripType {
		case "TRAIN":
			_, err = db.Exec(`INSERT INTO trains (trip_id, stars, room_type) VALUES ($1, $2, $3)`, tripID, gofakeit.Number(1, 5), gofakeit.RandomString([]string{"4-BED", "6-BED"}))
		case "FLIGHT":
			depAirport := gofakeit.RandomString(airportNames)
			arrAirport := gofakeit.RandomString(airportNames)
			for depAirport == arrAirport {
				arrAirport = gofakeit.RandomString(airportNames)
			}
			_, err = db.Exec(`INSERT INTO flights (trip_id, class, departure_airport, arrival_airport) VALUES ($1, $2, $3, $4)`, tripID, gofakeit.RandomString([]string{"Economy class", "Business class", "First class"}), depAirport, arrAirport)
		case "BUS":
			_, err = db.Exec(`INSERT INTO buses (trip_id, class, chair_type) VALUES ($1, $2, $3)`, tripID, gofakeit.RandomString([]string{"VIP", "Standard", "Sleeper"}), gofakeit.RandomString([]string{"1-2", "2-2"}))
		}
		if err != nil {
			log.Printf("❌ Failed to insert transport detail for trip %d: %v", tripID, err)
		}
	}
	fmt.Println("✅ All transport-specific details inserted.")
}

var reportTopics = []string{"Incorrect price", "Seat not available", "Wrong destination", "Late departure"}
var reportContents = []string{"I was charged more than the shown price.", "Seat selected was not reserved for me."}

// seedReports creates random reports linked to reservations.
func seedReports(db *sql.DB) {
	var reservationIDs []int
	rows, err := db.Query("SELECT reservation_id FROM reservations")
	if err != nil {
		log.Fatalf("Failed to query reservations for reports: %v", err)
	}
	defer rows.Close()
	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			log.Fatalf("Failed to scan reservation_id for reports: %v", err)
		}
		reservationIDs = append(reservationIDs, id)
	}
	if len(reservationIDs) == 0 {
		fmt.Println("✅ No reservations to create reports for.")
		return
	}

	for i := 0; i < numReports; i++ {
		resID := reservationIDs[rand.Intn(len(reservationIDs))]

		var userID int
		err := db.QueryRow(`SELECT user_id FROM reservations WHERE reservation_id = $1`, resID).Scan(&userID)
		if err != nil {
			continue
		}

		_, err = db.Exec(`INSERT INTO reports (user_id, link_type, link_id, topic, content) VALUES ($1, 'RESERVATION', $2, $3, $4)`, userID, resID, gofakeit.RandomString(reportTopics), gofakeit.RandomString(reportContents))
		if err != nil {
			log.Printf("❌ Failed to insert report for reservation %d: %v", resID, err)
		}
	}
	fmt.Printf("✅ Inserted %d reservation-based reports.\n", numReports)
}
