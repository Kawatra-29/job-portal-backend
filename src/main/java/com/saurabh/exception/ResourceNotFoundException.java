package com.saurabh.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // A dedicated exception for "thing not found" cases (jobs, users, skills, etc.)
    // This gives GlobalExceptionHandler a specific type to catch and return 404,
    // instead of letting RuntimeException bubble up as a 500.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}