package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.enicarthage.enimanage.DTO.EventStatsDTO;
import tn.enicarthage.enimanage.Model.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT AVG(f.noteGlobale) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAverageNoteGlobaleByEventId(@Param("eventId") Long eventId);

    @Query("SELECT AVG(f.pertinenceEtudes) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAveragePertinenceEtudesByEventId(@Param("eventId") Long eventId);

    @Query("SELECT AVG(f.qualiteOrganisation) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAverageQualiteOrganisationByEventId(@Param("eventId") Long eventId);

    @Query("SELECT AVG(f.noteAmbiance) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAverageNoteAmbianceByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.event.id = :eventId AND f.recommandation = true")
    Long countByEventIdAndRecommandation(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.event.id = :eventId")
    Long countByEventId(@Param("eventId") Long eventId);

    List<Feedback> findByEventId(Long eventId);

    @Query("SELECT " +
            "new tn.enicarthage.enimanage.DTO.EventStatsDTO(" +
            "AVG(f.noteGlobale), " +
            "AVG(f.pertinenceEtudes), " +
            "AVG(f.qualiteOrganisation), " +
            "AVG(f.noteAmbiance), " +
            "SUM(CASE WHEN f.recommandation = true THEN 1 ELSE 0 END), " +
            "COUNT(f)) " +
            "FROM Feedback f WHERE f.event.id = :eventId")
    EventStatsDTO getEventStats(@Param("eventId") Long eventId);

}
