package com.beaconfire.applicationservice.exception;

public class rejectApplicationFailedException extends RuntimeException{
    public rejectApplicationFailedException(){
        super(String.format("Cannot reject a rejected or approved application!"));
    }
}
