package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;

public class Ticket {
    private Long tripId;

    private AgeRange age;

    private int price;

    private TripType tripVehicle;

    public Ticket(Long tripId, AgeRange age, int price, TripType tripVehicle) {
        this.tripId = tripId;
        this.age = age;
        this.price = price;
        this.tripVehicle = tripVehicle;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public AgeRange getAge() {
        return age;
    }

    public void setAge(AgeRange age) {
        this.age = age;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public TripType getTripVehicle() {
        return tripVehicle;
    }

    public void setTripVehicle(TripType tripVehicle) {
        this.tripVehicle = tripVehicle;
    }
}
