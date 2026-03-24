package com.saurabh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // FIX 8: Before, only EmailAlreadyExistsException was handled here.
    // Every other RuntimeException ("Job not found", "Unauthorized", etc.)
    // returned HTTP 500 — which means your frontend saw a server crash for
    // what are actually normal 404/403 situations.
    //
    // Now we handle each case with the correct HTTP status:
    //   ResourceNotFoundException  -> 404 Not Found
    //   EmailAlreadyExistsException -> 400 Bad Request
    //   IllegalStateException       -> 409 Conflict  (e.g. "already applied")
    //   AccessDeniedException       -> 403 Forbidden
    //   BadCredentialsException     -> 401 Unauthorized
    //   RuntimeException (fallback) -> 500 (only for truly unexpected errors)

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailExists(EmailAlreadyExistsException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // "Skill already added", "Already applied for this job"
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Triggered when @PreAuthorize fails or when service throws AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied");
    }

    // Wrong password on login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    // Catch-all for anything unexpected — still 500, but at least it's structured JSON
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(RuntimeException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message),
                status);
    }
}