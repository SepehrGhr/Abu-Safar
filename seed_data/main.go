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

	connStr := "user=db_user password=db_password dbname=db_name sslmode=disable host=localhost port=db_port"
	db, err = sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	gofakeit.Seed(time.Now().UnixNano())

	seedUsers(db)
	seedUserContact(db)
	seedLocationDetails(db)
	seedTrips(db)
	seedTickets(db)
	seedReservations(db)
	seedPayments(db, 301, 600)
	seedAdditionalServices(db, 1, 60)
	seedTransportDetails(db)
	seedReports(db)
	seedRoundTrips(db)
}

func hashPassword(password string) (string, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(hashedPassword), nil
}

func seedUsers(db *sql.DB) {
	numUsers := 30

	for i := 0; i < numUsers; i++ {
		firstName := gofakeit.FirstName()
		lastName := gofakeit.LastName()
		userRole := "USER"
		accountStatus := "ACTIVE"
		city := gofakeit.City()
		signUpDate := gofakeit.DateRange(time.Now().AddDate(-2, 0, 0), time.Now())

		password := gofakeit.Password(true, true, true, true, false, 12)
		hashedPassword, err := hashPassword(password)
		if err != nil {
			log.Printf("Error hashing password for user %d: %v\n", i+1, err)
			continue
		}

		profilePicture := "default.png"

		_, err = db.Exec(`
			INSERT INTO users (
				first_name, last_name, user_role, account_status, city, 
				hashed_password, profile_picture, sign_up_date
			) 
			VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
		`, firstName, lastName, userRole, accountStatus, city, hashedPassword, profilePicture, signUpDate)

		if err != nil {
			log.Printf("Error inserting user %d: %v\n", i+1, err)
		}
	}

	fmt.Println("✅ Seeded", numUsers, "users.")
}

func seedUserContact(db *sql.DB) {
	numUsers := 1030

	for i := 1001; i <= numUsers; i++ {
		contactTypes := rand.Intn(2) + 1

		if contactTypes == 1 {
			if rand.Intn(2) == 0 {
				email := gofakeit.Email()
				_, err := db.Exec(`
					INSERT INTO user_contact (user_id, contact_type, contact_info)
					VALUES ($1, 'EMAIL', $2)
				`, i, email)
				if err != nil {
					log.Printf("Error inserting email for user %d: %v\n", i, err)
				}
			} else {
				phone := gofakeit.Phone()
				_, err := db.Exec(`
					INSERT INTO user_contact (user_id, contact_type, contact_info)
					VALUES ($1, 'PHONE_NUMBER', $2)
				`, i, phone)
				if err != nil {
					log.Printf("Error inserting phone number for user %d: %v\n", i, err)
				}
			}
		} else if contactTypes == 2 {
			email := gofakeit.Email()
			phone := gofakeit.Phone()

			_, err := db.Exec(`
				INSERT INTO user_contact (user_id, contact_type, contact_info)
				VALUES ($1, 'EMAIL', $2)
			`, i, email)
			if err != nil {
				log.Printf("Error inserting email for user %d: %v\n", i, err)
			}

			_, err = db.Exec(`
				INSERT INTO user_contact (user_id, contact_type, contact_info)
				VALUES ($1, 'PHONE_NUMBER', $2)
			`, i, phone)
			if err != nil {
				log.Printf("Error inserting phone number for user %d: %v\n", i, err)
			}
		}
	}

	fmt.Println("✅ Seeded user_contact data.")
}

var locations = map[string]map[string][]string{
	"USA": {
		"California": {"Los Angeles", "San Francisco", "San Diego"},
		"New York":   {"New York City", "Buffalo", "Rochester"},
		"Texas":      {"Austin", "Houston", "Dallas"},
	},
	"Canada": {
		"Ontario":          {"Toronto", "Ottawa", "Hamilton"},
		"Quebec":           {"Montreal", "Quebec City", "Gatineau"},
		"British Columbia": {"Vancouver", "Victoria", "Kelowna"},
	},
	"Germany": {
		"Berlin":  {"Berlin City", "Potsdam"},
		"Bavaria": {"Munich", "Nuremberg", "Augsburg"},
	},
	"Iran": {
		"Mazandaran": {"Sari", "Babol", "Ramsar"},
		"Tehran":     {"Tehran", "Damavand"},
		"Markazi":    {"Arak", "Saveh"},
	},
}

