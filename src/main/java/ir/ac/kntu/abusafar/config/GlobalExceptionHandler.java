package ir.ac.kntu.abusafar.config;

import ir.ac.kntu.abusafar.dto.exception.BindExceptionResponseDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.exception.DuplicateContactInfoException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<List<BindExceptionResponseDTO>>> handleMethodArgumentInvalidException(MethodArgumentNotValidException ex){
        List<BindExceptionResponseDTO> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(e -> {
            String field = ((FieldError) e).getField();
            String error = e.getDefaultMessage();
            errors.add(new BindExceptionResponseDTO(field, error));
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<List<BindExceptionResponseDTO>>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<BindExceptionResponseDTO> errors = ex.getConstraintViolations().stream()
                .map(violation -> new BindExceptionResponseDTO(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(errors));
    }

    @ExceptionHandler(DuplicateContactInfoException.class)
    public ResponseEntity<BaseResponse<String>> handleDuplicateContact(DuplicateContactInfoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(null, ex.getMessage(), 404));
    }

//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity<BaseResponse<String>> handleEntityNotFoundException(EntityNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(BaseResponse.fail(null, ex.getMessage(), 404));
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(BaseResponse.fail(ex.getMessage()));
    }

    /**
     * Handles any unexpected exceptions to prevent exposing internal errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail("An unexpected error occurred. Please try again later."));
    }
}
