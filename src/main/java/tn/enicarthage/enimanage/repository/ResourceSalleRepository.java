package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.enimanage.Model.ResourceSalle;

public interface ResourceSalleRepository extends JpaRepository<ResourceSalle, Long> {
    void deleteBySalleId(Long salleId);
}