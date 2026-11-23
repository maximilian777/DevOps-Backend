package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.ConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConditionRepo extends JpaRepository<ConditionEntity, Long> {
    List<ConditionEntity> findByPatientId(Long patientId);
}
