package tn.enicarthage.enimanage.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private Batiment batiment;

    @OneToMany(mappedBy = "salle", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<ResourceSalle> ressources = new ArrayList<>();

    public void addRessource(ResourceSalle ressource) {
        if (ressources == null) {
            ressources = new ArrayList<>();
        }
        ressources.add(ressource);
        ressource.setSalle(this);
    }
}
