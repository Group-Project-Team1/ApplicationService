package com.beaconfire.applicationservice.exception;

public class CannotAccessOtherUsersDataException extends RuntimeException {
    public CannotAccessOtherUsersDataException(String message) {
        super(String.format(message));
    }
}
