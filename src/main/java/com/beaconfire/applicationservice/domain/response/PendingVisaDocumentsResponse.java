package com.beaconfire.applicationservice.domain.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingVisaDocumentsResponse {
    private Integer employeeId;
    private String fileType;
    private String path;
}
