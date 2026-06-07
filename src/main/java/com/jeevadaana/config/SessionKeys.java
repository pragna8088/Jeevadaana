package com.jeevadaana.config;

/**
 * Constants for the keys used to store authenticated principals in the HTTP session.
 */
public final class SessionKeys {

    public static final String DONOR_ID = "AUTH_DONOR_ID";
    public static final String DONOR_NAME = "AUTH_DONOR_NAME";
    public static final String ORGANIZER_ID = "AUTH_ORGANIZER_ID";
    public static final String ORGANIZER_NAME = "AUTH_ORGANIZER_NAME";

    private SessionKeys() {
    }
}
