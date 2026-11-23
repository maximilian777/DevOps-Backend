package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.ObservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservationRepo extends JpaRepository<ObservationEntity, Long> {
    List<ObservationEntity> findByPatientId(Long patientId);
}