func seedLocationDetails(db *sql.DB) {
	for country, provinces := range locations {
		for province, cities := range provinces {
			for _, city := range cities {
				_, err := db.Exec(`
					INSERT INTO location_details (country, province, city)
					VALUES ($1, $2, $3)
				`, country, province, city)
				if err != nil {
					log.Printf("Error inserting location (%s, %s, %s): %v\n", country, province, city, err)
				}
			}
		}
	}

	fmt.Println("✅ Seeded location_details data.")
}

var vehicleCompanies = []string{
	"Delta Airlines", "Air France", "British Airways", "Emirates", "United Airlines",
	"Amtrak", "Deutsche Bahn", "Eurostar", "Indian Railways", "China Railway",
	"Singapore Airlines", "Qatar Airways", "Lufthansa", "Southwest Airlines", "Alitalia",
}

func seedTrips(db *sql.DB) {

	for i := 1; i <= 60; i++ {
		originLocationID := rand.Intn(30) + 1
		destinationLocationID := rand.Intn(30) + 1

		for originLocationID == destinationLocationID {
			destinationLocationID = rand.Intn(30) + 1
		}

		departureTimestamp := time.Now().Add(time.Duration(rand.Intn(1000)) * time.Hour)

		hoursCount := rand.Intn(24) + 1
		arrivalTimestamp := departureTimestamp.Add(time.Hour * time.Duration(hoursCount))

		stopCount := rand.Intn(3)

		totalCapacity := rand.Intn(51) + 50

		vehicleCompany := vehicleCompanies[rand.Intn(len(vehicleCompanies))]

		_, err := db.Exec(`
			INSERT INTO trips (
				origin_location_id, 
				destination_location_id, 
				departure_timestamp, 
				arrival_timestamp, 
				stop_count, 
				total_capacity, 
				vehicle_company
			)
			VALUES ($1, $2, $3, $4, $5, $6, $7)
		`, originLocationID, destinationLocationID, departureTimestamp, arrivalTimestamp, stopCount, totalCapacity, vehicleCompany)

		if err != nil {
			log.Printf("Error inserting trip from %d to %d: %v\n", originLocationID, destinationLocationID, err)
		}
	}

	fmt.Println("✅ Seeded trips data with vehicle company.")
}

func seedTickets(db *sql.DB) {

	tripTypes := []string{"TRAIN", "BUS", "FLIGHT"}
	ageCategories := []string{"BABY", "CHILD", "ADULT"}

	for tripID := 1; tripID <= 60; tripID++ {
		tripType := tripTypes[rand.Intn(len(tripTypes))]

		basePrice := float64(rand.Intn(101) + 100)

		priceMap := map[string]float64{
			"BABY":  basePrice,
			"CHILD": basePrice + 50,
			"ADULT": basePrice + 150,
		}

		for _, age := range ageCategories {
			_, err := db.Exec(`
				INSERT INTO tickets (trip_id, age, price, trip_vehicle)
				VALUES ($1, $2, $3, $4)
			`, tripID, age, priceMap[age], tripType)

			if err != nil {
				log.Printf("Error inserting ticket for trip %d (%s): %v", tripID, age, err)
			}
		}
	}

	fmt.Println("✅ Seeded tickets for trips 1 to 60.")
}

func seedReservations(db *sql.DB) {
	ageCategories := []string{"ADULT", "CHILD", "BABY"}

	count := 300 

	for i := 0; i < count; i++ {
		userID := gofakeit.Number(1, 1000)

		var reservationID int64
		err := db.QueryRow(`
			INSERT INTO reservations (user_id)
			VALUES ($1)
			RETURNING reservation_id
		`, userID).Scan(&reservationID)

		if err != nil {
			log.Printf("❌ Failed to insert reservation %d: %v", i+1, err)
			continue
		}

		tripID := gofakeit.Number(1, 60)
		age := ageCategories[gofakeit.Number(0, 2)]
		seatNumber := gofakeit.Number(1, 30)

		_, err = db.Exec(`
			INSERT INTO ticket_reservation (trip_id, age, reservation_id, seat_number)
			VALUES ($1, $2, $3, $4)
		`, tripID, age, reservationID, seatNumber)

		if err != nil {
			log.Printf("❌ Failed to insert ticket_reservation for reservation %d: %v", reservationID, err)
		}
	}

	fmt.Printf("✅ Seeded %d reservations and matching ticket_reservations.\n", count)
}

