// EventDTO.java
package tn.enicarthage.enimanage.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.enicarthage.enimanage.Model.EventStatus;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private Date dateStart;
    private Date dateEnd;
    private boolean isPrivate;
    private int capacity;
    private EventStatus status;
    private Long creatorId;
    private Long salleId;
    private String imageUrl;
    private List<EventResourceDTO> resources;
}