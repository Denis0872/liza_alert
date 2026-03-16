package com.example.landingdemo.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pet_profile")
public class PetProfile extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false, length = 40)
    private PetSpecies species;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "breed", length = 120)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false, length = 20)
    private PetSex sex;

    @Column(name = "age_years")
    private Integer ageYears;

    @Column(name = "size_label", length = 40)
    private String sizeLabel;

    @Column(name = "primary_color", length = 80)
    private String primaryColor;

    @Column(name = "secondary_color", length = 80)
    private String secondaryColor;

    @Column(name = "special_marks", length = 2000)
    private String specialMarks;

    @Column(name = "microchip_id", length = 120)
    private String microchipId;

    @Column(name = "collar_details", length = 255)
    private String collarDetails;

    @Column(name = "behavior_notes", length = 2000)
    private String behaviorNotes;
}