func seedPayments(db *sql.DB, fromID, toID int) {
	paymentMeans := []string{"CARD", "WALLET", "CRYPTO"}

	for reservationID := fromID; reservationID <= toID; reservationID++ {
		var userID int
		var tripID int
		var age string

		err := db.QueryRow(`SELECT user_id FROM reservations WHERE reservation_id = $1`, reservationID).Scan(&userID)
		if err != nil {
			log.Printf("❌ Failed to find reservation %d: %v", reservationID, err)
			continue
		}

		err = db.QueryRow(`
			SELECT trip_id, age FROM ticket_reservation WHERE reservation_id = $1
		`, reservationID).Scan(&tripID, &age)
		if err != nil {
			log.Printf("❌ Failed to find ticket_reservation for reservation %d: %v", reservationID, err)
			continue
		}

		var price float64
		err = db.QueryRow(`
			SELECT price FROM tickets WHERE trip_id = $1 AND age = $2
		`, tripID, age).Scan(&price)
		if err != nil {
			log.Printf("❌ Failed to find ticket price for reservation %d: %v", reservationID, err)
			continue
		}

		paymentType := paymentMeans[gofakeit.Number(0, 2)]

		_, err = db.Exec(`
			INSERT INTO payments (reservation_id, user_id, payment_status, payment_type, price)
			VALUES ($1, $2, 'SUCCESSFUL', $3, $4)
		`, reservationID, userID, paymentType, price)

		if err != nil {
			log.Printf("❌ Failed to insert payment for reservation %d: %v", reservationID, err)
			continue
		}
	}

	fmt.Printf("✅ Seeded payments for reservations %d to %d.\n", fromID, toID)
}

func seedAdditionalServices(db *sql.DB, fromID, toID int) {
	services := []string{"Internet", "Food service", "Bed"}

	for tripID := fromID; tripID <= toID; tripID++ {
		numServices := gofakeit.Number(0, 3)

		gofakeit.ShuffleStrings(services)
		selectedServices := services[:numServices]

		for _, service := range selectedServices {
			_, err := db.Exec(`
				INSERT INTO additional_services (trip_id, service_type)
				VALUES ($1, $2)
			`, tripID, service)
			if err != nil {
				log.Printf("❌ Failed to insert service %s for trip %d: %v", service, tripID, err)
			}
		}
	}

	fmt.Printf("✅ Seeded additional_services for trips %d to %d.\n", fromID, toID)
}

func seedTransportDetails(db *sql.DB) {
	airportNames := []string{
		"JFK International", "LAX", "Heathrow", "Charles de Gaulle", "Dubai Intl",
		"Singapore Changi", "Mehr Abad", "Frankfurt", "Amsterdam Schiphol", "Istanbul Airport", "Haneda Tokyo",
	}

	for tripID := 1; tripID <= 60; tripID++ {
		var tripType string
		err := db.QueryRow(`SELECT trip_vehicle FROM tickets WHERE trip_id = $1 LIMIT 1`, tripID).Scan(&tripType)
		if err != nil {
			log.Printf("❌ Failed to fetch trip type for trip %d: %v", tripID, err)
			continue
		}

		switch tripType {
		case "TRAIN":
			stars := gofakeit.Number(1, 5)
			roomType := gofakeit.RandomString([]string{"4-BED", "6-BED"})

			_, err = db.Exec(`
				INSERT INTO trains (trip_id, stars, room_type)
				VALUES ($1, $2, $3)
			`, tripID, stars, roomType)
			if err != nil {
				log.Printf("❌ Failed to insert train for trip %d: %v", tripID, err)
			}

		case "FLIGHT":
			class := gofakeit.RandomString([]string{"Economy class", "Business class", "First class"})

			var depAirport, arrAirport string
			for {
				depAirport = gofakeit.RandomString(airportNames)
				arrAirport = gofakeit.RandomString(airportNames)
				if depAirport != arrAirport {
					break
				}
			}

			_, err = db.Exec(`
				INSERT INTO flights (trip_id, class, departure_airport, arrival_airport)
				VALUES ($1, $2, $3, $4)
			`, tripID, class, depAirport, arrAirport)
			if err != nil {
				log.Printf("❌ Failed to insert flight for trip %d: %v", tripID, err)
			}

		case "BUS":
			busClass := gofakeit.RandomString([]string{"VIP", "Standard", "Sleeper"})
			chairType := gofakeit.RandomString([]string{"1-2", "2-2"})

			_, err = db.Exec(`
				INSERT INTO buses (trip_id, class, chair_type)
				VALUES ($1, $2, $3)
			`, tripID, busClass, chairType)
			if err != nil {
				log.Printf("❌ Failed to insert bus for trip %d: %v", tripID, err)
			}

		default:
			log.Printf("❓ Unknown trip type for trip %d: %s", tripID, tripType)
		}
	}

	fmt.Println("✅ All transport-specific details inserted.")
}

