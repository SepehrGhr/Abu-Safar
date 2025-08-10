package ir.ac.kntu.abusafar.exception;

public class PaymentFailedException extends RuntimeException{
    public PaymentFailedException(String message) {
        super(message);
    }
}
