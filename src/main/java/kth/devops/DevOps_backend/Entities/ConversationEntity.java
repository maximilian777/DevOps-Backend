package kth.devops.DevOps_backend.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserEntity recipient;

    @Column(nullable = false)
    private String subject;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ConversationEntity() {
    }

    public ConversationEntity(PatientEntity patient, UserEntity recipient, String subject) {
        this.patient = patient;
        this.recipient = recipient;
        this.subject = subject;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public UserEntity getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
