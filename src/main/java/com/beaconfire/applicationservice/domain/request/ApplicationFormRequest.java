package com.beaconfire.applicationservice.domain.request;

import com.beaconfire.applicationservice.domain.entity.*;
import lombok.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApplicationFormRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private String preferredName;
//    private String profilePicturePath;
    private Address address;
    private String email;
    private String cellPhoneNum;
    private String workPhoneNum;

    private String gender;
    private Date dateOfBirth;
    private String SSN;
//    private Car car;
    private Boolean isCitizenOrGreenCard;
    private String identity; // citizen or green card or Visa
    private VisaStatus visaStatus;
    private String driverLicenseNumber;
    private Date expirationDate;
    private Employee reference;
    private List<Contact> contacts;

}
