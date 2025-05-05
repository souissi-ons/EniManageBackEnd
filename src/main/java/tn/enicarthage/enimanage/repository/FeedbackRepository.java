package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enicarthage.enimanage.Model.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventId(Long eventId);

    // Ajouter pour les statistiques
    Double findAverageNoteGlobaleByEventId(Long eventId);
    Double findAveragePertinenceEtudesByEventId(Long eventId);
    Double findAverageQualiteOrganisationByEventId(Long eventId);
    Double findAverageNoteAmbianceByEventId(Long eventId);
    Long countByEventIdAndRecommandation(Long eventId, Boolean recommandation);
    Long countByEventId(Long eventId);

}