var reportTopics = []string{
	"Incorrect price", "Seat not available", "Wrong destination", "Late departure",
	"Overcharged", "Unclear cancellation policy", "Vehicle condition poor", "Misleading info",
	"Incorrect passenger type", "Refund not processed", "Booking failed", "Duplicate reservation",
	"Reservation timeout", "Wrong user info", "Incorrect travel class", "Schedule mismatch",
	"Service missing", "Trip was cancelled", "Unavailable support", "Other",
}

var reportContents = []string{
	"I was charged more than the shown price.", "Seat selected was not reserved for me.",
	"The trip destination was incorrect in the confirmation.", "The bus/train/flight left much later than scheduled.",
	"I didn't receive a refund after cancellation.", "There was no information on what to do after booking.",
	"I booked for an adult but was treated as a child passenger.", "I didn't get the class I paid for.",
	"My reservation failed but money was deducted.", "Customer support didn't help when I contacted them.",
	"The trip was cancelled but I wasn't informed in time.", "The platform showed wrong info about the trip.",
	"I got duplicate reservations for one booking.", "The system timed out during payment.",
	"My user details were changed without permission.", "The services mentioned (like internet) weren't available.",
	"Company was different than what I saw while booking.", "I need to change the passenger name but couldn't.",
	"Security on the vehicle was concerning.", "General issue not listed elsewhere.",
}

func seedReports(db *sql.DB) {

	for i := 0; i < 80; i++ {
		resID := gofakeit.Number(301, 600)

		var userID int
		err := db.QueryRow(`SELECT user_id FROM reservations WHERE reservation_id = $1`, resID).Scan(&userID)
		if err != nil {
			log.Printf("❌ Could not fetch user for reservation %d: %v", resID, err)
			continue
		}

		topic := gofakeit.RandomString(reportTopics)
		content := gofakeit.RandomString(reportContents)

		_, err = db.Exec(`
			INSERT INTO reports (user_id, link_type, link_id, topic, content)
			VALUES ($1, 'RESERVATION', $2, $3, $4)
		`, userID, resID, topic, content)

		if err != nil {
			log.Printf("❌ Failed to insert report for reservation %d: %v", resID, err)
		}
	}

	fmt.Println("✅ Inserted 80 reservation-based reports.")
}

