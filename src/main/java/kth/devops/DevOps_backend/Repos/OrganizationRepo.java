package kth.devops.DevOps_backend.Repos;

import kth.devops.DevOps_backend.Entities.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepo extends JpaRepository<OrganizationEntity, Long> {}