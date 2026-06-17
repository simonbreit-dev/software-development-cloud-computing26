package de.quadflal.sdfccbackend.common.security;

import de.quadflal.sdfccbackend.adapter.in.web.generated.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(HttpServletRequest request) {
        return buildProblem(request, HttpStatus.BAD_REQUEST, "Bad Request", "Validation failed");
    }

    private ResponseEntity<ErrorResponse> buildProblem(
            HttpServletRequest request,
            HttpStatus status,
            String title,
            String detail
    ) {
        ErrorResponse error = new ErrorResponse(URI.create("about:blank"), title, status.value());
        error.setDetail(detail);
        error.setInstance(URI.create(request.getRequestURI()));
        error.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.status(status).body(error);
    }
}
