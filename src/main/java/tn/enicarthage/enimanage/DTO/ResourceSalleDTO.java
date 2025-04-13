package tn.enicarthage.enimanage.DTO;

import lombok.Data;
import tn.enicarthage.enimanage.Model.Resource;
import tn.enicarthage.enimanage.Model.Salle;

@Data
public class ResourceSalleDTO {
    private Salle salle;
    private Resource resource;
    private Integer quantity;
}