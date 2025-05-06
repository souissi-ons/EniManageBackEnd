package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.enimanage.Model.Event;
import tn.enicarthage.enimanage.Model.EventStatus;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    // EventRepository.java (interface)
    List<Event> findByStatus(EventStatus status);
    
    @EntityGraph(attributePaths = {"creator", "salle"}) // This ensures the relations are loaded
    List<Event> findAllByStatus(EventStatus status);

    @EntityGraph(attributePaths = {"creator", "salle"})
    Optional<Event> findById(Long id);
}
