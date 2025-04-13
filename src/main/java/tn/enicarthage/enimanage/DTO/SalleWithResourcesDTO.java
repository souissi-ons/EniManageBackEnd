package tn.enicarthage.enimanage.DTO;

import jakarta.annotation.Nullable;
import lombok.Data;
import tn.enicarthage.enimanage.Model.Batiment;

import java.util.List;

@Data
public class SalleWithResourcesDTO {
    private Long id;
    private String name;
    private String description;
    private Integer capacity;
    private Batiment batiment;
    private List<ResourceSalleDTO> ressources;
}
