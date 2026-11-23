package kth.devops.DevOps_backend.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conditions")
public class ConditionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @ManyToOne
    @JoinColumn(name = "encounter_id")
    private EncounterEntity encounter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "diagnosed_by", nullable = false)
    private UserEntity diagnosedBy;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(name = "diagnosed_at", nullable = false)
    private LocalDateTime diagnosedAt;

    protected ConditionEntity() {
    }

    public ConditionEntity(PatientEntity patient, EncounterEntity encounter, UserEntity diagnosedBy, String name, String description, String status) {
        this.patient = patient;
        this.encounter = encounter;
        this.diagnosedBy = diagnosedBy;
        this.name = name;
        this.description = description;
        this.status = status;
        this.diagnosedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public EncounterEntity getEncounter() {
        return encounter;
    }

    public UserEntity getDiagnosedBy() {
        return diagnosedBy;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDiagnosedAt() {
        return diagnosedAt;
    }
}
