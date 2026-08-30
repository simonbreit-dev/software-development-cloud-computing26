package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.model.ErrorResponse;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.FieldError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorResponse body = new ErrorResponse(
                URI.create("about:blank"),
                "Constraint Violation",
                HttpStatus.BAD_REQUEST.value()
        );
        body.setDetail(ex.getMessage());
        ex.getConstraintViolations().forEach(violation -> body.addErrorsItem(
                new FieldError(violation.getPropertyPath().toString(), violation.getMessage())));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ErrorResponse body = new ErrorResponse(
                URI.create("about:blank"),
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value()
        );
        body.setDetail(ex.getMessage());
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> body.addErrorsItem(
                new FieldError(fieldError.getField(), fieldError.getDefaultMessage())));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }
}
