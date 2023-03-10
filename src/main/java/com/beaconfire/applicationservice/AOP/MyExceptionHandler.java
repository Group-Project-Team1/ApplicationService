package com.beaconfire.applicationservice.AOP;

import com.beaconfire.applicationservice.domain.response.ErrorResponse;
import com.beaconfire.applicationservice.exception.CannotAccessOtherUsersDataException;
import com.beaconfire.applicationservice.exception.ResubmitApplicationException;
import com.beaconfire.applicationservice.exception.approveApplicationFailedException;
import com.beaconfire.applicationservice.exception.rejectApplicationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(value = {approveApplicationFailedException.class,
                                CannotAccessOtherUsersDataException.class,
                                rejectApplicationFailedException.class,
                                ResubmitApplicationException.class})
    public ResponseEntity<ErrorResponse> handleRegistrationFailedException(Exception e){
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message("My ExceptionHandler: " + e.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

}

