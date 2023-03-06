package com.beaconfire.applicationservice.domain.response;

import com.beaconfire.applicationservice.domain.entity.Employee;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {
    Integer applicationId;
    String applicationStatus;
    Employee applicationDerail;
}
