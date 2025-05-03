package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enicarthage.enimanage.Model.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventId(Long eventId);  // Méthode de recherche par ID d'événement

}
