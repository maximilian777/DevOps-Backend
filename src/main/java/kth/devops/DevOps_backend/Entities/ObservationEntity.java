package kth.devops.DevOps_backend.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "observations")
public class ObservationEntity {

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
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    protected ObservationEntity() {
    }

    public ObservationEntity(PatientEntity patient, EncounterEntity encounter, UserEntity createdBy, String content) {
        this.patient = patient;
        this.encounter = encounter;
        this.createdBy = createdBy;
        this.content = content;
        this.recordedAt = LocalDateTime.now();
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

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}
