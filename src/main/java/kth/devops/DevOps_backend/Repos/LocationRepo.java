package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepo extends JpaRepository<LocationEntity, Long> {}