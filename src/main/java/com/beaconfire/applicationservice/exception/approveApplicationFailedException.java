package com.beaconfire.applicationservice.exception;

public class approveApplicationFailedException extends RuntimeException{
    public approveApplicationFailedException(){
        super(String.format("Cannot approve an rejected or approved application!"));
    }
}
