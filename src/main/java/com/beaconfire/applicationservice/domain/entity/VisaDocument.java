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
@Table(name = "visaDocuments", schema = "ApplicationService")
public class VisaDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "fileType", unique = true, nullable = false)
    private String fileType;
}
