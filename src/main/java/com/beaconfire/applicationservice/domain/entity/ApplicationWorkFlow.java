package com.beaconfire.applicationservice.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "application_workflow", schema = "ApplicationService")
public class ApplicationWorkFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "employee_id", unique = true, nullable = false)
    private int employeeId;

    @Column(name = "create_date", nullable = false)
    private Timestamp createDate;

    @Column(name = "last_modification_date", nullable = false)
    private Timestamp lastModificationDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "comment", length = 100)
    private String comment;
}
