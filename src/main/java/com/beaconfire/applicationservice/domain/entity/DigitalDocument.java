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
@Table(name = "digital_document", schema = "ApplicationService")
public class DigitalDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired;

    @Column(name = "type", length = 100, nullable = false)
    private String type;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "path", length = 100)
    private String path;

    @Column(name = "description", length = 100)
    private String description;
}
