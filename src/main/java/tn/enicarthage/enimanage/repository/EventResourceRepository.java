package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.enimanage.Model.Event;
import tn.enicarthage.enimanage.Model.EventResource;

import java.util.List;

@Repository
public interface EventResourceRepository extends JpaRepository<EventResource, Long> {
    List<EventResource> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);
}