func seedRoundTrips(db *sql.DB) {
	fmt.Println("🌱 Seeding round-trip data...")

	// Fetch all existing location IDs to create valid trips
	rows, err := db.Query("SELECT location_id FROM location_details")
	if err != nil {
		log.Fatalf("❌ Failed to fetch location IDs: %v", err)
	}
	defer rows.Close()

	var locationIDs []int
	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			log.Printf("⚠️ Could not scan location ID: %v", err)
			continue
		}
		locationIDs = append(locationIDs, id)
	}

	if len(locationIDs) < 2 {
		log.Fatal("❌ Not enough locations in the database to create trips. Please seed locations first.")
	}

	// Fetch all company IDs
	companyRows, err := db.Query("SELECT company_id FROM companies")
	if err != nil {
		log.Fatalf("❌ Failed to fetch company IDs: %v", err)
	}
	defer companyRows.Close()

	var companyIDs []int
	for companyRows.Next() {
		var id int
		if err := companyRows.Scan(&id); err != nil {
			log.Printf("⚠️ Could not scan company ID: %v", err)
			continue
		}
		companyIDs = append(companyIDs, id)
	}

	if len(companyIDs) == 0 {
		log.Fatal("❌ No companies in the database. Please seed companies first.")
	}

	tripTypes := []string{"TRAIN", "BUS", "FLIGHT"}
	ageCategories := []string{"ADULT", "CHILD", "BABY"}
	numRoundTrips := 10

	for i := 0; i < numRoundTrips; i++ {
		//--- Create the Outgoing Trip ---
		originLocationID := locationIDs[rand.Intn(len(locationIDs))]
		destinationLocationID := locationIDs[rand.Intn(len(locationIDs))]
		for originLocationID == destinationLocationID {
			destinationLocationID = locationIDs[rand.Intn(len(locationIDs))]
		}
		companyID := companyIDs[rand.Intn(len(companyIDs))]

		// Set departure for tomorrow or later
		departureDaysFromNow := rand.Intn(20) + 1
		departureTimestamp := time.Now().Add(time.Hour * 24 * time.Duration(departureDaysFromNow))

		//Trip duration between 2 and 12 hours
		tripDurationHours := rand.Intn(11) + 2
		arrivalTimestamp := departureTimestamp.Add(time.Hour * time.Duration(tripDurationHours))

		totalCapacity := rand.Intn(51) + 50

		var outgoingTripID int64
		err = db.QueryRow(`
			INSERT INTO trips (origin_location_id, destination_location_id, departure_timestamp, arrival_timestamp, company_id, stop_count, total_capacity)
			VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING trip_id
		`, originLocationID, destinationLocationID, departureTimestamp, arrivalTimestamp, companyID, rand.Intn(3), totalCapacity).Scan(&outgoingTripID)

		if err != nil {
			log.Printf("❌ Failed to insert outgoing trip: %v", err)
			continue
		}

		tripVehicleType := tripTypes[rand.Intn(len(tripTypes))]

		// Add tickets for the outgoing trip
		for _, age := range ageCategories {
			price := float64(gofakeit.Number(50, 500))
			_, err := db.Exec(`
				INSERT INTO tickets (trip_id, age, price, trip_vehicle) VALUES ($1, $2, $3, $4)
			`, outgoingTripID, age, price, tripVehicleType)
			if err != nil {
				log.Printf("❌ Failed to insert ticket for outgoing trip %d: %v", outgoingTripID, err)
			}
		}

		// --- Create the Return Trip ---
		// The return trip departs at least 1 day after the outgoing trip arrives
		returnDepartureDays := rand.Intn(5) + 1
		returnDepartureTimestamp := arrivalTimestamp.Add(time.Hour * 24 * time.Duration(returnDepartureDays))

		returnDurationHours := rand.Intn(11) + 2
		returnArrivalTimestamp := returnDepartureTimestamp.Add(time.Hour * time.Duration(returnDurationHours))

		var returnTripID int64
		err = db.QueryRow(`
			INSERT INTO trips (origin_location_id, destination_location_id, departure_timestamp, arrival_timestamp, company_id, stop_count, total_capacity)
			VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING trip_id
		`, destinationLocationID, originLocationID, returnDepartureTimestamp, returnArrivalTimestamp, companyID, rand.Intn(3), totalCapacity).Scan(&returnTripID)

		if err != nil {
			log.Printf("❌ Failed to insert return trip: %v", err)
			continue
		}

		for _, age := range ageCategories {
			price := float64(gofakeit.Number(50, 500))
			_, err := db.Exec(`
				INSERT INTO tickets (trip_id, age, price, trip_vehicle) VALUES ($1, $2, $3, $4)
			`, returnTripID, age, price, tripVehicleType)
			if err != nil {
				log.Printf("❌ Failed to insert ticket for return trip %d: %v", returnTripID, err)
			}
		}

		fmt.Printf("✅ Seeded round trip: Outgoing Trip ID %d -> Return Trip ID %d (Type: %s)\n", outgoingTripID, returnTripID, tripVehicleType)
	}

	fmt.Println("✅ Finished seeding round-trip data.")
}


