package com.beaconfire.applicationservice.domain.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "visaDocumentStatus", schema = "ApplicationService")
public class VisaDocumentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "employee_id", unique = true, nullable = false)
    private Integer employeeId;

    @Column(name = "fileId")
    private Integer fileId;

    @Column(name = "path")
    private String path;

    @Column(name = "status")
    private String status;

    @Column(name = "comment")
    private String comment;
}
