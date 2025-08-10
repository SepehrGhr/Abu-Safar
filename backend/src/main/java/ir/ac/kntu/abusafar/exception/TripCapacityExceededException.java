package ir.ac.kntu.abusafar.exception;

public class TripCapacityExceededException extends RuntimeException{
    public TripCapacityExceededException(String message) {
        super(message);
    }
}
