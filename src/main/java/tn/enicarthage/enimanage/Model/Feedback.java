// Feedback.java
package tn.enicarthage.enimanage.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Nouveaux champs quantitatifs
    @Column(nullable = false)
    private Integer noteGlobale;         // 1-5

    @Column(nullable = false)
    private Integer pertinenceEtudes;    // 1-5

    @Column(nullable = false)
    private Integer qualiteOrganisation; // 1-5

    @Column(nullable = false)
    private Integer noteAmbiance;       // 1-5

    @Column(nullable = false)
    private Boolean recommandation;     // true = oui, false = non

    @Column(nullable = false)
    private Date creationDate;
}