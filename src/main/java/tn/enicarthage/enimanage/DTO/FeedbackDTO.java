// FeedbackDTO.java
package tn.enicarthage.enimanage.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private Long id;
    private Long eventId;
    private Long userId;
    private Integer noteGlobale;
    private Integer pertinenceEtudes;
    private Integer qualiteOrganisation;
    private Integer noteAmbiance;
    private Boolean recommandation;
    private Date creationDate;
}