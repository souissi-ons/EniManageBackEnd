package tn.enicarthage.enimanage.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatsDTO {
    private Double averageGlobalRating;
    private Double averageRelevanceRating;
    private Double averageOrganizationRating;
    private Double averageAtmosphereRating;
    private Long recommendationCount;
    private Long totalFeedbacks;
}