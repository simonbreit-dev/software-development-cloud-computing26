package de.quadflal.sdfccbackend.common.security;

import de.quadflal.sdfccbackend.adapter.in.web.generated.model.ErrorResponse;
import de.quadflal.sdfccbackend.core.exception.InvalidCredentialsException;
import de.quadflal.sdfccbackend.core.exception.ListNotFoundException;
import de.quadflal.sdfccbackend.core.exception.RestaurantAlreadyInListException;
import de.quadflal.sdfccbackend.core.exception.RestaurantNotFoundException;
import de.quadflal.sdfccbackend.core.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.BAD_REQUEST, "Bad Request", "Validation failed");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            HttpServletRequest request,
            MethodArgumentNotValidException exception
    ) {
        ErrorResponse response = buildProblemBody(request, HttpStatus.BAD_REQUEST, "Bad Request", "Validation failed");
        List<de.quadflal.sdfccbackend.adapter.in.web.generated.model.FieldError> errors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toGeneratedFieldError)
                .toList();
        response.setErrors(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.BAD_REQUEST, "Bad Request", "Malformed request body");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid credentials");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.NOT_FOUND, "Not Found", "User not found");
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantNotFound(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.NOT_FOUND, "Not Found", "Restaurant not found");
    }

    @ExceptionHandler(ListNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleListNotFound(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.NOT_FOUND, "Not Found", "List not found");
    }

    @ExceptionHandler(RestaurantAlreadyInListException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantAlreadyInList(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.CONFLICT, "Conflict", "Restaurant already in list");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            HttpServletRequest request,
            ResponseStatusException exception
    ) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String title = status.getReasonPhrase();
        String detail = exception.getReason() != null ? exception.getReason() : title;
        return buildProblem(request, status, title, detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error");
    }

    private ResponseEntity<ErrorResponse> buildProblem(
            HttpServletRequest request,
            HttpStatus status,
            String title,
            String detail
    ) {
        return ResponseEntity.status(status).body(buildProblemBody(request, status, title, detail));
    }

    private ErrorResponse buildProblemBody(
            HttpServletRequest request,
            HttpStatus status,
            String title,
            String detail
    ) {
        ErrorResponse error = new ErrorResponse(URI.create("about:blank"), title, status.value());
        error.setDetail(detail);
        error.setInstance(URI.create(request.getRequestURI()));
        error.setTimestamp(OffsetDateTime.now());
        return error;
    }

    private de.quadflal.sdfccbackend.adapter.in.web.generated.model.FieldError toGeneratedFieldError(FieldError fieldError) {
        return new de.quadflal.sdfccbackend.adapter.in.web.generated.model.FieldError(
                fieldError.getField(),
                fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value"
        );
    }
}
