package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepo extends JpaRepository<ConversationEntity, Long> {
    List<ConversationEntity> findByPatientId(Long patientId);

    List<ConversationEntity> findByRecipientId(Long recipientId);
}
