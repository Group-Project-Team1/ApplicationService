package com.beaconfire.applicationservice.exception;

public class submitApplicationFailedException extends RuntimeException{
    public submitApplicationFailedException(String message){
        super(String.format(message));
    }
}

