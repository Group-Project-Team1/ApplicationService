package com.beaconfire.applicationservice.domain.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contact {
    private Integer id;
    private String firstName;
    private String lastName;
    private String cellPhone;
    private String AlternatePhone;
    private String email;
    private String Relationship;
    private String type;
}