package ir.ac.kntu.abusafar.exception;

//@TODO add to global exception handler

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}