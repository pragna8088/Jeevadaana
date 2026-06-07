package com.jeevadaana.service;

/**
 * Thrown by the service layer for recoverable, user-facing errors
 * (e.g. duplicate email, invalid credentials, capacity reached).
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}
