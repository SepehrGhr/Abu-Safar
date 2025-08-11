package ir.ac.kntu.abusafar.exception;

//@TODO add to global exception handler
public class OtpValidationException extends RuntimeException {
    public OtpValidationException(String message) {
        super(message);
    }
}