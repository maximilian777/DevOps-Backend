package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepo extends JpaRepository<StaffEntity, Long> {}
