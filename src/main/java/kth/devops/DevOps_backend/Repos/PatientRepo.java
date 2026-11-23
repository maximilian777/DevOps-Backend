package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.PatientEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepo extends JpaRepository<PatientEntity, Long> {
    List<PatientEntity> findByNameContainingIgnoreCase(String name);

    boolean existsByUserId(Long userId);

    Optional<PatientEntity> findByUserId(Long userId);
}
