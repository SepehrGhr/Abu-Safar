package ir.ac.kntu.abusafar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateContactInfoException extends RuntimeException {
    public DuplicateContactInfoException(String message) {
        super(message);
    }
}
