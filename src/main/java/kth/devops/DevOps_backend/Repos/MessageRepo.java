package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByConversationIdOrderBySentAtAsc(Long conversationId);
}
