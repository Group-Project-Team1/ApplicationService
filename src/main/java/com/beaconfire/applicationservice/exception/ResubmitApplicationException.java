package com.beaconfire.applicationservice.exception;

public class ResubmitApplicationException extends RuntimeException{
    public ResubmitApplicationException(String message){
        super(String.format(message));
    }
}
