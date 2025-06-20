package ir.ac.kntu.abusafar.config;

import ir.ac.kntu.abusafar.dto.exception.BindExceptionResponseDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public ResponseEntity<BaseResponse<List<BindExceptionResponseDTO>>> handleMethodArgumentInvalidException(MethodArgumentNotValidException ex) {
        List<BindExceptionResponseDTO> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(e -> {
            String fieldName;
            String errorMessage = e.getDefaultMessage();

            if (e instanceof FieldError) {
                fieldName = ((FieldError) e).getField();
            } else {
                fieldName = e.getObjectName();
            }
            errors.add(new BindExceptionResponseDTO(fieldName, errorMessage));
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(null, ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleTicketNotFound(TicketNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(null, ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleTripNotFound(TripNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(null, ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleLocationNotFound(LocationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(null, ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleReservationNotFound(ReservationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(null, ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleCompanyNotFound(CompanyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(null, ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler({SeatUnavailableException.class, TripCapacityExceededException.class})
    public ResponseEntity<BaseResponse<String>> handleConflictExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(BaseResponse.fail(null, ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler({OtpValidationException.class, PaymentFailedException.class, InsufficientBalanceException.class, InvalidRoundTripException.class, ReservationFailedException.class})
    public ResponseEntity<BaseResponse<String>> handleBadRequestExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(null, ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler({ReservationPersistenceException.class, NotificationSendException.class})
    public ResponseEntity<BaseResponse<String>> handleInternalServerErrors(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(null, "An internal service error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
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
