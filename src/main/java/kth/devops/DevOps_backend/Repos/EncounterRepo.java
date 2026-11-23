package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.EncounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncounterRepo extends JpaRepository<EncounterEntity, Long> {
    List<EncounterEntity> findByPatientId(Long patientId);
}
