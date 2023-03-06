package com.beaconfire.applicationservice.domain.entity;


import lombok.*;

//@Document(collection = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {
//    private Integer id;
    private String addressLine1;
    private String addressLine2;
    private String City;
    private String State;
    private String zipCode;
}
