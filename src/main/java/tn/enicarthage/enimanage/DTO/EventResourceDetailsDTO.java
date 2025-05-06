package tn.enicarthage.enimanage.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResourceDetailsDTO {
    private Long id;
    private String name;
    private Integer quantity;
}