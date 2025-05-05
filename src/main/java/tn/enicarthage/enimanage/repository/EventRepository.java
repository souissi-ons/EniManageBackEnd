package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.enimanage.Model.Event;
import tn.enicarthage.enimanage.Model.EventStatus;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    // EventRepository.java (interface)
    List<Event> findByStatus(EventStatus status);
}
