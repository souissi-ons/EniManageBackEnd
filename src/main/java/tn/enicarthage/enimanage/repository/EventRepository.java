package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.enimanage.Model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
