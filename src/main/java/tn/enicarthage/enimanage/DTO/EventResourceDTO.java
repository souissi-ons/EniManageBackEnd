package tn.enicarthage.enimanage.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class EventResourceDTO {
        private Long resourceId;
        private String resourceName;
        private Integer quantity;
    }

