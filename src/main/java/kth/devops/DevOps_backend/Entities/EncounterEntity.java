
package kth.devops.DevOps_backend.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "encounters")
public class EncounterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @Column(name = "encounter_date", nullable = false)
    private LocalDateTime encounterDate;

    @Column(length = 1000)
    private String reason;

    protected EncounterEntity() {
    }

    public EncounterEntity(
            PatientEntity patient,
            OrganizationEntity organization,
            LocationEntity location,
            UserEntity createdBy,
            LocalDateTime encounterDate,
            String reason
    ) {
        this.patient = patient;
        this.organization = organization;
        this.location = location;
        this.createdBy = createdBy;
        this.encounterDate = encounterDate;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getEncounterDate() {
        return encounterDate;
    }

    public String getReason() {
        return reason;
    }
}
