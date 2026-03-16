package com.example.landingdemo.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lost_case")
public class LostCase extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_profile_id", nullable = false)
    private PetProfile petProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserAccount createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private CaseStatus status;

    @Column(name = "lost_at", nullable = false)
    private Instant lostAt;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "city", column = @Column(name = "last_seen_city", length = 120)),
        @AttributeOverride(name = "district", column = @Column(name = "last_seen_district", length = 120)),
        @AttributeOverride(name = "address", column = @Column(name = "last_seen_address", length = 255)),
        @AttributeOverride(name = "latitude", column = @Column(name = "last_seen_latitude", precision = 9, scale = 6)),
        @AttributeOverride(name = "longitude", column = @Column(name = "last_seen_longitude", precision = 9, scale = 6)),
        @AttributeOverride(name = "notes", column = @Column(name = "last_seen_notes", length = 1000))
    })
    private SearchLocation lastSeenLocation;

    @Column(name = "circumstances", nullable = false, length = 4000)
    private String circumstances;

    @Column(name = "contact_phone", nullable = false, length = 40)
    private String contactPhone;

    @Column(name = "reward_details", length = 500)
    private String rewardDetails;

    @Column(name = "urgent", nullable = false)
    private boolean urgent;
}
