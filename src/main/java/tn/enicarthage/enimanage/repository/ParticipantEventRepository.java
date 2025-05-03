package tn.enicarthage.enimanage.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.enicarthage.enimanage.Model.Event;
import tn.enicarthage.enimanage.Model.ParticipantEvent;
import tn.enicarthage.enimanage.Model.User;

import java.util.List;

public interface ParticipantEventRepository extends JpaRepository<ParticipantEvent, Long> {
    boolean existsByEventAndUser(Event event, User user);
    @Query("SELECT p FROM ParticipantEvent p WHERE p.event.id = :eventId")
    List<ParticipantEvent> findByEventId(@Param("eventId") Long eventId);

}
