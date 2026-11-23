package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepo extends JpaRepository<DoctorEntity, Long> { }
