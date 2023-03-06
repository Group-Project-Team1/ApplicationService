package com.beaconfire.applicationservice.domain.entity;

import org.bson.types.ObjectId;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "employee1")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {
    @ApiModelProperty(notes = "employee's objectId")
    private ObjectId _id;
    @ApiModelProperty(notes = "employee's employee id")
    private Integer userId;
    @ApiModelProperty(notes = "employee's first name")
    @NotNull
    private String firstName;
    @ApiModelProperty(notes = "employee's last name")
    @NotNull
    private String lastName;
    @ApiModelProperty(notes = "employee's middle name")
    private String middleName;
    @ApiModelProperty(notes = "employee's preferred name")
    private String preferredName;
//    @ApiModelProperty(notes = "employee's profile picture path")
//    private File profilePicture;
    @ApiModelProperty(notes = "employee's current address")
    private Address address;
    @ApiModelProperty(notes = "employee's email")
    @NotNull
    @Email
    private String email;
    @ApiModelProperty(notes = "employee's cell phone number")
    @NotNull
    private String cellPhoneNum;
    @ApiModelProperty(notes = "employee's alternate work phone number")
    private String workPhoneNum;

    @ApiModelProperty(notes = "employee's gender")
    private String gender;
    @ApiModelProperty(notes = "employee's birthday")
    private Date dateOFBirth;
    @ApiModelProperty(notes = "employee's SSN")
    @NotNull
    private String SSN;

    //    @ApiModelProperty(notes = "employee's car info")
    //    private Car car;
    @ApiModelProperty(notes = "employee's identity")
    private String identity; // citizen or green card or Visa
    @ApiModelProperty(notes = "employee's visa status")
    private VisaStatus visaStatus;
    @ApiModelProperty(notes = "employee's driver license's number")
    private String driverLicenseNumber;
    @ApiModelProperty(notes = "employee's driver license's expriation date")
    private Date expirationDate;
    @ApiModelProperty(notes = "employee's reference")
    private Employee reference;
    @ApiModelProperty(notes = "employee's emergency contact")
    private List<Contact> contacts;
    @ApiModelProperty(notes = "employee's personal documents")
    private List<PersonalDocument> personalDocuments = new ArrayList<>();

}
