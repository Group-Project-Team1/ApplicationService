package com.beaconfire.applicationservice.domain.entity;

import java.util.Date;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonalDocument {
    private Integer id;
    private String path;

    private String title;
    private String comment;
    private Date createDate;

}
